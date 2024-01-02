package org.zj.atm.project.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zj.atm.framework.starter.biz.user.core.UserContext;
import org.zj.atm.framework.starter.biz.user.core.UserInfoDTO;
import org.zj.atm.framework.starter.biz.user.toolkit.JWTUtil;
import org.zj.atm.framework.starter.convention.exception.ClientException;
import org.zj.atm.framework.starter.convention.exception.RemoteException;
import org.zj.atm.framework.starter.convention.exception.ServiceException;
import org.zj.atm.framework.starter.convention.result.Result;
import org.zj.atm.framework.starter.designpattern.chain.AbstractChainContext;
import org.zj.atm.project.dao.entity.DebitCardDO;
import org.zj.atm.project.dao.entity.DebitCardGotoDO;
import org.zj.atm.project.dao.entity.DepWdlRecordDO;
import org.zj.atm.project.dao.entity.IdToDebitCardDO;
import org.zj.atm.project.dao.mapper.DebitCardGotoMapper;
import org.zj.atm.project.dao.mapper.DebitCardMapper;
import org.zj.atm.project.dao.mapper.DepWdlRecordMapper;
import org.zj.atm.project.dao.mapper.IdToDebitCardMapper;
import org.zj.atm.project.dto.req.DebitCardDepOrWdlDTO;
import org.zj.atm.project.dto.req.DebitCardLoginReqDTO;
import org.zj.atm.project.dto.req.DebitCardRegisterReqDTO;
import org.zj.atm.project.dto.resp.*;
import org.zj.atm.project.remote.UserRemoteService;
import org.zj.atm.project.remote.dto.req.UserRegisterReqDTO;
import org.zj.atm.project.remote.dto.resp.UserActualMsgDTO;
import org.zj.atm.project.remote.dto.resp.UserAnonymizedMsg;
import org.zj.atm.project.service.DebitCardService;
import org.zj.atm.project.toolkit.IOS15DebitCardCreate;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.zj.atm.project.common.constant.RedisCacheConstant.*;
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

    @Value("${atm.login.if_fail.threshold}")
    private Long threshold;

    private static final String COUNT_LOGIN_FAIL_LUA = "lua/countLoginFailLua.lua";

    private final UserRemoteService userRemoteService;

    private final DebitCardMapper debitCardMapper;

    private final DebitCardGotoMapper debitCardGotoMapper;

    private final IdToDebitCardMapper idToDebitCardMapper;

    private final DepWdlRecordMapper depWdlRecordMapper;

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
        // 这一次根据身份证和银行卡号查，肯定能查到
        LambdaQueryWrapper<DebitCardDO> queryWrapper2 = Wrappers.lambdaQuery(DebitCardDO.class)
                .eq(DebitCardDO::getIdentityId, debitCardGotoDO.getIdentityId())
                .eq(DebitCardDO::getDebitCardId, requestParam.getDebitCardId());
        DebitCardDO debitCardDO2 = debitCardMapper.selectOne(queryWrapper2);
        if (debitCardDO2.getCardStatus() == 1) {
            throw new ServiceException(DEBIT_CARD_FREEZED);
        }
        // 结合 identityId 去查，防止读扩散
        // 这一次查 DebitDO 是判断密码对不对，不一定能查到，因为可能密码不对
        LambdaQueryWrapper<DebitCardDO> queryWrapper = Wrappers.lambdaQuery(DebitCardDO.class)
                .eq(DebitCardDO::getIdentityId, debitCardGotoDO.getIdentityId())
                .eq(DebitCardDO::getDebitCardId, requestParam.getDebitCardId())
                .eq(DebitCardDO::getPwd, requestParam.getPwd());
        DebitCardDO debitCardDO = debitCardMapper.selectOne(queryWrapper);
        if (Objects.isNull(debitCardDO)) {
            // 查询不到，则说明是本行银行卡，但是密码错误，要记录错误次数
            // TODO 这里考虑使用银行卡号对应主键作为键，先这么做，因为担心用银行卡号作为键不好
            // 使用 lua 脚本来保证【取值】、【判断值】和【自增值】的原子性
            // 承接 lua 脚本的对象
            DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
            // 设置脚本内容
            redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(COUNT_LOGIN_FAIL_LUA)));
            // 设置脚本返回值类型
            redisScript.setResultType(String.class);
            String res = null;
            try {
                // 执行脚本，execute 方法可以看一下源代码，三个参数
                // - 参数一：lua 脚本对象
                // - 参数二：keys 集合 这里将银行卡号的主键传入
                // - 参数三：可变参数 args 这里传入三个 args，arg[1]-阈值 arg[2]-时间 arg[3]-初始值
                res = stringRedisTemplate.execute(redisScript, List.of(String.format(LOGIN_FAIL_COUNT, debitCardDO2.getId())), threshold.toString(), freezeTime.toString(), "1");
            } catch (Throwable ex) {
                log.error("执行用户登录限制LUA脚本出错", ex);
                throw new ServiceException("识别失败，请稍后再试");
            }
            // 如果失败次数达到阈值，则冻结账号并抛出异常
            if (Long.parseLong(res) >= threshold) {
                LambdaUpdateWrapper<DebitCardDO> updateWrapper = Wrappers.lambdaUpdate(DebitCardDO.class)
                        .eq(DebitCardDO::getId, debitCardDO2.getId());
                DebitCardDO debitCardDO1 = DebitCardDO.builder()
                        .cardStatus(1)
                        .build();
                debitCardMapper.update(debitCardDO1, updateWrapper);
                throw new ServiceException(DEBIT_LOGIN_FAIL_UPPER);
            }
            throw new ServiceException(DEBIT_CARD_LOGIN_MATCH_PWD_FAIL);
        }
        // 先判断该卡是否重复登录了
        Map<Object, Object> hasLoginMap = stringRedisTemplate.opsForHash().entries(String.format(LOGIN_CACHE_KEY, debitCardDO.getId()));
        if (CollUtil.isNotEmpty(hasLoginMap)) {
            // 如果缓存存在，则说明还在登录中，则直接抛出异常
            throw new ServiceException(DEBIT_CARD_LOGIN_DUPLICATE);
        }
        // 如果没有重复登录
        // 如果查询到了但是被冻结了
        if (debitCardDO.getCardStatus() == 1) {
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

        // 能执行到这里就说明登录信息正确
        // 则清除记录登录失败次数的缓存
        stringRedisTemplate.delete(String.format(LOGIN_FAIL_COUNT, debitCardDO.getId()));
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
                .accountBalance(debitCardDO.getAccountBalance())
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

    private void returnJson(HttpServletResponse response, String json) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.print(json);
        }
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
     * 存取款接口实现
     * @param requestParam 存取款参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DebitCardDepOrWdlRecordDTO depositOrWithdrawal(DebitCardDepOrWdlDTO requestParam) {
        // 如果操作金额为空或操作金额不在限制内，则抛出异常
        if (Objects.isNull(requestParam.getOperationAmount()) ||
                requestParam.getOperationAmount().compareTo(new BigDecimal("100")) < 0 ||
                requestParam.getOperationAmount().compareTo(new BigDecimal("10000")) > 0 ||
                requestParam.getOperationAmount().divideAndRemainder(new BigDecimal("100"))[1].compareTo(BigDecimal.ZERO) != 0
        ) {
            throw new ClientException(OPERATION_AMOUNT_ERROR);
        }
        if (Objects.isNull(requestParam.getOperationType()) ||
                (requestParam.getOperationType() != 1 && requestParam.getOperationType() != 2)) {
            throw new ClientException("参数错误！");
        }

        // 用户表主键
        Long userId = UserContext.getUserId();
        Result<UserActualMsgDTO> result;
        UserActualMsgDTO userActualMsgDTO = null;
        try {
            result = userRemoteService.getActualMsgById(userId);
            if (!result.isSuccess() || Objects.isNull(userActualMsgDTO = result.getData())) {
                throw new RemoteException("用户服务远程调用，根据主键获取用户信息失败");
            }
        } catch (Throwable ex) {
            log.error("远程调用用户接口查询用户信息错误: 请求参数: {}", UserContext.getUserId());
            throw new ServiceException(ex.getMessage());
        }
        // 排他写锁
        // TODO 这里写银行卡表得加【写锁】，对应的，其它设计读银行卡表的业务记得添加【共享读锁】
        // 这里选用 redission 实现的读写锁
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(String.format(LOCK_DEBIT_READ_WRITE_KEY, UserContext.getCardId()));
        RLock rLock = readWriteLock.writeLock();
        if (!rLock.tryLock()) {
            // 如果获取写锁失败，则说明当前账户正在被查询
            throw new ServiceException("有人正在偷看当前账户。。。");
        }
        try {
            Date currentTime = new Date();
            LambdaQueryWrapper<DebitCardDO> queryWrapper = Wrappers.lambdaQuery(DebitCardDO.class)
                    .eq(DebitCardDO::getIdentityId, userActualMsgDTO.getIdentityId())
                    .eq(DebitCardDO::getId, UserContext.getCardId());
            DebitCardDO debitCardDO = debitCardMapper.selectOne(queryWrapper);
            LambdaUpdateWrapper<DebitCardDO> wrapper = Wrappers.lambdaUpdate(DebitCardDO.class)
                    .eq(DebitCardDO::getIdentityId, userActualMsgDTO.getIdentityId())
                    .eq(DebitCardDO::getId, UserContext.getCardId());
            DebitCardDO nextDebitCardDO = null;
            // 如果是存款操作
            if (requestParam.getOperationType() == 1) {
                if (Objects.isNull(debitCardDO.getAccountBalance())) {
                    debitCardDO.setAccountBalance(new BigDecimal("0"));
                }
                nextDebitCardDO = DebitCardDO.builder()
                        .accountBalance(debitCardDO.getAccountBalance().add(requestParam.getOperationAmount()))
                        .build();
            } else {
                if (requestParam.getOperationAmount().compareTo(debitCardDO.getAccountBalance()) > 0) {
                    throw new ClientException("你取钱取得太多了!");
                }
                // 如果是取款操作
                nextDebitCardDO = DebitCardDO.builder()
                        .accountBalance(debitCardDO.getAccountBalance().subtract(requestParam.getOperationAmount()))
                        .build();
            }
            int update = debitCardMapper.update(nextDebitCardDO, wrapper);
            if (update != 1) {
                throw new ServiceException("存款或取款服务出错！请稍后重试");
            }

            DepWdlRecordDO build = DepWdlRecordDO.builder()
                    .debitCardId(debitCardDO.getDebitCardId())
                    .identityId(debitCardDO.getIdentityId())
                    .operationType(requestParam.getOperationType())
                    .operationAmount(requestParam.getOperationAmount())
                    .operationTime(currentTime)
                    .build();
            depWdlRecordMapper.insert(build);

            LambdaQueryWrapper<DepWdlRecordDO> eq = Wrappers.lambdaQuery(DepWdlRecordDO.class)
                    .eq(DepWdlRecordDO::getDebitCardId, debitCardDO.getDebitCardId())
                    .orderByDesc(DepWdlRecordDO::getOperationTime);
            DepWdlRecordDO depWdlRecordDO = depWdlRecordMapper.selectList(eq).get(0);

            return DebitCardDepOrWdlRecordDTO.builder()
                    .id(depWdlRecordDO.getId())
                    .DebitCardId(debitCardDO.getDebitCardId())
                    .identityId(debitCardDO.getIdentityId())
                    .realName(userActualMsgDTO.getRealName())
                    .operationAmount(requestParam.getOperationAmount())
                    .operationTime(currentTime)
                    .build();
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 检查 token 是否过期
     * @return 是否过期
     */
    private boolean checkTokenExpiraTime() {
        UserInfoDTO userInfoDTO = JWTUtil.parseJwtToken(UserContext.getToken());
        return !Objects.isNull(userInfoDTO);
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
