/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sla;

import java.io.File;
import java.util.Date;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static sla.Tracker.getDay;
import static sla.Tracker.getHourPST;
import static sla.Tracker.getPSTTime;

/**
 *
 * @author Rodrigo
 */
public class TrackerTest {
    
    public TrackerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getPSTTime method, of class Tracker.
     */
    @Test
    public void testGetPSTTime() {
        System.out.println("getPSTTime");
        Date expResult = null;
        Date result = Tracker.getPSTTime();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getDay method, of class Tracker.
     */
    @Test
    public void testGetDay() {
        System.out.println("getDay");
        Date date = Tracker.getPSTTime();
        int expResult = 18;
        int result = Tracker.getDayPST(date);
        System.out.println("result = "+result+" exp = "+ expResult);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getHourPST method, of class Tracker.
     */
    @Test
    public void testGetHourPST() {
        //System.out.println("getHourPST");
        int expResult = 23;
        int result = Tracker.getHourPST();
        System.out.println("hourPST ==" + result);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }


    
    
    @Test
    public void testROWCell(){
        Tracker instance = new Tracker();
        int row = Tracker.getRow(Tracker.getPSTTime());
        System.out.println("row ==" +  row);
    }
}
