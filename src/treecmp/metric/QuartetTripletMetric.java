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

import java.util.HashMap;
import java.util.Map;
import pal.tree.Node;
import pal.tree.SimpleNode;
import pal.tree.SimpleTree;
import pal.tree.Tree;
import pal.tree.TreeUtils;
import treecmp.common.TreeCmpUtils;
import treecmp.spr.SprUtils;

public class QuartetTripletMetric extends BaseMetric implements Metric {

    private final TripletMetric2 tt2;

    public QuartetTripletMetric() {
        super();
        this.rooted = false;
        tt2 = new TripletMetric2();
    }

    @Override
    public double getDistance(Tree t1, Tree t2) {

        int n = t1.getExternalNodeCount();
        if (n <= 2){
            return 0.0;
        }
        //Node[] leaves2 = TreeCmpUtils.getAllLeaves(t2);
        Node[] leaves2 = new Node[1]; // FIXME!!!
        Map<String, Node> t2leafMap = new HashMap<>();
        
        for (Node ll : leaves2) {
            t2leafMap.put(ll.getIdentifier().getName(), ll);
        }
        
        double dist = 0.0;
        for (int i = 0; i < n; i++) {
            Node node1 = t1.getExternalNode(i);
            Node node2 = t2leafMap.get(node1.getIdentifier().getName());
            Tree t1c = getRootedAtEdge(node1, t1);
            Tree t2c = getRootedAtEdge(node2, t2);
            double d = tt2.getDistance(t1c, t2c);
            System.out.println("Dist " + i+ ": " + d);
            dist += d;
        }
        return dist / 4.0;

    } 
    
    
    private Tree getRootedAtEdge(Node leaf, Tree t) {
        Tree tc = t.getCopy();
        Node lc = tc.getExternalNode(leaf.getNumber());
        Node pc = lc.getParent();
        
        TreeUtils.reroot(tc, pc);
        Node root = new SimpleNode();
        int childPos = SprUtils.findChildPos(lc, pc);
        pc.removeChild(childPos);
        
        root.addChild(lc);
        lc.setParent(root);
        root.addChild(pc);
        pc.setParent(root);
        root.setParent(null);
        tc.setRoot(root);
        return tc;
    }
}
