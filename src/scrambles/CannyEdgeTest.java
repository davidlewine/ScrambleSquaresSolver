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

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class CannyEdgeTest {
    private BufferedImage edges;
    public CannyEdgeTest(BufferedImage img){  
       CannyEdgeDetector detector = new CannyEdgeDetector();
     //adjust its parameters as desired
       detector.setLowThreshold(0.5f);
       detector.setHighThreshold(1f);
    //apply it to an image
       detector.setSourceImage(img);
       detector.process();
       edges = detector.getEdgesImage();
    }
    
    public BufferedImage getEdgesImage(){
        return edges;
    }
 
 
    
}
