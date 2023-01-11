package io.foreshore.redy.service.upload

import com.obvioustraverse.miska.lang.EnumBase
import com.obvioustraverse.miska.support.javax.persistence.EnumBaseConverter
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import org.hibernate.annotations.Type
import javax.persistence.*
@Entity
@Table(name = "upload_file")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "storage_type")
@Suppress("UnnecessaryAbstractClass")
abstract class UploadFile { //NOSONAR instance 를 만들어선 안된다.
    @Id
    @GenericGenerator(
        name = "guidGenerator", strategy = "org.hibernate.id.UUIDGenerator",
        parameters = [Parameter(name = "separator", value = "-")]
    )
    @GeneratedValue(generator = "guidGenerator")
    @Column(length = 36)
    var fileId: String? = null

    @Column(length = 512, nullable = false)
    var fileName: String = ""

    @Column(length = 512, nullable = false)
    var originalFileName: String = ""

    @Column(length = 50, nullable = false)
    var contentType: String = ""

    @Column(nullable = false)
    var fileSize: Long = 0

    @Column(length = 2048)
    var fileComment: String? = null
}

@Entity
@DiscriminatorValue("F")
class UploadFileFs : UploadFile() {
    @Column(length = 1024)
    var repositoryPath: String? = null
}

@Entity
@DiscriminatorValue("D")
class UploadFileDb : UploadFile() {
    @Column
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    var fileContent: ByteArray? = null
}

/** 여기 설명.
 */
enum class EStorageType(override val code: String) : EnumBase<String> {
    FS("F"),
    DB("D"),
    ;

    companion object : EnumBase.Values<String, EStorageType>()
}

@Converter(autoApply = true)
class EStorageTypeConverter : EnumBaseConverter<String, EStorageType>(EStorageType.valuesMap)
