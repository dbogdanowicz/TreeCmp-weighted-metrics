/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.spr;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pal.tree.Node;
import pal.tree.Tree;

/**
 *
 * @author Damian
 */
public class SprUtilsTest {
    
    public SprUtilsTest() {
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
     * Test of generateRSprNeighbours method, of class SprUtils.
     */
    @Test
    public void testGenerateRSprNeighbours() {
        System.out.println("generateRSprNeighbours");
        Tree tree = null;
        Tree[] expResult = null;
        Tree[] result = SprUtils.generateRSprNeighbours(tree);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findBestNeighbour method, of class SprUtils.
     */
    @Test
    public void testFindBestNeighbour() throws Exception {
        System.out.println("findBestNeighbour");
        Tree tree = null;
        BestTreeChooser btc = null;
        double neighSizeFrac = 0.0;
        double inputTreeValue = 0.0;
        TreeValuePair expResult = null;
        TreeValuePair result = SprUtils.findBestNeighbour(tree, btc, neighSizeFrac, inputTreeValue);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sameParent method, of class SprUtils.
     */
    @Test
    public void testSameParent() {
        System.out.println("sameParent");
        Node n1 = null;
        Node n2 = null;
        boolean expResult = false;
        boolean result = SprUtils.sameParent(n1, n2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isChildParent method, of class SprUtils.
     */
    @Test
    public void testIsChildParent() {
        System.out.println("isChildParent");
        Node n1 = null;
        Node n2 = null;
        boolean expResult = false;
        boolean result = SprUtils.isChildParent(n1, n2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isInnerMove method, of class SprUtils.
     */
    @Test
    public void testIsInnerMove() {
        System.out.println("isInnerMove");
        Node s = null;
        Node t = null;
        boolean expResult = false;
        boolean result = SprUtils.isInnerMove(s, t);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isValidMove method, of class SprUtils.
     */
    @Test
    public void testIsValidMove() {
        System.out.println("isValidMove");
        Node s = null;
        Node t = null;
        boolean expResult = false;
        boolean result = SprUtils.isValidMove(s, t);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNodeDepth method, of class SprUtils.
     */
    @Test
    public void testGetNodeDepth() {
        System.out.println("getNodeDepth");
        Node node = null;
        int expResult = 0;
        int result = SprUtils.getNodeDepth(node);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of calcSprNeighbours method, of class SprUtils.
     */
    @Test
    public void testCalcSprNeighbours() {
        System.out.println("calcSprNeighbours");
        Tree baseTree = null;
        int expResult = 0;
        int result = SprUtils.calcSprNeighbours(baseTree);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createSprTree method, of class SprUtils.
     */
    @Test
    public void testCreateSprTree() {
        System.out.println("createSprTree");
        Tree baseTree = null;
        Node s = null;
        Node t = null;
        Tree expResult = null;
        Tree result = SprUtils.createSprTree(baseTree, s, t);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findChildPos method, of class SprUtils.
     */
    @Test
    public void testFindChildPos() {
        System.out.println("findChildPos");
        Node child = null;
        Node parent = null;
        int expResult = 0;
        int result = SprUtils.findChildPos(child, parent);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findOtherChild method, of class SprUtils.
     */
    @Test
    public void testFindOtherChild() {
        System.out.println("findOtherChild");
        Node child1 = null;
        Node parent = null;
        Node expResult = null;
        Node result = SprUtils.findOtherChild(child1, parent);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}