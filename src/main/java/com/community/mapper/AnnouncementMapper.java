package com.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.community.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {

    /** 分页查询公告，附带发布人姓名（子查询避免 JOIN 列名歧义） */
    @Select("SELECT *, (SELECT real_name FROM sys_user WHERE id = publisher_id) AS publisher_name " +
            "FROM announcement ${ew.customSqlSegment}")
    <E extends IPage<Announcement>> E selectAnnouncementPageWithPublisher(E page, @Param(Constants.WRAPPER) Object wrapper);
}
