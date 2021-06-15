package com.quipy.server.security

import com.quipy.server.security.SecurityUserService.Companion.ROLE_GUEST
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component


@Component
class AuthenticationProvider {
    /**
     * Tests if current user is anonymous
     * @return true if current user is anonymous
     */
    val isAnonymous: Boolean
        get() = user.role == ROLE_GUEST

    /**
     * Returns User entity of current user.
     * If user is anonymous, a dummy id will be set and role will be "ROLE_ANONYMOUS".
     * @return currently logged in user's entity
     */
    val user: User
        get() = SecurityContextHolder.getContext().authentication.principal as User
}