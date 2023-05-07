package com.baizhi.controller;


import com.baizhi.dto.CommentCreateDTO;
import com.baizhi.dto.CommentDTO;
import com.baizhi.dto.ResultDTO;
import com.baizhi.enums.CommentTypeEnum;
import com.baizhi.exception.CustomizeErrorCode;
import com.baizhi.model.Comment;
import com.baizhi.model.User;
import com.baizhi.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CommentController {
    @Autowired
    CommentService commentService;

    @ResponseBody
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");//从sesstion中拿到user对象
        if (user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);//进行异常判断
        }
        if (commentCreateDTO == null || StringUtils.isBlank(commentCreateDTO.getContent())) {
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);//如果要评论的对象为空或者输入的内容不存在
        }


        //前端返回被接受
        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());//将前端这些回复的内容，父类Id,类型（一级评论
        // or二级评论），创建时间拿到
        comment.setContent(commentCreateDTO.getContent());//回复的评论
        comment.setType(commentCreateDTO.getType());//判断是几级回复
        comment.setGmtModified(System.currentTimeMillis());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setCommentator(user.getId());//找到user中的id
        comment.setLikeCount(0L);
        commentService.insert(comment, user);//插入到数据库中
        return ResultDTO.okOf();
    }

    @ResponseBody
    @RequestMapping(value = "/comment/{id}", method = RequestMethod.GET)
    public ResultDTO comments(@PathVariable(name = "id") Long id) {
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.COMMENT);//传进去id,和question类型
        return ResultDTO.okOf(commentDTOS);
    }
}
