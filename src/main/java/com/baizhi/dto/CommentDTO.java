package com.baizhi.dto;


import com.baizhi.model.User;
import lombok.Data;

@Data
public class CommentDTO {
    private Long id;
    private Long parentId;
    private Integer type;
    private Long commentator;
    private Long gmtCreate;
    private Long gmtModified;
    private Integer commentCount;
    private Long likeCount;
    private String content;
    private User user;
}
