package com.howe.community.service;

import java.util.List;
import java.util.Map;

public interface FollowService {

    void follow(int userId, int entityType, int entityId);

    void unFollow(int userId, int entityType, int entityId);

    // 查询关注的人的数量
    long findFolloweeCount(int userId, int entityType);

    // 查询粉丝数量
    long findFollowerCount(int entityType, int entityId);

    // 查询当前用户是否已关注该实体
    boolean hasFollowed(int userId, int entityType, int entityId);

    // 查询某个用户关注的人
    List<Map<String, Object>> findFollowees(int userId, int offset, int limit);

    //查询某用户的分析
    List<Map<String, Object>> findFollowers(int userId, int offset, int limit);
}
