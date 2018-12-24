package sec.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // this is how
        // it is set up in development
        http.authorizeRequests().anyRequest().permitAll();
        http.headers().frameOptions().disable();
        http.headers().httpStrictTransportSecurity().disable();
        http.headers().xssProtection().disable();
        http.csrf().disable();
    }
}
