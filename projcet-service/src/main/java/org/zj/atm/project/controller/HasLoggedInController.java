package org.zj.atm.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.zj.atm.framework.starter.convention.result.Result;
import org.zj.atm.framework.starter.convention.result.Results;
import org.zj.atm.project.dto.req.DebitCardLoginReqDTO;
import org.zj.atm.project.dto.req.DebitCardRegisterReqDTO;
import org.zj.atm.project.dto.resp.CheckHeartBeatRespDTO;
import org.zj.atm.project.dto.resp.DebitCardLoginRespDTO;
import org.zj.atm.project.dto.resp.DebitCardRegisterRespDTO;
import org.zj.atm.project.service.DebitCardService;

/**
 * 银行卡管理控制层
 * 这里也是银行卡管理的控制层,只不过路径都是得加到黑名单上接收检验的接口
 */
@Slf4j
@RestController
@RequestMapping("/api/atm/project-service/has-logged-in")
@RequiredArgsConstructor
public class HasLoggedInController {

    private final DebitCardService debitCardService;

    /**
     * 前端心跳检查
     * @return
     */
    @GetMapping("/v1/check-heartbeat")
    public Result<CheckHeartBeatRespDTO> checkHeartBeat() {
        return Results.success(debitCardService.checkHeartBeat());
    }

//    @GetMapping("/")

}
