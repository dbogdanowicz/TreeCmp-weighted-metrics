/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.supertree;

import java.util.ArrayList;
import java.util.List;
import pal.tree.Tree;

/**
 *
 * @author Damian
 */
public class SuperTreeResultData {
    private Tree superTree;
    private double dist;
    private List<Tree> inputTrees;
    private List<Double> distList = new ArrayList<Double>();

    public int getNumSPRs() {
        return distList.size() - 1;
    }

    public List<Double> getDistList() {
        return distList;
    }

    public void insertDist(double dist){
        distList.add(dist);
    }

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }

    public List<Tree> getInputTrees() {
        return inputTrees;
    }

    public void setInputTrees(List<Tree> inputTrees) {
        this.inputTrees = inputTrees;
    }

    public Tree getSuperTree() {
        return superTree;
    }

    public void setSuperTree(Tree superTree) {
        this.superTree = superTree;
    }


}
