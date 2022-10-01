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
import treecmp.common.NodeUtilsExt;
import treecmp.spr.SubtreeUtils;
import treecmp.test.util.TreeCreator;

/**
 *
 * @author Damian
 */
public class MASTMetricTest {
    
    public MASTMetricTest() {
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
     * Test of getDistance method, of class MASTMetric.
     */
    @Test
    public void testGetDistance() {
        System.out.println("getDistance");
        Tree t1 = TreeCreator.getTreeFromString("((a,b),(c,d));");
        Tree t2 = TreeCreator.getTreeFromString("((a,b),(c,d));");
        MastMetric instance = new MastMetric();
        MatchingPairMetric mp = new MatchingPairMetric();
        MatchingClusterMetric mc = new MatchingClusterMetric();
        RFClusterMetric rfc = new RFClusterMetric();
        TripletMetric tt = new TripletMetric();
        NodalL2SplittedMetric ns = new NodalL2SplittedMetric();
        MastBinMetric mastBin = new MastBinMetric();
        double expResult = 0.0;
        double result = instance.getDistance(t1, t2);
        assertEquals(expResult, result, 0.0);
        
        Tree[] trees =  SubtreeUtils.reduceCommonBinarySubtrees(t1, t2);
        String t1Desc = NodeUtilsExt.treeToSimpleString(trees[0], false) + ";";
        System.out.println(t1Desc);
        String t2Desc = NodeUtilsExt.treeToSimpleString(trees[1], false) + ";";
        System.out.println(t2Desc);
        expResult = 0.0;
        result = instance.getDistance(trees[0],trees[1]);
        assertEquals(expResult, result, 0.0);
        
        result = mp.getDistance(trees[0],trees[1]);        
        assertEquals(expResult, result, 0.0);
        result = mc.getDistance(trees[0],trees[1]);        
        assertEquals(expResult, result, 0.0);    
        result = ns.getDistance(trees[0],trees[1]);        
        assertEquals(expResult, result, 0.0);    
        result = rfc.getDistance(trees[0],trees[1]);        
        assertEquals(expResult, result, 0.0);
        result = tt.getDistance(trees[0],trees[1]);        
        assertEquals(expResult, result, 0.0);
        result = mastBin.getDistance(trees[0],trees[1]);        
        assertEquals(expResult, result, 0.0);
  
      
    }
}