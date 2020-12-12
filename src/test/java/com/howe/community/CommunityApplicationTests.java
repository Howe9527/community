package com.howe.community;

import com.howe.community.pojo.DiscussPost;
import com.howe.community.pojo.User;
import com.howe.community.service.DiscussPostService;
import com.howe.community.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class CommunityApplicationTests {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {
    }

    @Test
    public void testHome(){
        List<DiscussPost> list = discussPostService.selectDiscussPosts(0, 0, 10);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null){
            for (DiscussPost post : list){
                Map<String, Object> map = new HashMap<>();
                map.put("post",post);
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);
                discussPosts.add(map);
                System.out.println(discussPosts);
            }
        }
    }

}
