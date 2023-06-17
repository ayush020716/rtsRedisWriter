package org.example.utils;

import javax.sound.sampled.*;
import java.io.*;

public class AudioReader {
    private final String BASE_PATH = "/Users/ayush.tiwari/Downloads/rtsRedisWriter/src/main/resources/";
    public AudioInputStream readAudio(String path) throws UnsupportedAudioFileException, IOException {
        File file = new File(BASE_PATH+"audio/"+path);
        return AudioSystem.getAudioInputStream(file);
    }
    public void saveToOutput(AudioInputStream audioInputStream, String outputPath) throws IOException {
        File file = new File(BASE_PATH+outputPath);
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);
    }

    public AudioInputStream pcmToUlaw(AudioInputStream pcmAudio) {
        AudioFormat muLawFormat = new AudioFormat(AudioFormat.Encoding.ULAW, 8000, 8, 1, 1, 8000, false);
        return AudioSystem.getAudioInputStream(muLawFormat, pcmAudio);
    }
}
