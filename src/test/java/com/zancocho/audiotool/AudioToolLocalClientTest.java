package com.zancocho.audiotool;

import com.zancocho.audiotool.client.AudioToolLocalClient;
import com.zancocho.audiotool.exception.AudioToolException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AudioToolLocalClientTest {

    private static AudioToolLocalClient client = new AudioToolLocalClient();

    @Test
    public void convertMp3ToWavTest(){
        try {
            client.convertMp3ToWav("arcarde", "temporal/");
        } catch (AudioToolException e) {
            Assertions.fail();
        }
    }

    @Test
    public void joinMp3Test(){
        try {
            client.joinMp3("arcarde", "escopetas", "temporal/");
        } catch (AudioToolException e) {
            Assertions.fail();
        }
    }

    @Test
    public void blendMp3Test(){
        try {
            client.blendMp3("arcarde", "escopetas", "temporal/");
        } catch (AudioToolException e) {
            Assertions.fail();
        }
    }
}
