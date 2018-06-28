public class Helpers{
    public static String getFileExtension(String path) {
        if(path.lastIndexOf(".") != -1 && path.lastIndexOf(".") != 0)
        return path.substring(path.lastIndexOf(".")+1);
        else return "";
    }
}