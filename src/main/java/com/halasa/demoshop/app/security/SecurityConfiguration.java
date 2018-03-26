package com.halasa.demoshop.app.security;

import com.halasa.demoshop.api.AuthRestPaths;
import com.halasa.demoshop.api.RestApiPaths;
import com.halasa.demoshop.app.security.jwt.JwtAuthSuccessHandler;
import com.halasa.demoshop.app.security.jwt.JwtSecurityFilter;
import com.halasa.demoshop.app.security.jwt.JwtVerifier;
import com.halasa.demoshop.service.RevokedTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth, AuthenticationProvider authenticationProvider) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    @Configuration
    @Order(1)
    public static class AuthTokenWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        private static String REALM = "DEMOSHOP_APP_REALM";

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            httpSecurity
                    // Authentication endpoint
                    .antMatcher(AuthRestPaths.TOKEN)
                    .authorizeRequests().anyRequest().authenticated()
                    .and().httpBasic().realmName(REALM)
                    .and().exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                    .and().csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        }

    }

    @Configuration
    @Order(2)
    public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        private final List<JwtVerifier> jwtVerifiers;
        private final JwtAuthSuccessHandler authSuccessHandler;
        private final RevokedTokenService revokedTokenService;

        public ApiWebSecurityConfigurationAdapter(
                List<JwtVerifier> jwtVerifiers,
                JwtAuthSuccessHandler authSuccessHandler,
                RevokedTokenService revokedTokenService) {
            this.jwtVerifiers = jwtVerifiers;
            this.authSuccessHandler = authSuccessHandler;
            this.revokedTokenService = revokedTokenService;
        }

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            final AuthenticationEntryPoint authenticationEntryPoint = new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
            final JwtSecurityFilter jwtSecurityFilter = new JwtSecurityFilter(
                    this.jwtVerifiers,
                    authenticationEntryPoint,
                    this.authSuccessHandler,
                    this.revokedTokenService);

            httpSecurity
                    // REST API
                    .antMatcher(RestApiPaths.BASE + "/**")
                    .authorizeRequests().anyRequest().authenticated()
                    .and().addFilterBefore(jwtSecurityFilter, UsernamePasswordAuthenticationFilter.class)
                    .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                    .and().csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        }
    }

    @Configuration
    @Order(3)
    public static class PublicWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            httpSecurity
                    // Any other URI
                    .authorizeRequests().anyRequest().permitAll()
                    .and().csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        }
    }
}
