package com.quipy.server.configuration

import com.quipy.server.security.AuthInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
class SecurityConfiguration : WebSecurityConfigurerAdapter() {
    @Autowired
    lateinit var authInterceptor: AuthInterceptor

    override fun configure(http: HttpSecurity) {
        http
            .formLogin().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .httpBasic().disable()
            .addFilterBefore(authInterceptor, UsernamePasswordAuthenticationFilter::class.java)
            .csrf().disable()
    }
}