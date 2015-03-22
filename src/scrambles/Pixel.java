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
public class Pixel implements Comparable<Pixel>{
    public int color, red, green, blue, x, y;
    public Edge parentEdge;
    public Square parentSquare;
    public int squareNum; //0-8
    public int edgeNum; // 0-3
    public double corner = 0;
    
//    public Pixel(int c, Edge p){
//        color = c;
//        red = (color >> 16) & 0xFF;
//        green = (color >> 8) & 0xFF;
//        blue = color & 0xFF;
//        parentEdge = p;
//        x = 0;
//        y = 0;
//       
//    }
    
    public Pixel(int c, int xx, int yy){
        color = c;    

        red = (color >> 16) & 0xFF;
        green = (color >> 8) & 0xFF;
        blue = color & 0xFF;
        x = xx;
        y = yy;
        
    }
    @Override
    public int compareTo(Pixel p){
        if(red < p.red) return -1;
        else if(red > p.red)return 1;
        else if(green< p.green)return -1;
        else if(green > p.green)return 1;
        else if(blue< p.blue)return -1;
        else if(blue > p.blue)return 1;
        else return 0;
    }
    
    public double eDistTo(Pixel pix){
        return Math.sqrt(Math.pow(red - pix.red, 2) + Math.pow(green - pix.green, 2) + Math.pow(blue - pix.blue, 2));
    }
    
    public void setParentEdge(Edge e){
        parentEdge = e;
        edgeNum = e.idNum;
        squareNum = e.squareNum;
    }
    
    
}
