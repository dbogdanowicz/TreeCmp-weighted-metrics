/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.spr;

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pal.tree.Tree;

/**
 *
 * @author Damian
 */
public class SubtreeUtilsTest {
    
    public SubtreeUtilsTest() {
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
     * Test of reduceCommonBinarySubtrees method, of class SubtreeUtils.
     */
    @Test
    public void testReduceCommonBinarySubtrees() {
        System.out.println("reduceCommonBinarySubtrees");
        Tree t1 = null;
        Tree t2 = null;
        Tree[] expResult = null;
        Tree[] result = SubtreeUtils.reduceCommonBinarySubtrees(t1, t2);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of reduceCommonBinarySubtreesEx method, of class SubtreeUtils.
     */
    @Test
    public void testReduceCommonBinarySubtreesEx() {
        System.out.println("reduceCommonBinarySubtreesEx");
        Tree t1 = null;
        Tree t2 = null;
        List<SubtreeUtils.SubtreeDef> subtrees = null;
        Tree[] expResult = null;
        Tree[] result = SubtreeUtils.reduceCommonBinarySubtreesEx(t1, t2, subtrees);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}