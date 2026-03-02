package com.aenggukland.letspt.log;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApiLogMapper {

    void save(ApiLog apiLog);
}
