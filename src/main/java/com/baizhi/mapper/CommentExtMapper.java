package com.baizhi.mapper;

import com.baizhi.model.Comment;
import com.baizhi.model.CommentExample;
import com.baizhi.model.Question;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface CommentExtMapper {
    int inCommentCount(Comment comment);
}