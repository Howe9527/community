package com.howe.community.service.impl;

import com.howe.community.dao.DiscussPostMapper;
import com.howe.community.pojo.DiscussPost;
import com.howe.community.service.DiscussPostService;
import com.howe.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Override
    public List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }

    @Override
    public int selectDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    @Override
    public int addDiscussPost(DiscussPost discussPost) {
        if (discussPost == null){
            throw new IllegalArgumentException("参数不能为空");
        }

        //title跟content中的类容需要进行过滤
        // 同时需要防止XSS攻击。处理html中的标签
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle())); //转义html标签
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        //过滤敏感词
        discussPost.setTitle(sensitiveFilter.sensitiveFilter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.sensitiveFilter(discussPost.getContent()));

        return discussPostMapper.insertDiscussPost(discussPost);
    }

    @Override
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    @Override
    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    @Override
    public int updateType(int id, int type) {
        return discussPostMapper.updateType(id, type);
    }

    @Override
    public int updateStatus(int id, int status) {
        return discussPostMapper.updateStatus(id, status);
    }

    @Override
    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id, score);
    }
}
