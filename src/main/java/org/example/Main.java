package org.example;

import org.example.benchmark.Benchmark;
import org.example.benchmark.RedisWriteAudio;
import org.example.utils.AudioReader;

import javax.sound.sampled.AudioInputStream;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
//        Benchmark.runBenchmark(new PcmToUlaw(), 20);
        Benchmark.runBenchmark(new RedisWriteAudio(), 20);
//        AudioReader audioReader = new AudioReader();
//        AudioInputStream ais = audioReader.readAllAudioSample().get(113);
//        //convert to ulaw audio stream
//        AudioInputStream ulaw = audioReader.pcmToUlaw(audioReader.readAllAudioSample().get(113));
//        byte[] a = audioReader.audioInputStreamToByteArray(ais);
//        byte[] b = audioReader.audioInputStreamToByteArray(ulaw);
//        System.out.println(Arrays.toString(a).length());
//        System.out.println(Arrays.toString(b).length());
    }
}