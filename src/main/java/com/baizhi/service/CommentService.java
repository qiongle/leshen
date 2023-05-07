package com.baizhi.service;


import com.baizhi.dto.CommentDTO;
import com.baizhi.enums.CommentTypeEnum;
import com.baizhi.enums.NotificationStatusEnum;
import com.baizhi.enums.NotificationTypeEnum;
import com.baizhi.exception.CustomizeErrorCode;
import com.baizhi.exception.CustomizeException;
import com.baizhi.mapper.*;
import com.baizhi.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    QuestionMapper questionMapper;

    @Autowired
    CommentMapper commentMapper;
    @Autowired
    QuestionExtMapper questionExtMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    CommentExtMapper commentExtMapper;

    @Autowired
    NotificationMapper notificationMapper;

    @Transactional//执行异常事务回滚
    public void insert(Comment comment, User commentator) {
        if (comment.getParentId() == null || comment.getParentId() == 0) {//判断要回复评论正不正确
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {//判断是一级还是二级评论
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }
        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) {
            //回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null) {
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());
            if (question == null) {//如果找不到问题就抛出异常
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);//如果找到父类id的话证明是回复评论，把回复评论的插进去
            //增加父类评论数，因为无论是回复评论还是回复问题最后的评论数都是算父类的评论数
            Comment parentComment = new Comment();
            parentComment.setId(comment.getParentId());
            parentComment.setCommentCount(1);
            commentExtMapper.inCommentCount(parentComment);//增加评论数
            //创建通知
            createNotify(comment, dbComment.getCommentator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_COMMENT, question.getId());
        } else {
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null) {//如果找不到问题就抛出异常
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            question.setCommentCount(0);//将初始值变为0
            commentMapper.insert(comment);//插入问题
            question.setCommentCount(1);//评论数的默认为1
            questionExtMapper.inCommentCount(question);//加上自身的评论数
            //创建通知
            createNotify(comment, question.getCreator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_COMMENT, question.getId());
        }
    }

    private void createNotify(Comment comment, Long receiver, String notifierName, String outerTitle, NotificationTypeEnum notificationType, Long outerId) {//创建通知
//        if (receiver == comment.getCommentator()) {//如果接收人与评论人是同一个那就不用通知自己
//            return;
//        }
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(notificationType.getType());
        notification.setOuterid(outerId);//当前问题的ID
        notification.setNotifier(comment.getCommentator());//通知人的ID
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setReceiver(receiver);//接收人的ID
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);//问题的标题
        notificationMapper.insert(notification);
    }

    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type) {
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType());
        commentExample.setOrderByClause("gmt_create desc");//按时间倒序在列表上呈现
        List<Comment> comments = commentMapper.selectByExample(commentExample);//comment表中找到父类id和都是回复问题的数据
        if (comments.size() == 0) {
            return new ArrayList<>();
        }
        //获取去重的评论人
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());//java8流式编程
        List<Long> userIds = new ArrayList<>();
        userIds.addAll(commentators);//获取到去重的集合

        //获取评论人并转换为Map
        //直接将信息绑定在id后面大大优化了时间复杂度
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);//找到评论的uer
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        // 转换 comment 为 commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));//快速查找id然后知道user
            return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOS;
    }
}
