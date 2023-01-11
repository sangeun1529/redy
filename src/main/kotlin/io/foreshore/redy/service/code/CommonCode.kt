package io.foreshore.redy.service.code

import com.obvioustraverse.miska.lang.EEnabled
import com.obvioustraverse.miska.support.javax.persistence.EEnabledConverter
import io.foreshore.redy.support.ManuallyAssignedIdEntity
import javax.persistence.*
@Entity
@Table(name = "common_code")
class CommonCode(
    @Id
    @Column(length = 20, name = "code")
    var code: String,

    @Column(length = 50, name = "code_name", nullable = false)
    var name: String

) : ManuallyAssignedIdEntity<String>() {

    @Column(length = 2048, name = "code_description")
    var description: String? = null

    @Column(length = 1, name = "yn_use", nullable = false)
    @Convert(converter = EEnabledConverter::class)
    var enabled: EEnabled = EEnabled.YES

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_code")
    var parent: CommonCode? = null

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "parent_code")
    val codes: MutableList<CommonCode> = mutableListOf()

    override fun getId(): String? = code
}
