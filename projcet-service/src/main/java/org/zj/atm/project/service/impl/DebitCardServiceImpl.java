package org.zj.atm.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zj.atm.framework.starter.convention.exception.ServiceException;
import org.zj.atm.framework.starter.designpattern.chain.AbstractChainContext;
import org.zj.atm.framework.starter.designpattern.chain.AbstractChainHandler;
import org.zj.atm.project.dao.enrity.DebitCardDO;
import org.zj.atm.project.dao.enrity.DebitCardGotoDO;
import org.zj.atm.project.dao.mapper.DebitCardGotoMapper;
import org.zj.atm.project.dao.mapper.DebitCardMapper;
import org.zj.atm.project.dto.req.DebitCardLoginReqDTO;
import org.zj.atm.project.dto.req.DebitCardRegisterReqDTO;
import org.zj.atm.project.dto.resp.DebitCardLoginRespDTO;
import org.zj.atm.project.dto.resp.DebitCardRegisterRespDTO;
import org.zj.atm.project.remote.UserRemoteService;
import org.zj.atm.project.remote.dto.req.UserRegisterReqDTO;
import org.zj.atm.project.service.DebitCardService;
import org.zj.atm.project.toolkit.IOS15DebitCardCreate;

import java.math.BigDecimal;

import static org.zj.atm.project.common.enums.DebitCardErrorCodeEnum.DEBIT_CARD_NUM_BEYOND_UPPER_LIMIT;
import static org.zj.atm.project.common.enums.DebitChainMarkEnum.DEBIT_CARD_REGISTER_FILTER;

/**
 * 银行卡接口实现层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DebitCardServiceImpl extends ServiceImpl<DebitCardMapper, DebitCardDO> implements DebitCardService {

    @Value("${atm.debit_card.bank_card_prefix}")
    private String debitCardPrefix;

    @Value("${atm.debit_card.card_num_single_man}")
    private Integer cardNumUpperLimit;

    @Value("${atm.debit_card.rate}")
    private BigDecimal rate;

    private final UserRemoteService userRemoteService;

    private final DebitCardMapper debitCardMapper;

    private final DebitCardGotoMapper debitCardGotoMapper;

    private final StringRedisTemplate stringRedisTemplate;

    private final RedissonClient redissonClient;

    private final AbstractChainContext<DebitCardRegisterReqDTO> abstractChainContext;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public DebitCardRegisterRespDTO register(DebitCardRegisterReqDTO requestParam) {
        // 校验参数
        abstractChainContext.handler(DEBIT_CARD_REGISTER_FILTER.name(), requestParam);
        // 校验该用户是否还有资格申请
        // 检查用户
        // TODO 如果用户反复申请又注销呢?
        // TODO 再建立一个job服务,用来判断用户是否长时间未操作以及其它定时任务
        // 查询 debit_card 表中对应身份证且未注销的
        LambdaQueryWrapper<DebitCardDO> queryWrapper = Wrappers.lambdaQuery(DebitCardDO.class)
                .eq(DebitCardDO::getIdentityId, requestParam.getIdentityId());
        Long cardNum = debitCardMapper.selectCount(queryWrapper);
        if (cardNum >= cardNumUpperLimit) {
            throw new ServiceException(DEBIT_CARD_NUM_BEYOND_UPPER_LIMIT);
        }
        // 分配银行卡号
        String debitCardId = IOS15DebitCardCreate.createDebitCardId(debitCardPrefix, stringRedisTemplate, redissonClient);
        // 银行卡表
        DebitCardDO debitCardDO = DebitCardDO.builder()
                .debitCardId(debitCardId)
                .pwd(requestParam.getPwd())
                .identityId(requestParam.getIdentityId())
                .phone(requestParam.getPhone())
                .initialBalance(requestParam.getInitailBalance())
                .accountBalance(requestParam.getInitailBalance())
                .cardType(requestParam.getCardType())
                .rate(rate)
                .cardStatus(0)
                .build();
        debitCardMapper.insert(debitCardDO);
        // 路由表
        DebitCardGotoDO debitCardGotoDO = DebitCardGotoDO.builder()
                .debitCardId(debitCardId)
                .identityId(requestParam.getIdentityId())
                .build();
        debitCardGotoMapper.insert(debitCardGotoDO);
        // 如果用户第一次办卡,即在 User 表中无记录,则还需要在 User 表中记录
        if (cardNum == 0) {
            UserRegisterReqDTO userRegisterReqDTO = UserRegisterReqDTO.builder()
                    .identityId(requestParam.getIdentityId())
                    .realName(requestParam.getRealName())
                    .address(requestParam.getAddress())
                    .build();
            userRemoteService.register(userRegisterReqDTO);
        }
        // 返回数据集
        DebitCardRegisterRespDTO debitCardRegisterRespDTO = DebitCardRegisterRespDTO.builder()
                .debitCardId(debitCardId)
                .realName(requestParam.getRealName())
                .phone(requestParam.getPhone())
                .identityId(requestParam.getIdentityId())
                .currentBalance(requestParam.getInitailBalance())
                .build();

        return debitCardRegisterRespDTO;
    }

    @Override
    public DebitCardLoginRespDTO login(DebitCardLoginReqDTO requestParam) {


        return null;
    }
}
