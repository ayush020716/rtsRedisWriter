package org.example.redis;

import redis.clients.jedis.Jedis;

public class RedisConfig {
    private final String hostname = "localhost"; // Redis server hostname
    private final int port = 6379; // Redis server port number
    private Jedis jedis;
    public RedisConfig(){
        this.jedis = new Jedis(hostname, port);
    }
    public Jedis getJedis(){
        return this.jedis;
    }
}
