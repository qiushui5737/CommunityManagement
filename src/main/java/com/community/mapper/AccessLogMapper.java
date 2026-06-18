package com.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.entity.AccessLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AccessLogMapper extends BaseMapper<AccessLog> {

    Page<AccessLog> selectLogPage(Page<AccessLog> page,
                                  @Param("cardNo") String cardNo,
                                  @Param("userName") String userName,
                                  @Param("direction") String direction,
                                  @Param("accessStatus") String accessStatus,
                                  @Param("buildingId") Long buildingId);

    List<Map<String, Object>> selectDailyStats(@Param("days") int days);

    List<Map<String, Object>> selectLocationStats();
}
