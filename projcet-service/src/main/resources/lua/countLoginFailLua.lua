-- 尝试根据键拿值
local currentValue = redis.call('GET', KEYS[1])
-- 阈值
local threshold = tonumber(ARGV[1])
-- 过期时间是 ARGV[2] 初始值是 ARGV[3]
-- 如果值存在则自增
if currentValue then
    -- 自增 1
    redis.call('INCR', KEYS[1])
    redis.call('EXPIRE', KEYS[1], ARGV[2])
else
    redis.call('SET', KEYS[1], ARGV[3])
    redis.call('EXPIRE', KEYS[1], ARGV[2])
end
return redis.call('GET', KEYS[1])