package com.dynamic.repository;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.dynamic.model.SqlSessionCollect;

@Repository
public class GameMapperImpl implements GameMapper {
    @Autowired
    @Qualifier("gameDBSession")
    private SqlSessionCollect sessionCollect;

    public List<Map<String, Object>> selectQuery(Map<String, Object> params, String wid) {
        return sessionCollect.getSession(wid).selectList("query", params);
    }
}
