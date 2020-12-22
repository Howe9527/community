package com.howe.community.service;

import com.howe.community.pojo.Comment;

import java.util.List;

public interface CommentService {

    List<Comment> findCommentByEntity(int entityType, int entityId, int offset, int limit);

    int findCountByEntity(int entityType, int entityId);

    // 增加评论的业务
    public int addComment(Comment comment);

}
