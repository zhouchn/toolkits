-- 原子性解锁脚本
-- KEYS[1]: 锁的 key
-- ARGV[1]: 锁的 value (用于验证是否是当前线程的锁)

if redis.call('get', KEYS[1]) == ARGV[1] then
  return redis.call('del', KEYS[1])
else
  return 0
end