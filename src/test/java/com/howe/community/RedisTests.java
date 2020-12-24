package com.howe.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {

    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate template;

    @Test
    public void testStrings(){
        String redisKey = "test:count";
        template.opsForValue().set(redisKey, 1);
        System.out.println(template.opsForValue().get(redisKey));
        System.out.println(template.opsForValue().increment(redisKey));
        System.out.println(template.opsForValue().decrement(redisKey));
    }

    @Test
    public void testBoundOperations(){
        String redisKey = "test:count";
        BoundValueOperations operations = template.boundValueOps(redisKey);
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }

}
