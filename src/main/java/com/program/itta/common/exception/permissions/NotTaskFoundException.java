package com.program.itta.common.exception.permissions;

/**
 * @program: itta
 * @description: 无任务创建权限
 * @author: Mr.Huang
 * @create: 2020-04-24 16:28
 **/
public class NotTaskFoundException extends RuntimeException{
    /**
     * 错误码
     */
    protected Integer code;

    protected String msg;

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 有参构造器，返回码在枚举类中，这里可以指定错误信息
     *
     * @param msg
     */
    public NotTaskFoundException(String msg) {
        super(msg);
        this.msg = msg;
    }
}
