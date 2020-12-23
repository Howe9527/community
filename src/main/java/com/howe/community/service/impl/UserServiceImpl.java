package com.howe.community.service.impl;

import com.howe.community.dao.LoginTicketMapper;
import com.howe.community.dao.UserMapper;
import com.howe.community.pojo.LoginTicket;
import com.howe.community.pojo.User;
import com.howe.community.service.IMailService;
import com.howe.community.service.UserService;
import com.howe.community.utils.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    /**
     * 激活成功
     */
    private final static int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    private final static int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    private final static int ACTIVATION_FAILURE = 2;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private IMailService mailService;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    @Override
    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    @Override
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        // 首先对空值进行判断处理
        if (user == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }

        // 验证账号
        User selectedUser = userMapper.selectByName(user.getUsername());
        if (selectedUser != null){
            map.put("usernameMsg","账号已存在");
            return map;
        }

        //验证邮箱
        selectedUser = userMapper.selectByEmail(user.getEmail());
        if (selectedUser != null){
            map.put("emailMsg", "该邮箱已被注册");
            return map;
        }

        // 注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:9090/community/activation/101/code 动态拼接url
        String url = domain + contextPath + "/activation/" + user.getId() + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailService.sendHtmlMail(user.getEmail(), "账号激活", content);

        return map;
    }

    /**
     * 判断激活的状态，以此来进行操作
     * @return 激活操作后的相关码
     */
    @Override
    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    @Override
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(username)){
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        //验证账号
        User user = userMapper.selectByName(username);
        if (user == null){
            map.put("usernameMsg", "该账号不存在");
            return map;
        }

        //判断账号是否激活
        if (user.getStatus() == 0){
            map.put("usernameMsg", "该账号未激活");
            return map;
        }

        //验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        assert password != null;
        if (!password.equals(user.getPassword())){
            map.put("passwordMsg", "账号密码错误");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket", loginTicket.getTicket());

        return map;
    }

    @Override
    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket, 1);
    }

    @Override
    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    @Override
    public int updatePassword(int userId, String password) {
        User user = userMapper.selectById(userId);
        password = CommunityUtil.md5(password + user.getSalt());

        return userMapper.updatePassword(userId, password);
    }

    @Override
    public int updateHeader(int userId, String headerUrl) {
        return userMapper.updateHeader(userId, headerUrl);
    }
}
