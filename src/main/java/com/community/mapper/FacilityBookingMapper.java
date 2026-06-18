package com.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.community.entity.FacilityBooking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FacilityBookingMapper extends BaseMapper<FacilityBooking> {

    /**
     * 联表查询：借用记录 + 申请人信息 + 设施信息
     */
    @Select("SELECT b.*, " +
            "(SELECT real_name FROM sys_user WHERE id = b.owner_id) AS owner_name, " +
            "(SELECT phone    FROM sys_user WHERE id = b.owner_id) AS owner_phone, " +
            "(SELECT name     FROM facility WHERE id = b.facility_id) AS facility_name, " +
            "(SELECT image_url FROM facility WHERE id = b.facility_id) AS facility_image " +
            "FROM facility_booking b ${ew.customSqlSegment}")
    <E extends IPage<FacilityBooking>> E selectBookingPage(E page, @Param(Constants.WRAPPER) Object wrapper);
}
