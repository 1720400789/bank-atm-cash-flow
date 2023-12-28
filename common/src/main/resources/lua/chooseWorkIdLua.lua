local hashKey = 'snowflake_work_id_key'
local dataCenterIdKey = 'dataCenterId'
local workIdKey = 'workId'

-- 判断redis中是否存在hashKey，如果不存在就说明分布式雪花算法Id还没有开启的，我们就开启一下
if (redis.call('exists', hashKey) == 0) then
    -- 创建hashKey
    redis.call('hincrby', hashKey, dataCenterIdKey, 0)
    redis.call('hincrby', hashKey, workIdKey, 0)
    -- 直接退出
    return { 0, 0 }
end

-- 如果hashKey存在，则取出上一次的标识位（workId + dataCenterId）
local dataCenterId = tonumber(redis.call('hget', hashKey, dataCenterIdKey))
local workId = tonumber(redis.call('hget', hashKey, workIdKey))

local max = 31
local resultWorkId = 0
local resultDataCenterId = 0

-- 判断dataCenterId、workerId 是否达到上限了
if (dataCenterId == max and workId == max) then
    -- 达到上限了则赋值默认为0
    redis.call('hset', hashKey, dataCenterIdKey, '0')
    redis.call('hset', hashKey, workIdKey, '0')
-- 否则判断workId是否 没有 达到上限
elseif (workId ~= max) then
    -- workId自增
    resultWorkId = redis.call('hincrby', hashKey, workIdKey, 1)
    resultDataCenterId = dataCenterId
-- 判断dataCenterId是否 没有 达到上限
elseif (dataCenterId ~= max) then
    -- 自增dataCenterId并设置workerId为0
    resultWorkId = 0
    resultDataCenterId = redis.call('hincrby', hashKey, dataCenterIdKey, 1)
    redis.call('hset', hashKey, workIdKey, '0')

end

return { resultWorkId, resultDataCenterId }
