package com.nowcoder.dao;

import org.apache.ibatis.annotations.*;

import com.nowcoder.model.Comment;
import com.nowcoder.model.Feed;

import java.util.List;

/**
 * Created by zhan on 2018/9/2.
 */
@Mapper
public interface FeedDAO {
    String TABLE_NAME = " feed ";
    String INSERT_FIELDS = " user_id, data, created_date, type ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    /**
     * 增加新鲜事
     * @param feed
     * @return
     */
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId},#{data},#{createdDate},#{type})"})
    int addFeed(Feed feed);

    /**
     * 推模式
     * @param id
     * @return
     */
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Feed getFeedById(int id);

    /**
     * 拉模式
     * @param maxId  增量，越往下拉，feed小于maxid
     * @param userIds 从关注好友中查找
     * @param count   分页
     * @return
     */
    List<Feed> selectUserFeeds(@Param("maxId") int maxId,
                               @Param("userIds") List<Integer> userIds,
                               @Param("count") int count);
}
