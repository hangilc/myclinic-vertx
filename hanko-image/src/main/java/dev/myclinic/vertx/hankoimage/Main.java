package dev.myclinic.vertx.hankoimage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {
        CmdArgs cargs = CmdArgs.parse(args);
        BufferedImage srcImage = ImageIO.read(new File(cargs.inputFile));
        System.out.printf("%d x %d\n", srcImage.getWidth(), srcImage.getHeight());
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        BufferedImage dstImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int shu = makeRGB(217, 55, 54);
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                int rgb = srcImage.getRGB(j, i);
                Color c = new Color(rgb);
                System.out.printf("%s", isShu(c.getRed(), c.getGreen(), c.getBlue()) ? "*" : " ");
            }
            System.out.println();
        }

    }

    private static int makeRGB(int r, int g, int b){
        return (r << 8) + (g << 4) + b;
    }

    private static boolean isShu(int r, int g, int b){
        int prod = r * 217 + g * 55 + b * 54;
        return prod > 36000;
    }
}
