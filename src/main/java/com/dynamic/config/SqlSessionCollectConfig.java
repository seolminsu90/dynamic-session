package com.dynamic.config;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.dynamic.model.SqlSessionCollect;

@Configuration
public class SqlSessionCollectConfig {

    @Component("gameDBSession")
    protected class GameDBSessionCollect implements SqlSessionCollect {
        @Autowired
        Map<String, SqlSessionTemplate> sqlSessionTemplateMap;

        @Override
        public SqlSession getSession(String wid) {
            return sqlSessionTemplateMap.get(wid);
        }
    }
}
