package org.zj.atm.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.zj.atm.project.dao.enrity.DebitCardDO;
import org.zj.atm.project.dto.req.DebitCardLoginReqDTO;
import org.zj.atm.project.dto.req.DebitCardRegisterReqDTO;
import org.zj.atm.project.dto.resp.DebitCardLoginRespDTO;
import org.zj.atm.project.dto.resp.DebitCardRegisterRespDTO;

public interface DebitCardService extends IService<DebitCardDO> {

    /**
     * 开户接口
     * @param requestParam 开户请求参数
     * @return 开户返回实体
     */
    DebitCardRegisterRespDTO register(DebitCardRegisterReqDTO requestParam);

    DebitCardLoginRespDTO login(DebitCardLoginReqDTO requestParam);
}
