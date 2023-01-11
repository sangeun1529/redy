package io.foreshore.redy.service.code

import io.foreshore.redy.support.ManuallyAssignedIdEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
@Entity
@Table(name = "property")
class DbProperty(
    @Id
    @Column(length = 50, name = "property_key")
    var key: String,

    @Column(length = 255, name = "property_value")
    var value: String?,

    @Column(length = 2048, name = "property_description")
    var description: String?
) : ManuallyAssignedIdEntity<String>() {
    override fun getId(): String? = key
}
