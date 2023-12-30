package org.zj.atm.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zj.atm.framework.starter.convention.result.Result;
import org.zj.atm.framework.starter.convention.result.Results;
import org.zj.atm.project.dto.req.DebitCardLoginReqDTO;
import org.zj.atm.project.dto.req.DebitCardRegisterReqDTO;
import org.zj.atm.project.dto.resp.DebitCardLoginRespDTO;
import org.zj.atm.project.dto.resp.DebitCardRegisterRespDTO;
import org.zj.atm.project.service.DebitCardService;

/**
 * 银行卡管理控制层
 */
@Slf4j
@RestController
@RequestMapping("/api/atm/project-service/debit_card-service")
@RequiredArgsConstructor
public class DebitCardController {

    private final DebitCardService debitCardService;

    /**
     * 申请银行卡
     * @param requestParam 用户提交信息
     * @return 展示给用户的信息实体类 -银行卡号 -姓名等
     */
    @PostMapping("/v1/register-for-debit_card")
    public Result<DebitCardRegisterRespDTO> register(@RequestBody DebitCardRegisterReqDTO requestParam) {
        return Results.success(debitCardService.register(requestParam));
    }

    /**
     * TODO 开发银行卡登录接口,要考虑计时模块
     * 如果用户在前端超过 1 分钟没有进入新页面就"退卡",考虑一个折中的方案,
     * 后端另起一个"run"模块专门执行任务,一旦用户"插卡"成功就开始倒计时,如果前端触发一次新页面则向后端发送一次请求,让倒计时重置
     * 如果倒计时结束,则强制退卡
     */
    public Result<DebitCardLoginRespDTO> loginByCard(@RequestBody DebitCardLoginReqDTO requestParam) {
        return Results.success(debitCardService.login(requestParam));
    }

}
