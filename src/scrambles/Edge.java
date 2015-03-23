/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scrambles;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
/**
 *
 * @author David
 */
public class Edge {
    public ArrayList<Pixel[]> pixelSets;
    public ArrayList<Pixel> edgePixels = new ArrayList();
    public Square parentSquare;
    public int idNum; // 0-3
    public int squareNum;
    public double[] eq;
    public double[] cornerA;
    public double[] cornerB;
    public RegionOI roi;
    public int[][] neighborInfo = new int[36][2];
    
    
    public Edge(ArrayList<Pixel[]> ps){
        pixelSets = ps;
    }
    
    public Edge(BufferedImage img, double[] ca, double[] cb, int id){
        cornerA = ca;
        cornerB = cb;
        idNum = id;//0-3 with 0 = top edge of square and going clockwise
        double numPoints = Math.sqrt(Math.pow((cornerA[0] - cornerB[0]),2) + Math.pow((cornerA[1] - cornerB[1]),2));
        double dx = (cornerB[0] - cornerA[0])/numPoints;
        double dy = (cornerB[1] - cornerA[1])/numPoints;
        for(int i = 0; i <=numPoints; i++){
            int x = (int)(cornerA[0] + i*dx);
            int y = (int)(cornerA[1] + i*dy);
            //System.out.println("x: " + x + " y: " + y);
            Pixel newPixel = new Pixel(img.getRGB(x, y), x, y);
            edgePixels.add(newPixel); 
        }
        roi = getRoi(img);   
    }
    
    private RegionOI getRoi(BufferedImage img){
        double a = 2, b = 5, offset = 2;
        double c = 2*a;
        int tlx, tly, w, h;
        BufferedImage roiImg;
        if(idNum == 0){
            w = (int)(Math.abs(cornerA[0] - cornerB[0])/a);
            h = (int)(Math.abs(cornerA[0] - cornerB[0])/b);
            tlx = (int)((cornerA[0] + cornerB[0])/2 - (Math.abs(cornerA[0] - cornerB[0])/c));//tlx = top left x.
            tly = (int)((cornerA[1] + cornerB[1])/2 + offset); 
            roiImg = img.getSubimage(tlx, tly, w, h);
            
        }
        else if(idNum == 1){
            w = (int)(Math.abs(cornerA[1] - cornerB[1])/b);
            h = (int)(Math.abs(cornerA[1] - cornerB[1])/a);
            tly = (int)((cornerA[1] + cornerB[1])/2 - (Math.abs(cornerA[1] - cornerB[1])/c));
            tlx = (int)((cornerA[0] + cornerB[0])/2 - (w + offset));  
            BufferedImage tempRoiImg = img.getSubimage(tlx, tly, w, h);
            roiImg = new BufferedImage(tempRoiImg.getHeight(), tempRoiImg.getWidth(), tempRoiImg.getType());
            for(int i = 0;i <tempRoiImg.getWidth(); i++ ){
                for(int j = 0; j < tempRoiImg.getHeight(); j++){
                    roiImg.setRGB(j, tempRoiImg.getWidth()-1 - i, tempRoiImg.getRGB(i, j));
                }
            }
            
        }
        else if(idNum == 2){
            w = (int)(Math.abs(cornerA[0] - cornerB[0])/a);
            h = (int)(Math.abs(cornerA[0] - cornerB[0])/b);
            tlx = (int)((cornerA[0] + cornerB[0])/2 - (Math.abs(cornerA[0] - cornerB[0])/c));
            tly = (int)((cornerA[1] + cornerB[1])/2 - (h + offset));
            BufferedImage tempRoiImg = img.getSubimage(tlx, tly, w, h);
            roiImg = new BufferedImage(tempRoiImg.getWidth(), tempRoiImg.getHeight(), tempRoiImg.getType());
            for(int i = 0;i <tempRoiImg.getWidth(); i++ ){
                for(int j = 0; j < tempRoiImg.getHeight(); j++){
                    roiImg.setRGB(tempRoiImg.getWidth()-1 - i, tempRoiImg.getHeight()-1 - j, tempRoiImg.getRGB(i, j));
                }
            }
        }
        else {
            w = (int)(Math.abs(cornerA[1] - cornerB[1])/b);
            h = (int)(Math.abs(cornerA[1] - cornerB[1])/a);
            tly = (int)((cornerA[1] + cornerB[1])/2 - (Math.abs(cornerA[1] - cornerB[1])/c));
            tlx = (int)((cornerA[0] + cornerB[0])/2 + offset); 
            BufferedImage tempRoiImg = img.getSubimage(tlx, tly, w, h);
            roiImg = new BufferedImage(tempRoiImg.getHeight(), tempRoiImg.getWidth(), tempRoiImg.getType());
            for(int i = 0;i <tempRoiImg.getWidth(); i++ ){
                for(int j = 0; j < tempRoiImg.getHeight(); j++){
                    roiImg.setRGB(tempRoiImg.getHeight()-1 - j, i, tempRoiImg.getRGB(i, j));
                }
            }
        }
        RegionOI roi = new RegionOI(roiImg, tlx, tly);
        return roi;
    }

    public  void setParentSquare(Square sq){
        parentSquare = sq;
        squareNum = sq.idNum;
        
        
    }
    public void setPixelParents(){
        for(Pixel[] pixelSet: pixelSets){
            for(Pixel pixel: pixelSet){
                pixel.parentEdge = this;
                pixel.parentSquare = parentSquare;
                pixel.edgeNum = this.idNum;
                pixel.squareNum = this.squareNum;
            }
        }
    }
}
