package org.zj.atm.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.zj.atm.project.dao.entity.DebitCardDO;
import org.zj.atm.project.dto.req.DebitCardDepOrWdlDTO;
import org.zj.atm.project.dto.req.DebitCardLoginReqDTO;
import org.zj.atm.project.dto.req.DebitCardRegisterReqDTO;
import org.zj.atm.project.dto.resp.CheckHeartBeatRespDTO;
import org.zj.atm.project.dto.resp.DebitCardDepOrWdlRecordDTO;
import org.zj.atm.project.dto.resp.DebitCardLoginRespDTO;
import org.zj.atm.project.dto.resp.DebitCardRegisterRespDTO;

public interface DebitCardService extends IService<DebitCardDO> {

    /**
     * 开户接口
     * @param requestParam 开户请求参数
     * @return 开户返回实体
     */
    DebitCardRegisterRespDTO register(DebitCardRegisterReqDTO requestParam);

    /**
     * 用户使用银行卡登录
     * @param requestParam 账号密码
     * @return 账户信息
     */
    DebitCardLoginRespDTO login(DebitCardLoginReqDTO requestParam);

    /**
     * 心跳检查接口
     * @return 全新的 token 令牌
     */
    CheckHeartBeatRespDTO checkHeartBeat();

    /**
     * 存取款操作接口
     * @param requestParam 存取款参数
     */
    DebitCardDepOrWdlRecordDTO depositOrWithdrawal(DebitCardDepOrWdlDTO requestParam);
}
