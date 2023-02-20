package com.xxl.job.core.biz.model;

import java.io.Serializable;

/**
 * common return
 *
 * @param <T>
 * @author xuxueli 2015-12-4 16:32:31
 */
public class ReturnT<T> implements Serializable {
    public static final long serialVersionUID = 42L;

    public static final int SUCCESS_CODE = 200;
    public static final String SUCCESS_MESSAGE = "SUCCESS";
    public static final int FAIL_CODE = 500;
    public static final String FAIL_MESSAGE = "FAILURE";

    public static final ReturnT<String> SUCCESS = new ReturnT<String>(null);
    public static final ReturnT<String> FAIL = new ReturnT<String>(FAIL_CODE, null);

    private int code;
    private String msg;
    private T content;

    public ReturnT() {
    }

    public ReturnT(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ReturnT(T content) {
        this.code = SUCCESS_CODE;
        this.content = content;
    }

    public static ReturnT<String> success() {
        return new ReturnT<String>(SUCCESS_CODE, SUCCESS_MESSAGE);
    }

    public static ReturnT<String> failure() {
        return new ReturnT<String>(FAIL_CODE, FAIL_MESSAGE);
    }

    public static ReturnT<String> failure(String message) {
        return new ReturnT<String>(FAIL_CODE, FAIL_MESSAGE + " - " + message);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ReturnT [code=" + code + ", msg=" + msg + ", content=" + content + "]";
    }

}
