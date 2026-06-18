package com.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.entity.FeedbackReply;
import java.util.List;

public interface FeedbackReplyService extends IService<FeedbackReply> {
    List<FeedbackReply> selectRepliesByFeedbackId(Long feedbackId);
}
