package treecmp.treeanalyzer;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class ConnectPanel extends JPanel
{
    private Set leftMastLoc;
    private Set rightMastLoc;
    private final int XSTART = 0;
    private final int XEND = 100;
    private Tree leftTree;
    private Tree rightTree;
    private Tree mastTree;
    private Set mastLeafSet;

    public ConnectPanel()
    {
        super();
        leftTree = new Tree();
        rightTree = new Tree();
        mastTree = new Tree();
        mastLeafSet = new HashSet();
        leftMastLoc = new HashSet();
        rightMastLoc = new HashSet();
    }

    public ConnectPanel(Tree inLeftTree, Tree inRightTree, Set mastLeaf)
    {
        super();
        setTree(inLeftTree, inRightTree, mastLeaf);
    }

	public ConnectPanel(Tree inLeftTree, Tree inRightTree, Tree inMastTree)
    {
        super();
        setTree(inLeftTree, inRightTree, inMastTree);
    }
    public void setLeftTree(Tree inLeftTree)
    {
        leftTree = new Tree();
        leftTree = inLeftTree;
        leftMastLoc.clear();
        setLeftLoc(leftTree.getRoot());
    }

    public void setRightTree(Tree inRightTree)
    {
        rightTree = new Tree();
        rightTree = inRightTree;
        rightMastLoc.clear();
        setRightLoc(rightTree.getRoot());
    }

    public void setMastLeaf(Set mastLeaf)
    {
        mastLeafSet = new HashSet();
        mastLeafSet = mastLeaf;
        leftMastLoc.clear();
        rightMastLoc.clear();
        setLeftLoc(leftTree.getRoot());
        setRightLoc(rightTree.getRoot());
    }

    public void setTree(Tree inLeftTree, Tree inRightTree, Set mastLeaf)
    {
        /* if inLeftTree is null, then the left tree doesn't have to
         * be changed.
         */

        if (inLeftTree !=null)
        	leftTree = inLeftTree;
        if (inRightTree !=null)
        	rightTree = inRightTree;
        mastLeafSet = mastLeaf;

        if ((inLeftTree !=null)||(mastLeaf!=null)){
        	leftMastLoc.clear();
        	setLeftLoc(leftTree.getRoot());
        }
        if ((inRightTree!=null)||(mastLeaf!=null)){
        	rightMastLoc.clear();
        	setRightLoc(rightTree.getRoot());
        }
        repaint();
    }

	public void setTree(Tree inLeftTree, Tree inRightTree, Tree inMastTree)
    {
        /* if inLeftTree is null, then the left tree doesn't have to
         * be changed.
         */

        if (inLeftTree !=null)
        	leftTree = inLeftTree;
        if (inRightTree !=null)
        	rightTree = inRightTree;
		mastTree = inMastTree;
        mastLeafSet = mastTree.getRoot().getLeafSet();

        if ((inLeftTree !=null)||(mastLeafSet!=null)){
        	leftMastLoc.clear();
        	setLeftLoc(leftTree.getRoot());
        }
        if ((inRightTree!=null)||(mastLeafSet!=null)){
        	rightMastLoc.clear();
        	setRightLoc(rightTree.getRoot());
        }
        repaint();
    }

    private void setLeftLoc(TreeNode node)
    {
         if (node.isLeaf()){
			 //modified label -> name
			 //if (mastLeafSet.contains((String)node.getGeneLabel())){
			 if (mastLeafSet.contains((String)node.getGeneName())){
             	    leftMastLoc.add(new MastLeafLoc(node.getGeneName(), node.getY()));
             }
         }
         else {
         	setLeftLoc(node.getLeftNode());
         	setLeftLoc(node.getRightNode());
         }

    }

    private void setRightLoc(TreeNode node)
    {
         if (node.isLeaf()){
             //modified label -> name
             //if (mastLeafSet.contains((String)node.getGeneLabel())){
			if (mastLeafSet.contains((String)node.getGeneName())){
				rightMastLoc.add(new MastLeafLoc(node.getGeneName(), node.getY()));
	     }
	 }
         else {
         	setRightLoc(node.getLeftNode());
         	setRightLoc(node.getRightNode());
         }

    }

    private void drawConnection(Graphics g)
    {
    	MastLeafLoc temp = new MastLeafLoc();
    	MastLeafLoc temp2 = new MastLeafLoc();

    	g.setColor(Color.red);
        if ((leftMastLoc!=null)&&(rightMastLoc!=null)){
            Iterator it = leftMastLoc.iterator();
            while(it.hasNext()){
                temp = new MastLeafLoc();
            	temp = (MastLeafLoc)it.next();
            	Iterator it2 = rightMastLoc.iterator();
            	while(it2.hasNext()){
            		temp2 = new MastLeafLoc();
            		temp2 = (MastLeafLoc)it2.next();
            		if (temp.name.equals(temp2.name)){
            			//modified on 10.02
            			g.drawLine(XSTART, temp.yLoc, this.getWidth(), temp2.yLoc);
            			break;
            		}
            	}
            }
        }
    }
    public void paintComponent(Graphics g)
    {
         super.paintComponent(g);
         drawConnection(g);
    }

}



