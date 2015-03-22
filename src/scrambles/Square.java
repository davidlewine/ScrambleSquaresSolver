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
import java.awt.image.*;

public class Square {
    //corners are counted clockwise from upper left corner = 0.
    //edges are counted clockwise from upper/North edge;
    Edge[] edges = new Edge[4];
    BufferedImage puzzleImg;
    BufferedImage squareImg;
    int rotation = 0;
    int idNum;
    double[][] corners;
    
    
    public Square(BufferedImage img){
        puzzleImg = img;
        
    }
    
    public Square(BufferedImage img, double[][] cs){
        puzzleImg = img;
        corners = cs;
        edges = edgesFromCorners(corners);
        
    }
    
    private BufferedImage getSquareImage(){
        
        return new BufferedImage(5,5, puzzleImg.getType());
    }
    
    private Edge[] edgesFromCorners(double[][] corners){
        if(corners.length != 4) System.out.println("corners array doesn't have 4 elements");
        
        Edge[] edges = new Edge[4];
        for(int i = 0; i< 4; i++){
            Edge newEdge = new Edge(puzzleImg, corners[i], corners[(i+1)%4], i);
            newEdge.parentSquare = this;
            edges[i] = newEdge; 
        }
        return edges;
    }
    
    public void rotate(int n){
        rotation = rotation + n;
    }
    
    public void setEdgeParents(int id){
        idNum = id;
        for(int i = 0; i < edges.length; i++){
            edges[i].parentSquare = this;
            edges[i].idNum = i;
            edges[i].squareNum = idNum;
            edges[i].setPixelParents();
            
        }
    }
    
}
