package org.example;

import org.example.benchmark.Benchmark;
import org.example.benchmark.PcmToUlaw;
import org.example.benchmark.RedisWriteAudio;
import org.example.redis.RedisConfig;
import org.example.utils.AudioReader;
import redis.clients.jedis.Jedis;

import javax.sound.sampled.AudioInputStream;

public class Main {
    public static void main(String[] args) throws Exception {
//        Benchmark.runBenchmark(new PcmToUlaw(), 20);
//        Benchmark.runBenchmark(new RedisWriteAudio(), 20);
        AudioReader audioReader = new AudioReader();
        AudioInputStream ais = audioReader.readAllAudioSample().get(0);
        audioReader.saveToOutput(ais, "inpcm.wav");
        //convert to ulaw audio stream
        AudioInputStream ulaw = audioReader.pcmToUlaw(audioReader.readAllAudioSample().get(0));
        audioReader.saveToOutput(ulaw, "inulaw.wav");
        Thread.sleep(2000);
    }
}