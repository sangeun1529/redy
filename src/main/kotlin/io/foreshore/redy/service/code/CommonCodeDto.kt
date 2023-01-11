package io.foreshore.redy.service.code

import com.obvioustraverse.miska.lang.EEnabled
import javax.validation.constraints.NotNull

data class CommonCodeDto(
    val parentCode: String?,
    @field:NotNull
    val code: String,
    @field:NotNull
    var name: String,
    var description: String?,
    var enabled: EEnabled
) {
    constructor(code: CommonCode) : this(
        code.parent?.id, code.id ?: "",
        code.name, code.description, code.enabled
    )

    fun newEntityWithoutParent() = CommonCode(code, name).also {
        it.enabled = this.enabled
        it.description = this.description
    }
}

fun CommonCode.updateBy(dto: CommonCodeDto) = this.apply {
    enabled = dto.enabled
    description = dto.description
    name = dto.name
}

val CommonCode.dto get() = CommonCodeDto(this.parent?.code, this.code, this.name, this.description, this.enabled)
