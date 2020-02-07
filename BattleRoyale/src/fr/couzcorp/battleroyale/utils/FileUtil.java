package fr.couzcorp.battleroyale.utils;

import java.io.*;

public class FileUtil {

    public static void copyFolder(File src, File target) throws IOException {
        if (src.isDirectory()) {
            String[] files;
            if (!target.exists()) {
                target.mkdir();
            }
            for (String file : files = src.list()) {
                File srcFile = new File(src, file);
                File targetFile = new File(target, file);
                FileUtil.copyFolder(srcFile, targetFile);
            }
        } else {
            FileOutputStream out;
            try (FileInputStream in = new FileInputStream(src)) {
                int lenght;
                out = new FileOutputStream(target);
                byte[] buffer = new byte[1024];
                while ((lenght = in.read(buffer)) > 0) {
                    out.write(buffer, 0, lenght);
                }
                in.close();
                out.close();
            }
        }
    }

}
