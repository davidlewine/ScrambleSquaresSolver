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
import org.apache.commons.math3.stat.descriptive.*;

public class ImageGame extends JPanel {

    // instance variables - replace the example below with your own
    int imgHeight, imgWidth;

    BufferedImage img;
    BufferedImage borderImage;
    BufferedImage hvBorderImage;
    BufferedImage rotatedImage;
    BufferedImage borderSkeletonImage;
    BufferedImage edgeImage;
    BufferedImage squaresBasedImage;
    BufferedImage processedImage;
    //BufferedImage[] subImages = new BufferedImage[3];
    ArrayList<double[]> borderEqs = new ArrayList();
    Square[][] squares = new Square[3][3];
    int pixelStripLength = 12;
    //edges is a field now but should really be what the function hvBorderImage returns (instead of returning an image).
    ArrayList<ArrayList<Pixel[]>> edges = new ArrayList();//should be of sixe 36, with first 18 being horizontal edges and second 18 being vertical edges.
    ArrayList<Integer>[] neighborGroups = new ArrayList[36];
    double[][][] neighborData = new double[36][36][2];

    public ImageGame() {

        try {
            //img = ImageIO.read(new File("teapot.PNG"));
            //img = ImageIO.read(new URL("http://cdn.rainbowresource.netdna-cdn.com/products/010871.jpg"));
            String path = "http://cdn.rainbowresource.netdna-cdn.com/products/010871.jpg";
            //System.out.println(img.getType());
            //img = ImageIO.read(new File("kittens.jpg"));
            //String path = "http://www.bendixens.com/mm5/graphics/00000001/scramhummingbirds.jpg";
            //String path = "http://www.theoriginalhorsetackcompany.com/images_products/bats-scramble-squares-8216big.jpg";
            //String path = "http://s5.thisnext.com/media/largest_dimension/Symphony-Scramble-Squares_5DECA6A5.jpg";

            URL url = new URL(path);
            img = ImageIO.read(url);
            imgWidth = img.getWidth();
            imgHeight = img.getHeight();
//        for (Square[] sa : squares) {
//            for (Square s : sa) {
//                s = new Square(img);
//            }
//        }
            squares = processImage(img);
            //getNeighbors(squares);
            squaresBasedImage = roiMapImg(squares);

            //print cornerFeatures for each regionOI of each square:
//        for(Square[] rowOfSquares: squares){
//            for(Square square: rowOfSquares){
//                for(Edge edge: square.edges){
//                        for(Feature f: edge.roi.features){
//                            
//                            //System.out.println("x: " + f.x + " y: " + f.y + " score: " + f.score + " edge: " + edge.toString());
//                        }
//                        //System.out.println();
//                    
//                }
//            }
//        }
            writeDataFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void writeDataFile() throws IOException {
//        ArrayList<Pixel> pixelList = new ArrayList();
//        BufferedWriter bw = new BufferedWriter(new FileWriter("Rdata.txt"));
//        
//        for(Square[] sqArr: squares){
//            for(Square sq: sqArr){
//                for(Edge edge: sq.edges){
//                    for(Pixel[] pixSet: edge.pixelSets){
//                        for(Pixel pixel: pixSet){
//                            pixelList.add(pixel);
//                        }
//                    }
//                }
//            }
//        }
//        Collections.sort(pixelList);
//        pixIdData(bw, pixelList);
//        //countData(bw, pixelList);
//
//        
//        bw.close();
    }

    private void countData(BufferedWriter bw, ArrayList<Pixel> pixelList) {
        try {
            bw.write("red green blue count");
            bw.newLine();
            int pCount = 1;
            for (int i = 0; i < pixelList.size() - 1; i++) {
                if (pixelList.get(i).compareTo(pixelList.get(i + 1)) == 0) {
                    pCount++;
                } else {
                    bw.write("" + pixelList.get(i).red + " " + pixelList.get(i).green + " " + pixelList.get(i).blue + " " + pCount);
                    bw.newLine();
                    pCount = 1;
                }
            }
            //handle last item:
            bw.write("" + pixelList.get(pixelList.size() - 1).red + " " + pixelList.get(pixelList.size() - 1).green + " " + pixelList.get(pixelList.size() - 1).blue + " " + pCount);
            bw.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void pixIdData(BufferedWriter bw, ArrayList<Pixel> pixelList) {
        try {
            bw.write("red green blue id");
            bw.newLine();
            for (int i = pixelList.size() / 4; i < 3 * pixelList.size() / 4; i++) {
                bw.write("" + pixelList.get(i).red + " " + pixelList.get(i).green + " " + pixelList.get(i).blue
                        + " " + (pixelList.get(i).squareNum * 4 + pixelList.get(i).edgeNum));
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void compare(Edge a, Edge b) {

    }

    public int[][] getNeighbors(Square[][] squares) {
        int maxGroupLength = 12;
        int[][] neighborPairs = new int[36][3];
        int[][] distInfo = new int[36][36];
        for (int r = 0; r < 3; r++) {
            for (int s = 0; s < 3; s++) {
                for (int e = 0; e < 4; e++) {
                    int edgeCount = 0;
                    int minDist = 1000000;
                    int neighbor = 0;
                    for (int r2 = 0; r2 < 3; r2++) {
                        for (int s2 = 0; s2 < 3; s2++) {
                            for (int e2 = 0; e2 < 4; e2++) {
                                squares[r][s].edges[e].neighborInfo[edgeCount][0] = edgeCount;
                                squares[r][s].edges[e].neighborInfo[edgeCount][1] = -1;
                                neighborData[(r * 3 + s) * 4 + e][edgeCount][0] = edgeCount;
                                neighborData[(r * 3 + s) * 4 + e][edgeCount][1] = -1;

                                if (squares[r][s].edges[e] != squares[r2][s2].edges[e2]) {
                                    int dist = squares[r][s].edges[e].roi.compareTo(squares[r2][s2].edges[e2].roi);
                                    squares[r][s].edges[e].neighborInfo[edgeCount][1] = dist;
                                    neighborData[(r * 3 + s) * 4 + e][edgeCount][1] = dist;

                                    if (dist < minDist) {
                                        minDist = dist;
                                        neighbor = (r2 * 3 + s2) * 4 + e2;
                                    }
                                }
                                edgeCount++;
                            }
                        }
                    }
                    neighborPairs[(r * 3 + s) * 4 + e][0] = (r * 3 + s) * 4 + e;
                    neighborPairs[(r * 3 + s) * 4 + e][1] = neighbor;
                    neighborPairs[(r * 3 + s) * 4 + e][2] = minDist;
                }
            }
        }
        //create distInfo 2d array
        System.out.println("&&&&&&&&& dist info &&&&&&&");
        for(int p = 0; p < 36; p++){
            for(int q = 0; q < 36; q++){
                distInfo[p][q] = (int)neighborData[p][q][1];
                System.out.print(distInfo[p][q] + ",");
            }
            System.out.println();
        }
       
//        for(int[] n: neighborPairs){
//            System.out.println(Arrays.toString(n)); 
//        }
//        System.out.println();
        int edgeCount = 0;
//        for(Square[] row: squares){
//            for(Square square: row){
//                for(Edge edge: square.edges){
//                    sort(edge.neighborInfo);
//                    for(int i = 0; i < edge.neighborInfo.length; i++){
//                        System.out.print("" + edge.neighborInfo[i][0] + " " );
//                    }
//                    System.out.println();
//                    for(int i = 0; i < edge.neighborInfo.length; i++){
//                        System.out.print("" + edge.neighborInfo[i][1] + " " );
//                    }
//                    System.out.println();
//                }
//            }
//        }

        for (int i = 0; i < neighborData.length; i++) {
            sort(neighborData[i]);
            for (int j = 0; j < neighborData[i].length; j++) {
                System.out.print("" + neighborData[i][j][0] + " ");
            }
            System.out.println();
            for (int j = 0; j < neighborData[i].length; j++) {
                System.out.print("" + neighborData[i][j][1] + " ");
            }
            System.out.println();

        }
//        System.out.println("$$$$$$$$$$$$$$$$$$$$$");

        //get groups usinsg neighborData v1
//        for(int i = 0; i < neighborData.length; i++){
//            int upperThresh = 30;
//            int lowerThresh = 5;
//            sort(neighborData[i]);
//            ArrayList<Integer> group = new ArrayList<Integer>();
//            group.add(neighborData[i][0][0]);//add first element of the edge's data to group
//            for(int j = 1; j < neighborData[i].length-1;j++){
//                int m;
//                if(j == 1) m = 2;
//                else m = 1;
//                if(neighborData[i][j][1]-neighborData[i][j-1][1] < upperThresh*m || neighborData[i][j+1][1]-neighborData[i][j][1] > lowerThresh){
//                    group.add(neighborData[i][j][0]);
//                }
//                else{
//                    break;
//                }
//            } 
//            System.out.println(Arrays.toString(group.toArray()));
//            neighborGroups[i] = group;
//        }
        //get groups using neighborData v2 lin reg.
        //for (int n = 8; n < 30; n++) {//n = max group size
        ArrayList<ArrayList<Integer>> finalGroups = new ArrayList();
        int maxGroup = 20;
                int minGroup = 5;
                for(int m = minGroup; m < maxGroup; m++){
            for (int i = 0; i < neighborData.length; i++) {//for each edge
                int groupBound = -1;
                ArrayList<Integer> tempGroup = new ArrayList<Integer>();
                
                
//                SimpleRegression regression = new SimpleRegression();
//                double minError = 1000000;
//                
//                //getSumSquaredErrors();
//                //System.out.println("" + i + ": ");
//                for (int j = 1; j < n - 1; j++) {//for each division of first n closest edges (in indices 0 - 14)
//                    //get two data sets: 0-j, 15-j
//                    double[][] d1 = new double[j][2];
//                    double[][] d2 = new double[n - j][2];
//                    for (int p = 0; p < n; p++) {
//                        if (p < j) {
//                            d1[p][0] = p;
//                            d1[p][1] = neighborData[i][p][1];
//                        } else {
//                            d2[p - j][0] = p;
//                            d2[p - j][1] = neighborData[i][p][1];
//                        }
//                    }
////                System.out.print("d1: ");
////                for(double[] data: d1){
////                        System.out.print("" + data[0] + " ");
////                }
////                System.out.println();
////                System.out.print("d1: ");
////                for(double[] data: d1){
////                        System.out.print("" + data[1] + " ");
////                }
////                System.out.println();
////                System.out.print("d2: ");
////                for(double[] data: d2){
////                        System.out.print("" + data[0] + " ");
////                }
////                System.out.println();
////                System.out.print("d2: ");
////                for(double[] data: d2){
////                        System.out.print("" + data[1] + " ");
////                }
////                System.out.println();
//
//                    //getSumSquaredErrors of both and add
//                    double d1Error = 0, d2Error = 0;
//                    if (j == 1) {
//                        d1Error = 0;
//                    } else {
//                        regression.addData(d1);
//                        for (double[] datum : d1) {
//                            //d1Error += Math.pow(datum[1] - regression.predict(datum[0]),2);
//                            d1Error += Math.abs(datum[1] - regression.predict(datum[0]));
//                        }
//                        //d1Error = regression.getRSquare();
//                    }
//                    if (j == n - 1) {
//                        d2Error = 0;
//                    } else {
//                        regression.clear();
//                        regression.addData(d2);
//                        for (double[] datum : d2) {
//                            //d2Error += Math.pow(datum[1] - regression.predict(datum[0]),2);
//                            d2Error += Math.abs(datum[1] - regression.predict(datum[0]));
//                        }
//                    }
//                    double errorSum = d1Error + d2Error;
                //System.out.println("e1: " + d1Error + " e2: " + d2Error + " sum: " + errorSum + " ");
                    //look for minimum value of this sum
//                    if (errorSum < minError) {
//                        minError = errorSum;
//                        groupBound = j;
//                    }
//                }
//                
                
//                double minAngle = 180;
//                
//                for(int p = 1; p < m; p++){
//                    double[] a = neighborData[i][0];
//                    double[] b = neighborData[i][p];
//                    double[] c = neighborData[i][m];
//                    double angle = Math.atan((p - 0)/(b[1] - a[1])) + Math.atan(1.0*(c[1] - b[1])/(m - p));
//                    if(angle < minAngle){
//                        minAngle = angle;
//                        groupBound = p;
//                    }
//                }
                
                double minError = 1000;
                
                for(int p = 1; p < m; p++){
                    double[] a = neighborData[i][0];
                    double[] b = neighborData[i][p];
                    double[] c = neighborData[i][m];
                    double slopeAB = (b[1] - a[1])/p;
                    double interceptAB = 0;
                    double errorAB = 0;
                    double slopeBC = (c[1] - b[1])/m-p;
                    double interceptBC = c[1] - slopeBC*m;
                    double errorBC = 0;
                    for(int x = 0; x <= p; x++){
                        errorAB += Math.abs((slopeAB*x + interceptAB) - neighborData[i][x][1]);
                    }
                    for(int x = p; x <= m; x++){
                        errorBC += Math.abs((slopeBC*x + interceptBC) - neighborData[i][x][1]);
                    }
                    
                    
                    
                    if(minError > errorAB + errorBC){
                        minError = errorAB + errorBC;
                        groupBound = p;
                    }
                }
                  
                
                for (int k = 0; k < groupBound; k++) {
                    tempGroup.add((int)(neighborData[i][k][0]));
                }

                System.out.println(Arrays.toString(tempGroup.toArray()));
                neighborGroups[i] = tempGroup;
            
            }

            finalGroups = cullGroups(neighborGroups);
            if(finalGroups.size() == 8){
                break;
            }
            }
//            System.out.println("n: " + n + " groups: " + finalGroups.size());
            //if (finalGroups.size() == 8) {
                System.out.println("%%%%%%%% final groups %%%%%%%%%%%%");
                for (ArrayList group : finalGroups) {
                    System.out.println(Arrays.toString(group.toArray()));
                }
           //}

        
        return distInfo;
    }

    private ArrayList<ArrayList<Integer>> cullGroups(ArrayList<Integer>[] neighborGroups) {
        //ignore groups of more than 10.
        //given that size of x <= size of y:
        //if the intersection of x and y is empty, keep x and y.
        //if x is a subset of y, discard x and keep y.
        //if x is not a subset of y and their intesection is not empy, discard both.
        ArrayList<ArrayList<Integer>> finalGroups = new ArrayList();
        boolean addNew = false;
        for (int i = 0; i < neighborGroups.length; i++) {
            addNew = false;
            ArrayList<Integer> removalList = new ArrayList();
            if (neighborGroups[i].size() < 11) {
                addNew = true;
                for (int j = 0; j < finalGroups.size(); j++) {
                    switch (compareNeighborGroups(neighborGroups[i], finalGroups.get(j))) {

                        //intersection is empty
                        case 0:
                            break;
                        //old is subset of new
                        case 1:
                            removalList.add(j);
                            break;
                        // intersection is not empty but neither is a subset
                        case 2:
                            removalList.add(j);
                            addNew = false;
                            break;
                        //new is a subset of old;
                        case 3:
                            addNew = false;
                            ;
                        default:
                            break;
                    }

                }
            }

//            System.out.println(Arrays.toString(removalList.toArray()));
            for (int r = removalList.size() - 1; r >= 0; r--) {
                finalGroups.remove(removalList.get(r));
            }
            if (addNew) {
                finalGroups.add(neighborGroups[i]);
            }
        }

        return finalGroups;
    }

    private int compareNeighborGroups(ArrayList<Integer> newGroup, ArrayList<Integer> oldGroup) {
        //return 0 if groups dont intersect; 1 if old is a subset of new; 2 if intersection is not empty and not 1, 3 otherwise
        int intersectionCount = 0;
        if (oldGroup.size() < newGroup.size()) {
            for (int i = 0; i < oldGroup.size(); i++) {
                if (newGroup.contains(oldGroup.get(i))) {
                    intersectionCount++;
                }
            }
            if (intersectionCount == 0) {
                return 0;
            } else if (intersectionCount == oldGroup.size()) {
                return 1;
            } else {
                return 2;
            }
        } else {
            for (int i = 0; i < newGroup.size(); i++) {
                if (oldGroup.contains(newGroup.get(i))) {
                    intersectionCount++;
                }
            }
            if (intersectionCount == 0) {
                return 0;
            } else if (intersectionCount == newGroup.size()) {
                return 3;
            } else {
                return 2;
            }

        }
    }

    private void sort(int[][] neighborInfo) {
        for (int i = 1; i < neighborInfo.length; i++) {
            int place = i - 1;
            int[] temp = neighborInfo[i];
            while (place >= 0 && neighborInfo[place][1] > temp[1]) {
                neighborInfo[place + 1] = neighborInfo[place];
                place--;
            }
            neighborInfo[place + 1] = temp;
        }
    }

    private void sort(double[][] neighborInfo) {
        for (int i = 1; i < neighborInfo.length; i++) {
            int place = i - 1;
            double[] temp = neighborInfo[i];
            while (place >= 0 && neighborInfo[place][1] > temp[1]) {
                neighborInfo[place + 1] = neighborInfo[place];
                place--;
            }
            neighborInfo[place + 1] = temp;
        }
    }

    private double corner(BufferedImage img, int x, int y) {
        //calculates corner score.
        double dxr = dPix(img, x, y, x + 1, y);
        double dxl = dPix(img, x, y, x - 1, y);
        double dyd = dPix(img, x, y, x, y + 1);
        double dyu = dPix(img, x, y, x, y - 1);
        return 0;
    }

    private double dPix(BufferedImage img, int x1, int y1, int x2, int y2) {
        int[] rgb1 = sepColors(img.getRGB(x1, y1));
        int[] rgb2 = sepColors(img.getRGB(x2, y2));
        return Math.abs(rgb1[0] - rgb2[0]) + Math.abs(rgb1[1] - rgb2[1]) + Math.abs(rgb1[2] - rgb2[2]);
    }

    public Square[][] processImage(BufferedImage img) {
        //this is where the loop varying edgeThresh and consThresh will go.
        //result could perhaps be tested with corners or squares
        int edgeThresh;
        int consThresh;
        double[][][][] bestCorners = new double[3][3][4][2];
        double minEdgeSD = 1000;

        for (edgeThresh = 50; edgeThresh < 120; edgeThresh += 10) {
            for (consThresh = 5; consThresh < 12; consThresh++) {
                try {
                    borderEqs = new ArrayList();

                    borderImage = hEdgeImage(img, edgeThresh, consThresh);
                    edgeImage = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
                    rotatedImage = rotate(borderImage);
                    hvBorderImage = hEdgeImage(rotatedImage, edgeThresh, consThresh);
        //at this point, 'edges' list is complete but unProcessed.
                    //second 18 edges must be transformed to vertical edges,
                    //then edges must be assembled into squares.

                    //rotate equations 18 - 35;
                    for (int i = 18; i < 36; i++) {
                        borderEqs.set(i, rotateEq(borderEqs.get(i)));
                    }
//                for(double[] eq: borderEqs){
//                    System.out.println(" " + Arrays.toString(eq));
//                }
//                System.out.println();

                    double[][][][] corners = getSquareCorners(borderEqs);
                    double edgeSD = getEdgeSD(corners);
                    if (edgeSD < minEdgeSD) {
                        minEdgeSD = edgeSD;
                        bestCorners = corners;
                    }
                    System.out.println("" + edgeThresh + " " + consThresh + " " + edgeSD);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("min sd: " + minEdgeSD);

        processedImage = cornerImage(img, bestCorners);
        //return createSquares(edges);
        return createSquares(bestCorners);
    }

    private double getEdgeSD(double[][][][] corners) {

        double[] edgeLengths = new double[36];
        for (int row = 0; row < 3; row++) {
            for (int square = 0; square < 3; square++) {
                for (int corner = 0; corner < 4; corner++) {//for each corner of the square
                    edgeLengths[(row * 3 + square) * 4 + corner] = Process.eucDist(corners[row][square][corner], corners[row][square][(corner + 1) % 4]);
                }
            }
        }
//        System.out.println(Arrays.toString(edgeLengths));
        double sd = (new DescriptiveStatistics(edgeLengths)).getStandardDeviation();
        return sd;
    }

    private double[][][][] getSquareCorners(ArrayList<double[]> eqs) {
        double[][][][] corners = new double[3][3][4][2];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                corners[i][j][0] = eqIntersection(eqs.get(2 * i + j * 6), eqs.get(30 - i * 6 + 2 * j));
                corners[i][j][1] = eqIntersection(eqs.get(2 * i + j * 6), eqs.get(31 - i * 6 + 2 * j));
                corners[i][j][2] = eqIntersection(eqs.get(2 * i + 1 + j * 6), eqs.get(31 - i * 6 + 2 * j));
                corners[i][j][3] = eqIntersection(eqs.get(2 * i + 1 + j * 6), eqs.get(30 - i * 6 + 2 * j));
            }
        }

        return corners;
    }

    private double[] eqIntersection(double[] l1, double[] l2) {
        double[] intersection = new double[2];
        intersection[0] = (l1[1] - l2[1]) / (l2[0] - l1[0]);
        intersection[1] = l1[0] * intersection[0] + l1[1];
        return intersection;
    }

    private BufferedImage cornerImage(BufferedImage img, double[][][][] corners) {
        BufferedImage newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        //set newImage to a copy of img.
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {

                newImage.setRGB(i, j, img.getRGB(i, j));
            }
        }
        for (double[][][] row : corners) {
            for (double[][] square : row) {
                for (double[] corner : square) {
                    Graphics g = newImage.getGraphics();
                    g.setColor(Color.red);
                    g.fillOval((int) corner[0], (int) corner[1], 5, 5);
                }
            }
        }
        return newImage;

    }

    private BufferedImage squaresImage1(Square[][] squares) {
        BufferedImage sqsImage = new BufferedImage(4 * squares[0][0].edges[0].pixelSets.size(), 4 * squares[0][0].edges[1].pixelSets.size(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = sqsImage.createGraphics();
        for (Square[] sqArr : squares) {
            for (Square square : sqArr) {
                for (Edge edge : square.edges) {
                    for (Pixel[] pixelSet : edge.pixelSets) {
                        for (Pixel pixel : pixelSet) {
                            sqsImage.setRGB(pixel.x, pixel.y, pixel.color);
                        }
                    }
                }
            }
        }
        return sqsImage;
    }

    private BufferedImage squaresImage2(Square[][] squares) {
        BufferedImage sqsImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = sqsImage.createGraphics();
        for (Square[] row : squares) {
            for (Square square : row) {
                for (Edge edge : square.edges) {
                    BufferedImage edgesROI = edgeTransform(edge.roi.img);
                    g2.drawImage(edgesROI, edge.roi.x, edge.roi.y, this);
                }
            }
        }
        return sqsImage;
    }

    private BufferedImage squaresImage3(Square[][] squares) {
        BufferedImage sqsImage = new BufferedImage(img.getWidth() * 2, img.getHeight() * 2, img.getType());

        Graphics2D g2 = sqsImage.createGraphics();
        int n = 0;
        int x = 10;
        int y = 10;

        for (Square[] row : squares) {
            for (Square square : row) {
                for (Edge edge : square.edges) {
                    BufferedImage oldRoi = edge.roi.img;
                    BufferedImage newRoi = oldRoi;
                    if (edge.idNum == 0) {
                        //BufferedImage edgesROI = edgeTransform(edge.roi.img);
                        //g2.drawImage(edgesROI, edge.roi.x, edge.roi.y, this);
                        //g2.drawImage(newRoi, edge.roi.x, edge.roi.y, this);
                    } else if (edge.idNum == 1) {//create a new roi image rotated 90;
                        newRoi = new BufferedImage(oldRoi.getHeight(), oldRoi.getWidth(), oldRoi.getType());
                        for (int i = 0; i < oldRoi.getWidth(); i++) {
                            for (int j = 0; j < oldRoi.getHeight(); j++) {
                                newRoi.setRGB(j, oldRoi.getWidth() - 1 - i, oldRoi.getRGB(i, j));
                            }
                        }
                        //BufferedImage edgesROI = edgeTransform(newRoi);
                        //g2.drawImage(edgesROI, edge.roi.x, edge.roi.y, this);
                        //g2.drawImage(newRoi, edge.roi.x, edge.roi.y, this);
                    } else if (edge.idNum == 2) {//create a new roi image rotated 180;
                        newRoi = new BufferedImage(oldRoi.getWidth(), oldRoi.getHeight(), oldRoi.getType());
                        for (int i = 0; i < oldRoi.getWidth(); i++) {
                            for (int j = 0; j < oldRoi.getHeight(); j++) {
                                newRoi.setRGB(oldRoi.getWidth() - 1 - i, oldRoi.getHeight() - 1 - j, oldRoi.getRGB(i, j));
                            }
                        }
                        //BufferedImage edgesROI = edgeTransform(newRoi);
                        //g2.drawImage(edgesROI, edge.roi.x, edge.roi.y, this);
                        //g2.drawImage(newRoi, edge.roi.x, edge.roi.y, this);
                    } else if (edge.idNum == 3) {//create a new roi image rotated 270;
                        newRoi = new BufferedImage(oldRoi.getHeight(), oldRoi.getWidth(), oldRoi.getType());
                        for (int i = 0; i < oldRoi.getWidth(); i++) {
                            for (int j = 0; j < oldRoi.getHeight(); j++) {
                                newRoi.setRGB(oldRoi.getHeight() - 1 - j, i, oldRoi.getRGB(i, j));
                            }
                        }
                        //BufferedImage edgesROI = edgeTransform(newRoi);
                        //g2.drawImage(edgesROI, edge.roi.x, edge.roi.y, this);
                        //g2.drawImage(newRoi, edge.roi.x, edge.roi.y, this);

                    }

                    x = ((n / 4) % 3) * 130;
                    y = (n / 12) * 150 + n % 4 * 35;
                    g2.drawImage(newRoi, x, y, this);
                    g2.setColor(Color.GREEN);
                    g2.setFont(g2.getFont().deriveFont(22));
                    g2.drawString("" + n, x + 100, y + 10);
                    System.out.println(n);
                    n += 1;

//                    Feature newRoiFeature = new Feature(newRoi);
//                    g2.setColor(Color.YELLOW);
//                    g2.drawString(String.format( "%.1f", newRoiFeature.score), edge.roi.x + 10, edge.roi.y-10);
                }
            }
        }
        return sqsImage;
    }

    private BufferedImage roiMapImg(Square[][] squares) {
        BufferedImage map = new BufferedImage(img.getWidth() * 2, img.getHeight() * 2, img.getType());

        Graphics2D g2 = map.createGraphics();
        int n = 0;
        int x = 10;
        int y = 10;
        int[][] neighborPairs = getNeighbors(squares);

        for (Square[] row : squares) {
            for (Square square : row) {
                for (Edge edge : square.edges) {
                    BufferedImage roiImg = edge.roi.img;

                    x = ((n / 4) % 3) * 130;
                    y = (n / 12) * 150 + n % 4 * 35;
                    g2.drawImage(roiImg, x, y, this);
                    g2.setColor(Color.GREEN);
                    g2.setFont(g2.getFont().deriveFont(22));
                    g2.drawString("" + n + " " + neighborPairs[n][1], x + 75, y + 10);
                    System.out.println(n);
                    n += 1;

//                    Feature newRoiFeature = new Feature(newRoi);
//                    g2.setColor(Color.YELLOW);
//                    g2.drawString(String.format( "%.1f", newRoiFeature.score), edge.roi.x + 10, edge.roi.y-10);
                }
            }
        }
        return map;
    }

    private BufferedImage edgeTransform(BufferedImage img) {
        CannyEdgeDetector detector = new CannyEdgeDetector();
        //adjust its parameters as desired
        detector.setLowThreshold(.1f);
        detector.setHighThreshold(1f);
        //apply it to an image
        detector.setSourceImage(img);
        detector.process();
        return detector.getEdgesImage();
    }

    private BufferedImage squareImage(Square square) {
        //create an image of edge strips based on information in square.
        BufferedImage sqImage = new BufferedImage(square.edges[0].pixelSets.size() + 20, square.edges[1].pixelSets.size() + 20, BufferedImage.TYPE_INT_RGB);
        for (Edge edge : square.edges) {
            for (Pixel[] pixelSet : edge.pixelSets) {
                for (Pixel pixel : pixelSet) {
                    sqImage.setRGB(pixel.x, pixel.y, pixel.color);
                }
            }
        }
        return sqImage;
    }

    public int vEdgeScore(int x, int y, BufferedImage img) {
        int dLeft = 0, dRight = 0;
        int[] pixelRgb = sepColors(img.getRGB(x, y));
        if (x < img.getWidth() - 2) {
            int[] pixRightRgb = sepColors(img.getRGB(x + 2, y));
            for (int i = 0; i < 3; i++) {
                dRight += Math.abs(pixelRgb[i] - pixRightRgb[i]);
            }
        } else {
            dRight = 750;
        }
        if (x > 1) {
            int[] pixLeftRgb = sepColors(img.getRGB(x - 2, y));
            for (int i = 0; i < 3; i++) {
                dLeft += Math.abs(pixelRgb[i] - pixLeftRgb[i]);
            }
        } else {
            dLeft = 750;
        }

        return Math.abs(dRight - dLeft);

    }

    public int hEdgeScore(int x, int y, BufferedImage img) {
        //compares change from n-1 to n to n+1
        //For n-2 to n to n+2, change img.getHeight()-1 to -2
        //and y+1 to y+2
        int dUp = 0, dDown = 0;
        int[] pixelRgb = sepColors(img.getRGB(x, y));
        if (y < img.getHeight() - 2) {
            int[] pixDownRgb = sepColors(img.getRGB(x, y + 2));
            for (int i = 0; i < 3; i++) {
                dDown += Math.abs(pixelRgb[i] - pixDownRgb[i]);
            }
        } else {
            dDown = 750;
        }
        //for n-2 to n to n+2, 
        //change 0 to 1, y - 1 to y - 2.
        if (y > 1) {
            int[] pixUpRgb = sepColors(img.getRGB(x, y - 2));
            for (int i = 0; i < 3; i++) {
                dUp += Math.abs(pixelRgb[i] - pixUpRgb[i]);
            }
        } else {
            dUp = 750;
        }

        return Math.abs(dDown - dUp);
    }

    private BufferedImage hEdgeImage(BufferedImage img, int edgeThresh, int consThresh) {
        //this is called twice, first with img, then with img rotated 90 degrees so that 
        //the vertical edges get created as horizontal edges.
        BufferedImage pImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        //set pImage to a copy of img.
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {

                pImage.setRGB(i, j, img.getRGB(i, j));
            }
        }
        //divide pInage into three vertical strip subimages.
//        BufferedImage[] subImages = new BufferedImage[3];
//        for (int i = 0; i < 3; i++) {
//            subImages[i] = pImage.getSubimage(i * pImage.getWidth() / 3, 0, pImage.getWidth() / 3, pImage.getHeight());
//        }

        //divide data into three vertical strips
        int[][] data = vhEdgeData(pImage, edgeThresh);
        int[][][] dataStrips = new int[3][data.length / 3][data[0].length];
        for (int p = 0; p < 3; p++) {
            for (int i = 0; i < dataStrips[p].length; i++) {
                for (int j = 0; j < dataStrips[p][0].length; j++) {
                    dataStrips[p][i][j] = data[i + p * data.length / 3][j];
                }
            }
        }

        //for each strip, get equations and draw lines and construct edges.
        for (int i = 0; i < dataStrips.length; i++) {
            ArrayList<double[]> newHBorderEqs = getHBorderEqs(dataStrips[i], consThresh);
            for (int j = 0; j < newHBorderEqs.size(); j++) {//for each equation, draw line
                double[] eq = newHBorderEqs.get(j);
                borderEqs.add(eq);
                ArrayList<Pixel[]> newEdgePixelSets = new ArrayList();
                for (int x = 0; x < dataStrips[i].length; x++) {//for each column or x coordinate in subImage
                    int px = x + i * dataStrips[i].length;

                    Pixel[] newPixelSet = new Pixel[pixelStripLength];
                    //pixelSet is a row of 3 or so pixels along the y axis to store the pixel data 
                    //from the first three or so rows in from the edge of the square.
                    //for even values of j, i.e. top edges, use py + p, and for odd values of j,
                    //i.e. for bottom edges, use py - p.
                    for (int p = 0; p < pixelStripLength; p++) {
                        int py = (int) (eq[0] * x + eq[1]) + (2 * (j % 2) - 1) * -p;
                        int pixelColor = pImage.getRGB(px, py);
                        //System.out.println("pixel color: " + pixelColor);
                        Pixel newPixel = new Pixel(pixelColor, px, py);
                        newPixelSet[p] = newPixel;
                        //pImage.setRGB(x + i * dataStrips[i].length, (int) (eq[0] * x + eq[1]), 0xff << 16);
                    }
                    newEdgePixelSets.add(newPixelSet);
                }
                //edges is of type ArrayList<ArrayList<Pixel[]>>: 
                //for each edge there is an array of pixelSets corresponding to each point on the edge
                edges.add(newEdgePixelSets);
            }
        }

        // System.out.println("edges size: " + edges.size());
        // System.out.println("***equations:");
//        for (int i = 0; i < borderEqs.size(); i++) {
//
//               System.out.println("m: " + borderEqs.get(i)[0] + " b: " + borderEqs.get(i)[1]);
//        }
        return pImage;
    }

    private double[] rotateEq(double[] eq) {
        double[] rotatedEq = new double[2];
        if (eq[0] != 0) {
            rotatedEq[0] = -1 / eq[0];
        } else {
            rotatedEq[0] = 999;
        }
        double px = eq[1];
        double py = img.getHeight();
        rotatedEq[1] = py - rotatedEq[0] * px;
        return rotatedEq;
    }

    private Square[][] createSquares(ArrayList<ArrayList<Pixel[]>> edges) {
        Square[][] squares = new Square[3][3]; //create 2D array of squares
        //initialize 2D array of squares 
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Square tempSquare = new Square(img);
                squares[i][j] = tempSquare;
            }
        }

        for (int i = 18; i < 36; i++) {//for each edge that should be a vertical edge
            for (int j = 0; j < edges.get(i).size(); j++) {//for each pixel set from the edge
                for (int k = 0; k < pixelStripLength; k++) {//for each pixel in the set
                    //rotate x and y coordinates of the pixel
                    //*********** also rotate equation 90 degrees ccw
                    Pixel pixel = edges.get(i).get(j)[k];
                    int temp = pixel.x;
                    pixel.x = pixel.y;
                    pixel.y = imgHeight - temp;
                }
            }
        }

        //assign edges to squares and equations to edges
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                squares[j][i].edges[0] = new Edge(edges.get(2 * (3 * i + j)));
                squares[j][i].edges[2] = new Edge(edges.get(2 * (3 * i + j) + 1));
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                squares[2 - i][j].edges[3] = new Edge(edges.get(18 + 2 * (3 * i + j)));
                squares[2 - i][j].edges[1] = new Edge(edges.get(18 + 2 * (3 * i + j) + 1));
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                squares[i][j].setEdgeParents(3 * i + j);//3*i + j becomes the square's idNum
            }
        }

        return squares;
    }

    private Square[][] createSquares(double[][][][] corners) {
        Square[][] squares = new Square[3][3]; //create 2D array of squares
        //initialize 2D array of squares 
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Square tempSquare = new Square(img, corners[i][j]);
                squares[i][j] = tempSquare;
            }
        }
        return squares;
    }

    private int[][] vhEdgeData(BufferedImage img, int edgeThresh) {
        //0 = no edge; 1 = vertical edge; 
        //2 = horizontal edge; 3(2 + 1) = horizontal and vertical edge.
        int thresh = edgeThresh;
        int[][] data = new int[img.getWidth()][img.getHeight()];
        for (int row = 0; row < img.getHeight(); row++) {
            //System.out.println();
            //System.out.print("" + row + ":");
            for (int col = 0; col < img.getWidth(); col++) {
                data[col][row] = 0;
                if (vEdgeScore(col, row, img) >= thresh) {
                    data[col][row] += 1;
                }
                if (hEdgeScore(col, row, img) >= thresh) {
                    data[col][row] += 2;
                }
                //System.out.print(data[col][row]);
            }
        }
        return data;
    }

    private ArrayList<double[]> getHBorderEqs(int[][] edgeData, int consThresh) {
        //vEdge = 1; hEdge = 2; both = 3.
        //find hBorders within edgeData.
        //edgeData is data for a vertical strip 1/3 the width of the whole original puzzle image
        int w = edgeData.length;
        int h = edgeData[0].length;
        int consecutiveThresh = (int) (w / consThresh);
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

            //System.out.println("upper lb: " + leftBorder + " mb: " + middleBorder + " rb: " + rightBorder);
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

            //System.out.println("lower lb: " + leftBorder + " mb: " + middleBorder + " rb: " + rightBorder);
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
//        for(int i = 0; i < borders.size(); i++){
//            System.out.println("equations:");
//            System.out.println("m: " + borders.get(i)[0] + " b: " + borders.get(i)[1]);
//        }
        return borders;
    }

    private double[] linRegHorizontal(int n, int m, int[][] edgePoints) {
        //n and m represent the rows within the edgePoints region to use as basis
        //for regression? (was I worried about shadow or white space along edges?)
        //System.out.println("n: " + n + " m: " + m);
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
        g2.rotate(Math.toRadians(90), w / 2, h / 2);
        g2.drawImage(img, null, 0, 0);
        return newImage;
    }

    public static void main(String[] args) {

        JFrame w = new JFrame("array image");
        w.setSize(800, 800);
        w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageGame panel = new ImageGame();
        panel.setBackground(Color.WHITE);
        Container c = w.getContentPane();

        c.add(panel);

        w.setVisible(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(processedImage, 20, 20, this);
        g.drawImage(squaresBasedImage, img.getWidth() + 50, 20, this);
    }
}
