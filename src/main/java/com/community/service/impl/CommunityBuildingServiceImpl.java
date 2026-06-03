package com.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.CommunityBuilding;
import com.community.mapper.CommunityBuildingMapper;
import com.community.service.CommunityBuildingService;
import org.springframework.stereotype.Service;

@Service
public class CommunityBuildingServiceImpl extends ServiceImpl<CommunityBuildingMapper, CommunityBuilding> implements CommunityBuildingService {
}
