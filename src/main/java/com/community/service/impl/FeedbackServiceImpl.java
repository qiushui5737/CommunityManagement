package com.community.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.Feedback;
import com.community.mapper.FeedbackMapper;
import com.community.service.FeedbackService;
import org.springframework.stereotype.Service;

@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {
    @Override
    public Page<Feedback> selectFeedbackPage(Page<Feedback> page, Long ownerId, String type, String status) {
        return baseMapper.selectFeedbackPage(page, ownerId, type, status);
    }
}
