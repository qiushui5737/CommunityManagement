package com.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.CommunityUnit;
import com.community.mapper.CommunityUnitMapper;
import com.community.service.CommunityUnitService;
import org.springframework.stereotype.Service;

@Service
public class CommunityUnitServiceImpl extends ServiceImpl<CommunityUnitMapper, CommunityUnit> implements CommunityUnitService {
}
