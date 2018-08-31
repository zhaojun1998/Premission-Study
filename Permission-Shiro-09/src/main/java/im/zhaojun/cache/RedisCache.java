package im.zhaojun.cache;

import im.zhaojun.util.JedisUtil;
import org.apache.log4j.Logger;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Set;

@Component
public class RedisCache<K, V> implements Cache<K, V> {

    private static final Logger logger = Logger.getLogger(RedisCache.class);

    @Resource
    private JedisUtil jedisUtil;

    private final String CACHE_PREFIX = "shiro-cache:";

    private byte[] getKeyBytes(K k) {
        return (CACHE_PREFIX + k).getBytes();
    }

    @Override
    public V get(K k) throws CacheException {
        logger.info("从 Redis 中读取授权信息...");
        byte[] key = getKeyBytes(k);
        byte[] value = jedisUtil.get(key);
        if (value != null) {
            return (V) SerializationUtils.deserialize(value);
        }
        return null;
    }

    @Override
    public V put(K k, V v) throws CacheException {
        byte[] key = getKeyBytes(k);
        byte[] value = SerializationUtils.serialize(v);
        jedisUtil.set(key, value);
        jedisUtil.expire(key, 600);
        return v;
    }

    @Override
    public V remove(K k) throws CacheException {
        byte[] key = getKeyBytes(k);
        byte[] value = jedisUtil.get(key);
        jedisUtil.del(key);

        if (value != null) {
            SerializationUtils.deserialize(value);
        }
        return null;
    }

    @Override
    public void clear() throws CacheException {
        jedisUtil.delKeysByPrefix(CACHE_PREFIX);
    }

    @Override
    public int size() {
        return jedisUtil.getKeysByPrefix(CACHE_PREFIX).size();
    }

    @Override
    public Set<K> keys() {
        return (Set<K>) jedisUtil.getKeysByPrefix(CACHE_PREFIX);
    }

    @Override
    public Collection<V> values() {
        return jedisUtil.getValuesByPrefix(CACHE_PREFIX);
    }
}