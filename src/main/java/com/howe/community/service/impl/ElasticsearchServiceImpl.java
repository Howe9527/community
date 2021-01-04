package com.howe.community.service.impl;

import com.howe.community.dao.elasticserach.DiscussPostRepository;
import com.howe.community.pojo.DiscussPost;
import com.howe.community.service.ElasticsearchService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchRestTemplate template;

    @Override
    public void saveDiscussPost(DiscussPost post) {
        discussPostRepository.save(post);
    }

    @Override
    public void deleteDiscussPost(int id) {
        discussPostRepository.deleteById(id);
    }


    @Override
    public List<DiscussPost> searchDiscussPost(String keyWord, Integer currentPage, Integer limit) {
        /*if (currentPage == null || currentPage == 0){
            currentPage = 0;
        }
        if (limit == null){
            limit = 10;
        }*/
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.matchQuery("content",keyWord))
                .must(QueryBuilders.matchQuery("title",keyWord));

        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(currentPage,limit))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                );
        SearchHits<DiscussPost> query = template.search(builder.build(),DiscussPost.class);
        List<DiscussPost> list = new ArrayList<>();
        for (SearchHit<DiscussPost> hit : query) {
            DiscussPost post = new DiscussPost();

            post.setId(hit.getContent().getId());
            post.setUserId(hit.getContent().getUserId());
            post.setTitle(hit.getContent().getTitle());
            post.setContent(hit.getContent().getContent());
            post.setType(hit.getContent().getType());
            post.setStatus(hit.getContent().getStatus());
            post.setCreateTime(hit.getContent().getCreateTime());
            post.setCommentCount(hit.getContent().getCommentCount());
            post.setScore(hit.getContent().getScore());
            if (hit.getHighlightField("title").get(0) != null){
                post.setTitle(hit.getHighlightField("title").get(0));
            }
            if (hit.getHighlightField("content").get(0) != null){
                post.setContent(hit.getHighlightField("content").get(0));
            }
            list.add(post);
        }
        return list;

    }
}
