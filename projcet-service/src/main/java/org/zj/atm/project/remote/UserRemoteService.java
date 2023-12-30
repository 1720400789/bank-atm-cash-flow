package org.zj.atm.project.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.zj.atm.framework.starter.convention.result.Result;
import org.zj.atm.project.remote.dto.req.UserRegisterReqDTO;

/**
 * 用户远程服务调用
 */
@FeignClient(value = "atm-user-service")
public interface UserRemoteService {

    /**
     * 根据乘车人 ID 集合查询乘车人列表
     */
    @GetMapping("/api/atm/user-service/v1/register")
    Result<Void> register(@RequestBody UserRegisterReqDTO requestParam);

}
