package dev.myclinic.vertx.hankoimage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {
        CmdArgs cargs = CmdArgs.parse(args);
        BufferedImage srcImage = ImageIO.read(new File(cargs.inputFile));
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        BufferedImage dstImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int shuRGB = makeRGB(217, 55, 54);
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                int rgb = srcImage.getRGB(j, i);
                Color c = new Color(rgb);
                if( isShu(c.getRed(), c.getGreen(), c.getBlue()) ){
                    dstImage.setRGB(j, i, 0xFF000000 + shuRGB);
                } else {
                    dstImage.setRGB(j, i, 0x00FFFFFF);
                }
            }
        }
        ImageIO.write(dstImage, "PNG", new File(cargs.outputFile));
    }

    private static int makeRGB(int r, int g, int b){
        return (r << 16) + (g << 8) + b;
    }

    private static boolean isShu(int r, int g, int b){
        return r + g + b < 600;
    }
}
