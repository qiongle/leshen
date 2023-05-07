package com.baizhi.enums;

public enum NotificationStatusEnum {//定义了通知的枚举
    UNREAD(0), READ(1);
    private int status;

    public int getStatus() {
        return status;
    }

    NotificationStatusEnum(int status) {
        this.status = status;
    }
}
