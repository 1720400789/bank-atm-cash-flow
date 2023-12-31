package org.zj.atm.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zj.atm.admin.common.convention.result.Result;
import org.zj.atm.admin.common.convention.result.Results;

/**
 * 用户控制层
 */

@Slf4j
@RestController
@RequestMapping("/api/atm/admin-service")
@RequiredArgsConstructor
public class AdminController {

    @GetMapping("/v1/test")
    public Result<String> getUserByUsername() {
        return Results.success("succuss");
    }
}
