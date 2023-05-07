package com.baizhi.mapper;

import com.baizhi.dto.QuestionQueryDTO;
import com.baizhi.model.Question;
import com.baizhi.model.QuestionExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

@Mapper
public interface QuestionExtMapper {
    int incView(Question row);//阅读数

    int inCommentCount(Question row);//评论数,每增加一个就自增一下，不会因为多人打开评论导致最后只加一个评论

    List<Question> selectRelated(Question question);//查询类似的标签

    Integer countBySearch(QuestionQueryDTO questionQueryDTO);//查找的问题数

    List<Question> selectBySearch(QuestionQueryDTO questionQueryDTO);//查找问题
}