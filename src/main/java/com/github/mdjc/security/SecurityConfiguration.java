package com.github.mdjc.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.Http401AuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.github.mdjc.domain.BillRepository;
import com.github.mdjc.domain.CondoRepository;
import com.github.mdjc.domain.OutlayRepository;
import com.github.mdjc.domain.UserRepository;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	private static final String CONDO = "@webSecurity.checkHasAccessToCondo(authentication,#condoId)";
	private static final String CONDO_MANAGER = "@webSecurity.checkHasAccessToCondoAsManager(authentication,#condoId)";
	private static final String OUTLAY = "@webSecurity.checkHasAccessToOutlay(authentication,#outlayId)";
	private static final String OUTLAY_MANAGER = "@webSecurity.checkHasAccessToOutlayAsManager(authentication,#outlayId)";
	private static final String BILL = "@webSecurity.checkHasAccessToBill(authentication,#billId)";
	private static final String BILL_MANAGER = "@webSecurity.checkHasAccessToBillAsManager(authentication,#billId)";

	//TODO: enable csrf for production
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.httpBasic()
				.authenticationEntryPoint(getBasicAuthenticationEntryPoint())
				.and()
			.logout()
			.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
			.and()
			.authorizeRequests()
				.antMatchers(HttpMethod.POST, "/condos/{condoId}/condoBills").access(CONDO_MANAGER)
				.antMatchers(HttpMethod.POST, "/condos/{condoId}/outlays").access(CONDO_MANAGER)
				.antMatchers("/condos/{condoId}").access(CONDO)
				.antMatchers("/condos/{condoId}/**").access(CONDO)
				.antMatchers(HttpMethod.DELETE, "/outlays/{outlayId}").access(OUTLAY_MANAGER)
				.antMatchers("/outlays/{outlayId}").access(OUTLAY)
				.antMatchers(HttpMethod.DELETE, "/condoBills/{billId}").access(BILL_MANAGER)
				.antMatchers(HttpMethod.PATCH, "/condoBills/{billId}/payment").access(BILL_MANAGER)
				.antMatchers("/condoBills/{billId}").access(BILL)
				.anyRequest().authenticated()
				.and()
			.cors()
			.and()
			.csrf().disable();
	}

	@Bean
	public AuthenticationEntryPoint getBasicAuthenticationEntryPoint() {
		return new Http401AuthenticationEntryPoint(null);
	}

	@Autowired
	public void configureGlobalSecurity(AuthenticationManagerBuilder auth, AuthenticationProvider provider)
			throws Exception {
		auth.authenticationProvider(provider);
	}

	@Bean
	public AuthenticationProvider authenticationProvider(UserRepository repository) {
		return new AppAuthenticationProvider(repository);
	}    
    
    @Bean 
    public RoleHierarchy roleHierarchy() {
    	RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
    	roleHierarchy.setHierarchy("MANAGER > RESIDENT");
    	return roleHierarchy;
    }
    
    @Bean
    public RoleVoter roleVoter() {
    	return new RoleHierarchyVoter(roleHierarchy());
    }
    
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
	//TODO: configure cors authorized urls or remove it on production.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public WebSecurity webSecurity(CondoRepository condoRepo, OutlayRepository outlayRepo, BillRepository billRepo) {
    	return new WebSecurity(condoRepo, outlayRepo, billRepo);
    }
}