/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scrambles;

import java.awt.image.BufferedImage;
import java.awt.Color;

/**
 *
 * @author David
 */
public class Box {
    //x,y are coordinates of upper left corner of box.
    //Assert: x + w and y + h are not out of bounds.
    
    
    int x, y, w, h;
    int threshUniform = 3;
    int threshCompare = 20;
    BufferedImage img;
    Color avgColor = null;
    
    public Box(BufferedImage im, int xx, int yy, int ww, int hh){
       x = xx;
       y = yy;
       img = im;
       w = ww;
       h = hh;
       
    
    }
    
    public int distTo(Box other){
        int tcDist;
        tcDist =  Math.abs(avgColor.getRed() - other.avgColor.getRed()) 
                + Math.abs(avgColor.getGreen() - other.avgColor.getGreen())
                + Math.abs(avgColor.getBlue() - other.avgColor.getBlue());
        return tcDist;
    }
    
    public boolean uniform(){
        int thresh = 3;
        int[][][] pixArr = new int[h][w][3];
    
        for(int i = 0; i < h; i++){
            for(int j = 0; j < w; j++){
                pixArr[i][j] = sepColors(img.getRGB(x+j, y+i));
            }
        }
        int[] min = {300, 300, 300};
        int[] max = {0, 0, 0};
        for(int[][] row: pixArr){
            for(int[] pix: row){
                for(int i = 0; i < 3; i++){
                    if (pix[i] < min[i]){
                        min[i] = pix[i];
                    }
                    if (pix[i] > max[i]){
                        max[i] = pix[i];
                    }
                }
            }
        }
        for(int i = 0; i < 3; i++){
            if(max[i] - min[i] > w*threshUniform){
                return false;
            }
        }
        avgColor = new Color((max[0] + min[0])/2, (max[1] + min[1])/2, (max[2] + min[2])/2);
        //System.out.println("" + avgColor.getRed() + " " + avgColor.getGreen() + " " + avgColor.getBlue());
        return true;
    }
   
    private int[] sepColors(int n) {
        int[] colors = new int[3];
        colors[0] = (n >> 16) & 0xFF;
        colors[1] = (n >> 8) & 0xFF;
        colors[2] = n & 0xFF;
        return colors;
    }
    
}
