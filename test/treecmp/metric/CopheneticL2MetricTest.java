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
public class CopheneticL2MetricTest {
    
    public CopheneticL2MetricTest() {
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
     * Test of getDistance method, of class CopheneticL2Metric.
     */
    @Test
    public void testGetDistance() {
        System.out.println("getDistance");
        Tree t1 = TreeCreator.getTreeFromString("((a,b),(c,d));");
        Tree t2 = TreeCreator.getTreeFromString("((a,b),(c,d));");
        CopheneticL2Metric instance = new CopheneticL2Metric();
        double expResult = 0.0;
        double result = instance.getDistance(t1, t2);
        assertEquals(expResult, result, 0.0);
    }
  @Test
    public void testGetDistance2() {
        System.out.println("getDistance2");
        Tree t1 = TreeCreator.getTreeFromString("((a,b),((c,d),e));");
        Tree t2 = TreeCreator.getTreeFromString("((a,c),((b,e),d));");
        CopheneticL2Metric instance = new CopheneticL2Metric();
        double expResult = 3.46;
        double result = instance.getDistance(t1, t2);
        assertEquals(expResult, result, 0.01);
    }
    
}
