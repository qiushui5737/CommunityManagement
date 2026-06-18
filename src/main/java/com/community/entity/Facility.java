package com.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 社区公共设施
 */
@Data
@TableName("facility")
public class Facility {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 设施名称 */
    private String name;
    /** 设施分类：运动器材/工具设备/文娱用品/其他 */
    private String category;
    /** 设施描述 */
    private String description;
    /** 设施图片 */
    private String imageUrl;
    /** 存放位置 */
    private String location;
    /** 借用押金（可为0） */
    private BigDecimal deposit;
    /** 设施状态：AVAILABLE=可借用 / BOOKED=已借出 / MAINTENANCE=维护中 / RETIRED=已报废 */
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
