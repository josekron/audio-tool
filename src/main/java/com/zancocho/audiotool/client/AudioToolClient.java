package com.zancocho.audiotool.client;

import com.zancocho.audiotool.client.inputstream.MixingAudioInputStream;
import com.zancocho.audiotool.client.inputstream.SequenceAudioInputStream;
import com.zancocho.audiotool.exception.AudioToolException;
import com.zancocho.audiotool.util.FileUtil;
import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.JavaLayerException;

import javax.sound.sampled.*;
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;

/**
 * @author Jose A.H
 *
 * AudioToolLocalClient: this client is local and it was built only for running on your local machine
 * so the MP3 files need to be in your machine and folder.
 */
public class AudioToolClient implements IAudioToolClient {

    private static String DEFAULT_PATH = "temporal/";
    private FileUtil fileUtil;

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
    public String convertMp3ToWavFromResources(String fileName) throws AudioToolException {
        fileUtil = FileUtil.getInstance();

        try {
            String mp3File1 = fileUtil.getFilePathFromResources(fileName + ".mp3");

            Converter myConverter1 = new Converter();
            myConverter1.convert(mp3File1, DEFAULT_PATH + fileName + ".wav");

        } catch (URISyntaxException e) {
            throw new AudioToolException(e.getMessage());

        } catch (JavaLayerException e) {
            throw new AudioToolException(e.getMessage());
        }

        return DEFAULT_PATH + fileName + ".wav";
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

            AudioInputStream audio1 = getAudioInputStream(wavFile1);
            AudioInputStream audio2 = getAudioInputStream(wavFile2);

            Collection list=new ArrayList();
            list.add(audio2);
            list.add(audio1);

            AudioFormat audioFormat = audio1.getFormat();

            AudioInputStream audioInputStream = new SequenceAudioInputStream(audioFormat, list);

            File fileOut = new File(filePath + mp3Name1 + mp3Name2 + ".wav");
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, fileOut);

            File target = new File(filePath + mp3Name1 + mp3Name2 + ".mp3");
            convertWavFileToMp3File(fileOut, target);

        } catch (UnsupportedAudioFileException e) {
            throw new AudioToolException(e.getMessage());

        } catch (IOException e) {
            throw new AudioToolException(e.getMessage());
        }

        return filePath + mp3Name1 + mp3Name2 + ".mp3";
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

            File wavFile1 = new File(filePath + mp3Name1 + ".wav");
            File wavFile2 = new File(filePath + mp3Name2 + ".wav");

            AudioInputStream audio1 = getAudioInputStream(wavFile1);
            AudioInputStream audio2 = getAudioInputStream(wavFile2);

            Collection list=new ArrayList();
            list.add(audio2);
            list.add(audio1);

            AudioFormat audioFormat = audio1.getFormat();

            MixingAudioInputStream audioInputStream = new MixingAudioInputStream(audioFormat, list);

            File fileOut = new File(filePath + mp3Name1 + mp3Name2 + ".wav");
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, fileOut);

            File target = new File(filePath + mp3Name1 + mp3Name2 + ".mp3");
            convertWavFileToMp3File(fileOut, target);

        } catch (IOException | UnsupportedAudioFileException e) {
            throw new AudioToolException(e.getMessage());
        }

        return filePath + mp3Name1 + mp3Name2 + ".mp3";
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
