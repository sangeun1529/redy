package io.foreshore.redy.service.upload

import com.obvioustraverse.miska.io.FileSystemRepository
import com.obvioustraverse.miska.util.nullable
import io.foreshore.redy.service.upload.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.MimeTypeUtils
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
@Service
@Transactional(readOnly = true)
class UploadFileService(
    @Autowired val uploadFileRepository: UploadFileRepository,
    @Autowired val fileSystemRepository: FileSystemRepository<Unit>
) {
    fun findByIdOrNull(fileId: String) = uploadFileRepository.findById(fileId).nullable()
    fun findByIdOrElseThrow(fileId: String): UploadFile = uploadFileRepository.findById(fileId).orElseThrow()
    fun findAll(search: String?, pageable: Pageable) = search?.let {
        uploadFileRepository.findAll(
            QUploadFile.uploadFile.fileId.containsIgnoreCase(search)
                .or(
                    QUploadFile.uploadFile.fileName.containsIgnoreCase(search)
                        .or(QUploadFile.uploadFile.fileComment.containsIgnoreCase(search))
                ), pageable
        ).map { it.dto }
    } ?: uploadFileRepository.findAll(pageable).map { it.dto }

    @Transactional(readOnly = false)
    fun createFileDb(request: UploadFileRequestDto, multipartFile: MultipartFile) =
        UploadFileDb().updateByMultiPartFile(request, multipartFile).let {
            uploadFileRepository.saveAndFlush(it).dto
        }

    @Transactional(readOnly = false)
    fun createFileFs(request: UploadFileRequestDto, multipartFile: MultipartFile) =
        UploadFileFs().updateByMultiPartFile(request, multipartFile).let {
            val fs = it as UploadFileFs
            val tobeSaved = fileSystemRepository.tobeSaved(multipartFile.originalFilename!!)
            if (!tobeSaved.parentFile.exists() && !tobeSaved.parentFile.mkdirs()) {
                throw IOException("parent directory making has failed - " + tobeSaved.absolutePath)
            }
            multipartFile.transferTo(tobeSaved)
            fs.repositoryPath = fileSystemRepository.getRelativePath(tobeSaved)
            uploadFileRepository.saveAndFlush(it).dto
        }

    @Transactional(readOnly = false)
    fun update(request: UploadFileRequestDto, multipartFile: MultipartFile?) = with(uploadFileRepository) {
        val fs = findById(request.fileId ?: "").orElseThrow()
        if (multipartFile != null && fs is UploadFileFs && fs.repositoryPath != null) {
            fileSystemRepository.remove(fs.repositoryPath!!)
        }
        fs.updateByMultiPartFile(request, multipartFile).let {
            saveAndFlush(it).dto
        }
    }

    @Transactional(readOnly = false)
    fun delete(fileId: String) {
        val file = uploadFileRepository.findById(fileId).orElseThrow()
        if (file is UploadFileFs && file.repositoryPath != null) {
            fileSystemRepository.remove(file.repositoryPath!!)
        }
        uploadFileRepository.delete(file)
    }

    private val UploadFile.dto get() = UploadFileResponseDto(this)

    private fun UploadFile.updateByMultiPartFile(request: UploadFileRequestDto, file: MultipartFile?) = this.apply {
        fileName = request.fileName ?: fileName
        fileComment = request.fileComment ?: fileComment

        file?.let {
            originalFileName = file.originalFilename ?: ""
            if (request.fileName.isNullOrBlank()) fileName = originalFileName
            fileSize = file.size
            contentType = file.contentType ?: MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE
        }
    }

    private fun UploadFileDb.updateByMultiPartFile(request: UploadFileRequestDto, file: MultipartFile?) = this.apply {
        (this as UploadFile).updateByMultiPartFile(request, file)
        if (file != null) {
            fileContent = file.bytes
        }
    }
}

interface UploadFileRepository : JpaRepository<UploadFile, String>, QuerydslPredicateExecutor<UploadFile>
