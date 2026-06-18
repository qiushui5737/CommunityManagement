package com.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.entity.FeedbackReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface FeedbackReplyMapper extends BaseMapper<FeedbackReply> {

    @Select("SELECT r.*, u.real_name AS user_name FROM feedback_reply r " +
            "LEFT JOIN sys_user u ON r.user_id = u.id " +
            "WHERE r.feedback_id = #{feedbackId} ORDER BY r.create_time ASC")
    List<FeedbackReply> selectRepliesByFeedbackId(@Param("feedbackId") Long feedbackId);
}
