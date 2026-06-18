package com.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String role;
    private String avatarUrl;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String email;
    private String gender;
    private LocalDate birthday;
    private String idCard;
    private String emergencyContact;
    private String emergencyPhone;

    // 👇 核心修改：加上 exist = false，MyBatis-Plus 就不会在 SQL 中查询这两列了
    @TableField(exist = false)
    private Long buildingId;

    @TableField(exist = false)
    private Long houseId;

    // 联表查询展示字段（用于前端显示楼栋和房间）
    @TableField(exist = false)
    private String buildingName;
    @TableField(exist = false)
    private String roomNo;
}
