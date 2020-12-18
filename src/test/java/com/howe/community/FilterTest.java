package com.howe.community;

import com.howe.community.utils.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class FilterTest {

    @Autowired
    SensitiveFilter filter;

    @Test
    public void testFilter(){
        String text = "这里可以<>赌<//>博b可以asd,嫖>娼可以<>吸//毒，可以开黑";
        text = filter.sensitiveFilter(text);
        System.out.println(text);
    }

}
