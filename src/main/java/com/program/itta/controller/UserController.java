package com.program.itta.controller;

import com.program.itta.common.annotation.RequestLog;
import com.program.itta.common.config.JwtConfig;
import com.program.itta.common.exception.user.UserDelFailException;
import com.program.itta.common.exception.user.UserNotExistsException;
import com.program.itta.common.exception.user.UserUpdateFailException;
import com.program.itta.common.result.HttpResult;
import com.program.itta.common.util.COSClientUtil;
import com.program.itta.common.util.SslUtil;
import com.program.itta.domain.dto.UserDTO;
import com.program.itta.domain.entity.User;
import com.program.itta.service.UserService;
import io.swagger.annotations.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.util.UUID;

import static com.program.itta.common.result.ResultCodeEnum.SERVER_ERROR;

/**
 * @program: itta
 * @description: 用户API
 * @author: Mr.Huang
 * @create: 2020-04-04 10:40
 **/
@Api(tags = "用户接口")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Resource
    private COSClientUtil cosClientUtil;

    @Resource
    private JwtConfig jwtConfig;

    @RequestLog(module = "用户模块", operationDesc = "编辑用户信息")
    @ApiOperation(value = "编辑用户信息", notes = "(编辑该用户的详细信息)")
    @ApiResponses({@ApiResponse(code = 200, message = "请求成功"), @ApiResponse(code = 10003, message = "用户更新失败")})
    @PutMapping("/updateUser")
    public HttpResult updateUser(@ApiParam(name = "用户DTO类", value = "传入Json格式", required = true)
                                 @RequestBody
                                 @Validated UserDTO userDTO) {
        User user = userDTO.convertToUser();
        Boolean updateUser = userService.updateUser(user);
        if (!updateUser) {
            throw new UserUpdateFailException("用户信息更新失败");
        }
        return HttpResult.success();
    }

    @RequestLog(module = "用户模块", operationDesc = "查找用户信息")
    @ApiOperation(value = "查找用户信息", notes = "(查看该用户的详细信息)")
    @ApiResponses({@ApiResponse(code = 200, message = "请求成功"), @ApiResponse(code = 10002, message = "用户不存在")})
    @GetMapping("/seletUser")
    public HttpResult selectUser() {
        UserDTO userDTO = userService.selectUser();
        jwtConfig.removeThread();
        if (userDTO == null) {
            throw new UserNotExistsException("该用户不存在");
        }
        return HttpResult.success(userDTO);
    }

    @RequestLog(module = "用户模块", operationDesc = "上传用户头像")
    @ApiOperation(value = "上传用户头像", notes = "(上传该用户的头像)")
    @ApiResponses({@ApiResponse(code = 200, message = "请求成功")})
    @PostMapping("/upload")
    public HttpResult upload(@ApiParam(name = "上传图片", value = "选择用户头像文件", required = true)
                             @RequestParam(value = "file") MultipartFile file) {
        String url = cosClientUtil.upload(file, "userHead/");
        if (url != null) {
            Boolean updateUserHead = userService.updateUserHead(url);
            jwtConfig.removeThread();
            if (!updateUserHead) {
                throw new UserUpdateFailException("用户头像更新失败");
            }
            return HttpResult.success();
        }
        return HttpResult.failure(SERVER_ERROR);
    }
}
