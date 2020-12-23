package com.howe.community;

import com.howe.community.dao.DiscussPostMapper;
import com.howe.community.dao.LoginTicketMapper;
import com.howe.community.dao.MessageMapper;
import com.howe.community.dao.UserMapper;
import com.howe.community.pojo.DiscussPost;
import com.howe.community.pojo.LoginTicket;
import com.howe.community.pojo.Message;
import com.howe.community.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);

    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("11178");
        user.setSalt("qwezxv");
        user.setEmail("test@163.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdateUser(){
        int rows = userMapper.updateStatus(150,1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150,"http://www.nowcoder.com/101.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150,"heiheihei");
        System.out.println(rows);
    }

    @Test
    public void testSelectDiscussPost(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(149,0,10);
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }

        int i = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(i);
    }

    @Autowired
    LoginTicketMapper loginTicketMapper;

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("acb");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
        int i = loginTicketMapper.insertLoginTicket(loginTicket);
        System.out.println(i);
    }

    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("acb");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("acb", 1);
        System.out.println(loginTicket);
    }

    @Test
    public void testSelectLetters(){
        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
        for (Message message : messages) {
            System.out.println(message);
        }

        System.out.println("-------------------");
        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);

        System.out.println("-----------------------");
        messages = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message : messages) {
            System.out.println(message);
        }

        System.out.println("----------------------");
        int letterCount = messageMapper.selectLetterCount("111_112");
        System.out.println(letterCount);

        System.out.println("----------------------");
        count = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(count);
    }
}
