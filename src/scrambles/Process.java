/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scrambles;

/**
 *
 * @author David
 */
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.awt.image.*;
import java.awt.*;

public class Process {

    public static double getImageDiff(BufferedImage imgA, BufferedImage imgB) {
        double result = 0;
        for (int i = 0; i < Math.min(imgA.getWidth(), imgB.getWidth()); i++) {
            for (int j = 0; j < Math.min(imgA.getHeight(), imgB.getHeight()); j++) {
                int[] rgbA = sepColors(imgA.getRGB(i, j));
                int[] rgbB = sepColors(imgB.getRGB(i, j));
                for (int p = 0; p < 3; p++) {
                    result += Math.abs(rgbA[p] - rgbB[p]);
                }
            }
        }
        result /= 1.0 * Math.min(imgA.getWidth(), imgB.getWidth()) * Math.min(imgA.getHeight(), imgB.getHeight());
        return result;
    }

    public static int[] sepColors(int n) {
        int[] colors = new int[3];
        colors[0] = (n >> 16) & 0xFF;
        colors[1] = (n >> 8) & 0xFF;
        colors[2] = n & 0xFF;
        return colors;
    }
    
    public static double eucDist(double[] p, double[] q){
        return Math.sqrt(Math.pow(p[0] - q[0], 2) + Math.pow(p[1] - q[1], 2));
    }
    
    public static BufferedImage rotate(BufferedImage img, double degrees) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage newImage = new BufferedImage(h, w, img.getType());
        Graphics2D g2 = newImage.createGraphics();
        g2.rotate(Math.toRadians(degrees), w / 2, h / 2);
        g2.drawImage(img, null, 0, 0);
        return newImage;
    }

}
