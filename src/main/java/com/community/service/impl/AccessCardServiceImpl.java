package com.community.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.AccessCard;
import com.community.mapper.AccessCardMapper;
import com.community.service.AccessCardService;
import org.springframework.stereotype.Service;

@Service
public class AccessCardServiceImpl extends ServiceImpl<AccessCardMapper, AccessCard> implements AccessCardService {
    @Override
    public Page<AccessCard> selectCardPage(Page<AccessCard> page, Long ownerId, String cardType, String status, String keyword) {
        return baseMapper.selectCardPage(page, ownerId, cardType, status, keyword);
    }
}
