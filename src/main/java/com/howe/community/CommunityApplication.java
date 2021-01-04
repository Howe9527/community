package com.howe.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
@MapperScan("com.howe.community.dao")
public class CommunityApplication {

    @PostConstruct
    public void init() {
        // 解決netty啓動衝突問題
        // Netty4Utils.setAvailableProcessors()中出現的衝突錯誤
        System.setProperty("es.set.netty.runtime.available.processors","false");
    }

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}
