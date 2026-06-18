package com.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.entity.AccessCard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AccessCardMapper extends BaseMapper<AccessCard> {

    Page<AccessCard> selectCardPage(Page<AccessCard> page,
                                    @Param("ownerId") Long ownerId,
                                    @Param("cardType") String cardType,
                                    @Param("status") String status,
                                    @Param("keyword") String keyword);
}
