package com.howe.community.controller.interceptor;

import com.howe.community.pojo.User;
import com.howe.community.service.DateCountService;
import com.howe.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class DataCountInterceptor implements HandlerInterceptor {

    @Autowired
    private DateCountService dateCountService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 统计UV
        String ip = request.getRemoteHost();
        dateCountService.recordUV(ip);

        // 统计DAU
        User user = hostHolder.getUser();
        if (user != null){
            dateCountService.recordDAU(user.getId());
        }

        return true;
    }
}
