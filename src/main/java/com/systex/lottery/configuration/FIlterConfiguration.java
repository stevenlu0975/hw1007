package com.systex.lottery.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.systex.lottery.filter.CharacterEncodingFilter;
import com.systex.lottery.filter.ExcludePathFilter;
import com.systex.lottery.filter.LoginAndRegisterFilter;

@Configuration
public class FIlterConfiguration {
	 	@Bean("myCharacterEncodingFilter")
	    public FilterRegistrationBean<CharacterEncodingFilter> characterEncodingFilter() {
	        FilterRegistrationBean<CharacterEncodingFilter> registrationBean = new FilterRegistrationBean<>();
	        registrationBean.setFilter(new CharacterEncodingFilter());
	        registrationBean.addUrlPatterns("/*"); // 這裡設置你的 URL 模式
	        registrationBean.setOrder(1);
	        return registrationBean;
	    }
	 	@Bean
	    public FilterRegistrationBean<ExcludePathFilter> excludePathFilter() {
	        FilterRegistrationBean<ExcludePathFilter> registrationBean = new FilterRegistrationBean<>();
	        registrationBean.setFilter(new ExcludePathFilter());
	        registrationBean.addUrlPatterns("/*"); // 這裡設置你的 URL 模式
	        registrationBean.setOrder(2);
	        return registrationBean;
	    }
	 	@Bean
	    public FilterRegistrationBean<LoginAndRegisterFilter> loginAndRegisterFilter() {
	        FilterRegistrationBean<LoginAndRegisterFilter> registrationBean = new FilterRegistrationBean<>();
	        registrationBean.setFilter(new LoginAndRegisterFilter());
	        registrationBean.addUrlPatterns("/check_user_name","/register","/login"); // 這裡設置你的 URL 模式
	        registrationBean.setOrder(3);
	        return registrationBean;
	    }
}
