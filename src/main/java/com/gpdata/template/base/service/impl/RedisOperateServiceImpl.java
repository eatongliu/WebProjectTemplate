package com.gpdata.template.base.service.impl;

import com.gpdata.template.base.cache.RedisCache;
import com.gpdata.template.base.common.utils.AESCoder;
import com.gpdata.template.base.service.RedisOperateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class RedisOperateServiceImpl implements RedisOperateService {

    public static final String DEFUALT_TOKEN_PREFIX = "trade:token:";
    public static final String ONLINE_USERS_KEY = "trade:onlineusers:";
    public static final String DEFUALT_TOKEN_VALUE = "1";
    public static final int DEFUALT_TOKEN_EXPIRE_SECONDS = 36000;
    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private RedisCache redisCache;

    @Resource
    public void setRedisCache(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    /*
     * (non-Javadoc)
     * @see com.gpdata.trade.base.service.RedisOperateService#createRedisToken(java.lang.String, int)
     */
    @Override
    public String createRedisToken(String prefix, int expireSeconds) {

        String key = UUID.randomUUID().toString();
        try (ShardedJedis jedis = this.redisCache.getShardedJedis()) {
            jedis.setex(prefix + key, expireSeconds, DEFUALT_TOKEN_VALUE);
            return key;
        } catch (JedisConnectionException e) {
            LOGGER.error("Exception : {}", e);
            throw new RuntimeException(e);
        }

    }

    /*
     * (non-Javadoc)
     * @see com.gpdata.trade.base.service.RedisOperateService#createRedisToken(int)
     */
    @Override
    public String createRedisToken(int expireSeconds) {
        return this.createRedisToken(DEFUALT_TOKEN_PREFIX, expireSeconds);
    }

    /*
     * (non-Javadoc)
     * @see com.gpdata.trade.base.service.RedisOperateService#createRedisToken()
     */
    @Override
    public String createRedisToken() {
        return this.createRedisToken(DEFUALT_TOKEN_EXPIRE_SECONDS);
    }

    /*
     * (non-Javadoc)
     * @see com.gpdata.trade.base.service.RedisOperateService#hasRedisToken(java.lang.String, java.lang.String)
     */
    @Override
    public boolean hasRedisToken(String prefix, String key) {

        Long effectual = this.redisCache.getShard().del(prefix + key);
        return effectual.intValue() == 1;
    }

    /*
     * (non-Javadoc)
     * @see com.gpdata.trade.base.service.RedisOperateService#hasRedisToken(java.lang.String)
     */
    @Override
    public boolean hasRedisToken(String key) {
        return this.hasRedisToken(DEFUALT_TOKEN_PREFIX, key);
    }

    @Override
    public void initOnlineUsers() {

        this.redisCache.getShard().set(ONLINE_USERS_KEY, "0");

    }

    @Override
    public long updateOnlineUsers(int v) {

        long result = 0;

        if (v >= 0) {
            result = this.redisCache.getShard().incr(ONLINE_USERS_KEY);
        } else {
            result = this.redisCache.getShard().decr(ONLINE_USERS_KEY);
        }
        return result;
    }

    @Override
    public void sendVFCodeToRds(String rediskey, String regcode, String SEED) {
        try {
            redisCache.set(rediskey, AESCoder.encryptStringToBase64(regcode, SEED), 60);
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }
    }

    @Override
    public String rcvVFCodeFromRds(String rediskey) {
        String regcode = "";
        try {
            regcode = redisCache.getString(rediskey);
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }
        return regcode;
    }


    @Override
    public String setValue(String key, String value) {

        String result = null;
        try (ShardedJedis jedis = this.redisCache.getShardedJedis()) {
            result = jedis.set(key, value);
        } catch (JedisConnectionException e) {
            LOGGER.error("Exception : {}", e);
        }
        return result;
    }

    @Override
    public String getValue(String key) {

        String result = null;
        try (ShardedJedis jedis = this.redisCache.getShardedJedis()) {
            result = jedis.get(key);
        } catch (JedisConnectionException e) {
            LOGGER.error("Exception : {}", e);
        }
        return result;

    }

    @Override
    public Long delete(String key) {

        Long effect = Long.valueOf(0L);
        try (ShardedJedis jedis = this.redisCache.getShardedJedis()) {
            effect = jedis.del(key);
        } catch (JedisConnectionException e) {
            LOGGER.error("Exception : {}", e);
        }

        return effect;
    }

    @Override
    public Set<String> keys(String keysPattern) {
        Set<String> result = null;
        try (Jedis jedis = this.redisCache.getShard()) {
            result = jedis.keys(keysPattern);
        } catch (JedisConnectionException e) {
            LOGGER.error("Exception : {}", e);
        }

        return result != null ? result : new HashSet<>();
    }

    /*
     * (non-Javadoc)
     * @see com.gpdata.trade.base.service.RedisOperateService#saveVisitCountTrace(java.lang.String, java.lang.String)
     */
    @Override
    public void saveVisitCountTrace(String path, String remoteIp) {
        String key = path + ":count:" + LocalDate.now();
        try (ShardedJedis jedis = this.redisCache.getShardedJedis()) {
            jedis.incr(key);
        } catch (JedisConnectionException e) {
            LOGGER.error("Exception : {}", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.gpdata.trade.base.service.RedisOperateService#getVisitCountTrace(java.lang.String, java.lang.String)
     */
    @Override
    public Long getVisitCountTrace(String path, String dateString) {

        String key = path + ":count:" + dateString;
        Long result = Long.valueOf(0L);

        try (ShardedJedis jedis = this.redisCache.getShardedJedis()) {
            String value = jedis.get(key);
            result = Long.valueOf(value, 10);
        } catch (JedisConnectionException e) {
            LOGGER.error("Exception : {}", e.getMessage());
        } catch (NumberFormatException e) {
            LOGGER.error("Exception : {}", e.getMessage());
        }
        return result;

    }
}
