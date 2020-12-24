package com.howe.community.utils;

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";   //B是A的Followee
    private static final String PREFIX_FOLLOWER = "follower"; // A关注B，A是B的follower

    // 生成某一个实体的赞
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    // 某个用户的赞
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    // 某个用户关注的人
    // followee:userId:entityType -> zset(entityId, nowTime) 有序集合
    public static String getFolloweeKey(int userId, int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    // 某个用户的粉丝
    // follower:entityType:entityId -> zset(userId, nowTime)
    public static String getFollowerKey(int entityType, int entityId){
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

}
