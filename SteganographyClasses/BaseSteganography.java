public abstract class BaseSteganography{
    // Constructor
    public BaseSteganography(){};
    public abstract File encode(File image, String str, bool isEncrypted) throws TextTooLongException, CannotDecryptException, TextKeyTooShortException, AWTException, IOException, FileNotFoundException, ImageTooSmallException, UnsuitableImageException, TooManyColorsException, TextEmptyException;
    public abstract void decode(File image);
    public abstract String[] getHeader();
    public abstract String setHeader(String message);
    public abstract String setHeader(File file);
    public abstract long getCapacity();
    public abstract boolean getEncryptionStatus();
}