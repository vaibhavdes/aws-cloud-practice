package com.test.awsdemo;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class RestSecurity extends WebSecurityConfigurerAdapter {

	@Override
    protected void configure(HttpSecurity http) throws Exception 
    {
        http
         .cors()
         .and()
         .csrf().disable()
         .authorizeRequests()
         .antMatchers("/").anonymous()
		 .antMatchers("/upload").access("hasRole('ADMIN')")   
		 .antMatchers("/download").anonymous()      
		 .antMatchers("/images").authenticated()  
		 .antMatchers("/bucket").access("hasRole('ADMIN')")
         .and()
         .httpBasic()
         .and()
         .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
  
	@Autowired
    public void basicUserGlobalSecurity(AuthenticationManagerBuilder auth) 
            throws Exception 
    {
		// Without Following Login No REST API can be accessed
        auth.inMemoryAuthentication()
            .withUser("vaibhav")
            .password("{noop}vaibhav")
            .roles("ADMIN")
            .and()
            .withUser("test")
            .password("{noop}test")
            .roles("USER");
    }
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Access-Control-Allow-Headers","Access-Control-Allow-Origin","Access-Control-Request-Method", "Access-Control-Request-Headers","Origin","Cache-Control", "Content-Type", "Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
	}
}
