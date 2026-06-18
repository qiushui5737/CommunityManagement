package com.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FeedbackMapper extends BaseMapper<Feedback> {

    Page<Feedback> selectFeedbackPage(Page<Feedback> page,
                                      @Param("ownerId") Long ownerId,
                                      @Param("type") String type,
                                      @Param("status") String status);
}
