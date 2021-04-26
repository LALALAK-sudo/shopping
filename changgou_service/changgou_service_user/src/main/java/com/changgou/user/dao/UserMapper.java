package com.changgou.user.dao;

import com.changgou.user.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface UserMapper extends Mapper<User> {

    @Update("update tb_user set points=points+#{point} where username=#{username}")
    int updateUserPoint(@Param("username") String username, @Param("point") int point);
}