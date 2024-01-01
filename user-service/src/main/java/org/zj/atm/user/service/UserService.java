package org.zj.atm.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.zj.atm.framework.starter.convention.result.Result;
import org.zj.atm.user.dao.entity.UserDO;
import org.zj.atm.user.dto.req.UserRegisterReqDTO;
import org.zj.atm.user.dto.resp.UserActualMsgDTO;
import org.zj.atm.user.dto.resp.UserAnonymizedMsg;

public interface UserService extends IService<UserDO> {

    /**
     * 用户注册
     * @param requestParam 用户注册请求实体类
     */
    Result<Void> register(UserRegisterReqDTO requestParam);

    Result<UserAnonymizedMsg> getAnonymized(String identityId);

    Result<UserActualMsgDTO> getActualMsgById(Long userId);
}
