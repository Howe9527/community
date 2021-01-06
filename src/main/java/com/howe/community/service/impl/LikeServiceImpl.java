package com.howe.community.service.impl;

import com.howe.community.service.LikeService;
import com.howe.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    // 点赞操作
    @Override
    public void like(int userId, int entityType, int entityId, int entityUserId) {
        /*String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        if (isMember){
            redisTemplate.opsForSet().remove(entityLikeKey, userId);
        } else {
            redisTemplate.opsForSet().add(entityLikeKey, userId);
        }*/
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);

                boolean isMember = redisOperations.opsForSet().isMember(entityLikeKey, userId);

                redisOperations.multi();
                if (isMember){
                    redisOperations.opsForSet().remove(entityLikeKey,userId);
                    redisOperations.opsForValue().decrement(userLikeKey);
                } else {
                    redisOperations.opsForSet().add(entityLikeKey,userId);
                    redisOperations.opsForValue().increment(userLikeKey);
                }

                return redisOperations.exec();
            }
        });
    }

    // 查询用户查看自己的帖子收到的赞
    public int findUserLikeCount(int userId){

        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer)redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count;
    }

    // 查询实体点赞数量
    public long findEntityLikeCount(int entityType, int entityId){

        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // 查询某人对实体（帖子或评论）点赞状态
    public int findEntityLikeStatus(Integer userId, int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        if (userId == null){
            return 0;
        }
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }
}
