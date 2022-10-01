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
import pal.tree.Tree;
import pal.tree.TreeUtils;
import treecmp.common.IntersectInfoMatrix;
import treecmp.common.TreeCmpUtils;
/*
 * Mast algorith for rooted binary trees based on 
 * "Calculation, Visualization, and Manipulation of MASTs (Maximum Agreement Subtrees)"
 * by Shiming Dong and Eileen Kraemer where the orignal algorithm form
 * W. Goddard, E. Kubicka, G. Kubicki and F. R. McMorris. "The agreement metric for labeled
 * binary trees", Mathematical Biosciences 123: 215-226, 1994 is described. 
 */

public class MastBinMetric extends BaseMetric implements Metric {

    public MastBinMetric() {
        super();
    }

    public double getDistance(Tree t1, Tree t2) {
      
        int n = t1.getExternalNodeCount();
        if (n <= 2) {
            return 0.0;
        }
        
        int intSize1 = t1.getInternalNodeCount();
        int intSize2 = t2.getInternalNodeCount();

        if (intSize1 != intSize2) {
            throw new UnsupportedOperationException(" Both trees must be binary for Mast Bin metric");
        }
        
        IdGroup id1 = TreeUtils.getLeafIdGroup(t1);
        IntersectInfoMatrix mastIntMatrix = TreeCmpUtils.calcMastIntersectMatrix(t1, t2, id1);
        short mast = (short) (n - mastIntMatrix.getSize(t1.getRoot(), t2.getRoot()));
        return mast;
    }
}
