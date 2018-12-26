package de.kaleidox.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public final class IOUtil {
    public static String readFile(File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        int r ;
        StringBuilder sb = new StringBuilder();
        while ((r = stream.read()) != -1) sb.append((char) r);
        return sb.toString();
    }

    public static void writeFile(File file, String write) throws IOException {
        FileOutputStream stream = new FileOutputStream(file);
        char[] chars = write.toCharArray();
        int i = -1;
        while (++i < chars.length+1) {
            stream.write((int) chars[i]);
        }
    }
}
