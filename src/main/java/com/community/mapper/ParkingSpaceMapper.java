package com.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.entity.ParkingSpace;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ParkingSpaceMapper extends BaseMapper<ParkingSpace> {
    // 继承 BaseMapper 后，MyBatis-Plus 自动注入 save/update/delete/select 等基础方法
}
