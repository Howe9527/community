package com.howe.community.service;

import com.howe.community.pojo.LoginTicket;
import com.howe.community.pojo.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

public interface UserService {

    //查询用户接口
    User findUserById(int id);

    User findUserByName(String username);

    //注册接口
    Map<String, Object> register(User user);

    //激活邮件激活
    int activation(int userId, String code);

    //登录接口
    Map<String, Object> login(String username, String password, int expiredSeconds);

    //退出登录，更改登录凭证的状态
    void logout(String ticket);

    LoginTicket findLoginTicket(String ticket);

    int updatePassword(int userId, String password);

    int updateHeader(int userId, String headerUrl);

    /**
     * 用户权限方法实现
     */
    Collection<? extends GrantedAuthority> getAuthorities(int userId);

}
