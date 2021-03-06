package com.program.itta.domain.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class Timer  implements Serializable {
    private Integer id;

    private Integer scheduleId;

    private Integer year;

    private Integer month;

    private Integer week;

    private Integer day;

    private Date createTime;

    private Date updateTime;

    @Tolerate
    public Timer() {
    }
}