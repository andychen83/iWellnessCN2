package com.lefu.es.event;

import com.lefu.es.entity.UserModel;

/**
 * Created by Administrator on 2017/1/19.
 */

public class ReFlushBabyEvent {
    protected UserModel userModel;

    public ReFlushBabyEvent(UserModel user){
        userModel = user;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
}
