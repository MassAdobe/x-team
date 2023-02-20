package com.x.team.test.server.repository;

import com.x.team.test.server.entity.TUser;
import com.x.team.database.springboot.starter.mapper.XTeamMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface TUserMapper extends XTeamMapper<TUser> {
}