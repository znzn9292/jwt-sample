package com.znzn.tutorial.config;

import com.znzn.tutorial.jwt.JwtAccessDeniedHandler;
import com.znzn.tutorial.jwt.JwtAuthenticationEntryPoint;
import com.znzn.tutorial.jwt.JwtSecurityConfig;
import com.znzn.tutorial.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)  // @preAuthorize 어노테이션을 메소드 단위로 추가하기 위해서 적용
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(
            TokenProvider tokenProvider,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    // PasswordEncoder 는 BCryptPasswordEncoder 사용
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .antMatchers(
                        "/h2-console/**"
                        ,"/favicon.ico"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // basic auth를 사용하기 위해 csrf 보호 기능

                .exceptionHandling() // Exception 핸들링
                .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 401 Unauthorized
                .accessDeniedHandler(jwtAccessDeniedHandler) // 403 Forbidden

                // H2-Console 허용 설정
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                // Session 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/api/hello").permitAll()
                .antMatchers("/api/authenticate").permitAll()
                .antMatchers("/api/signup").permitAll()
                .anyRequest().authenticated()

                // JwtFilter 를 addFilterBefore로 등록했던 JwtSecurityConfig 적용
                .and()
                .apply(new JwtSecurityConfig(tokenProvider));
    }
}

