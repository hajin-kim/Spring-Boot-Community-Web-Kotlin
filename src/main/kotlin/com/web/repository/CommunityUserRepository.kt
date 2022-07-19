package com.web.repository

import com.web.domain.CommunityUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CommunityUserRepository : JpaRepository<CommunityUser, Long> {
    fun findByEmail(email: String): CommunityUser?
}
