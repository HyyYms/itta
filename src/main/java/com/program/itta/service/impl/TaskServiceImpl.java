package com.program.itta.service.impl;

import java.util.ArrayList;

import com.program.itta.common.config.JwtConfig;
import com.program.itta.common.exception.item.ItemNotExistsException;
import com.program.itta.common.exception.task.TaskNameExistsException;
import com.program.itta.common.exception.task.TaskNotExistsException;
import com.program.itta.domain.dto.TaskDTO;
import com.program.itta.domain.entity.Item;
import com.program.itta.domain.entity.Task;
import com.program.itta.domain.entity.UserTask;
import com.program.itta.mapper.ItemMapper;
import com.program.itta.mapper.TaskMapper;
import com.program.itta.mapper.UserTaskMapper;
import com.program.itta.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: itta
 * @description: Service Task实现类
 * @author: Mr.Huang
 * @create: 2020-04-17 15:47
 **/
@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private UserTaskMapper userTaskMapper;

    @Resource
    private JwtConfig jwtConfig;

    @Override
    public Boolean addTask(Task task) {
        Integer userId = jwtConfig.getUserId();
        task.setUserId(userId);
        task.setSuperId(0);
        judgeItemExists(task.getItemId());
        judgeTaskName(task);
        int insert = taskMapper.insert(task);
        if (insert != 0) {
            logger.info("用户：" + task.getUserId() + "添加任务" + task.getName());
            return true;
        }
        return false;
    }

    @Override
    public Boolean deleteTask(Task task) {
        judgeTaskExists(task);
        int delete = taskMapper.deleteByPrimaryKey(task.getId());
        if (delete != 0) {
            logger.info("删除任务：" + task.getName() + "任务id为：" + task.getId());
            return true;
        }
        return false;
    }

    @Override
    public Boolean updateTask(Task task) {
        judgeTaskExists(task);
        Task task1 = taskMapper.selectByPrimaryKey(task.getId());
        if (!task1.getName().equals(task.getName())) {
            judgeTaskName(task);
        }
        task.setUpdateTime(new Date());
        int update = taskMapper.updateByPrimaryKey(task);
        if (update != 0) {
            logger.info("任务：" + task.getId() + "更新任务信息为：" + task);
            return true;
        }
        return false;
    }


    @Override
    public List<TaskDTO> selectByItemId(Integer itemId) {
        List<Task> taskList = taskMapper.selectByItemId(itemId);
        if (taskList != null && !taskList.isEmpty()) {
            return convertToTaskDTOList(taskList);
        }
        return null;
    }

    @Override
    public List<TaskDTO> selectAllMyTask() {
        Integer userId = jwtConfig.getUserId();
        List<UserTask> userTaskList = userTaskMapper.selectByUserId(userId);
        List<Task> taskList = convertToTaskList(userTaskList);
        if (taskList != null && !taskList.isEmpty()) {
            return convertToTaskDTOList(taskList);
        }
        return null;
    }

    @Override
    public List<TaskDTO> selectByUserId() {
        Integer userId = jwtConfig.getUserId();
        List<Task> taskList = taskMapper.selectByUserId(userId);
        if (taskList != null && !taskList.isEmpty()) {
            return convertToTaskDTOList(taskList);
        }
        return null;
    }

    @Override
    public List<TaskDTO> selectBySuperId(Task task) {
        List<Task> taskList = taskMapper.selectBySuperId(task.getId());
        if (taskList != null && !taskList.isEmpty()) {
            return convertToTaskDTOList(taskList);
        }
        return null;
    }

    @Override
    public List<TaskDTO> selectByItemMember(Integer itemId, Integer userId) {
        List<Task> memberTaskList = new ArrayList<>();
        List<Task> taskList = taskMapper.selectByItemId(itemId);
        for (Task task : taskList) {
            if (userId.equals(task.getUserId())) {
                memberTaskList.add(task);
            }
        }
        return convertToTaskDTOList(memberTaskList);
    }

    @Override
    public List<TaskDTO> selectAllSubTask(Integer taskId, List<Integer> taskIdList) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        List<TaskDTO> taskList = selectBySuperId(task);
        List<TaskDTO> subTaskList = new ArrayList<>();
        for (TaskDTO subTask : taskList) {
            for (Integer id : taskIdList) {
                if (id.equals(subTask.getId())) {
                    subTaskList.add(subTask);
                }
            }
        }
        return subTaskList;
    }

    @Override
    public Boolean deleteByItemId(Integer itemId) {
        List<Task> taskList = taskMapper.selectByItemId(itemId);
        int delete;
        for (Task task : taskList) {
            List<UserTask> userTaskList = userTaskMapper.selectByTaskId(task.getId());
            for (UserTask userTask : userTaskList) {
                delete = userTaskMapper.deleteByPrimaryKey(userTask.getId());
                if (delete == 0) {
                    return false;
                }
            }
            delete = taskMapper.deleteByPrimaryKey(task.getId());
            if (delete == 0) {
                return false;
            }
        }
        logger.info("删除项目" + itemId + "下的所有任务");
        return true;
    }

    private List<Task> convertToTaskList(List<UserTask> userTaskList) {
        List<Task> taskList = new ArrayList<>();
        if (userTaskList != null && !userTaskList.isEmpty()) {
            List<Integer> taskIdList = userTaskList.stream()
                    .map(UserTask -> UserTask.getTaskId())
                    .collect(Collectors.toList());
            for (Integer taskId : taskIdList) {
                Task task = taskMapper.selectByPrimaryKey(taskId);
                taskList.add(task);
            }
            return taskList;
        }
        return null;
    }

    private void judgeTaskName(Task task) {
        boolean judgeTaskName = false;
        List<Task> taskList = taskMapper.selectByItemId(task.getItemId());
        for (Task task1 : taskList) {
            if (task.getName().equals(task1.getName())) {
                judgeTaskName = true;
            }
        }
        if (judgeTaskName) {
            throw new TaskNameExistsException("该任务名称已存在于此项目中");
        }
    }

    private void judgeTaskExists(Task task) {
        boolean judgeTaskExists = false;
        Task select = taskMapper.selectByPrimaryKey(task.getId());
        if (select != null) {
            judgeTaskExists = true;
        }
        if (!judgeTaskExists) {
            throw new TaskNotExistsException("该任务不存在，任务id查找为空");
        }
    }

    private void judgeItemExists(Integer itemId) {
        boolean judgeItemExists = false;
        Item item = itemMapper.selectByPrimaryKey(itemId);
        if (item != null) {
            judgeItemExists = true;
        }
        if (!judgeItemExists) {
            throw new ItemNotExistsException("该项目不存在");
        }
    }

    private List<TaskDTO> convertToTaskDTOList(List<Task> taskList) {
        List<TaskDTO> taskDTOList = new ArrayList<>();
        for (Task task : taskList) {
            TaskDTO taskDTO = new TaskDTO();
            taskDTO = taskDTO.convertFor(task);
            taskDTOList.add(taskDTO);
        }
        return taskDTOList;
    }
}
