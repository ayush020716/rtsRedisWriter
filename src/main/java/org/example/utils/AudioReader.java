package org.example.utils;

import javax.sound.sampled.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AudioReader {
    public static final int NUM_SAMPLES = 114;
    public static final String INPUT_BASE_PATH = "/Users/ayush.tiwari/Downloads/rtsRedisWriter/src/main/resources/audio";
    public static final String OUTPUT_BASE_PATH = "/Users/ayush.tiwari/Downloads/rtsRedisWriter/src/main/resources/output";
    public static final long SEGMENT_DURATION = 3000;
    public AudioInputStream readAudio(String path) throws UnsupportedAudioFileException, IOException {
        File file = new File(path);
        return AudioSystem.getAudioInputStream(file);
    }

    public List<AudioInputStream> readAllAudioSample() throws UnsupportedAudioFileException, IOException {
        List<AudioInputStream> result = new ArrayList<>();
        for(int i=1;i<=NUM_SAMPLES;i++) {
            AudioInputStream ais = readAudio(INPUT_BASE_PATH+"/speech-"+i+".wav");
            result.add(ais);
        }
        return result;
    }
    public void saveToOutput(AudioInputStream audioInputStream, String outputPath) throws IOException {
        File file = new File(OUTPUT_BASE_PATH+"/"+outputPath);
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);
        audioInputStream.close();
    }

    public AudioInputStream pcmToUlaw(AudioInputStream pcmAudio) throws IOException {
        pcmAudio.available();
        AudioFormat pcmFormat = pcmAudio.getFormat();
        AudioFormat ulawFormat = new AudioFormat(AudioFormat.Encoding.ULAW, pcmFormat.getSampleRate(), 8, 1, 1, pcmFormat.getSampleRate(), false);
        AudioInputStream ulawAudio = AudioSystem.getAudioInputStream(ulawFormat, pcmAudio);
        return ulawAudio;
    }

    public List<AudioInputStream> segmentAudioStreamInChunks(AudioInputStream audioInputStream, long duration) throws IOException {
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

    public String audioStreamToString(AudioInputStream audioInputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;
        while ((read = audioInputStream.read(buffer, 0, buffer.length)) != -1) {
            outputStream.write(buffer, 0, read);
        }
        return outputStream.toString();
    }

    public AudioInputStream stringToAudioStream(String audioString, AudioFormat audioFormat) {
        byte[] audioBytes = audioString.getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(audioBytes);
        return new AudioInputStream(inputStream, audioFormat, audioBytes.length / audioFormat.getFrameSize());
    }
}
