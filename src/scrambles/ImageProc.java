
package scrambles;

/**
 *
 * @author David
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;
import java.net.URL;


public class ImageProc extends JPanel {
    // instance variables - replace the example below with your own

    BufferedImage img;
    BufferedImage whiteImage;
    BufferedImage linesImage;
    BufferedImage edgesImage;
    BufferedImage vhEdgesImage;
 

    public ImageProc() {

        try {
            img = ImageIO.read(new File("kittens.jpg"));
            //img = ImageIO.read(new File("teapot.JPG"));
            //System.out.println(img.getType());
           // String path = "http://seathelights.com/store/lh_puzzle1.jpg";
            //URL url = new URL(path);
            //img = ImageIO.read(new URL("http://www.b-dazzle.com/store/pc/catalog/10072skiing_1837_detail.jpg"));
            //img = ImageIO.read(new URL("http://cdn.rainbowresource.netdna-cdn.com/products/010871.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
//        JLabel picLabel = new JLabel(new ImageIcon(img));
//        this.add(picLabel);
        
        //whiteImage = whiten(img);
        //linesImage = whiteLines(whiteImage);
        //edgesImage = addEdges(img);
        //vhEdgesImage = vhEdgePoints(img);
        
        //create the detector
CannyEdgeDetector detector = new CannyEdgeDetector();

//adjust its parameters as desired
detector.setLowThreshold(0.5f);
detector.setHighThreshold(1f);

//apply it to an image
detector.setSourceImage(img);
detector.process();
 edgesImage = detector.getEdgesImage();
    }
    
    public BufferedImage whiten(BufferedImage img) {
        int t = 30;
        System.out.println("h: " + img.getHeight() + " w: " + img.getWidth());
        int[][] pixels = new int[img.getHeight()][img.getWidth()];
        BufferedImage pImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                //System.out.println("i: " + i + " j: " + j);
                pixels[i][j] = img.getRGB(j, i);

            }
        }
        for (int i = 0; i < img.getHeight(); i++) {
            //get middle pixel's average color value
            int rm = (pixels[i][img.getWidth()/2] >> 16) & 0xFF;
            int gm = (pixels[i][img.getWidth()/2] >> 8) & 0xFF;
            int bm = pixels[i][img.getWidth()/2] & 0xFF; 
            int avg = (rm + gm + bm)/3;
            for (int j = 0; j < img.getWidth(); j++) {
                int r = (pixels[i][j] >> 16) & 0xFF;
                int g = (pixels[i][j] >> 8) & 0xFF;
                int b = pixels[i][j] & 0xFF;
   
                int pr = 0;
                if (Math.abs(r - avg) < t && Math.abs(g - avg) < t && Math.abs(b - avg) < t) {

                    pr = 0xFF;
                    pr = (pr << 8) | 0xFF;
                    pr = (pr << 8) | 0xFF;
                    pImage.setRGB(j, i, pr);
                } 
                else {
                    pr = r;
                    pr = (pr << 8) | g;
                    pr = (pr << 8) | b;
                    pImage.setRGB(j, i, pr);

                }
            }
        }
        
        for (int i = 0; i < img.getWidth(); i++) {
            //get middle pixel's average color value
            int rm = (pixels[img.getHeight()/2][i] >> 16) & 0xFF;
            int gm = (pixels[img.getHeight()/2][i] >> 8) & 0xFF;
            int bm = pixels[img.getHeight()/2][i] & 0xFF; 
            int avg = (rm + gm + bm)/3;
            for (int j = 0; j < img.getHeight(); j++) {
                int r = (pixels[j][i] >> 16) & 0xFF;
                int g = (pixels[j][i] >> 8) & 0xFF;
                int b = pixels[j][i] & 0xFF;
   
                int pr = 0;
                if (Math.abs(r - avg) < t && Math.abs(g - avg) < t && Math.abs(b - avg) < t) {

                    pr = 0xFF;
                    pr = (pr << 8) | 0xFF;
                    pr = (pr << 8) | 0xFF;
                    pImage.setRGB(j, i, pr);
                } 
                else {
                    pr = r;
                    pr = (pr << 8) | g;
                    pr = (pr << 8) | b;
                    pImage.setRGB(i, j, pr);

                }
            }
        }
        return pImage;
    }
    
    public BufferedImage whiteLines(BufferedImage img) {
        double thresh = .60;
        int[][] pixels = new int[img.getHeight()][img.getWidth()];
        int[] vLines = new int[img.getWidth()];
        int[] hLines = new int[img.getHeight()];
        vLines[0] = 1;
        vLines[img.getWidth()-1] = 1;
        hLines[0] = 1;
        hLines[img.getHeight()-1] = 1;
        BufferedImage pImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                //System.out.println("i: " + i + " j: " + j);
                pixels[i][j] = img.getRGB(j, i);
            }
        }
        for(int i = 0; i < hLines.length; i++){
            
            int j = 0;
            int whiteCount = 0;
            while(j < img.getWidth()){
                int r = (pixels[i][j] >> 16) & 0xFF;
                int g = (pixels[i][j] >> 8) & 0xFF;
                int b = pixels[i][j] & 0xFF;
                if(r + g + b > 760){
                    whiteCount++;
                }
                j = j+1; 
            }
            if(whiteCount > img.getWidth() * thresh){
                hLines[i] = 1;
            }
        }
        
        for(int i = 0; i < vLines.length; i++){
            int j = 0;
            int whiteCount = 0;
            while(j < img.getHeight()){
                int r = (pixels[j][i] >> 16) & 0xFF;
                int g = (pixels[j][i] >> 8) & 0xFF;
                int b = pixels[j][i] & 0xFF;
                if(r + g + b > 760){
                    whiteCount++;
                }
                j = j+1;  
            }
            if(whiteCount > img.getHeight() * thresh){
                vLines[i] = 1;
            }
        }
        
         for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                if(vLines[j] == 1 || hLines[i] == 1){
                   int pr = 0xFF;
                    pr = (pr << 16) | 0xFF;
                    pImage.setRGB(j, i, pr);
                }
                else{
                    pImage.setRGB(j, i, img.getRGB(j, i));
                }
            }
         }
        return pImage;
    }
    
    public BufferedImage addEdges( BufferedImage img){
        int[][] edgeMap = edges(img);
        double thresh = .70;
        BufferedImage pImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        int[] vLines = new int[edgeMap.length];
        int[] hLines = new int[edgeMap[0].length];
        vLines[0] = 1;
        vLines[img.getWidth()-1] = 1;
        hLines[0] = 1;
        hLines[img.getHeight()-1] = 1;
        
        for (int i = 0; i < edgeMap.length; i++) {
            int edgeCount = 0;
            for (int j = 0; j < edgeMap[0].length; j++) {
                if (edgeMap[i][j] == 1){
                    edgeCount++;
                }
                
            }
            if(edgeCount > img.getWidth() * thresh){
                hLines[i] = 1;
            }
        }
        
        for (int i = 0; i < edgeMap[0].length; i++) {
            int edgeCount = 0;
            for (int j = 0; j < edgeMap.length; j++) {
                if (edgeMap[j][i] == 1){
                    edgeCount++;
                }
                
            }
            if(edgeCount > img.getHeight() * thresh){
                vLines[i] = 1;
            }
        }
        System.out.println("vLines: " + Arrays.toString(vLines));
        System.out.println("hLines: " + Arrays.toString(hLines));
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                if(vLines[j] == 1 || hLines[i] == 1){
                   int pr = 0xFF;
                    pr = (pr << 16) | 0xFF;
                    pImage.setRGB(j, i, pr);
                }
                else{
                    pImage.setRGB(j, i, img.getRGB(j, i));
                }
            }
         }
        return pImage;
    }
    
    public int[][] edges(BufferedImage img){
        int[][] pixels = new int[img.getWidth()][img.getHeight()];
    
        for (int i = 1; i < img.getHeight()-2; i++) {
            for (int j = 1; j < img.getWidth()-2; j++) {
                if(edge(i,j,img)){
                    pixels[i][j] = 1;
                    pixels[i-1][j] = 1;
                    pixels[i+1][j] = 1;
                    pixels[i][j+1] = 1;
                    pixels[i][j-1] = 1;
                    
                }
            }
        }
        return pixels;
    }
    
        
    public boolean edge(int m, int n, BufferedImage img){
        int thresh = 75;
        int[] pixel = sepColors(img.getRGB(n,m));
        int[] pixBelow = sepColors(img.getRGB(n, m+2));
        for(int i = 0; i < 3; i++){
            if(Math.abs(pixel[i] - pixBelow[i]) > thresh){
                return true;
            }
        }
        int[] pixRight = sepColors(img.getRGB(n+2, m));
        for(int i = 0; i < 3; i++){
            if(Math.abs(pixel[i] - pixRight[i]) > thresh){
                return true;
            }
        }
        return false;
                
    }
    
    public boolean vEdge(int x, int y, BufferedImage img){
        int thresh = 100;
//        if(x == 1) System.out.println();
//        if(x == img.getWidth()/3) System.out.print("|");
//        if(x == img.getWidth()*2/3) System.out.print("|");
        if(x < img.getHeight() - 2){
            int[] pixel = sepColors(img.getRGB(x,y));
            int[] pixRight = sepColors(img.getRGB(x + 1, y));
            int dColor = 0;
            for(int i = 0; i < 3; i++){
                dColor += Math.abs(pixel[i] - pixRight[i]); 
            }
            if(dColor > thresh){
//                System.out.print("5");
                return true;
            }
            else{
//                System.out.print("0");
                return false;
            }
        }
        
        return true;         
    }
    
    public boolean hEdge(int x, int y, BufferedImage img){
        int thresh = 100;
//        if(x == 1) System.out.println();
//        if(x == img.getWidth()/3) System.out.print("|");
//        if(x == img.getWidth()*2/3) System.out.print("|");
        if(y < img.getWidth() - 2){
            int[] pixel = sepColors(img.getRGB(x,y));
            int[] pixDown = sepColors(img.getRGB(x, y + 1));
            int dColor = 0;
            for(int i = 0; i < 3; i++){
                dColor += Math.abs(pixel[i] - pixDown[i]); 
            }
            if(dColor > thresh){
//                System.out.print("5");
                return true;
            }
            else{
//                System.out.print("0");
                return false;
            }
        }
        
        return true;         
    }
    
    public BufferedImage vhEdgePoints(BufferedImage img){
        BufferedImage pImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 1; i < img.getHeight(); i++) {
            for (int j = 1; j < img.getWidth(); j++) {
                if(j == 1) System.out.println();
                if(j == img.getWidth()/3) System.out.print("*");
                if(j == img.getWidth()*2/3) System.out.print("*");
                if(vEdge(j, i, img)){
                    pImage.setRGB(j, i,  0xff << 16);
                    System.out.print("^");
                }
                else if(hEdge(j, i,  img)){
                    pImage.setRGB(j, i,  0xff << 8);
                    System.out.print(">");
                }
                else{
                    pImage.setRGB(j, i, (int)Math.pow(2, 24)-1);
                    System.out.print("-");
                }
                
            }
        }
        
        return pImage;
    }
    
    public int[] sepColors(int n){
        int[] colors = new int[3];
        colors[0] = (n >> 16) & 0xFF;
        colors[1] = (n >> 8) & 0xFF;
        colors[2] = n & 0xFF;
        return colors;
    }

    

    public static void main(String[] args) {

        JFrame w = new JFrame("array image");
        w.setSize(800, 500);
        w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageProc panel = new ImageProc();
        panel.setBackground(Color.WHITE);
        Container c = w.getContentPane();

        c.add(panel);

        w.setVisible(true);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 20, 20, this);
        //g.drawImage(whiteImage, img.getWidth() + 50, 0, this);
        //g.drawImage(linesImage, img.getWidth() + 50, 20, this);
        g.drawImage(edgesImage, img.getWidth() + 50, 20, this);
        //g.drawImage(vhEdgesImage, img.getWidth() + 50, 20, this);
    }
}

