package com.community.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.PaymentBill;
import com.community.mapper.PaymentBillMapper;
import com.community.service.PaymentBillService;
import org.springframework.stereotype.Service;
@Service
public class PaymentBillServiceImpl extends ServiceImpl<PaymentBillMapper, PaymentBill> implements PaymentBillService {}
