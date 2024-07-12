package com.maxuwang.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入操作自动填充
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("插入操作，公共字段填充...");
        // 设置插入时间
        metaObject.setValue("createTime", LocalDateTime.now());
        // 设置修改时间
        metaObject.setValue("updateTime", LocalDateTime.now());

        // 从ThreadLocal中获取“修改者ID”，设置“创建者ID”
        metaObject.setValue("createUser", BaseContext.getCurrentId());// 本节重点关注代码
        // 从ThreadLocal中获取“修改者ID”，设置“修改者ID”
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    /**
     * 修改操作自动填充
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("修改操作，公共字段填充...");
        metaObject.setValue("updateTime", LocalDateTime.now());

        // 从ThreadLocal中获取“修改者ID”，设置“修改者ID”
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
