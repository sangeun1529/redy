package io.foreshore.redy.support

import org.springframework.data.domain.Persistable
import java.io.Serializable
import javax.persistence.MappedSuperclass
import javax.persistence.PostLoad
import javax.persistence.PrePersist
@MappedSuperclass
abstract class ManuallyAssignedIdEntity<T> : Persistable<T>, Serializable {
    @Transient
    private var isNew = true
    override fun isNew(): Boolean {
        return isNew
    }

    @PrePersist
    @PostLoad
    fun markNotNew() {
        isNew = false
    }
    companion object {
        private const val serialVersionUID: Long = 1
    }
}
