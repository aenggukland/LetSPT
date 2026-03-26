package com.aenggukland.letspt.error;

import org.apache.ibatis.annotations.Mapper;

// 에러 로그 데이터 접근 Mapper: SQL은 ErrorLogMapper.xml에만 작성한다
@Mapper
public interface ErrorLogMapper {

    // 에러 로그 저장: GlobalExceptionHandler에서만 호출한다
    void save(ErrorLog errorLog);
}
