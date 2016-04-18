package org.jimsey.projects.turbine.condenser;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecuritySetup extends WebSecurityConfigurerAdapter {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        // http://stackoverflow.com/questions/31724994/spring-data-rest-and-cors
        .addFilterBefore(this.corsFilter(), ChannelProcessingFilter.class)
        .csrf().disable()
        .antMatcher("/turbine/**")
        .authorizeRequests()
        .anyRequest()
        .permitAll();
    // .anyRequest()
    // .hasAnyRole("USER")
    // .and()
    // .httpBasic();
  }

  /*
   * With Spring Security, automatic registration is still expected
   * by spring Boot but DOES NOT WORK
   * Instead, this filter is registered in the configure() method above
   * http://stackoverflow.com/questions/31724994/spring-data-rest-and-cors
   */
  @Bean
  public CorsFilter corsFilter() {
    logger.info("corsFilterDirect init...");
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true); // you USUALLY want this
    config.addAllowedOrigin("*");
    config.addAllowedHeader("*");
    config.addAllowedMethod("GET");
    config.addAllowedMethod("POST");
    config.addAllowedMethod("OPTIONS");
    config.addAllowedMethod("DELETE");
    config.addAllowedMethod("PUT");
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }

  /*
   * ...with spring security, atacama fails with
   * No 'Access-Control-Allow-Origin' header is present on the requested resource.
   * Origin 'http://localhost:3000' is therefore not allowed access.
   */
  // @Bean
  // public FilterRegistrationBean corsFilter() {
  // logger.info("corsFilter init...");
  // UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
  // CorsConfiguration config = new CorsConfiguration();
  // config.setAllowCredentials(true);
  // config.addAllowedOrigin("*");
  // config.addAllowedHeader("*");
  // config.addAllowedMethod("OPTIONS");
  // config.addAllowedMethod("HEAD");
  // config.addAllowedMethod("GET");
  // config.addAllowedMethod("PUT");
  // config.addAllowedMethod("POST");
  // config.addAllowedMethod("DELETE");
  // config.addAllowedMethod("PATCH");
  // source.registerCorsConfiguration("/**", config);
  // // return new CorsFilter(source);
  // final FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
  // bean.setOrder(0);
  // return bean;
  // }

  /*
   * ...with spring security, atacama fails with
   * No 'Access-Control-Allow-Origin' header is present on the requested resource.
   * Origin 'http://localhost:3000' is therefore not allowed access.
   */
  // @Bean
  // public WebMvcConfigurer corsConfigurer() {
  // logger.info("corsConfigurer init...");
  // return new WebMvcConfigurerAdapter() {
  // @Override
  // public void addCorsMappings(CorsRegistry registry) {
  // logger.info("adding CORS mappings...");
  // registry
  // .addMapping("/**")
  // .allowCredentials(true);
  //
  // // .allowedOrigins("*")
  // // .allowedMethods("POST", "GET", "OPTIONS", "DELETE")
  // // .maxAge(3600)
  // // .allowedHeaders("x-requested-with");
  // }
  // };
  // }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .inMemoryAuthentication()
        .withUser("user").password("password").roles("USER");
  }
}