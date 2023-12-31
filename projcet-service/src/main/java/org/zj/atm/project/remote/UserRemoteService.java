package org.zj.atm.project.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.zj.atm.framework.starter.convention.result.Result;
import org.zj.atm.project.remote.dto.req.UserRegisterReqDTO;
import org.zj.atm.project.remote.dto.resp.UserAnonymizedMsg;

/**
 * 用户远程服务调用
 */
@FeignClient(value = "atm-user-service")
public interface UserRemoteService {

    /**
     * 开户时注册
     */
    @PostMapping("/api/atm/user-service/v1/register")
    Result<Void> register(@RequestBody UserRegisterReqDTO requestParam);

    /**
     * 通过身份证号拿到脱敏后的信息
     */
    @GetMapping("/api/atm/user-service/v1/get-anonymized")
    Result<UserAnonymizedMsg> getAnonymizedMsg(@RequestParam("identityId") String identityId);

}
