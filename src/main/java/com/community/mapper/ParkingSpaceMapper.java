package com.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.community.entity.ParkingSpace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ParkingSpaceMapper extends BaseMapper<ParkingSpace> {

    // 联表查询：通过标量子查询获取业主姓名、电话、楼栋名称、楼栋编号
    @Select("SELECT *, " +
            "(SELECT real_name FROM sys_user WHERE id = owner_id) AS owner_name, " +
            "(SELECT phone FROM sys_user WHERE id = owner_id) AS owner_phone, " +
            "(SELECT name FROM community_building WHERE id = building_id) AS building_name, " +
            "(SELECT building_no FROM community_building WHERE id = building_id) AS building_no " +
            "FROM parking_space ${ew.customSqlSegment}")
    <E extends IPage<ParkingSpace>> E selectParkingPageWithOwner(E page, @Param(Constants.WRAPPER) Object wrapper);
}
