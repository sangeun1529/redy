package io.foreshore.redy.service.upload

import io.foreshore.redy.service.upload.EStorageType
import io.foreshore.redy.service.upload.UploadFile
import io.foreshore.redy.service.upload.UploadFileFs
@Suppress("MemberVisibilityCanBePrivate")
data class UploadFileResponseDto(
    var fileId: String,
    var originalFileName: String,
    var contentType: String,
    var storageType: EStorageType = EStorageType.DB
) {
    var fileName: String = originalFileName
    var repositoryPath: String? = null
    var fileComment: String? = null
    var fileSize: Long = 0

    constructor(uploadFile: UploadFile) : this(uploadFile.fileId ?: "", uploadFile.originalFileName, uploadFile.contentType) {
        fileComment = uploadFile.fileComment
        fileSize = uploadFile.fileSize
        fileName = uploadFile.fileName
        when (uploadFile) {
            is UploadFileFs -> {
                storageType = EStorageType.FS
                repositoryPath = uploadFile.repositoryPath
            }
        }
    }
}

data class UploadFileRequestDto(
    var fileId: String?,
    var fileName: String?,
    var fileComment: String?,
    var storageType: EStorageType = EStorageType.DB
)
