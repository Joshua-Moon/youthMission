package com.youthmission.config;

import com.youthmission.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity //내가 원하는 시큐리티설정
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AccountService accountService;
    private final DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //내가 설정한 링크는 시큐리티 타지 않도록 하기
        http.authorizeRequests()
                .mvcMatchers("/", "/login", "/sign-up", "/check-email-token",
                        "email-login", "/check-email-login", "/login-link").permitAll()
                .mvcMatchers(HttpMethod.GET,"/profile/*").permitAll()
                .anyRequest().authenticated();

        http.formLogin()//이것만 있는 경우에는 스프링시큐리티가 만들어주는 기본 로그인 뷰
                .loginPage("/login").permitAll(); // 이것을 해야 커스텀한 로그인 뷰 사용&권한

        http.logout()// 기본적 로그아웃 기능
                .logoutSuccessUrl("/"); //로그아웃 성공 시 어디로 보낼건지 등등의 기능

        http.rememberMe()
                .userDetailsService(accountService)
                .tokenRepository(tokenRepository());
    }

    private PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    //스테틱한 리소스 시큐리티 타지 않도록
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .mvcMatchers("/node_modules/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
