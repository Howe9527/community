package com.howe.community;

import com.howe.community.dao.DiscussPostMapper;
import com.howe.community.dao.elasticserach.DiscussPostRepository;
import com.howe.community.pojo.DiscussPost;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.naming.directory.SearchResult;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
@RunWith(SpringRunner.class)
public class ElasticsearchTests {

    @Autowired
    private DiscussPostMapper mapper;

    @Autowired
    private DiscussPostRepository repository;

    @Autowired
    private ElasticsearchRestTemplate template;

    @Test
    public void testInsert(){
        repository.save(mapper.selectDiscussPostById(280));
        repository.save(mapper.selectDiscussPostById(281));
        repository.save(mapper.selectDiscussPostById(276));
    }

    @Test
    public void testInsertList(){
        repository.saveAll(mapper.selectDiscussPosts(101,0,20));
        repository.saveAll(mapper.selectDiscussPosts(102,0,20));
        repository.saveAll(mapper.selectDiscussPosts(103,0,20));
        repository.saveAll(mapper.selectDiscussPosts(111,0,20));
        repository.saveAll(mapper.selectDiscussPosts(112,0,20));
        repository.saveAll(mapper.selectDiscussPosts(131,0,20));
        repository.saveAll(mapper.selectDiscussPosts(132,0,20));
        repository.saveAll(mapper.selectDiscussPosts(133,0,20));
        repository.saveAll(mapper.selectDiscussPosts(134,0,20));
    }

    @Test
    public void testUpdate(){
        DiscussPost post = mapper.selectDiscussPostById(248);
        post.setContent("哈哈哈，我這樣哈哈哈縂行了吧");
        repository.save(post);
    }

    @Test
    public void testSearchByRepository() {
        NativeSearchQuery searchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        Page<DiscussPost> page = repository.search(searchQueryBuilder);
        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for (DiscussPost post : page) {
            System.out.println(post);
        }
    }

    @Test
    public void testElasticsearch01(){
        String keyWord = "求职暖春计划";
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.matchQuery("content",keyWord))
                .must(QueryBuilders.matchQuery("title",keyWord));

        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(0,5))
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

        for (DiscussPost post : list) {
            System.out.println(post);
            System.out.println();
        }
    }

}
