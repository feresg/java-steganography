package Steganography.Logic;

import Steganography.Types.DataFormat;

public class HiddenData {

    public DataFormat format;
    public boolean isEncrypted;
    public boolean isCompressed;
    public byte pixelsPerByte;
    public byte pixelsPerPixel;
    public long length;
    public String extension;
    public int width;
    public int height;

    public HiddenData(byte[] header){
        switch((char) header[0]){
            case 'M':
                this.format = DataFormat.MESSAGE;
                this.extension = "txt";
                break;
            case 'D':
                this.format = DataFormat.DOCUMENT;
                StringBuilder extension = new StringBuilder();
                for(int j=7; j<header.length-1;j++)
                    extension.append((char) header[j]);
                this.extension = extension.toString();
                break;
            case 'I':
                this.format = DataFormat.IMAGE;
                this.pixelsPerPixel = header[1];
                this.extension = "png";
                StringBuilder height= new StringBuilder();StringBuilder width= new StringBuilder();
                for(int i=2; i<4; i++){
                    String w = String.format("%8s",Integer.toBinaryString(header[i])).replace(' ','0');
                    String h = String.format("%8s",Integer.toBinaryString(header[i+2])).replace(' ','0');
                    width.append(w.substring(w.length() - 8, w.length()));
                    height.append(h.substring(h.length() - 8, h.length()));
                }
                this.width = Integer.parseInt(width.toString(),2);
                this.height = Integer.parseInt(height.toString(),2);
                break;
        }
        if (this.format != DataFormat.IMAGE){
            this.isEncrypted = ((char) header[1]) == 'E';
            this.isCompressed = ((char) header[2]) == 'C';
            this.pixelsPerByte = header[3];
            StringBuilder length = new StringBuilder();
            int max = (this.format == DataFormat.DOCUMENT) ? 7 : 6;
            for(int i=4; i<max; i++){
                String l = String.format("%8s",Integer.toBinaryString(header[i])).replace(' ','0');
                length.append(l.substring(l.length() - 8, l.length()));
            }
            this.length = Integer.parseInt(length.toString(), 2);
        }
    }
}
