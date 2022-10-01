/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.metric;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pal.tree.Tree;
import treecmp.test.util.TreeCreator;

/**
 *
 * @author Damian
 */
public class MastBinMetricTest {
    
    public MastBinMetricTest() {
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
     * Test of getDistance method, of class MastBinMetric.
     */
    @Test
    public void testGetDistance() {
        System.out.println("getDistance");
        Tree t1 = TreeCreator.getTreeFromString("((a,b),(c,d));");
        Tree t2 = TreeCreator.getTreeFromString("((a,b),(c,d));");
        MastBinMetric instance = new MastBinMetric();
        double expResult = 0.0;
        double result = instance.getDistance(t1, t2);
        assertEquals(expResult, result, 0.0);
    }
    
     @Test
    public void testGetDistance2() {
        System.out.println("getDistance2");
        Tree t1 = TreeCreator.getTreeFromString("((a,b),((c,d),e));");
        Tree t2 = TreeCreator.getTreeFromString("((a,c),((b,e),d));");
        MastBinMetric instance = new MastBinMetric();
        MastMetric instance2 = new MastMetric();
        double expResult = instance2.getDistance(t1, t2);
        double result = instance.getDistance(t1, t2);
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
    }
}