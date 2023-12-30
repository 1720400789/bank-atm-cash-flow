package org.zj.atm.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan("org.zj.atm.user.dao.mapper")
public class ATMUserApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ATMUserApplication.class, args);
        System.out.println(run.getBean("idGenerator"));
    }
}
