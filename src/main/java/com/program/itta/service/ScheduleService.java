package com.program.itta.service;

import com.program.itta.domain.dto.ScheduleDTO;
import com.program.itta.domain.entity.Schedule;

import java.util.List;

public interface ScheduleService {
    // 添加日程
    Boolean addSchedule(Schedule schedule);

    // 删除日程
    Boolean deleteSchedule(Schedule schedule);

    // 更新日程
    Boolean updateSchedule(Schedule schedule);

    // 查找所有日程
    List<ScheduleDTO> selectAll();

    // 查找日程
    Schedule selectByPrimaryKey(Integer id);

    // 查找用户下的所有日程
    List<ScheduleDTO> selectByUserId(Integer month);

    // 查找用户今日未完成日程
    List<ScheduleDTO> selectNotFinishSchedule();

    // 查找用户今日完成日程
    List<ScheduleDTO> selectFinishSchedule();

    // 设置日程为完成状态
    Boolean setScheduleStatus(Schedule schedule);
}
