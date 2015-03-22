/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scrambles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import org.apache.commons.math.stat.inference.ManovaTests;
import org.apache.commons.math.stat.inference.TestUtils;
//import org.apache.commons.math.stat.inference.OneWayAnova;
//import org.apache.commons.math.stat.inference.TwoWayAnova;
//import org.apache.commons.math.stat.inference.OneWayManova;
//import org.apache.commons.math.stat.inference.TwoWayManova;

public class Zoomer implements Runnable {

    private JFrame frame;

    public BufferedImage img;
    public BufferedImage zoomImage;

    private MyZoomPanel zoomPanel;

    private int r = 5;//radius of included points around target point
    private int zoomFactor = 60;

    public Zoomer() {


        try {
            
            //img = ImageIO.read(new File("teapot.PNG"));
            String path = "http://cdn.rainbowresource.netdna-cdn.com/products/010871.jpg";
            //img = ImageIO.read(new File("aaron.JPG"));
            //System.out.println(img.getType());
            //img = ImageIO.read(new File("kittens.jpg"));
            //String path = "http://www.bendixens.com/mm5/graphics/00000001/scramhummingbirds.jpg";
            //String path = "http://www.theoriginalhorsetackcompany.com/images_products/bats-scramble-squares-8216big.jpg";
            //String path = "http://s5.thisnext.com/media/largest_dimension/Symphony-Scramble-Squares_5DECA6A5.jpg";

            URL url = new URL(path);
            //img = ImageIO.read(url);
            CannyEdgeDetector detector = new CannyEdgeDetector();
     //adjust its parameters as desired
       detector.setLowThreshold(0.5f);
       detector.setHighThreshold(1f);
    //apply it to an image
       detector.setSourceImage(ImageIO.read(url));
       detector.process();
       img = detector.getEdgesImage();
            
            zoomImage = new BufferedImage((r * 2 + 1) * zoomFactor, (r * 2 + 1) * zoomFactor, img.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        frame = new JFrame("Zoom Panel");

        zoomPanel = new MyZoomPanel(this);
        MyListener alpha = new MyListener(this);
        zoomPanel.addMouseMotionListener(alpha);
        zoomPanel.addMouseListener(alpha);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(zoomPanel);
        frame.setSize(1200, 700);
        frame.setVisible(true);
    }

    public JPanel getZoomPanel() {
        return zoomPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Zoomer());
    }

    public void drawZoomImage(int mx, int my, boolean pixInfo) {
        Graphics g = zoomImage.getGraphics();
        for (int i = 0; i < 2 * r + 1; i++) {
            for (int j = 0; j < 2 * r + 1; j++) {
                if (mx < img.getWidth() - r && mx > r && my < img.getHeight() - r && my > r) {
                    int x = mx - r + j;
                    int y = my - r + i;
                    g.setColor(new Color(img.getRGB(x, y)));
                    g.fillRect(j * zoomFactor, i * zoomFactor, zoomFactor, zoomFactor);
                    ArrayList<double[][]> pixelGroups = getPixGroups(x, y, img, 0);
                    double[] pValues = manovaPVs(pixelGroups);
                    g.setColor(Color.RED);
                    if(pValues[0] < .02 && pValues[0] > 0 ){
                        g.fillOval(j * zoomFactor + 30, i * zoomFactor + 30, 10, 10);
                    }
//                    ArrayList<double[][]> pixelGroups = getPixGroups(x, y, img, 0);
//                    double[] pValues = manovaPVs(pixelGroups);
//                    
//                    if (pixInfo) {
//                        g.setColor(Color.RED);
//                        g.drawString("" + (int)(pValues[0]*1000), j * zoomFactor + 5, i * zoomFactor + 15);
//                        g.drawString("" + (int)(pValues[1]*1000), j * zoomFactor + 5, i * zoomFactor + 25);
//                        g.drawString("" + (int)(pValues[2]*1000), j * zoomFactor + 5, i * zoomFactor + 35);
//                        g.drawString("" + (int)(pValues[3]*1000), j * zoomFactor + 5, i * zoomFactor + 45);
//                        
////                        Color pixColor = new Color(img.getRGB(x, y));
////                        g.drawString("" + pixColor.getRed(), j * zoomFactor + 5, i * zoomFactor + 15);
////                        g.drawString("" + pixColor.getGreen(), j * zoomFactor + 5, i * zoomFactor + 25);
////                        g.drawString("" + pixColor.getBlue(), j * zoomFactor + 5, i * zoomFactor + 35);
//                    }
                }
            }
        }
    }

    public void printPixelInfo(int mx, int my) {
        Graphics g = zoomImage.getGraphics();
        for (int i = 0; i < 2 * r + 1; i++) {
            for (int j = 0; j < 2 * r + 1; j++) {
                if (mx < img.getWidth() - r && mx > r && my < img.getHeight() - r && my > r) {
                    int x = mx - r + j;
                    int y = my - r + i;
                    ArrayList<double[][]> pixelGroups = getPixGroups(x, y, img, 0);
                    double[] pValues = manovaPVs(pixelGroups);
                    g.setColor(Color.RED);
                    if(pValues[0] < .02 && pValues[0] > 0){
                        g.drawString("" + (int)(pValues[0]*1000), j * zoomFactor + 5, i * zoomFactor + 15);
                        //g.fillOval(j * zoomFactor + 30, i * zoomFactor + 30, 10, 10);
                    }
//                    g.drawString("" + (int)(pValues[0]*1000), j * zoomFactor + 5, i * zoomFactor + 15);
//                    g.drawString("" + (int)(pValues[1]*1000), j * zoomFactor + 5, i * zoomFactor + 25);
//                    g.drawString("" + (int)(pValues[2]*1000), j * zoomFactor + 5, i * zoomFactor + 35);
//                    g.drawString("" + (int)(pValues[3]*1000), j * zoomFactor + 5, i * zoomFactor + 45);
                    //System.out.println(Arrays.toString(pValues));
//                    Color pixColor = new Color(img.getRGB(x - r + j, y - r + i));
//                    g.setColor(Color.WHITE);
//                    g.drawString("" + pixColor.getRed(), j * zoomFactor + 5, i * zoomFactor + 15);
//                    g.drawString("" + pixColor.getGreen(), j * zoomFactor + 5, i * zoomFactor + 25);
//                    g.drawString("" + pixColor.getBlue(), j * zoomFactor + 5, i * zoomFactor + 35);
                }
            }
        }
    }
    private double[] sepColors(int n) {
        double[] colors = new double[3];
        colors[0] = (n >> 16) & 0xFF;
        colors[1] = (n >> 8) & 0xFF;
        colors[2] = n & 0xFF;
        return colors;
    }
    
    

    private ArrayList<double[][]> getPixGroups(int x, int y, BufferedImage img, int dir){
        ArrayList<double[][]> groups = new ArrayList<double[][]>();
        double[][] outer = new double[3][7];
        double[][] inner = new double[3][7];
        double[] rgbVals = new double[3];
        if(dir == 0){//upper right
            rgbVals = sepColors(img.getRGB(x,y-2));
            //outer[0] = rgbVals;
            for(int i = 0; i < 3; i++){
                outer[i][0] = rgbVals[i];
                outer[i][0] = rgbVals[i];
                outer[i][0] = rgbVals[i];
            }
            rgbVals = sepColors(img.getRGB(x,y));
            //outer[1] = rgbVals;
            for(int i = 0; i < 3; i++){ 
                outer[i][1] = rgbVals[i];
                outer[i][1] = rgbVals[i];
                outer[i][1] = rgbVals[i];
            }
            rgbVals = sepColors(img.getRGB(x-2,y));
            //outer[2] = rgbVals;
            for(int i = 0; i < 3; i++){ 
                outer[i][2] = rgbVals[i];
                outer[i][2] = rgbVals[i];
                outer[i][2] = rgbVals[i];
            }
            rgbVals = sepColors(img.getRGB(x-1,y));
            //outer[3] = rgbVals;
            for(int i = 0; i < 3; i++){ 
                outer[i][3] = rgbVals[i];
                outer[i][3] = rgbVals[i];
                outer[i][3] = rgbVals[i];
            }
            rgbVals = sepColors(img.getRGB(x,y-1));
            //outer[4] = rgbVals;
            for(int i = 0; i < 3; i++){ 
                outer[i][4] = rgbVals[i];
                outer[i][4] = rgbVals[i];
                outer[i][4] = rgbVals[i];
            }
            rgbVals = sepColors(img.getRGB(x-3,y));
            //outer[3] = rgbVals;
            for(int i = 0; i < 3; i++){ 
                outer[i][3] = rgbVals[i];
                outer[i][3] = rgbVals[i];
                outer[i][3] = rgbVals[i];
            }
            rgbVals = sepColors(img.getRGB(x,y-3));
            //outer[4] = rgbVals;
            for(int i = 0; i < 3; i++){ 
                outer[i][4] = rgbVals[i];
                outer[i][4] = rgbVals[i];
                outer[i][4] = rgbVals[i];
            }
            rgbVals = sepColors(img.getRGB(x-1,y-2));
            //inner[0] = rgbVals;
            for(int i = 0; i < 3; i++){ 
                inner[i][0] = rgbVals[i];
                inner[i][0] = rgbVals[i];
                inner[i][0] = rgbVals[i];
            }
            rgbVals = sepColors(img.getRGB(x-1,y-1));
            //inner[1] = rgbVals;
            for(int i = 0; i < 3; i++){ 
                inner[i][1] = rgbVals[i];
                inner[i][1] = rgbVals[i];
                inner[i][1] = rgbVals[i];
            }
            rgbVals = sepColors(img.getRGB(x-2,y-1));
            //inner[2] = rgbVals;
            for(int i = 0; i < 3; i++){ 
                inner[i][2] = rgbVals[i];
                inner[i][2] = rgbVals[i];
                inner[i][2] = rgbVals[i];
            }
            rgbVals = sepColors(img.getRGB(x-2,y-2));
            //inner[2] = rgbVals;
            for(int i = 0; i < 3; i++){ 
                inner[i][3] = rgbVals[i];
                inner[i][3] = rgbVals[i];
                inner[i][3] = rgbVals[i];
            }
            rgbVals = sepColors(img.getRGB(x-3,y-3));
            //inner[2] = rgbVals;
            for(int i = 0; i < 3; i++){ 
                inner[i][4] = rgbVals[i];
                inner[i][4] = rgbVals[i];
                inner[i][4] = rgbVals[i];
            }
            rgbVals = sepColors(img.getRGB(x-1,y-3));
            //inner[2] = rgbVals;
            for(int i = 0; i < 3; i++){ 
                inner[i][3] = rgbVals[i];
                inner[i][3] = rgbVals[i];
                inner[i][3] = rgbVals[i];
            }
            rgbVals = sepColors(img.getRGB(x-4,y-1));
            //inner[2] = rgbVals;
            for(int i = 0; i < 3; i++){ 
                inner[i][4] = rgbVals[i];
                inner[i][4] = rgbVals[i];
                inner[i][4] = rgbVals[i];
            }
            
        }
       
        groups.add(outer);
        groups.add(inner);
        return groups;
    }
    
    private double featureValue(ArrayList<double[][]> groups){
        double val = 0;
        
        
        
        return val;
    }
    
    
    
    private double[] manovaPVs(ArrayList<double[][]> groups) {
        
        double[] manovaPvalues = new double[4];


//two way MANOVA grouping variables

        
        try {
//MANOVA object with degrees of freedom and eigenvalues
//ManovaStats manovaStats =
//TestUtils.oneWayManovaStats(matrices);
//MANOVA tests result
            double[][] manovaTests = TestUtils.oneWayManovaTests(groups);
            //for(double[] test: manovaTests) System.out.println(Arrays.toString(test));

//MANOVA tests P values
            manovaPvalues = TestUtils.oneWayManovaPvalues(groups);
            //System.out.println(Arrays.toString(manovaPvalues));
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return manovaPvalues;  
    }

//two
    private class MyListener extends MouseInputAdapter {

        private Zoomer zoomer;

        public MyListener(Zoomer zoomer) {
            this.zoomer = zoomer;
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            zoomer.drawZoomImage(e.getX(), e.getY(), false);

            zoomer.getZoomPanel().repaint();
        }

        public void mouseDragged(MouseEvent e) {
            zoomer.drawZoomImage(e.getX(), e.getY(), true);

            zoomer.getZoomPanel().repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            printPixelInfo(e.getX(), e.getY());
            zoomer.getZoomPanel().repaint();

        }

    }

    private class MyZoomPanel extends JPanel {

        private Zoomer zoomer;

        public MyZoomPanel(Zoomer zoomer) {
            this.zoomer = zoomer;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
            g.drawImage(zoomer.img, 0, 0, this);
            g.drawImage(zoomer.zoomImage, 40 + zoomer.img.getWidth(), 20, this);

        }
    }
}
