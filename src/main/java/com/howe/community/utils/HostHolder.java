package com.howe.community.utils;

import com.howe.community.pojo.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息并持续保存，代替session对象。
 * 通过线程池实现
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users= new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }

}
