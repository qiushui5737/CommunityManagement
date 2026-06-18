package com.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("access_log")
public class AccessLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long cardId;
    private String cardNo;
    private Long userId;
    private String userName;
    /** IN-进入 / OUT-离开 */
    private String direction;
    private String gateLocation;
    private Long buildingId;
    private LocalDateTime accessTime;
    /** SUCCESS-放行 / DENIED-拒绝 */
    private String accessStatus;
    private String denyReason;
}
