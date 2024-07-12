package com.maxuwang.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.maxuwang.reggie.common.BaseContext;
import com.maxuwang.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    // 路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 将ServletRequest强转为HttpServletRequest
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        // 将ServletResponse强转为HttpServletResponse
        HttpServletResponse response = (HttpServletResponse) servletResponse;


        // 将请求者的ID存入线程的ThreadLocal便于其他对象获取
        BaseContext.setCurrentId((Long) request.getSession().getAttribute("employee"));

        //1. 获取uri
        String URI = request.getRequestURI();

        //2. 判断请求是否需要拦截
        // 定义不需要拦截的请求路径，可以直接放行的路径。
        String[] Uris = new String[] {
                "/employee/login",// 登录请求
                "/employee/logout",// 退出登录请求
                "/backend/**",
                "/front/**"
                // 注意，这里为什么放行了/backend/** 与 /front/**，
                // 因为这两个只是前端的html页面，并不包含实际的信息，只是这些页面会执行一些获取信息，当获取这些信息的时候并不会走这些路径，然后并不会放行

                ,"/common/**"

                ,"/user/login"

        };
        Boolean check = check(Uris, URI);
        if (check){
            // 是不需要拦截的路径直接放行
            filterChain.doFilter(request, response);
            return;
        }

        //3.判断是否已经登录
        // 根据当前的session对象获取出是否有登录成功的标记属性“employee:用户ID”，判断员工，或者用户是否登录
        if ((request.getSession().getAttribute("employee") != null) || (request.getSession().getAttribute("user") != null)) {
            // 已经登录，放行
            filterChain.doFilter(request, response);
            return;
        }

        //4.未登录，响应未登录
        log.info("未登录...");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，判断请求是否需要拦截
     * @param URI
     * @return
     */
    public Boolean check(String[] Uris, String URI){
        for (String uri : Uris) {
            boolean match = PATH_MATCHER.match(uri, URI);
            if (match) {
                return true;
            }
        }
        return false;
    }

}
