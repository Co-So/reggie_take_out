package com.maxuwang.reggie.config;

import com.maxuwang.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration // 这个注解表示该类是一个配置类
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /**
     * 设置静态资源映射的方法
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始资源映射...");
        registry
                .addResourceHandler("/backend/**") // 设置请求访问时的请求URI
                .addResourceLocations("classpath:/backend/");// 设置映射到项目中的某个路径下

        registry.addResourceHandler("/front/**")
                .addResourceLocations("classpath:/front/");
    }

    /**
     * 消息转换扩展器
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 创建消息转换器
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        // 设置具体的对象转换器
        messageConverter.setObjectMapper(new JacksonObjectMapper());

        // 通过设置索引，让自己的转换器放在最前面，否者默认的jackson转换器会在前面，用户上自己设置的转换器
        converters.add(0, messageConverter);
    }
}
