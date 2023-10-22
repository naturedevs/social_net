package com.orbvpn.api.config.security;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(
  securedEnabled = true,
  jsr250Enabled = true,
  prePostEnabled = true
)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final JwtTokenFilter jwtTokenFilter;

  @Value("${application.website-url}")
  private String websiteUrl;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    System.out.println("configure");
    http.csrf()
        .disable()
//        .authorizeHttpRequests()
//        .anyRequest()
//        .authenticated()
//        .and()
//        .oauth2Login();
      .cors().configurationSource(corsConfig())
      .and()
      .authorizeRequests()
      .antMatchers("/oauth2/**").permitAll()
//      .antMatchers("/test/**").permitAll()
      .anyRequest().authenticated()
      .and()
      .formLogin()
      .loginPage("/login")
      .permitAll()
//      .and()
//      .formLogin()
//      .loginPage("/login")
//      .usernameParameter("email")
//      .permitAll()
//      .and()
//      .logout()
//      .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//      .logoutSuccessUrl("/logout-success")
//      .deleteCookies("JSESSIONID")
//      .invalidateHttpSession(true)
      .and()
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
      .csrf().disable()
      .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
      .oauth2Login()
//      .authorizationEndpoint()
//      .baseUri("/oauth2/authorize/*")
//      .and()
//      .redirectionEndpoint()
//      .baseUri("/oauth2/callback/*")
    ;
  }

  // Expose authentication manager bean
  @Override
  @Bean
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  private CorsConfigurationSource corsConfig() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("*"));
    configuration.setAllowedMethods(Arrays.asList("*"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

}
