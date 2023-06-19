package org.example.benchmark;

import org.example.utils.AudioReader;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PcmToUlaw extends Benchmark {
    /*
        Benchmarks the time required to convert 114 speech
        samples from PCM to uLaw.
    */
    private final List<AudioInputStream> pcmAudios = new ArrayList<>();
    private final List<List<AudioInputStream>> pcmAudioChunks = new ArrayList<>();
    private final List<List<Long>> averageTimesAcrossChunksAcrossIterations = new ArrayList<>();
    private final AudioReader audioReader = new AudioReader();
    private long totalChunksActedOn = -1;
    public static final AudioFormat PCM_AUDIO_FORMAT = new AudioFormat(8000, 16, 1, true, false);
    public static final AudioFormat ULAW_AUDIO_FORMAT = new AudioFormat(AudioFormat.Encoding.ULAW, 8000, 8, 1, 1, 8000, false);
    @Override
    public void exemptFromBenchmark() throws UnsupportedAudioFileException, IOException {
        for(int i=1;i<=AudioReader.NUM_SAMPLES;i++) {
            AudioInputStream ais = audioReader.readAudio(AudioReader.INPUT_BASE_PATH+"/speech-"+i+".wav");
            pcmAudios.add(ais);
        }
        for(AudioInputStream pcmAis: pcmAudios) {
            // Cut each pcm audio input stream in SEGMENT_DURATION(3000 ms) ms chunks
            this.pcmAudioChunks.add(audioReader.segmentAudioStreamInChunks(pcmAis, AudioReader.SEGMENT_DURATION));
        }
    }

    @Override
    public void execute() throws Exception {
        List<Long> averageTimeForPacket = new ArrayList<>();
        for(List<AudioInputStream> pcmAis: pcmAudioChunks) {
            AudioFormat muLawFormat = new AudioFormat(AudioFormat.Encoding.ULAW, 8000, 8, 1, 1, 8000, false);
            double totalTime = 0;
            double packets = pcmAis.size();
            double average = 0;
            for(AudioInputStream pcmStream: pcmAis){
                long st = System.nanoTime();
                AudioSystem.getAudioInputStream(muLawFormat, pcmStream);
                long ed = System.nanoTime();
                totalTime += ed-st;
            }
            average = totalTime/packets;
            totalChunksActedOn = (long)packets*AudioReader.NUM_SAMPLES;
            averageTimeForPacket.add((long) average);
        }
        averageTimesAcrossChunksAcrossIterations.add(averageTimeForPacket);
    }

    @Override
    public void collect() throws Exception {
        for(int i=0;i<averageTimesAcrossChunksAcrossIterations.size();i++) {
            long sum = 0;
            for(long j:averageTimesAcrossChunksAcrossIterations.get(i)){
                sum += j;
            }
            long average = sum/averageTimesAcrossChunksAcrossIterations.get(i).size();
            System.out.println("Iteration "+(i+1)+" took [ns, avg per "+AudioReader.SEGMENT_DURATION+" audio duration (ms) "+average);
        }
        System.out.println("Total Chunks in 1 iteration = " + totalChunksActedOn);
    }

}
