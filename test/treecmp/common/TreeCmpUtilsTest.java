/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.common;

import org.junit.Test;
import pal.tree.Node;
import treecmp.test.util.TreeCreator;

/**
 *
 * @author Damian
 */
public class TreeCmpUtilsTest {
     @Test
    public void testGetNodesInPostOrder(){
        Node[] nodes = TreeCmpUtils.getNodesInPostOrder(TreeCreator.getTreeFromString("((a,b),c);"));
        for (Node n: nodes) {
            System.out.println(n);
        }
     } 
}
