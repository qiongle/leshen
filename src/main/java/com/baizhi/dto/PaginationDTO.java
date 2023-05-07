package com.baizhi.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class PaginationDTO<T> {//因为用questionDTO比较难表现page分页的功能，PaginationDTO主要为了展现分页序列和questionDTO的展示
    private List<T> data;//questionDTO容器
    private boolean showPrevious;//是否有向前按钮
    private boolean showFirstPage;//是不是第一页
    private boolean showNext;//是否有向后按钮
    private boolean showEndPage;//最后一页
    private Integer page;//当前页面
    private List<Integer> pages = new ArrayList<>();//分页序列
    private Integer totalPage;//计算一共有多少页

    public void setPagination(Integer totalPage, Integer page) {


        this.totalPage = totalPage;
        this.page = page;
        pages.add(page);
        for (int i = 1; i <= 3; i++) {//最多呈现7页，往中间页的左右进行判断
            if (page - i > 0) {
                pages.add(0, page - i);//向头部插入
            }
            if (page + i <= totalPage) {
                pages.add(page + i);//向尾部插入
            }
        }
        if (page == 1) {//是否展示上一页
            showPrevious = false;
        } else {
            showPrevious = true;
        }
        if (page == totalPage) {//是否展示下一页
            showNext = false;
        } else {
            showNext = true;
        }
        if (pages.contains(1)) {//是否展示第一页
            showFirstPage = false;
        } else {
            showFirstPage = true;
        }
        if (pages.contains(totalPage)) {//是否展示最后一页
            showEndPage = false;
        } else {
            showEndPage = true;
        }
    }
}
