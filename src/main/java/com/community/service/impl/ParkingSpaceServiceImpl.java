package com.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.ParkingSpace;
import com.community.mapper.ParkingSpaceMapper;
import com.community.service.ParkingSpaceService;
import org.springframework.stereotype.Service;

@Service
public class ParkingSpaceServiceImpl extends ServiceImpl<ParkingSpaceMapper, ParkingSpace> implements ParkingSpaceService {
    // ServiceImpl 已自动实现 IService 所有方法
    // 若需事务控制（如购买车位扣减库存/生成订单），可在 Controller 加 @Transactional，或在此类写复杂逻辑
}
