package org.example;

import org.example.benchmark.Benchmark;
import org.example.benchmark.PcmToUlaw;
import org.example.redis.RedisConfig;
import redis.clients.jedis.Jedis;

public class Main {
    public static void main(String[] args) throws Exception {
        RedisConfig redisConfig = new RedisConfig();
        Jedis jedis = redisConfig.getJedis();
        jedis.set("foo", "bar");
        String value = jedis.get("foo");
        System.out.println(value);
        jedis.close();
        Benchmark.runBenchmark(new PcmToUlaw(), 20);
    }
}