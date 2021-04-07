package ru.dennis.systems.utils;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHandler {

    public static String readFile(String fileName) throws IOException {
        File file = new File(fileName);
        return new String(Files.readAllBytes(
                Paths.get(
                        URLDecoder.decode(file.getAbsolutePath(), StandardCharsets.UTF_8.toString())
                )
        ));
    }

    public static ByteArrayInputStream fileToByteArray(String path) throws IOException {
        return new ByteArrayInputStream(Files.readAllBytes(
                Paths.get(
                        URLDecoder.decode(path, StandardCharsets.UTF_8.toString())
                )
        ));
    }

    public static void writeFile(String fileName, String newContent) throws IOException {
        File file = new File(fileName);

        Path path = Paths.get(URLDecoder.decode(file.getAbsolutePath(), StandardCharsets.UTF_8.toString()));
        Files.write(path, newContent.getBytes());
    }

    public static boolean copyFilesRecursively(File toCopy, File destDir) throws IOException {
        assert destDir.isDirectory();

        if (!toCopy.isDirectory()) {
            return copyFile(toCopy, new File(destDir, toCopy.getName()));
        } else {
            File newDestDir = new File(destDir, toCopy.getName());
            if (!newDestDir.exists() && !newDestDir.mkdir()) {
                return false;
            }
            File[] filesToCopy = toCopy.listFiles();
            if (filesToCopy != null) {
                for (File child : filesToCopy) {
                    if (!copyFilesRecursively(child, newDestDir)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static boolean copyFile(final File toCopy, final File destFile) throws IOException {
        return copyStream(new FileInputStream(toCopy), new FileOutputStream(destFile));
    }

    private static boolean copyStream(final InputStream is, final OutputStream os) throws IOException {
        final byte[] buf = new byte[1024];

        int len = 0;
        while ((len = is.read(buf)) > 0) {
            os.write(buf, 0, len);
        }
        is.close();
        os.close();
        return true;
    }
}
