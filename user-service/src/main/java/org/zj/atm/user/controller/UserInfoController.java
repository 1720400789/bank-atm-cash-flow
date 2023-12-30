package org.zj.atm.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zj.atm.framework.starter.convention.result.Result;
import org.zj.atm.framework.starter.convention.result.Results;
import org.zj.atm.user.dto.req.UserRegisterReqDTO;
import org.zj.atm.user.service.UserService;

/**
 * 用户管理控制层
 */
@Slf4j
@RestController
@RequestMapping("/api/atm/user-service")
@RequiredArgsConstructor
public class UserInfoController {

    private final UserService userService;

    /**
     * 注册用户
     * @param requestParam 用户提交信息
     * @return 无返回值
     */
    @PostMapping("/v1/register")
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }
}
