/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scrambles;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.awt.image.*;
import java.awt.*;
/**
 *
 * @author David
 */
public class RegionOI implements Comparable<RegionOI>{
    BufferedImage img;
    int x, y;//these are top left coordinates of region relative to whole puzzle image
    int featureThresh = -1;
    ArrayList<Feature> features = new ArrayList();
    
    public RegionOI(BufferedImage im, int xx, int yy){
        img = im;
        x = xx;
        y = yy;
        //features = getFeatures();
        
    }
    
    private ArrayList<Feature> getFeatures(){
        ArrayList<Feature> fs = new ArrayList();
        for(int i = 0; i < img.getWidth(); i++){
            for(int j = 0; j < img.getHeight(); j++){
                Feature tempFeature = new Feature(img, i, j);
                if(tempFeature.score > featureThresh){
                    fs.add(tempFeature);
                    Graphics g = img.getGraphics();
                    g.setColor(new Color(255, Math.min((int)(tempFeature.score*10), 255), 0));
                    g.fillOval(tempFeature.x, tempFeature.y, 3, 3);
                }
            }
        }
        return fs; 
    }
    
    public int compareTo(RegionOI other){
        BufferedImage imgO = other.img;
        double minDiff = Math.pow(10, 7);
        int ow = imgO.getWidth();
        int oh = imgO.getHeight();
        int cxo = ow/2;
        int cyo = oh/2;
        int w = img.getWidth();
        int h = img.getHeight();
        int cx = w/2;
        int cy = h/2;
        int dxMax = w/8;
        int dyMax = h/8;
        int a = (h - oh)/2;
        int b = (w - ow)/2;
        int lb, rb, tb, bb, olb, orb, otb, obb;
        for(int dx = -dxMax; dx<=dxMax; dx++){
            for(int dy = -dyMax; dy<=dyMax; dy++){
                lb = Math.max(0, b - dx);
                rb = Math.min(w-1, w -(b+dx));
                tb = Math.max(0, a+dy);
                bb = Math.min(h-1, h-(a-dy));
                olb = Math.max(0, dx-b);
                orb = Math.min(ow-1, ow +(b+dx));
                otb = Math.max(0, -a-dy);
                obb = Math.min(oh-1, oh +(a-dy));
                BufferedImage s1 = img.getSubimage(lb, tb, rb - lb, bb - tb);
                BufferedImage s2 = imgO.getSubimage(olb, otb, orb - olb, obb - otb);
                double currentDiff = Process.getImageDiff(s1, s2);
                if( currentDiff < minDiff){
                    minDiff = currentDiff;
                }
            }
        }
        return (int)minDiff;  
    }  
}
