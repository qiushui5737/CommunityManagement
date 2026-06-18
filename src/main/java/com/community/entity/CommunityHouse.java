package com.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("community_house")
public class CommunityHouse {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long unitId;         // 所属单元ID
    private String roomNo;       // 房间号
    private BigDecimal area;     // 面积
    private String status;       // 状态：VACANT/OCCUPIED
    private Long ownerId;        // 关联业主ID
    private LocalDateTime createTime;
}
