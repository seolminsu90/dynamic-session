package com.dynamic.repository;

import java.util.List;
import java.util.Map;

public interface GameMapper {
    public List<Map<String, Object>> selectQuery(Map<String, Object> params, String wid);
}
