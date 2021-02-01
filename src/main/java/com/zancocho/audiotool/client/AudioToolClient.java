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
     * @param filePath
     * @return
     * @throws AudioToolException
     */
    @Override
    public String convertMp3ToWav(String fileName, String filePath) throws AudioToolException {
        fileUtil = FileUtil.getInstance();

        try {

            Converter myConverter1 = new Converter();
            myConverter1.convert(filePath + fileName + ".mp3",filePath + fileName + ".wav");

        } catch (JavaLayerException e) {
            throw new AudioToolException(e.getMessage());
        }

        return filePath + fileName + ".wav";
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
     * @return
     * @throws AudioToolException
     */
    public String convertMp3ToWavFromResources(String fileName, String resultPath) throws AudioToolException {
        fileUtil = FileUtil.getInstance();

        try {
            String mp3File1 = fileUtil.getFilePathFromResources(fileName + ".mp3");

            Converter myConverter1 = new Converter();
            myConverter1.convert(mp3File1, resultPath + fileName + ".wav");

        } catch (URISyntaxException e) {
            throw new AudioToolException(e.getMessage());

        } catch (JavaLayerException e) {
            throw new AudioToolException(e.getMessage());
        }

        return resultPath + fileName + ".wav";
    }

    /**
     * joinAudio: join two audio files. First one audio and after the second audio
     * @param audioName1
     * @param audioName2
     * @param audioType
     * @param filePath
     * @return
     * @throws AudioToolException
     */
    @Override
    public String joinAudio(String audioName1, String audioName2, AudioToolClient.AudioType audioType, String filePath) throws AudioToolException {

        if(audioType == null || (!audioType.equals(AudioType.WAV) && !audioType.equals(AudioType.MP3)))
            throw new AudioToolException("Audio format not supported");

        try {

            if(audioType.equals(AudioType.MP3)) {
                this.convertMp3ToWav(audioName1, filePath);
                this.convertMp3ToWav(audioName2, filePath);
            }

            File wavFile1 = new File(filePath + audioName1 + ".wav");
            File wavFile2 = new File(filePath + audioName2 + ".wav");

            AudioInputStream audio1 = AudioSystem.getAudioInputStream(wavFile1);
            AudioInputStream audio2 = AudioSystem.getAudioInputStream(wavFile2);

            Collection list=new ArrayList();
            list.add(audio2);
            list.add(audio1);

            AudioFormat audioFormat = audio1.getFormat();

            AudioInputStream audioInputStream = new SequenceAudioInputStream(audioFormat, list);

            File fileOut = new File(filePath + audioName1 + audioName2 + ".wav");
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, fileOut);

            if(audioType.equals(AudioType.MP3)) {
                File target = new File(filePath + audioName1 + audioName2 + ".mp3");
                convertWavFileToMp3File(fileOut, target);
            }

        } catch (UnsupportedAudioFileException e) {
            throw new AudioToolException(e.getMessage());

        } catch (IOException e) {
            throw new AudioToolException(e.getMessage());
        }

        return filePath + audioName1 + audioName2 + (audioType.equals(AudioType.MP3) ? ".mp3" : ".wav");
    }

    /**
     * blendAudio: overlap two audio files
     * @param audioName1
     * @param audioName2
     * @param audioType
     * @param filePath
     * @return
     * @throws AudioToolException
     */
    @Override
    public String blendAudio(String audioName1, String audioName2, AudioToolClient.AudioType audioType, String filePath) throws AudioToolException {

        if(audioType == null || (!audioType.equals(AudioType.WAV) && !audioType.equals(AudioType.MP3)))
            throw new AudioToolException("Audio format not supported");

        try {

            if(audioType.equals(AudioType.MP3)) {
                this.convertMp3ToWav(audioName1, filePath);
                this.convertMp3ToWav(audioName2, filePath);
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

            File fileOut = new File(filePath + audioName1 + audioName2 + ".wav");
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, fileOut);

            if(audioType.equals(AudioType.MP3)) {
                File target = new File(filePath + audioName1 + audioName2 + ".mp3");
                convertWavFileToMp3File(fileOut, target);
            }

        } catch (IOException | UnsupportedAudioFileException e) {
            throw new AudioToolException(e.getMessage());
        }

        return filePath + audioName1 + audioName2 + (audioType.equals(AudioType.MP3) ? ".mp3" : ".wav");
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
                this.convertMp3ToWav(audioName, filePath);

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
     * @param audioType
     * @param filePath
     * @param startSecond
     * @param totalSeconds
     * @return
     * @throws AudioToolException
     */
    @Override
    public String cutAudio(String audioName, AudioType audioType, String filePath, int startSecond, int totalSeconds) throws AudioToolException {

        if(audioType == null || (!audioType.equals(AudioType.WAV) && !audioType.equals(AudioType.MP3)))
            throw new AudioToolException("Audio format not supported");

        AudioInputStream inputStream = null;
        AudioInputStream shortenedStream = null;

        try {

            if(audioType.equals(AudioType.MP3))
                this.convertMp3ToWav(audioName, filePath);

            File wavFile = new File(filePath + audioName + ".wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wavFile);

            AudioFormat audioFormat = audioInputStream.getFormat();

            inputStream = AudioSystem.getAudioInputStream(wavFile);

            int bytesPerSecond = audioFormat.getFrameSize() * (int)audioFormat.getFrameRate();
            inputStream.skip(startSecond * bytesPerSecond);
            long framesOfAudioToCopy = totalSeconds * (int)audioFormat.getFrameRate();
            shortenedStream = new AudioInputStream(inputStream, audioFormat, framesOfAudioToCopy);

            File fileOut = new File(filePath + audioName + "_short" + ".wav");
            AudioSystem.write(shortenedStream, AudioFileFormat.Type.WAVE, fileOut);

            if(audioType.equals(AudioType.MP3)) {
                File target = new File(filePath + audioName + "_short" + ".mp3");
                convertWavFileToMp3File(fileOut, target);
            }

        } catch (IOException | UnsupportedAudioFileException e) {
            throw new AudioToolException(e.getMessage());
        }

        return filePath + audioName + "_short" + (audioType.equals(AudioType.MP3) ? ".mp3" : ".wav");
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
}
