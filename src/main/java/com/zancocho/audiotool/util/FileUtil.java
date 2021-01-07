package com.zancocho.audiotool.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Jose A.H
 */
public class FileUtil {

    private static final FileUtil instance = new FileUtil();

    private FileUtil(){}

    public static FileUtil getInstance(){
        return instance;
    }

    /**
     * getFileFromResource: return file from the folder resources
     * @param fileName
     * @return
     * @throws URISyntaxException
     */
    public File getFileFromResources(String fileName) throws URISyntaxException {

        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if(resource == null) {
            throw new IllegalArgumentException("File " + fileName + " not found");
        }

        return new File(resource.toURI());
    }

    public String getFilePathFromResources(String fileName) throws URISyntaxException {

        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if(resource == null) {
            throw new IllegalArgumentException("File " + fileName + " not found");
        }

        return resource.getPath();
    }
}
