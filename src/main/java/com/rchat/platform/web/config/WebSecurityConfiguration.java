package com.rchat.platform.web.config;

import com.rchat.platform.domain.User;
import com.rchat.platform.service.UserService;
import com.rchat.platform.web.security.RchatSecurityAuthenticationProvider;
import com.rchat.platform.web.security.SecurityUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.core.userdetails.cache.SpringCacheBasedUserCache;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.LazyCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Optional;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    private static final Log log = LogFactory.getLog(WebSecurityConfiguration.class);
    @Autowired
    private UserService userService;
    @Autowired
    private CacheManager cacheMgr;

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().cacheControl().disable();
        http.csrf()/* .disable(); */.csrfTokenRepository(csrfTokenRepository());
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorizeRequests = http
                .authorizeRequests();
        authorizeRequests.antMatchers("/voice/**").permitAll();
        authorizeRequests.antMatchers(permitResources()).permitAll();
        /**
         * swagger
         */
        authorizeRequests.antMatchers("/swagger-ui.html").permitAll();
        authorizeRequests.antMatchers("/webjars/**").permitAll();
        authorizeRequests.antMatchers("/v2/**").permitAll();
        authorizeRequests.antMatchers("/swagger-resources/**").permitAll();
        authorizeRequests.antMatchers("/swagger/**").permitAll();
        authorizeRequests.antMatchers("/sdk/**").permitAll();

        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authenticated = authorizeRequests
                .anyRequest().authenticated();
        authenticated.and().formLogin().loginPage("/login").defaultSuccessUrl("/", true).permitAll();

        authenticated.and().logout().invalidateHttpSession(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET")).addLogoutHandler(logoutHandler())
                .logoutSuccessUrl("/login").invalidateHttpSession(true).deleteCookies("XSRF-TOKEN").permitAll();
        // authenticated.and().httpBasic();
    }


    private String[] permitResources() {
        return new String[]{"/error", "/raws/**", "/stylesheets/**", "/javascripts/**", "/images/**", "/fonts/**",
                "/**/*.js", "/**/*.html","/swagger-ui.html"};
    }

    @Bean
    public LogoutHandler logoutHandler() {
        return (request, response, authentication) -> {
            if (authentication == null)
                return;

            Optional.ofNullable(authentication.getPrincipal()).ifPresent(principal -> {
                SecurityUser securityUser = (SecurityUser) principal;
                User user = securityUser.getUser();

                securityUser.setUser(userService.logout(user));
                log.info(String.format("%s 退出系统!!!", user.getName()));
            });

        };
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        CsrfTokenRepository delegate = CookieCsrfTokenRepository.withHttpOnlyFalse();
        return new LazyCsrfTokenRepository(delegate);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        // authenticationProvider.setUserCache(userCache());
        return new RchatSecurityAuthenticationProvider(userService);
    }

    @Bean
    public UserCache userCache() {
        Cache cache = cacheMgr.getCache("rchat");

        try {
            return new SpringCacheBasedUserCache(cache);
        } catch (Exception e) {
            log.warn("配置UserCache失败！");
            return new NullUserCache();
        }
    }
}
