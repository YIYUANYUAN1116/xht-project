package com.xht.program;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Program: xht_project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-04 20:54
 **/

@SpringBootApplication
@MapperScan("com.xht.program.mapper")
@ComponentScan(basePackages = "com.xht")
public class ProgramApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProgramApplication.class,args);
    }
}
