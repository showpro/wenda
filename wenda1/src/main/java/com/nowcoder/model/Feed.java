package com.nowcoder.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;
import java.util.Map;

/**
 * 新鲜事
 * 
 * Created by zhan on 2018/9/2.
 */
public class Feed {
    private int id;		//新鲜事id
    private int type;	//新鲜事类型
    private int userId;	//对应的用户id
    private Date createdDate;	//新鲜事产生时间
    private String data;//Json格式，存储数据
    private JSONObject dataJSON = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        //反序列化
        dataJSON = JSONObject.parseObject(data);
    }
    public String get(String key) {
        return dataJSON == null ? null : dataJSON.getString(key);
    }
}
