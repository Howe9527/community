package com.howe.community.service.impl;

import com.howe.community.service.DateCountService;
import com.howe.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DateCountServiceImpl implements DateCountService {

    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void recordUV(String ip) {
        String redisKey = RedisKeyUtil.getUVKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey, ip);
    }

    @Override
    public long calculateUV(Date start, Date end) {
        if (start == null || end == null){
            throw new IllegalArgumentException("参数不能为空");
        }

        // 整理在日期范围内的key
        List<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)){
            String uvKey = RedisKeyUtil.getUVKey(df.format(calendar.getTime()));
            keyList.add(uvKey);
            calendar.add(Calendar.DATE, 1);
        }

        // 合并这些数据
        String redisKey = RedisKeyUtil.getUVKey(df.format(start), df.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey, keyList.toArray());

        return redisTemplate.opsForHyperLogLog().size(redisKey);

    }

    @Override
    public void recordDAU(int userId) {
        String redisKey = RedisKeyUtil.getDAUKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey, userId, true);
    }

    @Override
    public long calculateDAU(Date start, Date end) {
        if (start == null || end == null){
            throw new IllegalArgumentException("参数不能为空");
        }

        // 整理在日期范围内的key
        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)){
            String uvKey = RedisKeyUtil.getDAUKey(df.format(calendar.getTime()));
            keyList.add(uvKey.getBytes());
            calendar.add(Calendar.DATE, 1);
        }

        // 进行OR运算
        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                String redisKey = RedisKeyUtil.getDAUKey(df.format(start), df.format(end));
                redisConnection.bitOp(RedisStringCommands.BitOperation.OR,redisKey.getBytes(),
                        keyList.toArray(new byte[0][0]));
                return redisConnection.bitCount(redisKey.getBytes());
            }
        });
    }
}
