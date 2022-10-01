/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.supertree;

import java.util.List;
import java.util.Locale;
import pal.tree.Tree;
import treecmp.common.DifferentLeafSetUtils;
import treecmp.common.NodeUtilsExt;
import treecmp.common.TreeCmpException;
import treecmp.io.ResultWriter;
import treecmp.metric.Metric;
import treecmp.spr.SprUtils;
import treecmp.spr.TreeValuePair;

/**
 *
 * @author Damian
 */
public abstract class AbstractSuperTreeHeuristic {

    private final double NEIGH_SEARCH_RATIO = 1.0;
    public abstract Metric getMetric();


public SuperTreeResultData runSearch (SuperTreeInputData stInputData, ResultWriter out) throws TreeCmpException{

    Tree bestTree = stInputData.getStartTree();
    Tree newTree = null;
    Metric m = getMetric();
    SuperTreeResultData stResultData = new SuperTreeResultData();

    String msg = String.format("Start tree: %s;",  NodeUtilsExt.treeToSimpleString(bestTree, false));
    System.out.println(msg);
    out.setText(msg);
    out.write();

    msg = String.format(Locale.US, "Approximate neighborhood search ratio: %f",  NEIGH_SEARCH_RATIO);
    System.out.println(msg);
    out.setText(msg);
    out.write();
    
    SuperTreeChooser superTreeChooser = new SuperTreeChooser(m, stInputData.getInputTrees());
    double bestValue = superTreeChooser.getValueForTree(bestTree);
    double newValue = bestValue;
    TreeValuePair tvPair = null;
    int i = 1;
    do{
        if (newValue < bestValue){
           bestValue = newValue;
           bestTree = newTree;
        }

        stResultData.insertDist(bestValue);
        System.out.println(String.format("SPR iteration: %d", i));
        out.setText(String.format("SPR iteration: %d", i));
        out.write();

        System.out.println(String.format(Locale.US, "Current best value %f", bestValue));
        out.setText(String.format(Locale.US, "Current best value %f", bestValue));
        out.write();

        tvPair = SprUtils.findBestNeighbour(bestTree, superTreeChooser, NEIGH_SEARCH_RATIO, bestValue);
    
        System.out.println(String.format(Locale.US, "Best dist: %f", tvPair.getValue()));
        out.setText(String.format(Locale.US, "Best dist: %f", tvPair.getValue()));
        out.write();

        System.out.println(String.format(Locale.US, "Best tree: %s", NodeUtilsExt.treeToSimpleString(tvPair.getTree(), false)));
        out.setText(String.format(Locale.US, "Best tree: %s", NodeUtilsExt.treeToSimpleString(tvPair.getTree(), false)));
        out.write();

        newValue = tvPair.getValue();
        newTree = tvPair.getTree();

        System.out.println(String.format(Locale.US, "Old value: %f, current value: %f", bestValue, newValue));
        out.setText(String.format(Locale.US, "Old value: %f, current value: %f", bestValue, newValue));
        out.write();
     
        i++;
    }while (newValue < bestValue);
    
    stResultData.setInputTrees(stInputData.getInputTrees());
    stResultData.setSuperTree(tvPair.getTree());
    stResultData.setDist(tvPair.getValue());
    return stResultData;

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
