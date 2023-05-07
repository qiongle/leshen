package com.baizhi.controller;

import com.baizhi.cache.TagCache;
import com.baizhi.dto.QuestionDTO;

import com.baizhi.model.Question;
import com.baizhi.model.User;
import com.baizhi.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {
    @Autowired
    private QuestionService questionService;

    @GetMapping("/publish/{id}")
    public String edit(@PathVariable(name = "id") Long id,
                       Model model) {
        QuestionDTO question = questionService.getById(id);
        model.addAttribute("title", question.getTitle());
        model.addAttribute("description", question.getDescription());
        model.addAttribute("tag", question.getTag());
        model.addAttribute("id", question.getId());

        model.addAttribute("tags", TagCache.get());//将标签包裹传递给前端
        return "publish";
    }

    @GetMapping("/publish")
    public String publish(Model model) {
        model.addAttribute("tags", TagCache.get());//将标签包裹传递给前端
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(
            @RequestParam(value = "title", required = false) String title,//从前端返回参数title等等
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "id", required = false) Long id,
            HttpServletRequest request,//请求
            Model model//存储
    ) {
        model.addAttribute("title", title);//--是不是可以不用这一句 先放到model中 为了方便以后往前端传数据
        model.addAttribute("description", description);
        model.addAttribute("tag", tag);
        model.addAttribute("tags", TagCache.get());//将标签包裹传递给前端
        if (title == null || title == "") {
            model.addAttribute("error", "标题不能为空");//如果出现空给前端页面传值
            return "publish";
        }
        if (description == null || description == "") {
            model.addAttribute("error", "标题不能为空");//给前端页面传值
            return "publish";
        }
        if (tag == null || tag == "") {
            model.addAttribute("error", "标题不能为空");//给前端页面传值
            return "publish";
        }


        String invalid = TagCache.filterInvalid(tag);
        if (StringUtils.isNotBlank(invalid)) {
            model.addAttribute("error", "输入非法标签" + invalid);
            return "publish";
        }
        User user = (User) request.getSession().getAttribute("user");//从前端获取User对象信息
        if (user == null) {
            model.addAttribute("error", "用户未登录");//给前端页面传值
            return "publish";
        }
        Question question = new Question();
        question.setTitle(title);//将返回的参数传入到title里面
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());//creator其实就是用户的ID账号
        question.setId(id);
        questionService.createOrUpdate(question);
        return "redirect:/";
    }
}
