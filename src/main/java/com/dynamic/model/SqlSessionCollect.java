package com.dynamic.model;

import org.apache.ibatis.session.SqlSession;

public interface SqlSessionCollect {
    public SqlSession getSession(String wid);
}
