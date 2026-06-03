package com.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.community.entity.RepairRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RepairRequestMapper extends BaseMapper<RepairRequest> {

    // 核心修复：用标量子查询获取业主姓名
    // 单表查询 + customSqlSegment，彻底避免 JOIN 导致的列名歧义和分页拦截器冲突
    @Select("SELECT *, (SELECT real_name FROM sys_user WHERE id = owner_id) AS owner_name " +
            "FROM repair_request ${ew.customSqlSegment}")
    <E extends IPage<RepairRequest>> E selectRepairPageWithOwner(E page, @Param(Constants.WRAPPER) Object wrapper);
}
