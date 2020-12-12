package com.howe.community.service;

import com.howe.community.pojo.DiscussPost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DiscussPostService {

    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit); //借助动态sql

    //@Param注解用于给参数取别名
    //如果只有一个参数，并且在<if>里面使用，则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);//借助动态sql

}
