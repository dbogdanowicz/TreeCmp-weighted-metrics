/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.supertree;

import java.util.List;
import pal.tree.Tree;

/**
 *
 * @author Damian
 */
public class SuperTreeInputData {
    private Tree startTree;
    private List<Tree> inputTrees;

    public List<Tree> getInputTrees() {
        return inputTrees;
    }

    public void setInputTrees(List<Tree> inputTrees) {
        this.inputTrees = inputTrees;
    }

    public Tree getStartTree() {
        return startTree;
    }

    public void setStartTree(Tree startTree) {
        this.startTree = startTree;
    }


}
