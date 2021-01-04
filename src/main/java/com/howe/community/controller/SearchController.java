package com.howe.community.controller;

import com.howe.community.pojo.DiscussPost;
import com.howe.community.pojo.Page;
import com.howe.community.service.ElasticsearchService;
import com.howe.community.service.LikeService;
import com.howe.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    // search?keyWord=xxx
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyWord, Page page, Model model){
        // 搜索帖子
        List<DiscussPost> lists = elasticsearchService.searchDiscussPost(keyWord, page.getCurrent() - 1, page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (lists != null){
            for (DiscussPost list : lists) {
                Map<String, Object> map = new HashMap<>();
                // 帖子
                map.put("post",list);
                // 作者
                map.put("user", userService.findUserById(list.getUserId()));
                // 点赞数量
                map.put("likeCount", likeService.findEntityLikeCount(1, list.getId()));
                discussPosts.add(map);
            }
            model.addAttribute("discussPosts",discussPosts);
        }
        model.addAttribute("keyWord", keyWord);
        // 设置分页信息，实现分页
        page.setPath("/search?keyWord=" + keyWord);
        page.setRows(lists == null ? 0 : lists.size());

        return "site/search";
    }

}
