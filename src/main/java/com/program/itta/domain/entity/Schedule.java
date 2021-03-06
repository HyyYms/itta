package com.program.itta.domain.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class Schedule  implements Serializable {
    private Integer id;

    private Integer userId;

    private String name;

    private String place;

    private Integer priority;

    private Boolean whetherFinish;

    private Date startTime;

    private Date endTime;

    private Date completionTime;

    private Date createTime;

    private Date updateTime;

    @Tolerate
    public Schedule() {
    }
}