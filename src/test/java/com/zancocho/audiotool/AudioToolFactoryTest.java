package com.zancocho.audiotool;

import com.zancocho.audiotool.client.AudioToolAWSClient;
import com.zancocho.audiotool.client.AudioToolLocalClient;
import com.zancocho.audiotool.client.IAudioToolClient;
import com.zancocho.audiotool.exception.AudioToolException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AudioToolFactoryTest {

    @Test
    public void test1(){

        IAudioToolClient client = null;
        try {
            client = new AudioToolFactory().getAudioToolClient(AudioToolFactory.ClientType.LOCAL);
        } catch (AudioToolException e) {
            Assertions.fail();
        }

        Assertions.assertEquals(true, client instanceof AudioToolLocalClient);
    }
}
