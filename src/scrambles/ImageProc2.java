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
import org.apache.commons.math3.stat.regression.*;

public class ImageProc2 extends JPanel {
    // instance variables - replace the example below with your own

    BufferedImage img;
    BufferedImage imgGrid;
    BufferedImage vhEdgesImage;
    BufferedImage borderImage;
    BufferedImage hvBorderImage;
    BufferedImage rotatedImage;
    //BufferedImage[] subImages = new BufferedImage[3];

    public ImageProc2() {

        try {
            //img = ImageIO.read(new File("teapot.PNG"));
            img = ImageIO.read(new URL("http://cdn.rainbowresource.netdna-cdn.com/products/010871.jpg"));
//            String path = "http://cdn.rainbowresource.netdna-cdn.com/products/010871.jpg";
//            URL url = new URL(path);
//            img = ImageIO.read(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        JLabel picLabel = new JLabel(new ImageIcon(img));
//        this.add(picLabel);
        //vhEdgesImage = vhEdgePoints(img);
        //imgGrid = addGrid(img);
        borderImage = hEdgeImage(img);
        rotatedImage = rotate(borderImage);
        hvBorderImage = hEdgeImage(rotatedImage);
        //vhEdgeData(img);
    }

    public BufferedImage addGrid(BufferedImage img) {

        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage gridAdded = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int col = 0; col < w; col++) {
            for (int row = 0; row < h; row++) {
                if (col == w / 3 || col == w * 2 / 3 || row == h / 3 || row == h * 2 / 3) {
                    gridAdded.setRGB(col, row, 0);
                } else {
                    gridAdded.setRGB(col, row, img.getRGB(col, row));

                }
            }
        }
        return gridAdded;
    }

    public boolean vEdge(int x, int y, BufferedImage img) {
        int thresh = 75;
//        if(x == 1) System.out.println();
//        if(x == img.getWidth()/3) System.out.print("|");
//        if(x == img.getWidth()*2/3) System.out.print("|");
        if (x < img.getWidth() - 2) {
            int[] pixel = sepColors(img.getRGB(x, y));
            int[] pixRight = sepColors(img.getRGB(x + 1, y));
            int dColor = 0;
            for (int i = 0; i < 3; i++) {
                dColor += Math.abs(pixel[i] - pixRight[i]);
            }
            if (dColor > thresh) {
//                System.out.print("5");
                return true;
            } else {
//                System.out.print("0");
                return false;
            }
        }

        return true;
    }

    public boolean hEdge(int x, int y, BufferedImage img) {
        int thresh = 75;
//        if(x == 1) System.out.println();
//        if(x == img.getWidth()/3) System.out.print("|");
//        if(x == img.getWidth()*2/3) System.out.print("|");
        if (y < img.getHeight() - 2) {
            int[] pixel = sepColors(img.getRGB(x, y));
            int[] pixDown = sepColors(img.getRGB(x, y + 1));
            int dColor = 0;
            for (int i = 0; i < 3; i++) {
                dColor += Math.abs(pixel[i] - pixDown[i]);
            }
            if (dColor > thresh) {
//                System.out.print("5");
                return true;
            } else {
//                System.out.print("0");
                return false;
            }
        }

        return true;
    }

    public BufferedImage vhEdgePoints(BufferedImage img) {
        BufferedImage pImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 1; i < img.getHeight(); i++) {
            System.out.println();
            if (i == img.getHeight() / 3 || i == img.getHeight() * 2 / 3) {
                System.out.println();
            }
            for (int j = 1; j < img.getWidth(); j++) {
                if (j == img.getWidth() / 3) {
                    System.out.print("*");
                }
                if (j == img.getWidth() * 2 / 3) {
                    System.out.print("*");
                }
                if (vEdge(j, i, img)) {
                    if (hEdge(j, i, img)) {
                        pImage.setRGB(j, i, 0xff);
                        System.out.print("x");
                    } else {
                        pImage.setRGB(j, i, 0xff << 16);
                        System.out.print("^");
                    }
                } else if (hEdge(j, i, img)) {
                    pImage.setRGB(j, i, 0xff << 8);
                    System.out.print(">");
                } else {
                    pImage.setRGB(j, i, (int) Math.pow(2, 24) - 1);
                    System.out.print("-");
                }

            }
        }

