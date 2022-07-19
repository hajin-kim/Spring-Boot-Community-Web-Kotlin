package com.web.domain

import com.web.domain.base.AuditLoggingBase
import com.web.domain.enums.BoardType
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table
class Board(
    @Column
    val title: String,

    @Column
    val subTitle: String,

    @Column
    val content: String,

    @Column
    @Enumerated(EnumType.STRING)
    val boardType: BoardType,

    @OneToOne(fetch = FetchType.LAZY)
    val communityUser: CommunityUser,
) : AuditLoggingBase(), Serializable {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val idx: Long? = null
}
