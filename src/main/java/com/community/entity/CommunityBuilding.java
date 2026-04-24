package com.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("community_building")
public class CommunityBuilding {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String buildingNo;   // 楼栋编号
    private String name;         // 楼栋名称
    private Integer totalFloors; // 总层数
    private LocalDateTime createTime;
}
