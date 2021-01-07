package com.zancocho.audiotool.client;

import com.zancocho.audiotool.exception.AudioToolException;
import com.zancocho.audiotool.util.FileUtil;
import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.JavaLayerException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;

/**
 * @author Jose A.H
 *
 * AudioToolLocalClient: this client is local and it was built only for running on your local machine
 * so the MP3 files need to be in your machine and folder.
 */
public class AudioToolLocalClient implements IAudioToolClient {

    private static String PATH_WAV_DEFAULT = "temporal/";
    private FileUtil fileUtil;

    public AudioToolLocalClient() {
        fileUtil = FileUtil.getInstance();
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
     * convertMp3ToWavFromResources: the original file must be in the resources folder. This is util for testing the jar.
     * @param fileName
     * @return
     * @throws AudioToolException
     */
    public String convertMp3ToWavFromResources(String fileName) throws AudioToolException {
        fileUtil = FileUtil.getInstance();

        try {
            String mp3File1 = fileUtil.getFilePathFromResources(fileName + ".mp3");

            Converter myConverter1 = new Converter();
            myConverter1.convert(mp3File1, PATH_WAV_DEFAULT + fileName + ".wav");

        } catch (URISyntaxException e) {
            throw new AudioToolException(e.getMessage());

        } catch (JavaLayerException e) {
            throw new AudioToolException(e.getMessage());
        }

        return PATH_WAV_DEFAULT + fileName + ".wav";
    }

    /**
     * joinMp3: join two mp3 files. First one mp3 and after the second mp3
     * @param mp3Name1
     * @param mp3Name2
     * @param filePath
     * @return
     * @throws AudioToolException
     */
    @Override
    public String joinMp3(String mp3Name1, String mp3Name2, String filePath) throws AudioToolException {
        this.convertMp3ToWav(mp3Name1, filePath);
        this.convertMp3ToWav(mp3Name2, filePath);

        try {
            File wavFile1 = new File(filePath + mp3Name1 + ".wav");
            File wavFile2 = new File(filePath + mp3Name2 + ".wav");
            File fileOut = new File(filePath + mp3Name1 + mp3Name2 + ".wav");

            AudioInputStream audio1 = getAudioInputStream(wavFile1);
            AudioInputStream audio2 = getAudioInputStream(wavFile2);

            AudioInputStream audioBuild = new AudioInputStream(new SequenceInputStream(audio1, audio2), audio1.getFormat(), audio1.getFrameLength() + audio2.getFrameLength());

            for(int i = 0; i < 5; i++){
                audioBuild = new AudioInputStream(new SequenceInputStream(audioBuild, audio2), audioBuild.getFormat(), audioBuild.getFrameLength() + audio2.getFrameLength());
            }

            AudioSystem.write(audioBuild, AudioFileFormat.Type.WAVE, fileOut);

        } catch (UnsupportedAudioFileException e) {
            throw new AudioToolException(e.getMessage());

        } catch (IOException e) {
            throw new AudioToolException(e.getMessage());
        }

        return filePath + mp3Name1 + mp3Name2 + ".wav";
    }

    /**
     * blendMp3: overlap two mp3 files
     * @param mp3Name1
     * @param mp3Name2
     * @param filePath
     * @return
     * @throws AudioToolException
     */
    @Override
    public String blendMp3(String mp3Name1, String mp3Name2, String filePath) throws AudioToolException {
        this.convertMp3ToWav(mp3Name1, filePath);
        this.convertMp3ToWav(mp3Name2, filePath);

        try {

            Path wavPath1 = Paths.get(filePath + mp3Name1 + ".wav");
            Path wavPath2 = Paths.get(filePath + mp3Name2 + ".wav");

            byte[] byte1 = Files.readAllBytes(wavPath1);
            byte[] byte2 = Files.readAllBytes(wavPath2);

            byte[] out = null;
            byte[] audioShort = null;
            byte[] audioLong = null;

            if(byte1.length > byte2.length){
                out = new byte[byte1.length];
                audioShort = byte2;
                audioLong = byte1;
            }
            else{
                out = new byte[byte2.length];
                audioShort = byte1;
                audioLong = byte2;
            }

            for (int i = 0; i < audioShort.length; i++)
                out[i] = (byte) ((audioShort[i] + audioLong[i]) >> 1);

            for (int j = audioShort.length; j < audioLong.length; j++)
                out[j] = (byte) (audioLong[j] >> 1);

            File file = new File(filePath + mp3Name1 + mp3Name2 + ".wav");
            OutputStream os = new FileOutputStream(file);
            os.write(out);
            os.close();

            File target = new File(filePath + mp3Name1 + mp3Name2 + ".mp3");
            convertWavFileToMp3File(file, target);

        } catch (IOException e) {
            throw new AudioToolException(e.getMessage());
        }

        return filePath + mp3Name1 + mp3Name2 + ".wav";
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
