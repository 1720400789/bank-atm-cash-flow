package org.zj.atm.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.zj.atm.admin.dao.mapper")
public class ATMAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(ATMAdminApplication.class, args);
    }
}
