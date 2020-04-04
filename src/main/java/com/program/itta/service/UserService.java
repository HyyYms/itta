package com.program.itta.service;

import com.program.itta.dto.UserDTO;
import com.program.itta.entity.User;

public interface UserService {
    // 添加用户
    Boolean addUser(User user);
    // 更新用户
    Boolean updateUser(User user);
    // 删除用户(尚完善)
    Boolean deleteUser(User user);
    // 判断用户是否存在
    Boolean judgeUser(User user);
    // 查找模块——后期可考虑ES
    User selectUser(Integer userId);

}
