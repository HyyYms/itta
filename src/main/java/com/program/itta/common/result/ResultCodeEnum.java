package com.program.itta.common.result;

/**
 * @program: itta
 * @description: 统一返回格式枚举
 * @author: Mr.Huang
 * @create: 2020-04-04 10:37
 **/
public enum  ResultCodeEnum {
    /*** 通用部分 100 - 599***/
    // 成功请求
    SUCCESS(200, "successful"),
    // 重定向
    REDIRECT(301, "redirect"),
    // 资源未找到
    NOT_FOUND(404, "not found"),
    // 服务器错误
    SERVER_ERROR(500,"server error"),
    /*** 这里可以根据不同模块用不同的区级分开错误码，例如:  ***/
    // 1000～1999 区间表示用户模块错误
    User_Exists_Exception(10001,"用户已存在"),
    User_Not_Exists_Exception(10002,"用户不存在"),
    User_Update_Fail_Exception(10003,"用户更新失败"),
    User_Del_Fail_Exception(10004,"用户删除失败"),
    // 2000～2999 区间表示项目模块错误
    Item_Name_Exists_Exception(20001,"项目名称已存在"),
    Item_Add_Fail_Exception(20002,"项目添加失败"),
    Item_Del_Fail_Exception(20003,"项目删除失败"),
    Item_Update_Fail_Exception(20004,"项目更新失败"),
    Item_Not_Exists_Exception(20005,"项目不存在，项目id查找为空"),
    // 3000～3999 区间表示任务模块错误
    Task_Add_Fail_Exception(30001,"任务添加失败"),
    Task_Name_Exists_Exception(30002,"任务名称已存在于该项目中"),
    Task_Update_Fail_Exception(30003,"任务更新失败"),
    Task_Del_Fail_Exception(30004,"任务删除失败"),
    Task_Not_Exists_Exception(30005,"任务不存在，任务id查找为空"),
    // 4000～4999 去见表示日程模块错误
    Schedule_Name_Exists_Exception(40001,"该日程名称重复"),
    Schedule_Not_Exists_Exception(40002,"该日程不存在"),
    Schedule_Add_Fail_Exception(40002,"日程添加失败"),
    Schedule_Del_Fail_Exception(40002,"日程删除失败"),
    Schedule_Update_Fail_Exception(40002,"日程更新失败"),
    // 5000～5999 去见表示权限控制模块错误
    Not_Item_Leader_Exception(50001,"不具备该项目负责人的相关权限"),
    Not_Task_Found_Exception(50002,"不具备该任务创建人的的相关权限"),
    // 。。。

    ;

    /**
     * 响应状态码
     */
    private Integer code;
    /**
     * 响应信息
     */
    private String message;

    ResultCodeEnum(Integer code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
