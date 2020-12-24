package com.howe.community.utils;

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";   //B是A的Followee
    private static final String PREFIX_FOLLOWER = "follower"; // A关注B，A是B的follower
    // redis中的验证码
    private static final String PREFIX_KAPTCHA = "kaptcha";
    // redis中生成登录凭证
    private static final String PREFIX_TICKET = "ticket";
    // redis中生成用户信息的缓存
    private static final String PREFIX_USER = "user";

    // 生成某一个实体的赞
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    // 某个用户的赞
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    // 某个用户关注的人
    // followee:userId:entityType -> zset(entityId, nowTime) 有序集合
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    // 某个用户的粉丝
    // follower:entityType:entityId -> zset(userId, nowTime)
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    // 在reids中拼验证码的key
    //在不知道用户id的情况下给用户发一个cookie存储用户信息
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    // 返回登录凭证
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET + SPLIT + ticket;
    }

    public static String getUserKey(int userId){
        return PREFIX_USER + SPLIT + userId;
    }
}