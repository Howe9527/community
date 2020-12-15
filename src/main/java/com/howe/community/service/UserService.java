package com.howe.community.service;

import com.howe.community.pojo.User;

import java.util.Map;

public interface UserService {

    //查询用户接口
    User findUserById(int id);

    //注册接口
    Map<String, Object> register(User user);

    //激活邮件激活
    int activation(int userId, String code);

    //登录接口
    Map<String, Object> login(String username, String password, int expiredSeconds);

    //退出登录，更改登录凭证的状态
    void logout(String ticket);

}
