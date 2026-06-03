package com.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.entity.CommunityHouse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommunityHouseMapper extends BaseMapper<CommunityHouse> {

    /**
     * 获取指定楼栋的房屋分布数据（用于平面图渲染）
     * 自动解析楼层号、关联单元名，并按楼层倒序排列
     */
    @Select("SELECT " +
            "h.id, h.room_no AS roomNo, h.status, h.area, " +
            "u.unit_no AS unitNo, " +
            "CAST(SUBSTRING(h.room_no, 1, LEN(h.room_no) - 2) AS INT) AS floorNum " +
            "FROM community_house h " +
            "INNER JOIN community_unit u ON h.unit_id = u.id " +
            "INNER JOIN community_building b ON u.building_id = b.id " +
            "WHERE b.id = #{buildingId} " +
            "ORDER BY floorNum DESC, u.unit_no, h.room_no")
    List<Map<String, Object>> selectBuildingMapData(@Param("buildingId") Long buildingId);
}
