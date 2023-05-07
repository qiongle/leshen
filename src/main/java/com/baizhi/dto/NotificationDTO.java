package com.baizhi.dto;

import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private Long gmtCreate;
    private Integer status;
    private Long notifier;//评论人的ID
    private String notifierName;//回复人的名称
    private String outerTitle;//回复的标题
    private Long outerid;
    private String typeName;
    private Integer type;//回复类型
}
