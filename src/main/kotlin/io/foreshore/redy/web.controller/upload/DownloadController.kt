package io.foreshore.redy.web.controller.upload

import com.obvioustraverse.miska.io.FileSystemRepository
import com.obvioustraverse.miska.support.spring.web.BinaryModelAndView
import io.foreshore.redy.service.upload.UploadFileDb
import io.foreshore.redy.service.upload.UploadFileFs
import io.foreshore.redy.service.upload.UploadFileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.servlet.ModelAndView
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Controller
class DownloadController(
    @Autowired private val uploadFileService: UploadFileService,
    @Autowired private val fileSystemRepository: FileSystemRepository<Unit>
) {
    // DB에 있으면 직접 보여주고 아니면 redirect
    // DB 파일은 사이즈가 작아야 한다. 유의할 것.
    @GetMapping(value = ["/file/dl/{fileId}", "/ajax/file/dl/{fileId}"])
    fun dlDb(@PathVariable("fileId") fileId: String) = uploadFileService.findByIdOrElseThrow(fileId).let {
        when (it) {
            is UploadFileDb -> ResponseEntity(it.fileContent, HttpHeaders().apply {
                add(HttpHeaders.CONTENT_TYPE, it.contentType)
                add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${URLEncoder.encode(it.fileName, StandardCharsets.UTF_8)}")
            }, HttpStatus.OK)

            is UploadFileFs -> ResponseEntity(HttpHeaders().apply { add(HttpHeaders.LOCATION, "/ajax/file/fs/$fileId") }, HttpStatus.SEE_OTHER)
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping(value = ["/file/fs/{fileId}", "/ajax/file/fs/{fileId}"])
    fun dlFs(@PathVariable("fileId") fileId: String): ModelAndView = uploadFileService.findByIdOrElseThrow(fileId).let {
        if (it is UploadFileFs) {
            return BinaryModelAndView(fileSystemRepository.getFile(it.repositoryPath!!), it.fileName)
        } else {
            throw NoSuchElementException()
        }
    }
}
