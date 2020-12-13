package com.howe.community;

import com.howe.community.service.IMailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    @Autowired
    private IMailService mailService;

    @Test
    public void sendMailTest(){
        mailService.sendSimpleMail("HoweXiao@163.com","test","内容：第一封邮件");
    }

}
