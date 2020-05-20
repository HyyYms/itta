package com.program.itta.service.impl;

import com.program.itta.domain.entity.Timer;
import com.program.itta.mapper.TimerMapper;
import com.program.itta.service.TimerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: itta
 * @description: Timer业务层实现类
 * @author: Mr.Huang
 * @create: 2020-05-20 17:06
 **/
@Service
public class TimerServiceImpl implements TimerService {

    @Autowired
    private TimerMapper timerMapper;

    @Override
    public List<Timer> selectAll() {
        List<Timer> timerList = timerMapper.selectAll();
        if (timerList != null && !timerList.isEmpty()) {
            return timerList;
        }
        return null;
    }
}