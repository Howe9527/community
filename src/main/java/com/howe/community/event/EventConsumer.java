package com.howe.community.event;

import com.alibaba.fastjson.JSONObject;
import com.howe.community.pojo.DiscussPost;
import com.howe.community.pojo.Event;
import com.howe.community.pojo.Message;
import com.howe.community.service.DiscussPostService;
import com.howe.community.service.ElasticsearchService;
import com.howe.community.service.MessageService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private static final String TOPIC_COMMENT = "comment";
    private static final String TOPIC_LIKE = "like";
    private static final String TOPIC_FOLLOW = "follow";

    @Autowired
    private MessageService messageService;

    @Autowired
    private DiscussPostService postService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    // 处理接收到的评论消息
    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record){
        if (record == null || record.value() == null){
            logger.error("消息的内容为空");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null){
            logger.error("消息格式错误");
            return;
        }

        // 发送站内通知
        Message message = new Message();
        // 系统用户的id
        message.setFromId(1);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());
        if (!event.getData().isEmpty()){
            for (Map.Entry<String, Object> entry : event.getData().entrySet()){
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    // 消费发帖事件
    @KafkaListener(topics = "publish")
    public void handlePublishMessage(ConsumerRecord record){
        if (record == null || record.value() == null){
            logger.error("消息的内容为空");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null){
            logger.error("消息格式错误");
            return;
        }

        DiscussPost post = postService.findDiscussPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(post);

    }

}
