package com.zancocho.audiotool.client;

import com.zancocho.audiotool.exception.AudioToolException;

import java.io.File;
import java.io.InputStream;

public interface IAudioToolClient {
    public String convertMp3ToWav(String fileName, String filePath) throws AudioToolException;

    public File convertInputStreamToMp3(InputStream inputStream, String fileName, String filePath) throws AudioToolException;

    public String joinMp3(String mp3Name1, String mp3Name2, String filePath) throws AudioToolException;

    public String blendMp3(String mp3Name1, String mp3Name2, String filePath) throws AudioToolException;

    public float getDurationAudio(String audioName, String filePath, AudioToolClient.AudioType audioType) throws AudioToolException;
}
