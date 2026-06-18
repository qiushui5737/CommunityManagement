package com.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community.entity.AccessCard;

public interface AccessCardService extends IService<AccessCard> {
    Page<AccessCard> selectCardPage(Page<AccessCard> page, Long ownerId, String cardType, String status, String keyword);
}
