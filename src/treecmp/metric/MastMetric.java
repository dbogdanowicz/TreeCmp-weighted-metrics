/** This file is part of TreeCmp, a tool for comparing phylogenetic trees
    using the Matching Split distance and other metrics.
    Copyright (C) 2011,  Damian Bogdanowicz

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package treecmp.metric;


import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.lib.MesquiteTree;
import mesquite.lib.Taxa;
import pal.misc.Identifier;
import pal.tree.Node;
import pal.tree.Tree;
import treecmp.common.AlignInfo;
import treecmp.common.NodeUtilsExt;
import treecmp.config.IOSettings;
import treecmp.masttreedist.MAST;
/*
 * This is a wrapper for MASTtreedist function to compute MAST.
 * See HONG HUANG and YONGJI LI, 
 * MASTtreedist: Visualization of Tree Space Based on Maximum Agreement Subtree,
 * JOURNAL OF COMPUTATIONAL BIOLOGY, Volume 20, Number 1, 2013, Pp. 42–49
 * See also MASTtreedist web page:  http://www.rc.usf.edu/MASTtree/
 */
public class MastMetric extends BaseMetric implements Metric {

    MAST mast = new MAST();
    
    public MastMetric() {
        super();
        mast.startJob(null, null, true);
    }

    public double getDistance(Tree t1, Tree t2) {

        if (t1.getExternalNodeCount() <= 2) {
            return 0.0;
        }
        Tree ft1 = fixNumericOnlyTaxa(t1);
        Tree ft2 = fixNumericOnlyTaxa(t2);

        String t1Desc = NodeUtilsExt.treeToSimpleString(ft1, false);
        String t2Desc = NodeUtilsExt.treeToSimpleString(ft2, false);

        System.out.println(t1Desc);
        System.out.println(t2Desc);
        
        MesquiteTree mesquiteTree1 = new MesquiteTree(new Taxa(0));
        mesquiteTree1.setPermitTaxaBlockEnlargement(true);
        mesquiteTree1.readTree(t1Desc);

        MesquiteTree mesquiteTree2 = new MesquiteTree(mesquiteTree1.getTaxa());
        mesquiteTree2.setPermitTaxaBlockEnlargement(true);
        mesquiteTree2.readTree(t2Desc);


        MesquiteNumber result = new MesquiteNumber();
        MesquiteString resultString = new MesquiteString();
        mast.calculateNumber(mesquiteTree1, mesquiteTree2, result, resultString);
        // mesquiteTree1.dispose();
        // mesquiteTree2.dispose();
        return result.getIntValue();
    }

    private Tree fixNumericOnlyTaxa(Tree t) {

        Tree ft = t.getCopy();
        for (int i = 0; i < ft.getExternalNodeCount(); i++) {
            Node node = ft.getExternalNode(i);
            String nodeName = node.getIdentifier().getName();
            String newName = "z" + nodeName;
            node.setIdentifier(new Identifier(newName));
        }
        ft.createNodeList();
        return ft;
    }
}
