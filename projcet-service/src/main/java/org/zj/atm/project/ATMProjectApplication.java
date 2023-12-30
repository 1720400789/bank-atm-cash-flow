package org.zj.atm.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("org.zj.atm.project.dao.mapper")
@EnableFeignClients("org.zj.atm.project.remote")
public class ATMProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ATMProjectApplication.class, args);
    }

}
