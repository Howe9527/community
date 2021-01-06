package com.howe.community.controller;

import com.howe.community.event.EventProducer;
import com.howe.community.pojo.*;
import com.howe.community.service.CommentService;
import com.howe.community.service.DiscussPostService;
import com.howe.community.service.LikeService;
import com.howe.community.service.UserService;
import com.howe.community.utils.CommunityUtil;
import com.howe.community.utils.HostHolder;
import com.howe.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    private static int ENTITY_TYPE_POST = 1;

    private static int ENTITY_COMMENT = 2;

    /**
     * 新创建的帖子只包含title和content
     * @return
     */
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content){
        User user = hostHolder.getUser();
        if (user == null){
            return CommunityUtil.getJSONString(403,"您还没有登录！");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        // 触发发帖事件
        Event event = new Event()
                .setTopic("public")
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);

        // 计算帖子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, post.getId());

        //报错的清空在将来另外处理
        return CommunityUtil.getJSONString(0,"发布成功");
    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){

        // 查询帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);

        // 作者显示
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        User hostUser = hostHolder.getUser();
        Integer hostUserId = null;
        if (hostUser != null){
            hostUserId = hostUser.getId();
        }

        // 点赞有关信息
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeCount", likeCount);

        int likeStatus = likeService.findEntityLikeStatus(hostUserId, ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeStatus", likeStatus);

        // 帖子的评论评论分页显示
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        List<Comment> commentList = commentService.findCommentByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null){
            for (Comment comment : commentList) {
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment", comment);
                commentVo.put("user", userService.findUserById(comment.getUserId()));

                // 点赞有关信息
                likeCount = likeService.findEntityLikeCount(ENTITY_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);
                likeStatus = likeService.findEntityLikeStatus(hostUserId, ENTITY_COMMENT, comment.getId());
                commentVo.put("likeStatus", likeStatus);

                // 对评论的回复
                List<Comment> replyList = commentService.findCommentByEntity(ENTITY_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                // 回复VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null){
                    for (Comment reply : replyList){
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply", reply);
                        // 作者
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        // 回复的目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        // 点赞有关信息
                        likeCount = likeService.findEntityLikeCount(ENTITY_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);
                        likeStatus = likeService.findEntityLikeStatus(hostUserId, ENTITY_COMMENT, reply.getId());
                        replyVo.put("likeStatus", likeStatus);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replies", replyVoList);
                // 回复的数量
                int replyCount = commentService.findCountByEntity(ENTITY_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }

    /**
     * 置顶帖子
     * 权限： 版主
     */
    @RequestMapping(path = "/top", method = RequestMethod.POST)
    @ResponseBody
    public String setTop(int postId){
        discussPostService.updateType(postId, 1);

        // 触发发帖事件,将帖子的修改过后的状态发布到ES中
        Event event = new Event()
                .setTopic("public")
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(postId);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }

    /**
     * 加精帖子
     * 权限： 版主
     */
    @RequestMapping(path = "/wonderful", method = RequestMethod.POST)
    @ResponseBody
    public String setWonderful(int postId){
        discussPostService.updateStatus(postId, 1);

        // 触发发帖事件,将帖子的修改过后的状态发布到ES中
        Event event = new Event()
                .setTopic("public")
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(postId);
        eventProducer.fireEvent(event);

        // 计算加精的分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, postId);

        return CommunityUtil.getJSONString(0);
    }

    /**
     * 删除帖子
     * 权限： 管理员
     */
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String setDelete(int postId){
        discussPostService.updateStatus(postId, 2);

        // 将帖子的修改过后的状态发布到ES中,触发删除帖子的事件，将帖子从es中删除
        Event event = new Event()
                .setTopic("delete")
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(postId);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }

}
