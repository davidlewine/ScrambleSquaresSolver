/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scrambles;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 *
 * @author David
 */
public class Feature {
    double dxr, dxl, dyd, dyu, ddx = 0, ddy = 0;
    int x, y;
    int d = 2;
    int threshLow = 20;
    int threshHigh = 120;
    double score = -1;
    
    public Feature (BufferedImage img, int xx, int yy){
        //calculates feature score for single pixel.
        x = xx;
        y = yy;
        score = pixFeatureScore(img, x, y);
    }
    
    public Feature (BufferedImage img){
        //calculates feature score for whole image.
       
        score = imgFeatureScore(img);
    }
    
    private double pixFeatureScore(BufferedImage img, int x, int y){
        if((x < img.getWidth()-d) && (x >= d) && (y < img.getHeight()-d) && (y >= d)){
            Box ulBox = new Box(img, x-2, y-2, d, d);
            Box lrBox = new Box(img, x, y, d, d);
            if(ulBox.uniform() && lrBox.uniform()) {
                return ulBox.distTo(lrBox);
            }
            else return -1;
        }
        else return -1;
    }
    
    private double imgFeatureScore(BufferedImage img){
        double[][] rgbData = new double[img.getWidth()][3];
        double[][] data = new double[img.getWidth()][2];
        for(int x = 0; x < img.getWidth(); x++){
            data[x][0]=x;
            for(int y = 0; y < img.getHeight(); y++){
               int[] rgb = sepColors(img.getRGB(x,y));
               for(int i = 0; i<3; i++){
                   rgbData[x][i] += rgb[i];
               }    
            }
            System.out.println(Arrays.toString(rgbData[x]));
            if(rgbData[x][0]>= rgbData[x][1] && rgbData[x][0]>= rgbData[x][2]){
                data[x][1] = (double)(rgbData[x][0])/img.getHeight()+600;
            }
            else if(rgbData[x][1]>= rgbData[x][0] && rgbData[x][1]>= rgbData[x][2]){
                data[x][1] = (double)(rgbData[x][1])/img.getHeight()+300;
            }
            else if(rgbData[x][2]>= rgbData[x][0] && rgbData[x][1]>= rgbData[x][1]){
                data[x][1] = (double)(rgbData[x][2])/img.getHeight();
            } 
        }
        for(int i = 0; i < data.length; i++){
            System.out.println(Arrays.toString(data[i]));
        }
        System.out.println();
        System.out.println("*******************************");
        SimpleRegression regression = new SimpleRegression();
        regression.addData(data);
        
        //eq[1] = regression.getIntercept();
        return regression.getSlope();

    }
    
    private double dPix(BufferedImage img, int x1, int y1, int x2, int y2){
        int[] rgb1 = sepColors(img.getRGB(x1,y1));
        int[] rgb2 = sepColors(img.getRGB(x2,y2));
        return  Math.abs(rgb1[0] - rgb2[0]) + Math.abs(rgb1[1] - rgb2[1]) + Math.abs(rgb1[2] - rgb2[2]); 
    }
    private int[] sepColors(int n) {
        int[] colors = new int[3];
        colors[0] = (n >> 16) & 0xFF;
        colors[1] = (n >> 8) & 0xFF;
        colors[2] = n & 0xFF;
        return colors;
    }
    
}
