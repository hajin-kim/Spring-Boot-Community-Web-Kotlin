package com.web.domain

import com.web.domain.base.AuditLoggingBase
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table
class CommunityUser(
    @Column
    val name: String,

    @Column
    val password: String,

    @Column
    val email: String,
) : AuditLoggingBase(), Serializable {
    @Id
    @Column
    @GeneratedValue
    val idx: Long? = null
}
