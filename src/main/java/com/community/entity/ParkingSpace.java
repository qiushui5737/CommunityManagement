package com.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("parking_space")
public class ParkingSpace {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String spaceNo;
    private String zone;
    private Integer rowNo;
    private Integer colNo;
    private BigDecimal area;
    private String type;
    private String status;
    private Long ownerId;
    private BigDecimal price;
    private LocalDateTime createTime;
}
