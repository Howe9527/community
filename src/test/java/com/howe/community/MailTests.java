package com.howe.community;

import com.howe.community.service.IMailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    @Autowired
    private IMailService mailService;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void sendMailTest(){
        mailService.sendSimpleMail("HoweXiao@163.com","test","内容：第一封邮件");
    }

    @Test
    public void htmlMailTest(){
        Context context = new Context();
        context.setVariable("username","sunday");

        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        mailService.sendHtmlMail("HoweXiao@163.com","HTML",content);
    }

}
