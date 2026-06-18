package com.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community.entity.Feedback;

public interface FeedbackService extends IService<Feedback> {
    Page<Feedback> selectFeedbackPage(Page<Feedback> page, Long ownerId, String type, String status);
}
