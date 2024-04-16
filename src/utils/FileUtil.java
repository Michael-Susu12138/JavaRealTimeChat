package utils;

import java.io.*;



public class FileUtil {
	public static void saveFile(InputStream input, String fileName) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }
        fos.close();
    }
	
	public static byte[] readFile(String fileName) throws IOException {
        File file = new File(fileName);
        byte[] buffer = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(buffer, 0, buffer.length);
        bis.close();
        return buffer;
    }

}
