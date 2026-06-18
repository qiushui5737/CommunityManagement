package com.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.Announcement;
import com.community.mapper.AnnouncementMapper;
import com.community.service.AnnouncementService;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    @Override
    public Page<Announcement> selectAnnouncementPageWithPublisher(Page<Announcement> page, LambdaQueryWrapper<Announcement> qw) {
        return baseMapper.selectAnnouncementPageWithPublisher(page, qw);
    }
}
