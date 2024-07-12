package com.maxuwang.reggie.common;

public class BaseContext {
    // 提供ThreadLocal静态属性，让其某个线程在不同位置也可以调用
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 存入“修改者ID”
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取“修改者ID”
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
