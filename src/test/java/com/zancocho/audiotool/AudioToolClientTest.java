package com.zancocho.audiotool;

import com.zancocho.audiotool.client.AudioToolClient;
import com.zancocho.audiotool.exception.AudioToolException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AudioToolClientTest {

    private static AudioToolClient client = AudioToolClient.getInstance();

    private static String TEMP_FOLDER = "temporal/";
    private static String AUDIO_1 = "audio1";
    private static String AUDIO_2 = "audio2";

    @Test
    public void convertMp3ToWavTest(){
        try {
            client.convertMp3ToWav(AUDIO_1, TEMP_FOLDER);
        } catch (AudioToolException e) {
            Assertions.fail();
        }
    }

    @Test
    public void joinMp3Test(){
        try {
            client.joinMp3(AUDIO_1, AUDIO_2, TEMP_FOLDER);
        } catch (AudioToolException e) {
            Assertions.fail();
        }
    }

    @Test
    public void blendMp3Test(){
        try {
            client.blendMp3(AUDIO_1, AUDIO_2, TEMP_FOLDER);
        } catch (AudioToolException e) {
            Assertions.fail();
        }
    }

    @Test
    public void durationAudioTest(){
        try {
            client.convertMp3ToWavFromResources(AUDIO_1);
            float durationInSeconds = client.getDurationAudio(AUDIO_1, "temporal/", AudioToolClient.AudioType.WAV);
            Assertions.assertEquals(true, durationInSeconds > 5.59);

        } catch (AudioToolException e) {
            Assertions.fail();
        }
    }
}
