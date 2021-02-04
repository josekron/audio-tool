package com.zancocho.audiotool.client;

import com.zancocho.audiotool.client.inputstream.MixingAudioInputStream;
import com.zancocho.audiotool.client.inputstream.SequenceAudioInputStream;
import com.zancocho.audiotool.exception.AudioToolException;
import com.zancocho.audiotool.util.FileUtil;
import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.JavaLayerException;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * @author Jose A.H
 *
 * AudioToolLocalClient: this client is local and it was built only for running on your local machine
 * so the audio files need to be in your machine and folder.
 *
 * Support WAV and MP3 files.
 */
public class AudioToolClient implements IAudioToolClient {

    private FileUtil fileUtil;

    public static enum AudioType {
        WAV,
        MP3,
    };

    private static final AudioToolClient instance = new AudioToolClient();

    private AudioToolClient(){
        fileUtil = FileUtil.getInstance();
    }

    public static AudioToolClient getInstance(){
        return instance;
    }

    /**
     * convertMp3ToWav: convert a mp3 file to wav.
     * @param fileName
     * @param fileNameResult
     * @param filePath
     * @return
     * @throws AudioToolException
     */
    @Override
    public String convertMp3ToWav(String fileName, String fileNameResult, String filePath) throws AudioToolException {
        fileUtil = FileUtil.getInstance();

        try {

            Converter myConverter1 = new Converter();
            myConverter1.convert(filePath + fileName + ".mp3",filePath + fileNameResult + ".wav");

        } catch (JavaLayerException e) {
            throw new AudioToolException(e.getMessage());
        }

        return filePath + fileNameResult + ".wav";
    }

    /**
     * convertInputStreamToMp3: convert a inputStream (mp3) to mp3 file
     * @param inputStream
     * @param fileName
     * @param filePath
     * @return
     * @throws AudioToolException
     */
    @Override
    public File convertInputStreamToMp3(InputStream inputStream, String fileName, String filePath) throws AudioToolException {
        File file = new File(filePath + fileName + ".mp3");

        try {
            try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
                int read;
                byte[] bytes = new byte[8192];
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            }
        } catch (IOException e) {
            throw new AudioToolException(e.getMessage());
        }

