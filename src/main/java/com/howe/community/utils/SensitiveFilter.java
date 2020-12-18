package com.howe.community.utils;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 实现敏感词的过滤功能
 *  1、定义前缀树
 *  2、根据敏感词初始化前缀树
 *  3、编写过滤敏感词的方法
 */
@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换字符
    private static final String REPLACEMENT = "***";

    // 初始化根节点
    private TrieNode root = new TrieNode();

    /**
     * 初始化前缀树
     */
    @PostConstruct
    public void init(){
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ){
            String keyWord;
            while ((keyWord = reader.readLine()) != null){
                //将获得的敏感词添加到前缀树中
                this.addKeyWord(keyWord);
            }
        } catch (IOException e){
            logger.error("加载敏感词信息文件失败" +  e.getMessage());
        }
    }

    /**
     * 编写过滤敏感词的方法
     * @param text 需要过滤的文本
     * @return 过滤后的文本
     */
    public String sensitiveFilter(String text) {
        if (StringUtils.isBlank(text)){
            return null;
        }

        //指针1
        TrieNode tempNode = root;
        //指针2
        int head = 0;
        //指针3
        int pos = 0;
        // 结果集字符串
        StringBuilder sb = new StringBuilder();

        while (pos < text.length()){
            char c = text.charAt(pos);

            // 跳过符号
            if (isSymbol(c)){
                // 如果指针1指向跟节点
                if (tempNode == root){
                    sb.append(c);
                    head++;
                }
                // 无论在指针1在开头或中间，指针3都要走一位
                pos++;
                continue;
            }
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null){
                // 以head开头的字符串不是敏感词
                sb.append(text.charAt(head));
                pos = ++head;
                //重新指跟节点
                tempNode = root;

            } else if (tempNode.isKeyWordEnd()){
                //发现敏感词
                sb.append(REPLACEMENT);
                head = ++pos;
                tempNode = root;
            } else {
                pos++;
            }
        }
        sb.append(text.substring(head));
        return sb.toString();
    }

    // 判断是否为符号
    public boolean isSymbol(Character c){

        // 东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80  || c > 0x9FFF);
    }

    // 将一个敏感词添加到前缀树当中
    private void addKeyWord(String keyWord) {

        TrieNode tempNode = root;
        for (int i = 0; i < keyWord.length(); i++){
            char c = keyWord.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode == null){
                //初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }

            // 指针指向子节点进行下一轮循环
            tempNode = subNode;

            // 设置结束标识，
            if (i == keyWord.length() - 1){
                tempNode.setKeyWordEnd(true);
            }
        }

    }

    /**
        定义前缀树
     */
    private static class TrieNode{

        //关键词结束标识
        private boolean isKeyWordEnd = false;

        // 当前节点的子节点(key是下级字符，value是下级节点)
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        //添加子节点
        public void addSubNode(Character c, TrieNode node){
            subNodes.put(c, node);
        }

        //获取字节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }

}
