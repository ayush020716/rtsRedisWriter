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

    public static final AudioFormat PCM_AUDIO_FORMAT = new AudioFormat(8000, 16, 1, true, false);
    public static final AudioFormat ULAW_AUDIO_FORMAT = new AudioFormat(AudioFormat.Encoding.ULAW, 8000, 8, 1, 1, 8000, false);
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

    public List<byte[]> segmentByteArrayInChunks(byte[] audioData, AudioFormat audioFormat, long duration) {
        // Calculate the chunk size in bytes based on the duration
        int chunkSizeBytes = (int) (duration * audioFormat.getSampleRate() / 1000.0) * audioFormat.getFrameSize();

        // Calculate the number of chunks based on the audio data length and chunk size
        int numChunks = (int) Math.ceil(audioData.length / (double) chunkSizeBytes);

        // Create a list to hold the audio data chunks
        List<byte[]> chunks = new ArrayList<>();

        // Segment the audio data into chunks
        for (int i = 0; i < numChunks; i++) {
            int offset = i * chunkSizeBytes;
            int length = Math.min(chunkSizeBytes, audioData.length - offset);
            byte[] chunk = Arrays.copyOfRange(audioData, offset, offset + length);
            chunks.add(chunk);
        }

        return chunks;
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


    public byte[] audioInputStreamToByteArray(AudioInputStream audioStream) throws IOException {
        assert audioStream.available()>0;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = audioStream.read(buffer, 0, buffer.length)) != -1) {
            byteStream.write(buffer, 0, bytesRead);
        }
        return byteStream.toByteArray();
    }

}
