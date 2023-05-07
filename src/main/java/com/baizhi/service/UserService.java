package com.baizhi.service;


import com.baizhi.mapper.UserMapper;
import com.baizhi.model.User;
import com.baizhi.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;


    public void createOrUpdate(User user) {
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andAccountIdEqualTo(user.getAccountId());
        List<User> users = userMapper.selectByExample(userExample);
        if (users.size() == 0) { //要是没有证明是新用户，插入时间和use
            //插入
            user.setGmtCreate(System.currentTimeMillis());//如果用户未登录
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        } else {//要是是不是新用户，就更新登录的时间，要是改头像和名字也更新，token在系统中重新生成
            //更新
            User dbUser = users.get(0);
            User updateUser = new User();
            updateUser.setGmtModified(System.currentTimeMillis());
            updateUser.setAvatarUrl(user.getAvatarUrl());
            updateUser.setName(user.getName());
            updateUser.setToken(user.getToken());
            UserExample example = new UserExample();
            example.createCriteria()
                    .andIdEqualTo(dbUser.getId());//通过dbuser中的id进行对比
            userMapper.updateByExampleSelective(updateUser, example);
        }
    }
}
