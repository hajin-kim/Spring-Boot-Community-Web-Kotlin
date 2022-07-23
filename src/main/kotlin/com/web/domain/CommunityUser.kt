package com.web.domain

import com.web.domain.base.AuditLoggingBase
import com.web.domain.enums.SocialType
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table
class CommunityUser(
    @Column
    val name: String,

    @Column
    val password: String?,

    @Column
    val email: String,

    @Column
    val pincipal: String?,

    @Column
    @Enumerated(EnumType.STRING)
    val socialType: SocialType?,
) : AuditLoggingBase(), Serializable {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val idx: Long? = null
}
