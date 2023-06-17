package org.example.benchmark;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PcmToUlaw extends Benchmark {
    /*
        Benchmarks the time required to convert 114 speech
        samples from PCM to uLaw.
    */
    private final int NUM_SAMPLES = 114;
    private final String INPUT_BASE_PATH = "/Users/ayush.tiwari/Downloads/rtsRedisWriter/src/main/resources/audio";
    private final List<AudioInputStream> pcmAudios = new ArrayList<>();
    private final List<List<AudioInputStream>> pcmAudioChunks = new ArrayList<>();
    private final List<List<Long>> averageTimesAcrossChunksAcrossIterations = new ArrayList<>();
    private final long[] CHUNK_TIMES = {3000};
    @Override
    public void exemptFromBenchmark() throws UnsupportedAudioFileException, IOException {
        for(int i=1;i<=NUM_SAMPLES;i++) {
            File file = new File(INPUT_BASE_PATH+"/speech-"+i+".wav");
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            pcmAudios.add(ais);
        }
        for(AudioInputStream pcmAis: pcmAudios) {
            // Cut each pcm audio input stream in 3000 ms chunks
            for(long time: CHUNK_TIMES){
                this.pcmAudioChunks.add(getChunks(pcmAis, time));
            }
        }
    }

    private List<AudioInputStream> getChunks(AudioInputStream audioInputStream, long duration) throws IOException {
        // Cut in chunks of duration ms.
        int chunkSizeBytes = (int) (duration * audioInputStream.getFormat().getFrameRate() / 1000.0) *
                audioInputStream.getFormat().getFrameSize();
        byte[] buffer = new byte[chunkSizeBytes];
        int bytesRead;
        int chunkCount = 0;
        // Read the audio data in chunks
        List<AudioInputStream> chunks = new ArrayList<>();
        while ((bytesRead = audioInputStream.read(buffer)) > 0) {
            chunks.add(new AudioInputStream(audioInputStream, audioInputStream.getFormat(), bytesRead));
            chunkCount++;
        }
        return chunks;
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
            System.out.println("Iteration "+(i+1)+" took [ns, avg per "+CHUNK_TIMES[0]+" audio duration (ms) "+average);
        }
    }
}
