package com.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.FeedbackReply;
import com.community.mapper.FeedbackReplyMapper;
import com.community.service.FeedbackReplyService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FeedbackReplyServiceImpl extends ServiceImpl<FeedbackReplyMapper, FeedbackReply> implements FeedbackReplyService {
    @Override
    public List<FeedbackReply> selectRepliesByFeedbackId(Long feedbackId) {
        return baseMapper.selectRepliesByFeedbackId(feedbackId);
    }
}
