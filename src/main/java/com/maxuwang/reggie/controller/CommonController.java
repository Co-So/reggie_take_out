package com.maxuwang.reggie.controller;

import com.maxuwang.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 通用controller，主要做的是文件上传和文件下载处理
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")// 读取application.yml文件中的reggi.path定义的路径
    private String basePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){// 这里的参数名，要与前端form表单的name的值一致，否则需要用到“@RequestPart("file")”注解

        // 获取上传文件的文件名
        String originalFilename = file.getOriginalFilename();

        System.out.println(originalFilename);

        // 获取文件的后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 这里的file是一个临时文件需要进行转存，否则当此次请求完了之后，则该文件将会被删除
        // 临时文件一般是在对应系统的用户目录下的临时目录下

        // 使用UUID生成文件名
        String fileNameUUID = UUID.randomUUID().toString();

        File dir = new File(basePath);
        // 判断目录是否存在
        if (!dir.exists()){
            // 目录不存在则创建
            dir.mkdir();
        }

        try {
            file.transferTo(new File(basePath + fileNameUUID + suffix));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return R.success(fileNameUUID + suffix);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        // 注意这里的返回类型是void，是因为这里是通过输出流的方式向前端写数据

        try {
            // 1.通过输入流的方式读取文件
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            // 2.通过输出流的方式向前端写文件流
            ServletOutputStream outputStream = response.getOutputStream();

            // 设置响应的数据的格式
            response.setContentType("image/jepg");

            int len = 0;
            byte[] bytes = new byte[1024];

            while(  (len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

            // 3.关闭输入流和输出流
            outputStream.close();
            fileInputStream.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
