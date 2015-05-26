/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scrambles;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;

/**
 *
 * @author David
 */
public class ComplementGroupsTest {
    
    public ComplementGroupsTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of getComplements method, of class ComplementGroups.
     */
    @Ignore @Test
    public void testGetComplements() {
        System.out.println("getComplements");
        ArrayList[] groups = null;
        Square[][] squares = null;
        int[][] expResult = null;
        int[][] result = ComplementGroups.getComplements(groups, squares);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of complementScore method, of class ComplementGroups.
     */
    @Test
    public void testComplementScore() {
        System.out.println("complementScore");
        BufferedImage img1 = new BufferedImage(20, 30, BufferedImage.TYPE_INT_RGB);
        BufferedImage img2 = new BufferedImage(20, 30, BufferedImage.TYPE_INT_RGB);
//        for(int i = 0; i<img1.getWidth ();i++){
//            if(i%10 == 0){
//                img1.setRGB(i, 0, 255);
//                img2.setRGB((img2.getWidth()-1)- i, 0, 255);
//            }
//            
//        }
        img1.setRGB(9, 0, 207);
        img1.setRGB(8, 0, 207);
        img2.setRGB(10, 0, 207);
        double expResult = 207;
        double result = ComplementGroups.complementScore(img1, img2);
        assertEquals(expResult, result, 0.0);
        
    }

    /**
     * Test of hEdgeScore method, of class ComplementGroups.
     */
    @Ignore @Test
    public void testHEdgeScore() {
        System.out.println("hEdgeScore");
        int p1 = 0;
        int p2 = 0;
        double expResult = 0.0;
        double result = ComplementGroups.hEdgeScore(p1, p2);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
