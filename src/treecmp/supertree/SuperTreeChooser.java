/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.supertree;

import java.util.List;
import pal.tree.Tree;
import treecmp.common.DifferentLeafSetUtils;
import treecmp.common.TreeCmpException;
import treecmp.metric.Metric;
import treecmp.spr.BestTreeChooser;

/**
 *
 * @author Damian
 */
public class SuperTreeChooser implements BestTreeChooser {

   private Metric m;
   private List<Tree> inputTrees;

    public SuperTreeChooser(Metric m, List<Tree> inputTrees) {
        this.m = m;
        this.inputTrees = inputTrees;
    }



    public double getValueForTree(Tree tree) throws TreeCmpException{
       return calcSuperTreeDist(tree, inputTrees, m);
    }

   protected double calcSuperTreeDist(Tree sTree, List<Tree> inputTrees, Metric m) throws TreeCmpException{

    double sum = 0.0;
    for(Tree inputTree: inputTrees){
        Tree[] trees =  DifferentLeafSetUtils.pruneTrees(sTree, inputTree);
        Tree sTreePruned = trees[0];
        Tree inputTreePruned = trees[1];
        double dist = m.getDistance(sTreePruned, inputTreePruned);
        sum += dist;
    }
    return sum;
}

}
