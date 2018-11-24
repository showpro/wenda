package com.nowcoder.dao;

import org.apache.ibatis.annotations.*;

import com.nowcoder.model.User;

/**
 * Created by zhan on 2018/9/2.
 */
@Mapper
public interface UserDAO {
    String TABLE_NAME = "user";
    String INSET_FIELDS = " name, password, salt, head_url ";
    String SELECT_FIELDS = " id, name, password, salt, head_url";

    /**
     * 添加用户
     * @param user
     * @return
     */
    @Insert({"insert into ", TABLE_NAME, "(", INSET_FIELDS,
            ") values (#{name},#{password},#{salt},#{headUrl})"})
    int addUser(User user);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    User selectById(int id);

    /**
     * 根据用户名查询
     * @param name
     * @return
     */
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where name=#{name}"})
    User selectByName(String name);

    /**
     * 修改密码
     * @param user
     */
    @Update({"update ", TABLE_NAME, " set password=#{password} where id=#{id}"})
    void updatePassword(User user);

    @Delete({"delete from ", TABLE_NAME, " where id=#{id}"})
    void deleteById(int id);
}
