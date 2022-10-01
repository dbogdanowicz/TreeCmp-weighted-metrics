/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.metric;

import pal.misc.IdGroup;
import pal.tree.Node;
import pal.tree.Tree;
import pal.tree.TreeUtils;
import treecmp.common.HungarianAlgorithm;
import treecmp.common.LapSolver;
import treecmp.common.TreeCmpUtils;

/**
 *
 * @author Damian
 */
public class MatchingPairMetricScaled extends BaseMetric implements Metric {

    protected int[] rowsol;
    protected int[] colsol;
    protected int[][] assigncost;
    protected double [][] scaledAssignCost;
    
    public MatchingPairMetricScaled() {
        super();
        this.rooted = true;
    }

    public double getDistance(Tree t1, Tree t2) {
        
        if (t1.getExternalNodeCount() <= 2){
            return 0.0;
        }
        
        IdGroup id1 = TreeUtils.getLeafIdGroup(t1);
        int[][] lcaMatrix1 = TreeCmpUtils.calcLcaMatrix(t1, null);
        int[][] lcaMatrix2 = TreeCmpUtils.calcLcaMatrix(t2, id1);
        //int[] alias1 = TreeUtilsExt.mapExternalIdsFromGroupToTree(id1, t1);
        //int[] alias2 = TreeUtilsExt.mapExternalIdsFromGroupToTree(id1, t2);

        int intT1Num = t1.getInternalNodeCount();
        int intT2Num = t2.getInternalNodeCount();

        Node[] postOrderT1 = TreeCmpUtils.getNodesInPostOrder(t1);
        Node[] postOrderT2 = TreeCmpUtils.getNodesInPostOrder(t2);

        short[] cSize1 = new short[intT1Num];
        short[] cSize2 = new short[intT2Num];

        TreeCmpUtils.calcCladeSizes(t1, postOrderT1, cSize1);
        TreeCmpUtils.calcCladeSizes(t2, postOrderT2, cSize2);

        int N = t1.getExternalNodeCount();

        int size = Math.max(intT1Num, intT2Num);
        if (size <= 0) {
            return 0;
        }

        assigncost = new int[size][size];
        scaledAssignCost = new double[size][size];
        rowsol = new int[size];
        colsol = new int[size];

        
        //iterate by all possible pairs of leaves
        //and fill assigncont with the value of intersection size
        for (int i = 0; i < N; i++){
            for (int j = i+1; j < N; j++){
               int int1 = lcaMatrix1[i][j];
               int int2 = lcaMatrix2[i][j];
               assigncost[int1][int2]++;
            }
        }
        //count LCA pairs for t1
        int[] t1IntPairCount = new int[intT1Num];
        for (int i = 0; i < intT1Num; i++){
            //Node n = t1.getInternalNode(alias1[i]);
            Node n = t1.getInternalNode(i);
            t1IntPairCount[i] =  coutChildrenPairs(n, cSize1);
        }
        //count LCA pairs for t2
        int[] t2IntPairCount = new int[intT2Num];
        for (int i = 0; i < intT2Num; i++){
            //Node n = t2.getInternalNode(alias2[i]);
            Node n = t2.getInternalNode(i);
            t2IntPairCount[i] =  coutChildrenPairs(n, cSize2);
        }

        //calc xor valuses of pairs sets and store it in assigncost matrix
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                if (i < intT1Num && j < intT2Num){
                    int diff = t1IntPairCount[i]+t2IntPairCount[j] - (assigncost[i][j] << 1);
                    int sum = t1IntPairCount[i]+t2IntPairCount[j] - assigncost[i][j];
                    scaledAssignCost[i][j] = ((double) diff)/((double)sum);
                } else if (i >= intT1Num && j < intT2Num){
                    scaledAssignCost[i][j] = 1.0;
                } else if (i < intT1Num && j >= intT2Num){
                    scaledAssignCost[i][j] = 1.0;
                }else {
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
        //int metric = LapSolver.lap(size, assigncost, rowsol, colsol, u, v);
        return sum;
    }
    
    
    int coutChildrenPairs(Node n, short[] clustSizeTab) {
        int chCount = n.getChildCount();
        int[] cSize = new int[chCount];

        for (int i = 0; i < chCount; i++) {
            Node chNode = n.getChild(i);
            if (chNode.isLeaf()) {
                cSize[i] = 1;
            } else {
                cSize[i] = clustSizeTab[chNode.getNumber()];
            }
        }
        int pairCount = 0;
        for (int i = 0; i < cSize.length; i++) {
            for (int j = i + 1; j < cSize.length; j++) {
                pairCount += (cSize[i] * cSize[j]);
            }
        }
        return pairCount;
    }
    
}

