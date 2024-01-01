package org.zj.atm.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.zj.atm.framework.starter.convention.result.Result;
import org.zj.atm.framework.starter.convention.result.Results;
import org.zj.atm.user.dto.req.UserRegisterReqDTO;
import org.zj.atm.user.dto.resp.UserActualMsgDTO;
import org.zj.atm.user.dto.resp.UserAnonymizedMsg;
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
        return userService.register(requestParam);
    }

    @GetMapping("/v1/get-anonymized")
    public Result<UserAnonymizedMsg> getAnonymized(@RequestParam("identityId") String identityId) {
        return userService.getAnonymized(identityId);
    }

    /**
     * 通过身份证号对应主键拿到真实信息
     */
    @GetMapping("/v1/get-actual-by-id")
    Result<UserActualMsgDTO> getActualMsgById(@RequestParam("userId") Long userId) {
        return userService.getActualMsgById(userId);
    }

}
