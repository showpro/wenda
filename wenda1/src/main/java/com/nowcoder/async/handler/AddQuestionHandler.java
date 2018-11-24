package com.nowcoder.async.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.controller.CommentController;
import com.nowcoder.service.SearchService;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zhan on 2018/9/6.
 */
@Component
public class AddQuestionHandler implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(AddQuestionHandler.class);
    @Autowired
    SearchService searchService;

    @Override
    public void doHandle(EventModel model) {
        try {
            searchService.indexQuestion(model.getEntityId(),
                    model.getExt("title"), model.getExt("content"));
        } catch (Exception e) {
            logger.error("增加题目索引失败");
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        //每增加一个题目，就在上面的doHandle方法中创建一个异步事件（增加索引）
    	return Arrays.asList(EventType.ADD_QUESTION);
    }
}
