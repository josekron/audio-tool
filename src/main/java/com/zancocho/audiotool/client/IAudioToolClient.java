package com.zancocho.audiotool.client;

import com.zancocho.audiotool.exception.AudioToolException;

import java.io.File;
import java.io.InputStream;

public interface IAudioToolClient {
    public String convertMp3ToWav(String fileName, String fileResultName, String filePath) throws AudioToolException;

    public File convertInputStreamToMp3(InputStream inputStream, String fileName, String filePath) throws AudioToolException;

    public String joinAudio(String audioName1, String audioName2, String fileNameResult, AudioToolClient.AudioType audioType, AudioToolClient.AudioType audioTypeResult, String filePath) throws AudioToolException;

    public String blendAudio(String audioName1, String audioName2, String fileNameResult, AudioToolClient.AudioType audioType, AudioToolClient.AudioType audioTypeResult, String filePath) throws AudioToolException;

    public String blendAudioWithOffset(String audioName, String audioBackground, String fileNameResult, AudioToolClient.AudioType audioType, AudioToolClient.AudioType audioTypeResult, String filePath, int startSecond, int totalSeconds) throws AudioToolException;

    public float getDurationAudio(String audioName, AudioToolClient.AudioType audioType, String filePath) throws AudioToolException;

    public String cutAudio(String audioName, String fileNameResult, AudioToolClient.AudioType audioType, AudioToolClient.AudioType audioTypeResult, String filePath, int startSecond, int totalSeconds) throws AudioToolException;

}
