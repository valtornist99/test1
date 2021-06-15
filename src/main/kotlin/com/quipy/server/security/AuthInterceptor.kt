package com.quipy.server.security

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthInterceptor : OncePerRequestFilter() {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(AuthInterceptor::class.java)
    }

    @Autowired
    lateinit var securityUserService: SecurityUserService

    @Override
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
//        // skip cors requests
//        // otherwise we won't be able to get access to "authorization" header
        if (request.method == "OPTIONS" || request.method == "HEAD") {
//            authenticate(role = "CORS")
            chain.doFilter(request, response)
        }

        val firebaseToken = request.getHeader("authorization")

        when (val user = securityUserService.getUserByFirebaseToken(firebaseToken)) {
            null -> {
                response.status = 403
                AuthInterceptor.logger.error("No user found for token $firebaseToken")
            }
            else -> {
                authenticate(user)
                AuthInterceptor.logger.error("User authenticated: $user")
                chain.doFilter(request, response)
            }
        }
    }

    private fun authenticate(user: User) {
        val token: Authentication = PreAuthenticatedAuthenticationToken(
            user,
            null,
            listOf(SimpleGrantedAuthority("ROLE_${user.role}"))
        )
        token.isAuthenticated = true
        SecurityContextHolder.getContext().authentication = token
    }
}