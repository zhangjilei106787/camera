package com.zjl.image.utils;

import java.io.File;
import java.io.FileOutputStream;

public  class FileUtils {




    public static void writeFile(byte[] bytes, String filePath) throws Exception {
        FileOutputStream outputStream = new FileOutputStream(filePath);
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();

    }

}
