/**
 * This file is part of TreeCmp, a tool for comparing phylogenetic trees using
 * the Matching Split distance and other metrics. Copyright (C) 2011, Damian
 * Bogdanowicz
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package treecmp.metric;

import pal.misc.IdGroup;
import pal.tree.Node;
import pal.tree.Tree;
import pal.tree.TreeUtils;
import treecmp.common.ClustIntersectInfoMatrix;
import treecmp.common.HungarianAlgorithm;
import treecmp.common.TreeCmpUtils;

public class AlignMetric extends BaseMetric implements Metric {

    protected double[][] assigncost;
    protected ClustIntersectInfoMatrix cIntM;

    public AlignMetric() {
        super();
    }

    public double getDistance(Tree t1, Tree t2) {

        IdGroup idGroup1 = TreeUtils.getLeafIdGroup(t1);
        cIntM = TreeCmpUtils.calcClustIntersectMatrix(t1, t2, idGroup1);
        int L = t1.getExternalNodeCount();
        int intSize1 = t1.getInternalNodeCount();
        int intSize2 = t2.getInternalNodeCount();

        if (intSize1 != intSize2) {
            throw new UnsupportedOperationException(" Both trees must be binary for Align metric");
        }

        int size = intSize1 - 1;
        if (size <= 0) {
            return 0;
        }
        double a00, a01, a10, a11;
        assigncost = new double[size][size];
        int n1Num, n2Num, n1Csize, n2Csize, intCsize;
        Node n1, n2;

        for (int i = 0; i < size; i++) {
            n1 = t1.getExternalNode(i);
            if (n1.isRoot()) continue;
            n1Num = n1.getNumber();
            for (int j = 0; j < size; j++) {
                n2 = t2.getExternalNode(j);
                 if (n2.isRoot()) continue;
                n2Num = n2.getNumber();
                n1Csize = cIntM.cSize1[n1Num];
                n2Csize = cIntM.cSize2[n2Num]; 
                intCsize = cIntM.intCladeSize[n1Num][n2Num];
                a00 = ((double) (intCsize)) / ((double) (n1Csize + n2Csize - intCsize));
                a11 = ((double) (L - n1Csize - n2Csize + intCsize)) / ((double) (L - intCsize));
                a10 = ((double) (n2Csize - intCsize)) / ((double) (L - n1Csize + intCsize));
                a01 = ((double) (n1Csize - intCsize)) / ((double) (L - n2Csize + intCsize));
                assigncost[i][j] = 1.0 - Math.max(Math.min(a00, a11), Math.min(a10, a01));
            }
        }
        int[][] assignment = HungarianAlgorithm.hgAlgorithm(assigncost, "min");

        double sum = 0;
        for (int i = 0; i < assignment.length; i++) {
            sum = sum + assigncost[assignment[i][0]][assignment[i][1]];
        }
        return sum;
    }
}
