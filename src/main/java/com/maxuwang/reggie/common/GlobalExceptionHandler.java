package com.maxuwang.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器
 */
@RestControllerAdvice(annotations = {RestController.class, Controller.class})// 这一段表示捕获有“@RestController”注解或“@Controller”的类的异常
@ResponseBody// 用于返回对象时将其序列化为JSON格式
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> ex(java.sql.SQLIntegrityConstraintViolationException ex){// 捕获 SQL完整性约束违规异常
        if (ex.getMessage().contains("Duplicate entry")) {// 添加了重复的条目进行处理
            String[] split = ex.getMessage().split(" ");// 将得到的异常信息进行分割字符串处理
            String str = split[2];// 获取到重复条目的信息
            return R.error(str+"已存在");
        }
        return R.error("可能操作有误，请联系管理员或查看使用手册");
    }

    /**
     * 捕获自定义异常CustomException
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> ex(CustomException ex){
        log.info(ex.getMessage());

        return R.error(ex.getMessage());
    }


}
