package io.foreshore.redy.service.code

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class PropertyDto(
    @field:Size(min = 1, max = 50)
    @field:NotEmpty
    @field:NotNull
    var key: String,

    @field:Size(max = 255)
    val value: String?,

    @field:Size(max = 2048)
    val description: String?
) {
    constructor(property: DbProperty) : this(property.key, property.value, property.description)
}

val PropertyDto.entity get() = DbProperty(key, value, description)
val DbProperty.dto get() = PropertyDto(this)
fun DbProperty.updateBy(request: PropertyDto) = this.apply {
    value = request.value
    description = request.description
}
