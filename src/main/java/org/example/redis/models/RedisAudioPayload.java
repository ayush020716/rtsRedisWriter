package org.example.redis.models;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RedisAudioPayload {
    private String audioId;
    private String audioData;
    private AudioFormat audioFormat;
    private boolean isFinal;
}
