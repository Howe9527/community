package com.howe.community.service;

import com.howe.community.pojo.DiscussPost;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ElasticsearchService {

    // 向elasticsearch中存值
    void saveDiscussPost(DiscussPost post);

    // 删除elasticsearch中的值
    void deleteDiscussPost(int id);

    // 搜索方法，实现对期望内容的搜索
    List<DiscussPost> searchDiscussPost(String keyWord, Integer currentPage, Integer limit);

}
