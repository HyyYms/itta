package com.program.itta.common.exception.schedule;

/**
 * @program: itta
 * @description: 日程删除失败
 * @author: Mr.Huang
 * @create: 2020-04-25 15:38
 **/
public class ScheduleDelFailException extends RuntimeException{
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
    public ScheduleDelFailException(String msg) {
        super(msg);
        this.msg = msg;
    }
}
