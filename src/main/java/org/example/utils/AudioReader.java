package org.example.utils;

import javax.sound.sampled.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AudioReader {
    public AudioInputStream readAudio(String path) throws UnsupportedAudioFileException, IOException {
        File file = new File(path);
        return AudioSystem.getAudioInputStream(file);
    }
    public void saveToOutput(AudioInputStream audioInputStream, String outputPath) throws IOException {
        File file = new File(outputPath);
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);
    }

    public AudioInputStream pcmToUlaw(AudioInputStream pcmAudio) {
        AudioFormat muLawFormat = new AudioFormat(AudioFormat.Encoding.ULAW, 8000, 8, 1, 1, 8000, false);
        return AudioSystem.getAudioInputStream(muLawFormat, pcmAudio);
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
}
