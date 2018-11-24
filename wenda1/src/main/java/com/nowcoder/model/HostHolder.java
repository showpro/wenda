package com.nowcoder.model;

import org.springframework.stereotype.Component;

/**
 * Created by zhan on 2018/7/30.
 */
@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<User>();

    public User getUser() {
        return users.get();//ThreadLocal的get方法
    }

    public void setUser(User user) {
        users.set(user);
    }

    public void clear() {
        users.remove();;
    }
}
