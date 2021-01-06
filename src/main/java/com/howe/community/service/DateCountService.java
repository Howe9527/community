package com.howe.community.service;

import java.util.Date;

public interface DateCountService {

    // 将指定IP计入UV
    void recordUV(String ip);

    // 统计指定日期范围的UV
    long calculateUV(Date start, Date end);

    // 将指定用户计入到DAU
    void recordDAU(int userId);

    // 统计指定日期范围内的DAU
    long calculateDAU(Date start, Date end);

}
