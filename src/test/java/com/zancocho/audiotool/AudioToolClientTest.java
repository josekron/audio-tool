package com.zancocho.audiotool;

import com.zancocho.audiotool.client.AudioToolClient;
import com.zancocho.audiotool.exception.AudioToolException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AudioToolClientTest {

    private static AudioToolClient client = AudioToolClient.getInstance();

    private static String TEMP_FOLDER = "temporal/";
    private static String AUDIO_1 = "audio1";
    private static String AUDIO_2 = "audio2";

    @BeforeAll
    public static void convertMp3ToWavTest(){
        try {
            client.convertMp3ToWavFromResources(AUDIO_1, TEMP_FOLDER);
            client.convertMp3ToWavFromResources(AUDIO_2, TEMP_FOLDER);
        } catch (AudioToolException e) {
            Assertions.fail();
        }
    }

    @Test
    public void joinAudioTest(){
        try {
            client.joinAudio(AUDIO_1, AUDIO_2, AudioToolClient.AudioType.WAV, TEMP_FOLDER);
        } catch (AudioToolException e) {
            Assertions.fail();
        }
    }

    @Test
    public void blendAudioTest(){
        try {
            client.blendAudio(AUDIO_1, AUDIO_2, AudioToolClient.AudioType.WAV, TEMP_FOLDER);
        } catch (AudioToolException e) {
            Assertions.fail();
        }
    }

    @Test
    public void durationAudioTest(){
        try {
            float durationInSeconds = client.getDurationAudio(AUDIO_1, AudioToolClient.AudioType.WAV, "temporal/");
            Assertions.assertEquals(true, durationInSeconds > 5.59 && durationInSeconds < 6.0);

        } catch (AudioToolException e) {
            Assertions.fail();
        }
    }

    @Test
    public void cutAudioTest(){
        try {
            client.cutAudio(AUDIO_1, AudioToolClient.AudioType.WAV, TEMP_FOLDER, 1, 3);
        } catch (AudioToolException e) {
            Assertions.fail();
        }
    }
}
