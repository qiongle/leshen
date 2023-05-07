package com.baizhi.dto;

import lombok.Data;

import java.util.List;

@Data
public class TagDTO {
    private String categoryName;//分类名称
    private List<String> tags;
}
