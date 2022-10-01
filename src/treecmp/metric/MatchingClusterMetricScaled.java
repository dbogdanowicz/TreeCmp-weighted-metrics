/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.metric;

import pal.misc.IdGroup;
import pal.tree.Node;
import pal.tree.Tree;
import pal.tree.TreeUtils;
import treecmp.common.ClustIntersectInfoMatrix;
import treecmp.common.HungarianAlgorithm;
import treecmp.common.TreeCmpUtils;

/**
 *
 * @author Damian
 */
public class MatchingClusterMetricScaled extends BaseMetric implements Metric {

    protected double [][] scaledAssignCost;
    protected ClustIntersectInfoMatrix cIntM;
    
    public MatchingClusterMetricScaled() {
        super();
        this.rooted = true;
    }

    @Override
    public double getDistance(Tree t1, Tree t2) {
        
        if (t1.getExternalNodeCount() <= 2){
            return 0.0;
        }
        
        IdGroup idGroup = TreeUtils.getLeafIdGroup(t1);
        cIntM = TreeCmpUtils.calcClustIntersectMatrix(t1, t2, idGroup);
        int size1 = t1.getInternalNodeCount();
        int size2 = t2.getInternalNodeCount();
        int size = Math.max(size1, size2);
        int t1NodeNum, t2NodeNum;
        Node t1Node, t2Node;
        scaledAssignCost = new double[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i < size1 && j < size2) {
                    t1Node = t1.getInternalNode(i);
                    t1NodeNum = t1Node.getNumber();
                    t2Node = t2.getInternalNode(j);
                    t2NodeNum = t2Node.getNumber();

                    int diff = cIntM.cSize1[t1NodeNum] + cIntM.cSize2[t2NodeNum] - (cIntM.intCladeSize[t1NodeNum][t2NodeNum] << 1);
                    int sum = cIntM.cSize1[t1NodeNum] + cIntM.cSize2[t2NodeNum] - cIntM.intCladeSize[t1NodeNum][t2NodeNum];
                    scaledAssignCost[i][j] = ((double) diff) / ((double) sum);
                } else if (i >= size1 && j < size2) {
                    scaledAssignCost[i][j] = 1.0;
                } else if (i < size1 && j >= size2) {
                    scaledAssignCost[i][j] = 1.0;
                } else {
                    //normally should not happen
                    scaledAssignCost[i][j] = 0.0;
                }
            }
        }
        int[][] assignment = HungarianAlgorithm.hgAlgorithm(scaledAssignCost, "min");

        double sum = 0;
        for (int i = 0; i < assignment.length; i++) {
            sum = sum + scaledAssignCost[assignment[i][0]][assignment[i][1]];
        }
        return sum;
    }
}

