package me.crazycranberry.vanillahungergames.utils;

import java.io.File;

public class FileUtils {
    public static boolean deleteRecursively(File path) {
        if (path == null) {
            return false;
        }
        if(path.exists()) {
            for (File file : path.listFiles()) {
                if (file.isDirectory()) {
                    deleteRecursively(file);
                } else {
                    file.delete();
                }
            }
        }
        return path.delete();
    }
}