        return pImage;
    }

    private BufferedImage hEdgeImage(BufferedImage img) {
        BufferedImage pImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        //set pImage to a copy of img.
        for(int i = 0; i < img.getWidth(); i++){
            for(int j = 0; j < img.getHeight(); j++){
                
                pImage.setRGB(i, j, img.getRGB(i, j));
            }
        }
        //divide pInage into three vertical strip subimages.
//        BufferedImage[] subImages = new BufferedImage[3];
//        for (int i = 0; i < 3; i++) {
//            subImages[i] = pImage.getSubimage(i * pImage.getWidth() / 3, 0, pImage.getWidth() / 3, pImage.getHeight());
//        }
        

        //divide data into three vertical strips
        int[][] data = vhEdgeData(pImage);
        int[][][] dataStrips = new int[3][data.length / 3][data[0].length];
        for (int p = 0; p < 3; p++) {
            for (int i = 0; i < dataStrips[p].length; i++) {
                for (int j = 0; j < dataStrips[p][0].length; j++) {
                    dataStrips[p][i][j] = data[i + p * data.length / 3][j];
                }
            }
        }
        
        //for each strip, get equations and draw lines
        for (int i = 0; i < dataStrips.length; i++) {
            ArrayList<double[]> hBorderEqs = getHBorderEqs(dataStrips[i]);
            for (int j = 0; j < hBorderEqs.size(); j++) {//for each equation, draw line
                double[] eq = hBorderEqs.get(j);
                for (int x = 0; x < dataStrips[i].length; x++) {//for each column or x coordinate in subImage
                    pImage.setRGB(x + i * dataStrips[i].length, (int) (eq[0] * x + eq[1]), 0xff << 16);
                }
            }
        }
        return pImage;
    }

    private int[][] vhEdgeData(BufferedImage img) {
        //0 = no edge; 1 = vertical edge; 
        //2 = horizontal edge; 3(2 + 1) = horizontal and vertical edge.
        int[][] data = new int[img.getWidth()][img.getHeight()];
        for (int row = 0; row < img.getHeight(); row++) {
            System.out.println();
            System.out.print("" + row + ":");
            for (int col = 0; col < img.getWidth(); col++) {
                data[col][row] = 0;
                if (vEdge(col, row, img)) {
                    data[col][row] += 1;
                }
                if (hEdge(col, row, img)) {
                    data[col][row] += 2;
                }
                System.out.print(data[col][row]);
            }
        }
        return data;
    }

    private ArrayList<double[]> getHBorderEqs(int[][] edgeData) {
        //vEdge = 1; hEdge = 2; both = 3.
        //find hBorders within edgeData.
        //edgeData is data for a vertical strip 1/3 the width of the whole original puzzle image
        int w = edgeData.length;
        int h = edgeData[0].length;
        int consecutiveThresh = w/6;
        int vEdgeCount = 0;
        ArrayList<double[]> borders = new ArrayList();
        ArrayList<int[]> data = new ArrayList();

        //pick point in middle of upper third of the strip.
        for (int i = 0; i < 3; i++) {
            int cx = w / 2;
            int cy = (2 * i + 1) * h / 6;

            //find hBorder above center point
            //first find left, middle, and right segments of border
            int leftBorder = -1, middleBorder = -1, rightBorder = -1;
            for (int row = cy - 1; row >= 0; row--) {
                if (consecutiveEdges(edgeData, consecutiveThresh, row, 0, w / 2)) {
                    if (leftBorder == -1) {
                        leftBorder = row;
                    }
                }
                if (consecutiveEdges(edgeData, consecutiveThresh, row, w / 4, w * 3 / 4)) {
                    if (middleBorder == -1) {
                        middleBorder = row;
                    }
                }
                if (consecutiveEdges(edgeData, consecutiveThresh, row, w / 2, w)) {
                    if (rightBorder == -1) {
                        rightBorder = row;
                    }
                }
            }

            System.out.println("upper lb: " + leftBorder + " mb: " + middleBorder + " rb: " + rightBorder);

            double[] newBorder = new double[2];
            if (leftBorder <= middleBorder && leftBorder <= rightBorder) { //left is least
                if (Math.abs(middleBorder - rightBorder) < 4) {
                    newBorder = linRegHorizontal(middleBorder, rightBorder, edgeData);
                } else if (middleBorder > rightBorder) {
                    newBorder = linRegHorizontal(leftBorder, rightBorder, edgeData);
                } else {
                    newBorder = linRegHorizontal(leftBorder, middleBorder, edgeData);
                }
            } else if (middleBorder <= leftBorder && middleBorder <= rightBorder) {//middle is least
                if (Math.abs(leftBorder - rightBorder) < 6) {
                    newBorder = linRegHorizontal(leftBorder, rightBorder, edgeData);
                } else if (leftBorder > rightBorder) {
                    newBorder = linRegHorizontal(middleBorder, rightBorder, edgeData);
                } else {
                    newBorder = linRegHorizontal(leftBorder, middleBorder, edgeData);
                }
            } else if (Math.abs(leftBorder - middleBorder) < 4) {//right is least
                newBorder = linRegHorizontal(leftBorder, middleBorder, edgeData);
            } else if (leftBorder > middleBorder) {
                newBorder = linRegHorizontal(middleBorder, rightBorder, edgeData);
            } else {
                newBorder = linRegHorizontal(leftBorder, rightBorder, edgeData);
            }
            
            borders.add(newBorder);

            //find hBorder below center point
            //first find left, middle, and right segments of border
            leftBorder = -1;
            middleBorder = -1;
            rightBorder = -1;
            for (int row = cy + 1; row < h; row++) {
                if (consecutiveEdges(edgeData, consecutiveThresh, row, 0, w / 3)) {
                    if (leftBorder == -1) {
                        leftBorder = row;
                    }
                }
                if (consecutiveEdges(edgeData, consecutiveThresh, row, w / 3, w * 2 / 3)) {
                    if (middleBorder == -1) {
                        middleBorder = row;
                    }
                }
                if (consecutiveEdges(edgeData, consecutiveThresh, row, w * 2 / 3, w)) {
                    if (rightBorder == -1) {
                        rightBorder = row;
                    }
                }
            }

            System.out.println("lower lb: " + leftBorder + " mb: " + middleBorder + " rb: " + rightBorder);

            newBorder = new double[2];
            if (leftBorder >= middleBorder && leftBorder >= rightBorder) { //left is greatest
                if (Math.abs(middleBorder - rightBorder) < 4) {
                    newBorder = linRegHorizontal(middleBorder, rightBorder, edgeData);
                } else if (middleBorder < rightBorder) {
                    newBorder = linRegHorizontal(leftBorder, rightBorder, edgeData);
                } else {
                    newBorder = linRegHorizontal(leftBorder, middleBorder, edgeData);
                }
            } else if (middleBorder >= leftBorder && middleBorder >= rightBorder) {//middle is greatest
                if (Math.abs(leftBorder - rightBorder) < 6) {
                    newBorder = linRegHorizontal(leftBorder, rightBorder, edgeData);
                } else if (leftBorder < rightBorder) {
                    newBorder = linRegHorizontal(middleBorder, rightBorder, edgeData);
                } else {
                    newBorder = linRegHorizontal(leftBorder, middleBorder, edgeData);
                }
            } else if (Math.abs(leftBorder - middleBorder) < 4) {//right is greatest
                newBorder = linRegHorizontal(leftBorder, middleBorder, edgeData);
            } else if (leftBorder < middleBorder) {
                newBorder = linRegHorizontal(middleBorder, rightBorder, edgeData);
            } else {
                newBorder = linRegHorizontal(leftBorder, rightBorder, edgeData);
            }
            
            borders.add(newBorder);
        }
        for(int i = 0; i < borders.size(); i++){
            System.out.println("equations:");
            System.out.println("m: " + borders.get(i)[0] + " b: " + borders.get(i)[1]);
        }
        return borders;
    }

    private double[] linRegHorizontal(int n, int m, int[][] edgePoints) {
        System.out.println("n: " + n + " m: " + m);
        SimpleRegression regression = new SimpleRegression();
        double[] eq = new double[2];
        ArrayList<double[]> data = new ArrayList();
        for (int i = 0; i < edgePoints.length; i++) {
            //System.out.println("n: " + n + " i: " + i);
            if (edgePoints[i][n] == 2) {
                double[] tempPoint = {(double) i, (double) n};
                data.add(tempPoint);
            }
            if (edgePoints[i][m] == 2) {
                double[] tempPoint = {(double) i, (double) m};
                data.add(tempPoint);
            }
        }
        double[][] arrayData = new double[data.size()][2];
        regression.addData(data.toArray(arrayData));
        eq[0] = regression.getSlope();
        eq[1] = regression.getIntercept();
        return eq;
    }

    private boolean consecutiveEdges(int[][] edgePoints, int thresh, int row, int lb, int rb) {
        //returns true if there are thresh consecutive only horizontal-edge pixels between left-bound(lb) and right bound (rb) in row 'row'.  
        int consecutiveCount = 0;
        for (int i = lb; i < rb; i++) {
            if (edgePoints[i][row] == 2) {
                consecutiveCount++;
                if (consecutiveCount >= thresh) {
                    return true;
                }
            } else {
                consecutiveCount = 0;
            }
        }
        return false;
    }

    private int[] sepColors(int n) {
        int[] colors = new int[3];
        colors[0] = (n >> 16) & 0xFF;
        colors[1] = (n >> 8) & 0xFF;
        colors[2] = n & 0xFF;
        return colors;
    }
    
    public BufferedImage rotate(BufferedImage img) {    
        int w = img.getWidth();    
        int h = img.getHeight();    
        BufferedImage newImage = new BufferedImage(h, w, img.getType());  
        Graphics2D g2 = newImage.createGraphics();  
        g2.rotate(Math.toRadians(90), w/2, h/2);    
        g2.drawImage(img,null,0,0);  
        return newImage;    
    }    

    public static void main(String[] args) {

        JFrame w = new JFrame("array image");
        w.setSize(800, 500);
        w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageProc2 panel = new ImageProc2();
        panel.setBackground(Color.WHITE);
        Container c = w.getContentPane();

        c.add(panel);

        w.setVisible(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(borderImage, 20, 20, this);
        g.drawImage(hvBorderImage, img.getWidth() + 50, 20, this);
    }
}