        return file;
    }

    /**
     * convertMp3ToWavFromResources: the original file must be in the resources folder. This is util for testing the jar.
     * @param fileName
     * @param fileNameResult
     * @param resultPath
     * @return
     * @throws AudioToolException
     */
    public String convertMp3ToWavFromResources(String fileName, String fileNameResult, String resultPath) throws AudioToolException {
        fileUtil = FileUtil.getInstance();

        try {
            String mp3File1 = fileUtil.getFilePathFromResources(fileName + ".mp3");

            Converter myConverter1 = new Converter();
            myConverter1.convert(mp3File1, resultPath + fileNameResult + ".wav");

        } catch (URISyntaxException e) {
            throw new AudioToolException(e.getMessage());

        } catch (JavaLayerException e) {
            throw new AudioToolException(e.getMessage());
        }

        return resultPath + fileNameResult + ".wav";
    }

    /**
     * joinAudio: join two audio files. First one audio and after the second audio
     * @param audioName1
     * @param audioName2
     * @param fileNameResult
     * @param audioType
     * @param audioTypeResult
     * @param filePath
     * @return
     * @throws AudioToolException
     */
    @Override
    public String joinAudio(String audioName1, String audioName2, String fileNameResult, AudioToolClient.AudioType audioType, AudioToolClient.AudioType audioTypeResult, String filePath) throws AudioToolException {

        if(audioType == null || (!audioType.equals(AudioType.WAV) && !audioType.equals(AudioType.MP3)))
            throw new AudioToolException("Audio format not supported");

        try {

            if(audioType.equals(AudioType.MP3)) {
                this.convertMp3ToWav(audioName1, audioName1, filePath);
                this.convertMp3ToWav(audioName2, audioName2, filePath);
            }

            File wavFile1 = new File(filePath + audioName1 + ".wav");
            File wavFile2 = new File(filePath + audioName2 + ".wav");

            AudioInputStream audio1 = AudioSystem.getAudioInputStream(wavFile1);
            AudioInputStream audio2 = AudioSystem.getAudioInputStream(wavFile2);

            Collection list=new ArrayList();
            list.add(audio1);
            list.add(audio2);

            AudioFormat audioFormat = audio1.getFormat();

            AudioInputStream audioInputStream = new SequenceAudioInputStream(audioFormat, list);

            File fileOut = new File(filePath + fileNameResult + ".wav");
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, fileOut);

            if(audioTypeResult.equals(AudioType.MP3)) {
                File target = new File(filePath + fileNameResult + ".mp3");
                convertWavFileToMp3File(fileOut, target);
            }

        } catch (UnsupportedAudioFileException e) {
            throw new AudioToolException(e.getMessage());

        } catch (IOException e) {
            throw new AudioToolException(e.getMessage());
        }

        return filePath + fileNameResult + (audioTypeResult.equals(AudioType.MP3) ? ".mp3" : ".wav");
    }

    /**
     * joinAudioSilent: join two audio files but the first one is an audio silent from the folder resources
     * @param audioSilent
     * @param audioName2
     * @param fileNameResult
     * @param audioType
     * @param audioTypeResult
     * @param filePath
     * @return
     * @throws AudioToolException
     */
    private String joinAudioSilent(String audioSilent, String audioName2, String fileNameResult, AudioToolClient.AudioType audioType, AudioToolClient.AudioType audioTypeResult, String filePath) throws AudioToolException {

        if(audioType == null || (!audioType.equals(AudioType.WAV) && !audioType.equals(AudioType.MP3)))
            throw new AudioToolException("Audio format not supported");

        try {

            if(audioType.equals(AudioType.MP3)) {
                this.convertMp3ToWav(audioName2, audioName2, filePath);
            }

            fileUtil = FileUtil.getInstance();

            File wavAudioSilentFile = new File(fileUtil.getFilePathFromResources(audioSilent + ".wav"));
            File wavFile2 = new File(filePath + audioName2 + ".wav");

            AudioInputStream audio1 = AudioSystem.getAudioInputStream(wavAudioSilentFile);
            AudioInputStream audio2 = AudioSystem.getAudioInputStream(wavFile2);

            Collection list=new ArrayList();
            list.add(audio1);
            list.add(audio2);

            AudioFormat audioFormat = audio1.getFormat();

            AudioInputStream audioInputStream = new SequenceAudioInputStream(audioFormat, list);

            File fileOut = new File(filePath + fileNameResult + ".wav");
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, fileOut);

            if(audioTypeResult.equals(AudioType.MP3)) {
                File target = new File(filePath + fileNameResult + ".mp3");
                convertWavFileToMp3File(fileOut, target);
            }

        } catch (UnsupportedAudioFileException e) {
            throw new AudioToolException(e.getMessage());

        } catch (IOException e) {
            throw new AudioToolException(e.getMessage());

        } catch (URISyntaxException e) {
            throw new AudioToolException(e.getMessage());
        }

        return filePath + fileNameResult + (audioTypeResult.equals(AudioType.MP3) ? ".mp3" : ".wav");
    }

    /**
     * blendAudio: overlap two audio files
     * @param audioName1
     * @param audioName2
     * @param fileNameResult
     * @param audioType
     * @param audioTypeResult
     * @param filePath
     * @return
     * @throws AudioToolException
     */
    @Override
    public String blendAudio(String audioName1, String audioName2, String fileNameResult, AudioType audioType, AudioType audioTypeResult, String filePath) throws AudioToolException {

        if(audioType == null || (!audioType.equals(AudioType.WAV) && !audioType.equals(AudioType.MP3)))
            throw new AudioToolException("Audio format not supported");

        try {

            if(audioType.equals(AudioType.MP3)) {
                this.convertMp3ToWav(audioName1, audioName1, filePath);
                this.convertMp3ToWav(audioName2, audioName2, filePath);
            }

            File wavFile1 = new File(filePath + audioName1 + ".wav");
            File wavFile2 = new File(filePath + audioName2 + ".wav");

            AudioInputStream audio1 = AudioSystem.getAudioInputStream(wavFile1);
            AudioInputStream audio2 = AudioSystem.getAudioInputStream(wavFile2);

            Collection list=new ArrayList();
            list.add(audio2);
            list.add(audio1);

            AudioFormat audioFormat = audio1.getFormat();

            MixingAudioInputStream audioInputStream = new MixingAudioInputStream(audioFormat, list);

            File fileOut = new File(filePath + fileNameResult + ".wav");
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, fileOut);

            if(audioTypeResult.equals(AudioType.MP3)) {
                File target = new File(filePath + fileNameResult + ".mp3");
                convertWavFileToMp3File(fileOut, target);
            }

        } catch (IOException | UnsupportedAudioFileException e) {
            throw new AudioToolException(e.getMessage());
        }

        return filePath + fileNameResult + (audioTypeResult.equals(AudioType.MP3) ? ".mp3" : ".wav");
    }

    /**
     * blendAudioWithOffset: overlap two audio files but also add silent seconds to the audio background cut it as well
     * @param audioName
     * @param audioBackground
     * @param fileNameResult
     * @param audioType
     * @param audioTypeResult
     * @param filePath
     * @param startSecond
     * @param totalSeconds
     * @return
     * @throws AudioToolException
     */
    @Override
    public String blendAudioWithOffset(String audioName, String audioBackground, String fileNameResult, AudioType audioType, AudioType audioTypeResult, String filePath, int startSecond, int totalSeconds) throws AudioToolException {

        if(audioType == null || (!audioType.equals(AudioType.WAV) && !audioType.equals(AudioType.MP3)))
            throw new AudioToolException("Audio format not supported");

        String audioBackgroundModif = null;

        try {

            if(audioType.equals(AudioType.MP3)) {
                this.convertMp3ToWav(audioName, audioName, filePath);
                this.convertMp3ToWav(audioBackground, audioBackground, filePath);
            }

            File wavFile1 = new File(filePath + audioName + ".wav");
            File wavFile2 = new File(filePath + audioBackground + ".wav");

            // Cut audio background:
            audioBackgroundModif = audioBackground + "_modif";
            this.cutAudio(audioBackground, audioBackgroundModif, AudioType.WAV, AudioType.WAV, filePath, 0, totalSeconds);

            // Add silent to audio background:
            if(startSecond > 0){
                //join seconds to audioBackground - calculate list of silence audios.
                int[] arrayAudioSilent = AudioToolClient.getArrayAudioSilent(startSecond);
                for(int i = 0; i < arrayAudioSilent.length; i++){
                    if(arrayAudioSilent[i] > 0){
                        while(arrayAudioSilent[i] > 0){
                            String silenceAudio = null;
                            switch (i){
                                case 4:
                                    silenceAudio = "silence_10s";
                                    break;
                                case 3:
                                    silenceAudio = "silence_5s";
                                    break;
                                case 2:
                                    silenceAudio = "silence_3s";
                                    break;
                                case 1:
                                    silenceAudio = "silence_2s";
                                    break;
                                case 0:
                                    silenceAudio = "silence_1s";
                                    break;
                            }
                            String tempAudioName = audioBackgroundModif + "_" + i + "_" + arrayAudioSilent[i];
                            joinAudioSilent(silenceAudio, audioBackgroundModif, tempAudioName, AudioType.WAV, AudioType.WAV, filePath);
                            audioBackgroundModif = tempAudioName;
                            arrayAudioSilent[i] = arrayAudioSilent[i] - 1;
                        }
                    }
                }
            }

            blendAudio(audioName, audioBackgroundModif, fileNameResult, AudioType.WAV, audioTypeResult, filePath);

        } catch (AudioToolException e) {
            throw e;
        }

        return filePath + fileNameResult + (audioTypeResult.equals(AudioType.MP3) ? ".mp3" : ".wav");

    }

    /**
     * getDurationAudio: return the duration of an MP3 or WAV audio.
     * @param audioName
     * @param audioType
     * @param filePath
     * @return
     * @throws AudioToolException
     */
    @Override
    public float getDurationAudio(String audioName, AudioType audioType, String filePath) throws AudioToolException {
        float durationInSeconds = 0.0f;

        if(audioType == null || (!audioType.equals(AudioType.WAV) && !audioType.equals(AudioType.MP3)))
            throw new AudioToolException("Audio format not supported");

        try {

            if(audioType.equals(AudioType.MP3))
                this.convertMp3ToWav(audioName, audioName, filePath);

            File wavFile = new File(filePath + audioName + ".wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wavFile);

            AudioFormat audioFormat = audioInputStream.getFormat();

            long audioFileLength = wavFile.length();
            int frameSize = audioFormat.getFrameSize();
            float frameRate = audioFormat.getFrameRate();
            durationInSeconds = (audioFileLength / (frameSize * frameRate));

        } catch (IOException | UnsupportedAudioFileException e) {
            throw new AudioToolException(e.getMessage());
        }

        return durationInSeconds;
    }

    /**
     * cutAudio: cut an audio file by giving a start second and the total duration.
     * @param audioName
     * @param fileNameResult
     * @param audioType
     * @param audioTypeResult
     * @param filePath
     * @param startSecond
     * @param totalSeconds
     * @return
     * @throws AudioToolException
     */
    @Override
    public String cutAudio(String audioName, String fileNameResult, AudioType audioType, AudioType audioTypeResult, String filePath, int startSecond, int totalSeconds) throws AudioToolException {

        if(audioType == null || (!audioType.equals(AudioType.WAV) && !audioType.equals(AudioType.MP3)))
            throw new AudioToolException("Audio format not supported");

        AudioInputStream inputStream = null;
        AudioInputStream shortenedStream = null;

        try {

            if(audioType.equals(AudioType.MP3))
                this.convertMp3ToWav(audioName, audioName, filePath);

            File wavFile = new File(filePath + audioName + ".wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wavFile);

            AudioFormat audioFormat = audioInputStream.getFormat();

            inputStream = AudioSystem.getAudioInputStream(wavFile);

            int bytesPerSecond = audioFormat.getFrameSize() * (int)audioFormat.getFrameRate();
            inputStream.skip(startSecond * bytesPerSecond);
            long framesOfAudioToCopy = totalSeconds * (int)audioFormat.getFrameRate();
            shortenedStream = new AudioInputStream(inputStream, audioFormat, framesOfAudioToCopy);

            File fileOut = new File(filePath + fileNameResult + ".wav");
            AudioSystem.write(shortenedStream, AudioFileFormat.Type.WAVE, fileOut);

            if(audioTypeResult.equals(AudioType.MP3)) {
                File target = new File(filePath + fileNameResult + ".mp3");
                convertWavFileToMp3File(fileOut, target);
            }

        } catch (IOException | UnsupportedAudioFileException e) {
            throw new AudioToolException(e.getMessage());
        }

        return filePath + fileNameResult + (audioTypeResult.equals(AudioType.MP3) ? ".mp3" : ".wav");
    }

    /**
     * convertWavFileToMp3File: convert a wav file to mp3. It uses the library:
     * https://github.com/Sciss/jump3r
     * @param source
     * @param target
     * @throws AudioToolException
     */
    private static void convertWavFileToMp3File(File source, File target) throws AudioToolException {
        String[] mp3Args = { "--preset","standard",
                "-q","0",
                "-m","s",
                source.getAbsolutePath(),
                target.getAbsolutePath()
        };
        de.sciss.jump3r.Main m = new de.sciss.jump3r.Main();
        try {
            m.run(mp3Args);
        }
        catch(Exception e) {
            throw new AudioToolException("ERROR processing MP3: " + e.getMessage());
        }
    }

    /**
     * getArrayAudioSilent: return an array with the number of silent audios (from the resources folder)
     * required for the input seconds
     * @param seconds
     * @return
     */
    public static int[] getArrayAudioSilent(int seconds){
        int[] arrayAudioSilent = new int[5];
        for(int i = 0; i < arrayAudioSilent.length; i++){
            arrayAudioSilent[i] = 0;
        }

        while(seconds > 0){
            if(seconds >= 10){
                arrayAudioSilent[4] = arrayAudioSilent[4] + 1;
                seconds = seconds - 10;
            }
            else if(seconds >= 5){
                arrayAudioSilent[3] = arrayAudioSilent[3] + 1;
                seconds = seconds - 5;
            }
            else if(seconds >= 3){
                arrayAudioSilent[2] = arrayAudioSilent[2] + 1;
                seconds = seconds - 3;
            }
            else if(seconds >= 2){
                arrayAudioSilent[1] = arrayAudioSilent[1] + 1;
                seconds = seconds - 2;
            }
            else{
                arrayAudioSilent[0] = arrayAudioSilent[0] + 1;
                seconds = seconds - 1;
            }
        }

        return arrayAudioSilent;
    }
}
