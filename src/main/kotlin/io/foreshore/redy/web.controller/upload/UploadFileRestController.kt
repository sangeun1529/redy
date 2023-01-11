package io.foreshore.redy.web.controller.upload

import com.obvioustraverse.miska.util.api.ApiReturn
import io.foreshore.redy.service.upload.EStorageType
import io.foreshore.redy.service.upload.UploadFileRequestDto
import io.foreshore.redy.service.upload.UploadFileResponseDto
import io.foreshore.redy.service.upload.UploadFileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.validation.Valid
import javax.validation.constraints.Size

@RestController
@RequestMapping("/ajax/upload")
@PreAuthorize("hasAuthority('SYS_CODE')")
class UploadFileRestController(
    @Autowired private val uploadFileService: UploadFileService
) {
    @GetMapping("/")
    fun list(@Valid @Size(max = 50) @RequestParam("search") search: String?, pageable: Pageable) =
        ApiReturn.of(uploadFileService.findAll(search, pageable))

    @PostMapping("/", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun create(
        @Valid @RequestPart("payload") request: UploadFileRequestDto,
        @RequestParam("file") multipartFile: Array<MultipartFile>
    ) = ApiReturn.of(
        if (request.storageType == EStorageType.DB)
            uploadFileService.createFileDb(request, multipartFile[0])
        else
            uploadFileService.createFileFs(request, multipartFile[0])
    )

    @PutMapping("/{fileId}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun update(
        @Valid @Size(min = 36, max = 36) @PathVariable("fileId") fileId: String,
        @Valid @RequestPart("payload") request: UploadFileRequestDto,
        @RequestParam(name = "file", required = false) file: Array<MultipartFile>?
    ) = ApiReturn.of(uploadFileService.update(request.apply { this.fileId = fileId }, file?.let { if (file.isNotEmpty()) file[0] else null }))

    @DeleteMapping("/{fileId}")
    fun delete(@Valid @Size(min = 36, max = 36) @PathVariable("fileId") fileId: String) = fileId.run { uploadFileService.delete(this); ApiReturn.OK }

    @GetMapping("/{fileId}")
    fun get(@Valid @Size(min = 36, max = 36) @PathVariable("fileId") fileId: String) =
        ApiReturn.of(UploadFileResponseDto(uploadFileService.findByIdOrElseThrow(fileId)))
}
