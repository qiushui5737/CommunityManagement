package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community.entity.Announcement;

public interface AnnouncementService extends IService<Announcement> {

    /** 分页查询公告（含发布人姓名） */
    Page<Announcement> selectAnnouncementPageWithPublisher(Page<Announcement> page, LambdaQueryWrapper<Announcement> qw);
}
