package com.zancocho.audiotool.client;

import com.zancocho.audiotool.exception.AudioToolException;

public interface IAudioToolClient {
    public String convertMp3ToWav(String fileName, String filePath) throws AudioToolException;

    public String joinMp3(String mp3Name1, String mp3Name2, String filePath) throws AudioToolException;

    public String blendMp3(String mp3Name1, String mp3Name2, String filePath) throws AudioToolException;
}
