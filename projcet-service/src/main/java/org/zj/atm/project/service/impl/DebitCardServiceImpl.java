package org.zj.atm.project.service.impl;

import com.alibaba.fastjson2.JSON;
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
import org.zj.atm.framework.starter.biz.user.core.UserContext;
import org.zj.atm.framework.starter.biz.user.core.UserInfoDTO;
import org.zj.atm.framework.starter.biz.user.toolkit.JWTUtil;
import org.zj.atm.framework.starter.convention.exception.RemoteException;
import org.zj.atm.framework.starter.convention.exception.ServiceException;
import org.zj.atm.framework.starter.convention.result.Result;
import org.zj.atm.framework.starter.designpattern.chain.AbstractChainContext;
import org.zj.atm.project.dao.enrity.DebitCardDO;
import org.zj.atm.project.dao.enrity.DebitCardGotoDO;
import org.zj.atm.project.dao.enrity.IdToDebitCardDO;
import org.zj.atm.project.dao.mapper.DebitCardGotoMapper;
import org.zj.atm.project.dao.mapper.DebitCardMapper;
import org.zj.atm.project.dao.mapper.IdToDebitCardMapper;
import org.zj.atm.project.dto.req.DebitCardLoginReqDTO;
import org.zj.atm.project.dto.req.DebitCardRegisterReqDTO;
import org.zj.atm.project.dto.resp.*;
import org.zj.atm.project.remote.UserRemoteService;
import org.zj.atm.project.remote.dto.req.UserRegisterReqDTO;
import org.zj.atm.project.remote.dto.resp.UserActualMsgDTO;
import org.zj.atm.project.remote.dto.resp.UserAnonymizedMsg;
import org.zj.atm.project.service.DebitCardService;
import org.zj.atm.project.toolkit.IOS15DebitCardCreate;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.zj.atm.project.common.constant.RedisCacheConstant.LOGIN_CACHE_KEY;
import static org.zj.atm.project.common.constant.RedisCacheConstant.LOGIN_FAIL_COUNT;
import static org.zj.atm.project.common.enums.DebitCardErrorCodeEnum.*;
import static org.zj.atm.project.common.enums.DebitChainMarkEnum.DEBIT_CARD_LOGIN_FILTER;
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

    @Value("${atm.login.expiry_time}")
    private Long expiryTime;

    @Value("${atm.login.if_fail.freeze_time}")
    private Long freezeTime;

    private final UserRemoteService userRemoteService;

    private final DebitCardMapper debitCardMapper;

    private final DebitCardGotoMapper debitCardGotoMapper;

    private final IdToDebitCardMapper idToDebitCardMapper;

    private final StringRedisTemplate stringRedisTemplate;

    private final RedissonClient redissonClient;

    private final AbstractChainContext<DebitCardRegisterReqDTO> abstractChainContext1;

    private final AbstractChainContext<DebitCardLoginReqDTO> abstractChainContext2;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public DebitCardRegisterRespDTO register(DebitCardRegisterReqDTO requestParam) {
        // 校验参数
        abstractChainContext1.handler(DEBIT_CARD_REGISTER_FILTER.name(), requestParam);
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
            try {
                Result<Void> result = userRemoteService.register(userRegisterReqDTO);
                if (!result.isSuccess()) {
                    throw new RemoteException("用户服务远程调用，新增用户记录失败");
                }
            } catch (Throwable exception) {
                throw new ServiceException("远程调用注册用户接口失败");
            }
        }
        LambdaQueryWrapper<DebitCardDO> wrapper = Wrappers.lambdaQuery(DebitCardDO.class)
                .eq(DebitCardDO::getDebitCardId, debitCardId)
                .eq(DebitCardDO::getCardType, 0);
        DebitCardDO debitCardDO1 = debitCardMapper.selectOne(wrapper);
        IdToDebitCardDO cardDO = IdToDebitCardDO.builder()
                .cardId(debitCardDO1.getId())
                .debitCardId(debitCardId)
                .build();
        idToDebitCardMapper.insert(cardDO);
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

    /**
     * import http from './axios'
     *
     * <p>前端 login 请求拿到响应后将 data 中的 accessToken 设置进 http 变量中，此后所有请求都用这个共同的 http 发送</p>
     * const fetchLogin = async (body) => {
     *   const { data } = await http({
     *     method: 'POST',
     *     url: '/api/user-service/v1/login',
     *     data: body
     *   })
     *   http.defaults.headers.common['Authorization'] = data.data?.accessToken
     *   return data
     * }
     *
     * const fetchRegister = async (body) => {
     *   const { data } = await http({
     *     method: 'POST',
     *     url: '/api/user-service/register',
     *     data: body
     *   })
     *   return data
     * }
     */
    @Override
    public DebitCardLoginRespDTO login(DebitCardLoginReqDTO requestParam) {
        // 银行卡登录参数校验
        abstractChainContext2.handler(DEBIT_CARD_LOGIN_FILTER.name(), requestParam);

        // TODO jwt 令牌通过数据实体响应给前端，然后前端所有的请求都通过一个共享的 http 变量发送，这样就设置好请求头了
        // 先查路由表
        LambdaQueryWrapper<DebitCardGotoDO> wrapper = Wrappers.lambdaQuery(DebitCardGotoDO.class)
                .eq(DebitCardGotoDO::getDebitCardId, requestParam.getDebitCardId());
        DebitCardGotoDO debitCardGotoDO = debitCardGotoMapper.selectOne(wrapper);
        // 如果根据银行卡号查询路由表没有记录，则说明非本行银行卡，直接抛出系统异常
        if (debitCardGotoDO == null) {
            throw new ServiceException(DEBIT_CARD_REGIX_ERROR);
        }
        // 结合 identityId 去查，防止读扩散
        LambdaQueryWrapper<DebitCardDO> queryWrapper = Wrappers.lambdaQuery(DebitCardDO.class)
                .eq(DebitCardDO::getIdentityId, debitCardGotoDO.getIdentityId())
                .eq(DebitCardDO::getDebitCardId, requestParam.getDebitCardId())
                .eq(DebitCardDO::getPwd, requestParam.getPwd());
        DebitCardDO debitCardDO = debitCardMapper.selectOne(queryWrapper);
        // 查询不到，则说明是本行银行卡，但是密码错误，要记录错误次数
        // TODO 这里考虑使用银行卡号对应主键作为键，先这么做，因为担心用银行卡号作为键不好
        // 使用 lua 脚本来保证【取值】、【判断值】和【自增值】的原子性
        stringRedisTemplate.opsForValue().set(String.format(LOGIN_FAIL_COUNT, ));
        // 如果查询得到但是被冻结了
        if (debitCardDO != null && debitCardDO.getCardStatus() == 1) {
            throw new ServiceException(DEBIT_CARD_FREEZED);
        }

        Result<UserAnonymizedMsg> result;
        UserAnonymizedMsg userAnonymizedMsg = null;
        // 再查一次用户表，拿到 realName
        try {
            result = userRemoteService.getAnonymizedMsg(debitCardDO.getIdentityId());
            if (!result.isSuccess() || Objects.isNull(userAnonymizedMsg = result.getData())) {
                throw new RemoteException("用户服务远程调用，根据身份证号获取用户脱敏失败");
            }
        } catch (Throwable ex) {
            log.error("远程调用用户接口查询脱敏用户信息错误: 请求参数:{}", debitCardDO.getDebitCardId());
            throw new ServiceException(ex.getMessage());
        }

        // 如果信息正确
        if (debitCardDO != null) {
            UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                    .userId(userAnonymizedMsg.getUserId())
                    .realName(userAnonymizedMsg.getRealName())
                    .cardId(debitCardDO.getId())
                    .build();
            // 生成 Token
            String accessToken = JWTUtil.generateAccessToken(userInfoDTO);
            DebitCardLoginRespDTO actualRespDTO = DebitCardLoginRespDTO.builder()
                    .debitCardId(debitCardDO.getDebitCardId())
                    .identityId(debitCardGotoDO.getIdentityId())
                    .realName(userAnonymizedMsg.getRealName())
                    // token 信息包含了用户插卡时的userId和银行卡id，不把其它敏感信息给 token，防止泄露
                    .accessToken(accessToken)
                    .build();

            // 将用户 token 存入分布式缓存，hash表名是银行卡表的主键而不是银行卡号，保证唯一的同时保证敏感信息不外泄, 键是 token，值是序列化的返回对象
            stringRedisTemplate.opsForHash().put(String.format(LOGIN_CACHE_KEY, debitCardDO.getId()), accessToken, JSON.toJSONString(actualRespDTO));
            // 设置缓存过期时间为 10 分钟
            // TODO 如果要修改这里的过期时间，记得去 gateway 和 common 服务里面的 JWTUtil 也改一下，保证缓存和token过期时间是差不多的
            stringRedisTemplate.expire(String.format(LOGIN_CACHE_KEY, debitCardDO.getId()), expiryTime, TimeUnit.SECONDS);
            return actualRespDTO;
        }
        throw new ServiceException("非本行或密码错误");
    }

    /**
     * 检查前端心跳
     * @return 新的重计时的 Token
     */
    @Override
    public CheckHeartBeatRespDTO checkHeartBeat() {
        String token = UserContext.getToken();
        if (Objects.isNull(token)) {
            throw new ServiceException("恶意请求，请先插卡登录");
        }
        // 旧 Token
        UserInfoDTO oldTokenInfo = JWTUtil.parseJwtToken(token);
        // 将旧 Token 中的用户信息放入 UserInfoDTO ，准备更新 Token 有效期
        UserInfoDTO nestestUserInfoDTO = UserInfoDTO.builder()
                .userId(oldTokenInfo.getUserId())
                .realName(oldTokenInfo.getRealName())
                .cardId(oldTokenInfo.getCardId())
                .build();
        // 拿到更新有效期后的 token
        String accessToken = JWTUtil.generateAccessToken(nestestUserInfoDTO);
        DebitCardActualMsgDTO debitCardActualMsgDTO = getActualDebitCard(nestestUserInfoDTO.getCardId());
        // 远程调用拿到身份证号码
        Result<UserActualMsgDTO> result;
        UserActualMsgDTO userActualMsgDTO = null;
        try {
            result = userRemoteService.getActualMsgById(oldTokenInfo.getUserId());
            if (!result.isSuccess() || Objects.isNull(userActualMsgDTO = result.getData())) {
                throw new RemoteException("用户服务远程调用,根据主键获取用户信息失败");
            }
        } catch (Throwable ex) {
            log.error("远程调用用户接口查询用户信息错误: 请求参数: {}", oldTokenInfo.getUserId());
            throw new ServiceException(ex.getMessage());
        }

        CheckHeartBeatRespDTO actualRespDTO = CheckHeartBeatRespDTO.builder()
                .debitCardId(debitCardActualMsgDTO.getDebitCardId())
                .identityId(userActualMsgDTO.getIdentityId())
                .accessToken(accessToken)
                .build();
        // 将用户 token 存入分布式缓存，hash表名是银行卡表的主键而不是银行卡号，保证唯一的同时保证敏感信息不外泄, 键是 token，值是序列化的返回对象
        stringRedisTemplate.opsForHash().put(String.format(LOGIN_CACHE_KEY, oldTokenInfo.getCardId()), accessToken, JSON.toJSONString(actualRespDTO));
        // 设置缓存过期时间为 10 分钟
        // TODO 如果要修改这里的过期时间，记得去 gateway 和 common 服务里面的 JWTUtil 也改一下，保证缓存和token过期时间是差不多的
        stringRedisTemplate.expire(String.format(LOGIN_CACHE_KEY, oldTokenInfo.getCardId()), expiryTime, TimeUnit.SECONDS);
        return actualRespDTO;
    }

    /**
     * 通过银行卡号记录的主键拿到银行卡记录的脱敏信息
     * @param cardId 银行卡号对应主键
     * @return 脱敏的银行卡记录信息
     */
    private DebitCardActualMsgDTO getActualDebitCard(Long cardId) {
        if (Objects.isNull(cardId)) {
            throw new ServiceException("主键不能为空");
        }
        LambdaQueryWrapper<IdToDebitCardDO> queryWrapper = Wrappers.lambdaQuery(IdToDebitCardDO.class)
                .eq(IdToDebitCardDO::getCardId, cardId);
        IdToDebitCardDO idToDebitCardDO = idToDebitCardMapper.selectOne(queryWrapper);
        LambdaQueryWrapper<DebitCardDO> wrapper = Wrappers.lambdaQuery(DebitCardDO.class)
                .eq(DebitCardDO::getDebitCardId, idToDebitCardDO.getDebitCardId())
                .eq(DebitCardDO::getCardStatus, 0);
        DebitCardDO debitCardDO = debitCardMapper.selectOne(wrapper);
        return DebitCardActualMsgDTO.builder()
                .debitCardId(debitCardDO.getDebitCardId())
                .pwd(debitCardDO.getPwd())
                .phone(debitCardDO.getPhone())
                .cardType(debitCardDO.getCardType())
                .initalBalance(debitCardDO.getInitialBalance())
                .accountBalance(debitCardDO.getAccountBalance())
                .build();
    }
}
