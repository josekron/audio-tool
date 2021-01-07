package com.zancocho.audiotool;

import com.zancocho.audiotool.client.AudioToolLocalClient;
import com.zancocho.audiotool.client.IAudioToolClient;
import com.zancocho.audiotool.exception.AudioToolException;

/**
 * @author Jose A.H
 *
 * AudioToolFactory: factory to return an AudioTool Client depending on the input parameter
 */
public class AudioToolFactory {

    public static enum ClientType{
        LOCAL
    }

    public AudioToolFactory() {
    }

    IAudioToolClient getAudioToolClient(ClientType clientType) throws AudioToolException {

        IAudioToolClient client = null;

        if(clientType.equals(ClientType.LOCAL))
            client = new AudioToolLocalClient();
        else
            throw new AudioToolException("Client " + clientType + " not recognized or implemented");

        return client;
    }
}
