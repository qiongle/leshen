package com.baizhi.dto;

import com.baizhi.model.User;
import lombok.Data;
import org.apache.ibatis.annotations.Options;

import java.util.List;

@Data
public class QuestionDTO<T> { //因为question中获取不到用户的头像，question中的creator与User中的account_id一样，所以把这两张表做一个关联，同时把
    //question中的全部属性和User中的头像路径获取到
    private Long id;
    private String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;//时间戳，格林威治时间
    private Long creator;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private User user;
}
