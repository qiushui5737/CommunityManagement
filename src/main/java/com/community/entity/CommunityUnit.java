package com.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("community_unit")
public class CommunityUnit {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long buildingId;  // 所属楼栋ID
    private String unitNo;    // 单元号（如 1单元、A单元）
    private LocalDateTime createTime;
}
