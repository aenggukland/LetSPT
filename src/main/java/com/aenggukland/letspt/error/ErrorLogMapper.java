package com.aenggukland.letspt.error;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ErrorLogMapper {

    void save(ErrorLog errorLog);
}
