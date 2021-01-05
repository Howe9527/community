package com.howe.community.config;

import com.howe.community.controller.interceptor.LoginRequiredInterceptor;
import com.howe.community.controller.interceptor.LoginTicketInterceptor;
import com.howe.community.controller.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截所有对静态资源的请求
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private  LoginTicketInterceptor loginTicketInterceptor;

    /*@Autowired
    private  LoginRequiredInterceptor loginRequiredInterceptor;*/

    @Autowired
    private  MessageInterceptor messageInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/css/*.css", "/js/*.js", "/img/*.png", "/img/*.jpg", "/img/*.jpeg");

        /*registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/css/*.css", "/js/*.js", "/img/*.png", "/img/*.jpg", "/img/*.jpeg");*/

        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/css/*.css", "/js/*.js", "/img/*.png", "/img/*.jpg", "/img/*.jpeg");
    }
}
