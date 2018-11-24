package com.nowcoder.async;

import java.util.List;

/**
 * Created by zhan on 2018/8/30.
 */
public interface EventHandler {
    void doHandle(EventModel model);

    List<EventType> getSupportEventTypes();
}
