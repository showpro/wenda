package com.nowcoder.service;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 关注 服务
 * 所有关注放在redis中
 * Created by zhan on 18/8/31.
 */
@Service
public class FollowService {
    @Autowired
    JedisAdapter jedisAdapter;

    /**
     * 用户关注了某个实体,可以关注问题,关注用户,关注评论等任何实体
     * @param userId  用户id
     * @param entityType 关注实体类型
     * @param entityId
     * @return
     */
    public boolean follow(int userId, int entityType, int entityId) {
        //粉丝key
    	String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        //关注对象的key
    	String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        //关注时间
        Date date = new Date();
        // 获取一个jedis
        Jedis jedis = jedisAdapter.getJedis();
        //开启事物
        Transaction tx = jedisAdapter.multi(jedis);
        //添加粉丝（添加关注用户到实体列表中）
        tx.zadd(followerKey, date.getTime(), String.valueOf(userId));
        // 当前用户对这类实体关注+1（添加关注对象）
        tx.zadd(followeeKey, date.getTime(), String.valueOf(entityId));
        //提交事务
        List<Object> ret = jedisAdapter.exec(tx, jedis);
        //是否成功
        return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0;
    }

    /**
     * 取消关注
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean unfollow(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        // 实体的粉丝-1
        tx.zrem(followerKey, String.valueOf(userId));
        // 关注列表中，当前用户对这类实体关注-1
        tx.zrem(followeeKey, String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx, jedis);
        //两个操作成功，返回行数2
        return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0;
    }

    /**
     * 获取所有关注者（每一个实体都有一个关注者）
     * @param entityType
     * @param entityId
     * @param count
     * @return
     */
    
    //将set结合转换为list集合
    private List<Integer> getIdsFromSet(Set<String> idset) {
        List<Integer> ids = new ArrayList<>();
        for (String str : idset) {
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }
    
    public List<Integer> getFollowers(int entityType, int entityId, int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey, 0, count));
    }

    public List<Integer> getFollowers(int entityType, int entityId, int offset, int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey, offset, offset+count));
    }


    /**
     * 获取所有关注对象
     * @param userId
     * @param entityType
     * @param count
     * @return
     */
    public List<Integer> getFollowees(int userId, int entityType, int count) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, 0, count));
    }

    public List<Integer> getFollowees(int userId, int entityType, int offset, int count) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, offset, offset+count));
    }

    /**
     * 某个实体有多少关注者（粉丝）
     * @param entityType
     * @param entityId
     * @return
     */
    public long getFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zcard(followerKey);
    }

    /**
     * 某个粉丝关注了多少个实体（关注对象）
     * @param userId
     * @param entityType
     * @return
     */
    public long getFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return jedisAdapter.zcard(followeeKey);
    }



    /**
     * 	是否相互关注
     *  判断用户是否关注了某个实体（是否是某个实体的粉丝）
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean isFollower(int userId, int entityType, int entityId) {
    	//实体中是否有粉丝的key
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        //返回分值，这里所有分值是根据时间而来的
        return jedisAdapter.zscore(followerKey, String.valueOf(userId)) != null;
    }
}
