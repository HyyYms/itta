package com.program.itta.service.impl;

import com.program.itta.common.config.JwtConfig;
import com.program.itta.domain.dto.TaskDTO;
import com.program.itta.domain.entity.Tag;
import com.program.itta.domain.entity.Task;
import com.program.itta.domain.entity.TaskTag;
import com.program.itta.mapper.TagMapper;
import com.program.itta.mapper.TaskMapper;
import com.program.itta.mapper.TaskTagMapper;
import com.program.itta.service.TaskTagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: itta
 * @description: 任务标签Service
 * @author: Mr.Huang
 * @create: 2020-05-13 21:23
 **/
@Service
public class TaskTagServiceImpl implements TaskTagService {

    private static final Logger logger = LoggerFactory.getLogger(TaskTagServiceImpl.class);


    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskTagMapper taskTagMapper;

    @Autowired
    private TagMapper tagMapper;

    @Resource
    private JwtConfig jwtConfig;

    @Override
    public Boolean addTaskTag(Integer taskId, String content) {
        Tag tag = tagMapper.selectByContent(content);
        TaskTag taskTag = TaskTag.builder()
                .taskId(taskId)
                .tagId(tag.getId())
                .build();
        logger.info("增加任务标签" + tag);
        return addJudge(taskTag);
    }

    @Override
    public Boolean addTaskTag(Task task, String content) {
        Tag tag = tagMapper.selectByContent(content);
        Integer userId = jwtConfig.getUserId();
        List<Task> taskList = taskMapper.selectByUserId(userId);
        Task task1 = selectTaskByList(taskList,task);
        TaskTag taskTag = TaskTag.builder()
                .taskId(task1.getId())
                .tagId(tag.getId())
                .build();
        logger.info("增加任务标签" + tag);
        return addJudge(taskTag);
    }

    @Override
    public List<Integer> selectByTaskId(Integer taskId) {
        List<TaskTag> taskTags = taskTagMapper.selectByTaskId(taskId);
        List<Integer> tagIds = taskTags.stream()
                .map(TaskTag -> TaskTag.getTagId())
                .collect(Collectors.toList());
        if (!tagIds.isEmpty()) {
            return tagIds;
        }
        return null;
    }

    @Override
    public Boolean deleteAllTaskTag(Task task) {
        return deleteTaskTagByTask(task);
    }

    @Override
    public Boolean deleteTaskTag(TaskTag taskTag) {
        TaskTag tag = taskTagMapper.selectByTaskTag(taskTag);
        int delete = taskTagMapper.deleteByPrimaryKey(tag.getId());
        if (delete != 0) {
            logger.info("删除任务" + tag.getTaskId() + "的标签" + tag);
            return true;
        }
        return false;
    }

    @Override
    public Boolean deleteMemberTaskTag(List<TaskDTO> taskList) {
        for (TaskDTO taskDTO : taskList) {
            Task task = taskDTO.convertToTask();
            Boolean deleteTaskTagByTask = deleteTaskTagByTask(task);
            if (!deleteTaskTagByTask) {
                return false;
            }
        }
        return true;
    }

    private Boolean deleteTaskTagByTask(Task task) {
        List<TaskTag> taskTagList = taskTagMapper.selectByTaskId(task.getId());
        for (TaskTag taskTag : taskTagList) {
            int delete = taskTagMapper.deleteByPrimaryKey(taskTag.getId());
            if (delete == 0) {
                return false;
            }
        }
        logger.info("删除任务" + task + "下的所有的标签");
        return true;
    }

    private Boolean judgeTaskTag(TaskTag taskTag) {
        TaskTag selectByTaskTag = taskTagMapper.selectByTaskTag(taskTag);
        return selectByTaskTag != null;
    }

    private Task selectTaskByList(List<Task> taskList,Task task){
        for (Task task1 : taskList){
            if (task1.getName().equals(task.getName())){
                if (task1.getItemId().equals(task.getItemId())){
                    return task1;
                }
            }
        }
        return null;
    }

    private Boolean addJudge(TaskTag taskTag){
        Boolean judgeTaskTag = judgeTaskTag(taskTag);
        if (judgeTaskTag) {
            return true;
        }
        int insert = taskTagMapper.insert(taskTag);
        return insert != 0;
    }
}
