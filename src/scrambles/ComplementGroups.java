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
import java.util.Arrays;

/**
 *
 * @author David
 */
public class ComplementGroups {
    
    public int[][] getComplements(ArrayList<ArrayList<Integer>> groups, Square[][] squares){
        //given array of 8 groups, return 4 complementary pairings.
        int[][] complements = new int[8][2];
        double[][] pairScores = new double[8][8];
        for(int i = 0; i<groups.size() - 1; i++){
            for(int j = i+1; j<groups.size(); j++){
                //take lowest complement score for pairs of images in groups i and j
                double minPairScore = 1000000;
                for(int g1: groups.get(i)){
                    int edge1 = g1%4;
                    int row1 = g1/12;
                    int col1 = (g1/4)%3;
                    for(int g2: groups.get(j)){
                        int edge2 = g2%4;
                        int row2 = g2/12;
                        int col2 = (g2/4)%3;
                        BufferedImage img1 = squares[row1][col1].edges[edge1].roi.img;
                        BufferedImage img2 = squares[row2][col2].edges[edge2].roi.img;
                        BufferedImage pairImg = new BufferedImage(img1.getWidth() *2, img1.getHeight() * 4, img1.getType());

                        Graphics2D gPairImg = pairImg.createGraphics();                  
                        //gPairImg.drawImage(img1, 20, 20, this);
                    
                    
                        double pairScore = complementScore(squares[row1][col1].edges[edge1].roi.img,squares[row2][col2].edges[edge2].roi.img);
                        if (pairScore < minPairScore){
                            minPairScore = pairScore;
                        }
                    }
                }
                pairScores[i][j] = minPairScore;
            }
        }
        for(int i = 0; i<pairScores.length - 1; i++){
            double minScore = -1;
            int complement = -1;
            for(int j = i+1; j<pairScores.length; j++){
                if(pairScores[i][j] < minScore || minScore < 0){
                    minScore = pairScores[i][j];
                    complement = j;
                }
            }
            complements[i][0] = i;
            complements[i][1] = complement;
        }
     System.out.println("***********  complement pairs  ***********");
     for(int[] pair: complements){
         System.out.println(Arrays.toString(pair));
     }
     return complements;  
    }
    
    public double complementScore(BufferedImage img1, BufferedImage img2){
        double score = 0, minScore = 100000;
        int img1o, img1c, img1x, img2o, img2c, img2x, rMax, img1r, img2r;
        rMax = (Math.min(img1.getWidth(), img2.getWidth()))/4;
        img1r = img1.getWidth()/2;
        img2r = img2.getWidth()/2;
        for(int r = -rMax; r <= rMax; r++){
            score = 0;
            int img1Start = Math.max(0, img1r -(img2r - r));
            int img1End = Math.min(img1.getWidth()-1, img1r + (img2r + r));
            int img2End = Math.max(0, img2r -(img1r - r));
            int img2Start = Math.min(img2.getWidth()-1, img2r + (img1r + r));
            for(int i = 0; i < Math.min(img1End-img1Start,img2Start - img2End); i++){
                double pixScore = hEdgeScore(img1.getRGB(img1Start + i, 0), img2.getRGB(img2Start - i, 0));
                score += pixScore;
            }
            if(score < minScore){
                minScore = score;
            }
                
        }
        return minScore;
    }
    
    public double hEdgeScore(int p1, int p2){
        double score = 0;
        int[] rgb1 = Process.sepColors(p1);
        int[] rgb2 = Process.sepColors(p2);
        for(int i = 0; i < 3; i++){
            score += Math.abs(rgb1[i] - rgb2[i]);
        }
        return score;
    }
    
}
