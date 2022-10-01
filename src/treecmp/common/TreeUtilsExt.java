/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.common;

import pal.misc.IdGroup;
import pal.misc.Identifier;
import pal.tree.Node;
import pal.tree.Tree;
import pal.tree.TreeUtils;

/**
 *
 * @author Damian
 */
public class TreeUtilsExt extends TreeUtils {

    /**
     * get list of the identifiers of the external nodes without rebuilding node list
     *
     * @return leaf identifier group
     */
    public static final IdGroup getLeafIdGroupExt(Tree tree) {

        IdGroup labelList =
                new SimpleIdGroupExt(tree.getExternalNodeCount());

        for (int i = 0; i < tree.getExternalNodeCount(); i++) {
            labelList.setIdentifier(i, tree.getExternalNode(i).getIdentifier());
        }

        return labelList;
    }
    /**
     * Creates a map of leaves:
     * for leaf i in IdGroup call map[i] to obtain its ID in the tree
     *
     * value -1 means that there is no leaf of given ID in given tree
     *
     * @param idGroup
     * @param tree
     * @return
     */
    public static final int[] mapExternalIdsFromGroupToTree(IdGroup idGroup, Tree tree) {

        int[] alias = new int[idGroup.getIdCount()];

        for (int i = 0; i < idGroup.getIdCount(); i++) {
            Identifier groupId = idGroup.getIdentifier(i);
            Node node = TreeUtils.getNodeByName(tree, groupId.getName());
            if (node != null) {
                alias[i] = node.getNumber();
            } else {
                alias[i] = -1;
            }
        }

        return alias;
    }
}
