package com.x.team.database.springboot.starter.mapper;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 描述：tk extends interface
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 15:56
 */
public interface XTeamMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
