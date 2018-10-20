package im.zhaojun.util;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;

@Component
public class JedisUtil {

    @Resource
    private JedisPool jedisPool;

    private Jedis getResource() {
        return jedisPool.getResource();
    }

    public byte[] set(byte[] key, byte[] value) {
        Jedis jedis = getResource();
        try {
            jedis.set(key, value);
            return value;
        } finally {
            jedis.close();
        }
    }

    public void expire(byte[] key, int seconds) {
        Jedis jedis = getResource();
        try {
            jedis.expire(key, seconds);
        } finally {
            jedis.close();
        }
    }

    public byte[] get(byte[] key) {
        Jedis jedis = getResource();
        byte[] bytes = jedis.get(key);
        jedis.close();
        return bytes;
    }

    public void del(byte[] key) {
        Jedis jedis = getResource();
        try {
            jedis.del(key);
        } finally {
            jedis.close();
        }
    }

    public Collection<byte[]> getKeysByPrefix(String prefix) {
        Jedis jedis = getResource();
        try {
            return jedis.keys((prefix + "*").getBytes());
        } finally {
            jedis.close();
        }
    }

    public void delKeysByPrefix(String prefix) {
        Jedis jedis = getResource();
        try {
            Collection<byte[]> keys = getKeysByPrefix(prefix);
            for (byte[] bytes : keys) {
                jedis.del(bytes);
            }
        } finally {
            jedis.close();
        }
    }

    public <V> Collection<V> getValuesByPrefix(String prefix) {
        ArrayList<V> list = new ArrayList<>();
        Jedis jedis = getResource();
        try {
            Collection<byte[]> keys = getKeysByPrefix(prefix);
            for (byte[] bytes : keys) {
                list.add((V) jedis.get(bytes));
            }
        } finally {
            jedis.close();
        }
        return list;
    }
}