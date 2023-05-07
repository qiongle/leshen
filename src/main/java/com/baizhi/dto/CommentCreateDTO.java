package com.baizhi.dto;

import lombok.Data;

@Data
public class CommentCreateDTO {//评论返回的类型
    private Long parentId;
    private Integer type;
    private String content;
}
