package com.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.PropertyFeeItem;
import com.community.mapper.PropertyFeeItemMapper;
import com.community.service.PropertyFeeItemService;
import org.springframework.stereotype.Service;

@Service
public class PropertyFeeItemServiceImpl extends ServiceImpl<PropertyFeeItemMapper, PropertyFeeItem> implements PropertyFeeItemService {
}
