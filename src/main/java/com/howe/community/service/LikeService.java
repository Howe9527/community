package com.howe.community.service;

public interface LikeService {

    // 点赞业务方法实现
    void like(int userId, int entityType, int entityId, int entityUserId);

    // 查询实体点赞数量
    long findEntityLikeCount(int entityType, int entityId);

    // 查询某人对实体（帖子或评论）点赞状态
    int findEntityLikeStatus(int userId, int entityType, int entityId);

    int findUserLikeCount(int userId);


}
