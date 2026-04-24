package com.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.community.entity.SysUser;
import org.apache.ibatis.annotations.Mapper; // 👈 确保导入这个
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper // 👈 必须加这一行
public interface SysUserMapper extends BaseMapper<SysUser> {
    @Select("SELECT u.*, b.name AS building_name, h.room_no AS room_no, h.id AS house_id, un.building_id AS building_id " +
            "FROM sys_user u " +
            "LEFT JOIN community_house h ON u.id = h.owner_id " +
            "LEFT JOIN community_unit un ON h.unit_id = un.id " +
            "LEFT JOIN community_building b ON un.building_id = b.id " +
            "${ew.customSqlSegment}")
    <E extends IPage<SysUser>> E selectUserPageWithHouse(E page, @Param(Constants.WRAPPER) Object wrapper);
}
