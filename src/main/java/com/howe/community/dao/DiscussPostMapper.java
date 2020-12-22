package com.howe.community.dao;

import com.howe.community.pojo.DiscussPost;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit); //借助动态sql

    //@Param注解用于给参数取别名
    //如果只有一个参数，并且在<if>里面使用，则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);//借助动态sql

    //发布新的帖子插入到数据库中
    int insertDiscussPost(DiscussPost discussPost);

    //帖子详情查询
    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id, int commentCount);

}
