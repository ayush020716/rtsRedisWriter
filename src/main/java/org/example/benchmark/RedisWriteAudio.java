package org.example.benchmark;

import org.example.redis.RedisConfig;
import org.example.redis.models.AudioFormat;
import org.example.utils.AudioReader;
import redis.clients.jedis.Jedis;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.lang.System.*;

public class RedisWriteAudio extends Benchmark {
    private final AudioFormat WRITE_ENCODING = AudioFormat.MULAW;
    private final String redisKey = "audioId";
    private final AudioReader audioReader = new AudioReader();
    private final Jedis jedis = (new RedisConfig()).getJedis();
    private List<List<byte[]>> audioData = new ArrayList<>();
    private final List<Long> averageChunkWriteTimeForSample = new ArrayList<>();
    private final List<Long> spaceConsumed = new ArrayList<>();
    public RedisWriteAudio() {
        Jedis jedis = (new RedisConfig()).getJedis();
        jedis.flushAll();
        System.out.println(jedis.memoryStats());
    }
    @Override
    public void exemptFromBenchmark() throws UnsupportedAudioFileException, IOException {
        jedis.flushAll();
        // conversion to byte[] will be done here for 114 samples
        List<AudioInputStream> inputSamples = audioReader.readAllAudioSample();
        if (WRITE_ENCODING == AudioFormat.MULAW) {
            List<AudioInputStream> mulawSamples = new ArrayList<>();
            for (AudioInputStream ais : inputSamples) {
                mulawSamples.add(audioReader.pcmToUlaw(ais));
            }
            inputSamples = mulawSamples;
        }
        List<byte[]> inputSamplesBytes = new ArrayList<>();
        for(AudioInputStream ais: inputSamples){
            inputSamplesBytes.add(audioReader.audioInputStreamToByteArray(ais));
        }
        List<List<byte[]>> inputSamplesChunks = new ArrayList<>();
        for(byte[] byteAudio: inputSamplesBytes){
            inputSamplesChunks.add(
                    audioReader.segmentByteArrayInChunks(
                            byteAudio,
                            (WRITE_ENCODING==AudioFormat.PCM) ? AudioReader.PCM_AUDIO_FORMAT : AudioReader.ULAW_AUDIO_FORMAT,
                            AudioReader.SEGMENT_DURATION)
            );
        }
        audioData = inputSamplesChunks;
        jedis.set(UUID.randomUUID().toString(), UUID.randomUUID().toString());
    }

    @Override
    public void execute() throws UnsupportedAudioFileException, IOException {
        exemptFromBenchmark();
        // For each 3000 ms packet, write will be executed
        // time will be benchmarked for each iteration
        long sum = 0;
        long chunksCount = 0;
        long redisMemoryConsumed = 0;
        for (int i = 0; i < audioData.size(); i++) {
            List<byte[]> chunkList = audioData.get(i);
            //write each chunk and average out time
            for (int j = 0; j < chunkList.size(); j++) {
                String key = redisKey + ":" + i + ":" + j;
                long start = nanoTime();
                jedis.set(key.getBytes(), chunkList.get(j));
                long end = nanoTime();
                redisMemoryConsumed += jedis.memoryUsage(key.getBytes());
                sum += end - start;
                chunksCount++;
            }
        }
        averageChunkWriteTimeForSample.add((long) (sum * 1.0) / chunksCount);
        spaceConsumed.add(redisMemoryConsumed);
    }

    @Override
    public void collect() {
        out.println("Average write time for chunks of each sample");
        for (int i = 1; i <= averageChunkWriteTimeForSample.size(); i++) {
            out.println("Write time for Sample " + i + ": " + averageChunkWriteTimeForSample.get(i - 1) + " : Redis Memory Consumed: " + spaceConsumed.get(i - 1));
        }
        out.println("Final Redis State \n" + jedis.memoryStats());
        jedis.flushAll();
    }

    private List<List<byte[]>> amplifyData(List<List<byte[]>> data, int n) {
        List<List<byte[]>> amplifiedData = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (List<byte[]> innerList : data) {
                List<byte[]> amplifiedInnerList = new ArrayList<>(innerList);
                amplifiedData.add(amplifiedInnerList);
            }
        }
        System.out.println("Amplified data size: " + amplifiedData.size());
        return amplifiedData;
    }

}
