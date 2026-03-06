package com.sq.sjc.dispatchentity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sjc_dispatch_track")
public class SjcDispatchTrackEntity {
    private Long id;
    private Long taskId;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private LocalDateTime trackTime;
    private Integer isDelete;
    private LocalDateTime createTime;
}
