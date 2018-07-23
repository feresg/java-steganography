package Steganography.Logic;

import java.io.File;
import java.util.List;

public class Helpers {
    public static String getFileExtension(File file) {
        String filename = file.getName();
        try {
            return filename.substring(filename.lastIndexOf(".")+1);
        } catch (Exception e) {
            return "";
        }
    }
    public static byte[] toByteArray(List<Byte> in) {
      
        final int n = in.size();
        byte ret[] = new byte[n];
        for (int i = 0; i < n; i++) {
            ret[i] = in.get(i);

        }
        return ret;
    }
}
