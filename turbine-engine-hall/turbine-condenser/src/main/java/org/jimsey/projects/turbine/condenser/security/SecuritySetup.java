/**
 * The MIT License
 * Copyright (c) ${project.inceptionYear} the-james-burton
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jimsey.projects.turbine.condenser.security;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecuritySetup extends WebSecurityConfigurerAdapter {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    // // http://stackoverflow.com/questions/31724994/spring-data-rest-and-cors

    http
        .addFilterBefore(newCorsFilter(), ChannelProcessingFilter.class)
        .httpBasic()
        .and()
        .authorizeRequests()
        // .antMatchers("/index.html", "/home.html", "/login.html", "/", "turbine/**", "/user")
        // .permitAll()
        .anyRequest()
        .hasAnyRole("USER")
        // .authenticated()
        .and()
        .csrf().csrfTokenRepository(newCsrfTokenRepository())
        .and()
        .addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class);

  }

  /**
   * https://spring.io/guides/tutorials/spring-security-and-angular-js/
   * 
   * "The other thing we have to do on the server is tell Spring Security to
   * expect the CSRF token in the format that Angular wants to send it back
   * (a header called "X-XRSF-TOKEN" instead of the default "X-CSRF-TOKEN").
   * We do this by customizing the CSRF filter:"
   * 
   * @return
   */
  private CsrfTokenRepository newCsrfTokenRepository() {
    HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
    repository.setHeaderName("X-XSRF-TOKEN");
    return repository;
  }

  /**
   * https://spring.io/guides/tutorials/spring-security-and-angular-js/
   * 
   * "The browser tries to negotiate with our resource server to find out
   * if it is allowed to access it according to the Cross Origin Resource
   * Sharing protocol. It’s not an Angular JS responsibility, so just like
   * the cookie contract it will work like this with all JavaScript in the
   * browser. The two servers do not declare that they have a common
   * origin, so the browser declines to send the request and the UI is broken.
   *
   * To fix that we need to support the CORS protocol which involves a
   * "pre-flight" OPTIONS request and some headers to list the allowed
   * behaviour of the caller."
   *
   * NOTE: With Spring Security, automatic registration is still expected
   * by spring Boot when annotated with @Bean but it DOES NOT WORK
   * Instead, this filter is registered in the configure() method above
   * http://stackoverflow.com/questions/31724994/spring-data-rest-and-cors
   */
  public static CorsFilter newCorsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true); // you USUALLY want this
    config.addAllowedOrigin("*");
    config.addAllowedHeader("*");
    config.addAllowedMethod("HEAD");
    config.addAllowedMethod("GET");
    config.addAllowedMethod("POST");
    config.addAllowedMethod("OPTIONS");
    config.addAllowedMethod("DELETE");
    config.addAllowedMethod("PUT");
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .inMemoryAuthentication()
        .withUser("user").password("password").roles("USER");
  }

  // ------------------------------------------------------------------------

  /*
   * https://spring.io/guides/gs/rest-service-cors/
   * This is the technique described as 'Global CORS configuration" on the
   * link above. However, with spring security, atacama fails with
   * No 'Access-Control-Allow-Origin' header is present on the requested resource.
   * Origin 'http://localhost:3000' is therefore not allowed access.
   * Instead, this class configures CORS by manually adding a filter as you
   * can see in the code above.
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
}