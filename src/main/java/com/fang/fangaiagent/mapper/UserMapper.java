package com.fang.fangaiagent.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fang.fangaiagent.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2024-01-20
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}