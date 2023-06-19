package org.example.benchmark;

import org.example.redis.RedisConfig;
import redis.clients.jedis.Jedis;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.List;

public class RedisWriteAudio extends Benchmark{
    private final String audioId = "audioId";
    private final Jedis jedis = (new RedisConfig()).getJedis();
    private final int iterations = 20;
    private List<List<List<String>>> audioData;
    private List<Integer> averageWriteTimeForChunk;
    @Override
    public void exemptFromBenchmark() throws UnsupportedAudioFileException, IOException {
        // conversion to string will be done here for 114 samples
        // segmented into 3000 ms packets

    }

    @Override
    public void execute() throws Exception {
        // For each 3000 ms packet, write will be executed
        // time will be benchmarked for each iteration

    }

    @Override
    public void collect() throws Exception {

    }
}
