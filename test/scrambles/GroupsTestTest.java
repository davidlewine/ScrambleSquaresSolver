/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scrambles;

import java.util.ArrayList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author David
 */
public class GroupsTestTest {
    
    public GroupsTestTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    

    /**
     * Test of getGroups method, of class GroupsTest.
     */
    @Test
    public void testGetGroups() {
        System.out.println("getGroups");
        int[][] dInfo = new int[36][36];
        GroupsTest.getGroups(dInfo);
        
    }

    
    /**
     * Test of getError method, of class GroupsTest.
     */
    @Test
    public void testGetError() {
        System.out.println("getError");
        int[][] data = {{0,2}, {0,3}, {0,6}, {0,7}, {0,10}};
        
        int pos = 2;
        int maxPos = 4;
        double expResult = 2;
        double result = GroupsTest.getError(data, pos, maxPos);
        assertEquals(expResult, result, 0.0);
        
    }

    
    
}
