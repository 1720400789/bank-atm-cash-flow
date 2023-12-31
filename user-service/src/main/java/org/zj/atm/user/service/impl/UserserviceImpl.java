package org.zj.atm.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zj.atm.framework.starter.convention.exception.RemoteException;
import org.zj.atm.framework.starter.convention.exception.ServiceException;
import org.zj.atm.framework.starter.designpattern.chain.AbstractChainContext;
import org.zj.atm.user.common.enums.UserChainMarkEnum;
import org.zj.atm.user.dao.entity.UserDO;
import org.zj.atm.user.dao.mapper.UserMapper;
import org.zj.atm.user.dto.req.UserRegisterReqDTO;
import org.zj.atm.user.dto.resp.UserAnonymizedMsg;
import org.zj.atm.user.service.UserService;
import org.zj.atm.user.toolkit.HashUtil;

import java.util.Objects;

import static org.zj.atm.user.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static org.zj.atm.user.common.enums.UserRegisterErrorCodeEnum.*;

/**
 * 用户接口实现层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserserviceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> uidRegisterCachePenetrationBloomFilter;
    private final AbstractChainContext<UserRegisterReqDTO> abstractChainContext;
    private final RedissonClient redissonClient;
    private final UserMapper userMapper;

    /**
     * 用户注册实现
     * @param requestParam 用户注册请求实体类
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(UserRegisterReqDTO requestParam) {
        // 责任链校验请求参数合法性
        abstractChainContext.handler(UserChainMarkEnum.USER_REGISTER_FILTER.name(), requestParam);

        // 尝试对身份证号加锁
        RLock rLock = redissonClient.getLock(String.format(LOCK_USER_REGISTER_KEY, requestParam.getIdentityId()));
        boolean tryLock = rLock.tryLock();
        if (! tryLock) {
            // 加锁失败则说明该身份证号正在被其它人注册
            throw new RemoteException(ID_CARD_REGISTERED);
        }
        try {
            UserDO userDO = UserDO.builder()
                    .realName(requestParam.getRealName())
                    .identityId(requestParam.getIdentityId())
                    .address(requestParam.getAddress())
                    .build();
            try {
                userMapper.insert(userDO);
            } catch (DuplicateKeyException dex) {
                log.error("用户 [{}] 注册身份证 [{}] 重复", requestParam.getRealName(), requestParam.getIdentityId());
                throw new RemoteException(ID_CARD_REGISTERED);
            }
        } finally {
            rLock.unlock();
        }
    }

    @Override
    public UserAnonymizedMsg getAnonymized(String identityId) {
        if (Objects.isNull(identityId)) {
            throw new RemoteException(ID_CARD_NOTNULL);
        }
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getIdentityId, identityId);
        UserDO userDO = userMapper.selectOne(queryWrapper);
        // 如果查询不到，则说明用户未办过卡或用户已销户
        if (Objects.isNull(userDO)) {
            throw new RemoteException(USER_IS_NULL);
        }
        // 如果查询得到并且 freezeFlag == 1则说明用户已冻结
        if (userDO.getFreezeFlag() == 1) {
            throw new RemoteException(USER_FREEZED);
        }

        return UserAnonymizedMsg.builder()
                .userId(userDO.getId())
                .realName(userDO.getRealName())
                .identityId(userDO.getIdentityId())
                .address(userDO.getAddress())
                .phone(userDO.getPhone())
                .gender(userDO.getGender())
                .deletionTime(userDO.getDeletionTime())
                .freezeTime(userDO.getFreezeTime())
                .build();
    }

    /**
     * 根据身份证获得唯一的对应6位长度的uid
     * @param requestParam 参数中有iden_id
     * @return 返回短链接
     */
    private String generateSuffix(UserRegisterReqDTO requestParam) {
        return HashUtil.hashToBase62(requestParam.getIdentityId());
    }
}
