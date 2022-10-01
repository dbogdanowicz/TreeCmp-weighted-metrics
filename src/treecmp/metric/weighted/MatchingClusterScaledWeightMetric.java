/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.metric.weighted;

import pal.misc.IdGroup;
import pal.tree.Node;
import pal.tree.Tree;
import pal.tree.TreeUtils;
import treecmp.common.ClustIntersectInfoMatrix;
import treecmp.common.HungarianAlgorithm;
import treecmp.common.TreeCmpUtils;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;

/**
 *
 * @author Damian Bogdanowicz
 */
public class MatchingClusterScaledWeightMetric extends BaseMetric implements Metric {

    protected double [][] scaledAssignCost;
    
      @Override
    public boolean isRooted() {
        return true;
    }

    @Override
    public double getDistance(Tree t1, Tree t2) {
        int t1_ExtNodes = t1.getExternalNodeCount();
        int t2_ExtNodes = t2.getExternalNodeCount();
        int t1_IntNodes = t1.getInternalNodeCount();
        int t2_IntNodes = t2.getInternalNodeCount();
        int t1_TotNum = t1_ExtNodes + t1_IntNodes;
        int t2_TotNum = t2_ExtNodes + t2_IntNodes; 
        Node[] t1Nodes = new Node[t1_TotNum - 1];
        Node[] t2Nodes = new Node[t2_TotNum - 1];
        int i, j;
       
        for (i = 0; i < t1_ExtNodes; i++) {
            t1Nodes[i] = t1.getExternalNode(i);
        }
        j = t1_ExtNodes;
        for (i = 0; i < t1_IntNodes; i++) {
            Node tmp = t1.getInternalNode(i);
            if (!tmp.isRoot()) {
                t1Nodes[j] = tmp;
                j++;
            }
        }

        for (i = 0; i < t2_ExtNodes; i++) {
            t2Nodes[i] = t2.getExternalNode(i);
        }
        j = t2_ExtNodes;
        for (i = 0; i < t2_IntNodes; i++) {
            Node tmp = t2.getInternalNode(i);
            if (!tmp.isRoot()) {
                t2Nodes[j] = tmp;
                j++;
            }
        }
        
        IdGroup idGroup = TreeUtils.getLeafIdGroup(t1);
        //first calculate the intersection matrix in order to 
        //quickly compute distance between clusters     
        ClustIntersectInfoMatrix cIntM = TreeCmpUtils.calcClustIntersectMatrix(t1, t2, idGroup);
        
        int size = Math.max(t1Nodes.length, t2Nodes.length);

        scaledAssignCost = new double[size][size];
        double hab = 0;
        double haO = 0;
        double hbO = 0;
        double s1 = 0, s2 = 0, s3 = 0;
        double fa = 0, gb = 0;
        Node t1Node, t2Node;
        //calculate costs
        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {  
                if (i < t1Nodes.length && j < t2Nodes.length) {                    
                    t1Node = t1Nodes[i];
                    t2Node = t2Nodes[j];
                    haO = 1.0;
                    hbO = 1.0;                     
                    double sum = cIntM.getSizeT1(t1Node) +  cIntM.getSizeT2(t2Node);
                    double common = cIntM.getInterSize(t1Node, t2Node);
                    hab = (sum - 2*common) / (sum - common);
                    
                    fa = t1Node.getBranchLength();
                    gb = t2Node.getBranchLength();
                    
                    if (fa < 0 || gb < 0) {
                        throw new IllegalArgumentException("Branch lenght cannot be negative.");
                    }

                    s1 = Math.min(fa, gb) * hab;
                    s2 = inc(fa, gb) * haO;
                    s3 = inc(gb, fa) * hbO;

                    scaledAssignCost[i][j] = s1 + s2 + s3;

                } else if (i < t1Nodes.length && j >= t2Nodes.length) {
                    t1Node = t1Nodes[i];
                    fa = t1Node.getBranchLength();
                    haO = 1.0;

                    s1 = fa * haO;
                    scaledAssignCost[i][j] = s1;

                } else if (i >= t1Nodes.length && j < t2Nodes.length) {
                    t2Node = t2Nodes[j];
                    gb = t2Node.getBranchLength();
                    hbO = 1.0;

                    s1 = gb * hbO;
                    scaledAssignCost[i][j] = s1;
                } else {
                    scaledAssignCost[i][j] = 0;
                }
            }
        }
       int[][] assignment = HungarianAlgorithm.hgAlgorithm(scaledAssignCost, "min");

        double sum = 0;
        for (int ii = 0; ii < assignment.length; ii++) {
            sum = sum + scaledAssignCost[assignment[ii][0]][assignment[ii][1]];
        }
        return sum;
    }
    
    public double inc(double x, double y) {
        return Math.max(0.0, x - y);
    }
}
