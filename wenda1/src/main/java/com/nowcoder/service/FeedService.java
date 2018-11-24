package com.nowcoder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nowcoder.dao.FeedDAO;
import com.nowcoder.model.Feed;

import java.util.List;

/**
 * 读取feed新鲜事
 * 
 * Created by zhan on 2018/9/2.
 */
@Service
public class FeedService {
    @Autowired
    FeedDAO feedDAO;

    /**
     * 拉模式：获取所关注人的feed
     * 
     * @param maxId
     * @param userIds
     * @param count
     * @return
     */
    public List<Feed> getUserFeeds(int maxId, List<Integer> userIds, int count) {
        return feedDAO.selectUserFeeds(maxId, userIds, count);
    }

    /**
     * 增加新鲜事feed
     * 
     * @param feed
     * @return
     */
    public boolean addFeed(Feed feed) {
        feedDAO.addFeed(feed);
        return feed.getId() > 0;
    }

    /**
     * 推模式：
     * 
     * @param id
     * @return
     */
    public Feed getById(int id) {
        return feedDAO.getFeedById(id);
    }
}
