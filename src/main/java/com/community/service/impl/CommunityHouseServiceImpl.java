package com.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.CommunityHouse;
import com.community.mapper.CommunityHouseMapper;
import com.community.service.CommunityHouseService;
import org.springframework.stereotype.Service;

@Service
public class CommunityHouseServiceImpl extends ServiceImpl<CommunityHouseMapper, CommunityHouse> implements CommunityHouseService {
}
