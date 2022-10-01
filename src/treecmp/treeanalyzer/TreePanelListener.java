package treecmp.treeanalyzer;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

//This class handles the mouse events from the treePanel and either rotates the children or
//opens the dialog to assign properties
public class TreePanelListener extends MouseAdapter{

    private Tree dendro;					//tree reference
    private JButton rotate;					//rotate button reference
    private JButton font;					//font button reference
    private JButton header;					//label header button reference
    private TreePanel treePanel;				//treePanel reference
    private MastTreePanel leftTreePanel;		//mastTreePanel reference
    private MastTreePanel rightTreePanel;
    private MastPanel mastPanel;
    private ConnectPanel connectPanel;
    private boolean adjustBorder1;				//boolean value for needing to adjust border1 or not
    private boolean [] adjustLabelBorder;			//boolean value for needing to adjust a label border or not
	private int dataOrMast = 0;
	final static int DATAP = 0;
	final static int MASTP = 1;
	final static int LEFTSIDE = 0;
	final static int RIGHTSIDE = 1;
	private Tree leftTree;
	private Tree rightTree;

    //default constructor
    public TreePanelListener(){
        dendro = null;
        leftTree = null;
        rightTree = null;
        treePanel = null;
        leftTreePanel = null;
        rightTreePanel = null;
        connectPanel = null;
        adjustBorder1 = false;
        adjustLabelBorder = new boolean[10];

        //intialize array to false
        for (int i=0; i < 10; i++)
            adjustLabelBorder[i] = false;
    }//TreePanelListener()

    //assign the tree value
    public void setTree(Tree t){
        dendro = t;
    }//setTree(t)

    //assign the panel this associates with
    public void setTreePanel(TreePanel t){
        treePanel = t;
        dataOrMast = DATAP;
    }//setTreePanel(t)

	/* new code */
	public void setMastTreePanel(MastTreePanel m, int side){
		dataOrMast = MASTP;
		if (side == LEFTSIDE)
			leftTreePanel = m;
		else rightTreePanel = m;
	}

	public void setMastPanel(MastPanel m)
	{
		dataOrMast = MASTP;
		mastPanel = m;
		leftTreePanel = mastPanel.getLeftPanel();
		rightTreePanel = mastPanel.getRightPanel();
		leftTree=leftTreePanel.getTree();
		rightTree = rightTreePanel.getTree();
		
	}
		
	public void setTrees(Tree inTree, int side)
	{
		if (side == LEFTSIDE){
			leftTree = inTree;
		}
		else {
				rightTree = inTree;
		}
	}

	public void setConnectPanel(ConnectPanel c)
	{
		connectPanel = c;
	}

	/* ---------*/
    //assign the rotate button to test if selected
    public void setRotateButton(JButton r){
        rotate = r;
    }//setRotateButton(r)

    //assign the font button to test if selected
    public void setFontButton(JButton f){
        font = f;
    }//setFontButton(f)

    //assign the label header button to test if selected
    public void setLabelHeaderButton(JButton h){
        header = h;
    }//setLabelHeader(h);

    //method to handle when the user clicks on the panel
    public void mousePressed(MouseEvent e){
        //test to see if the mouse was clicked on a border
        if (dataOrMast == DATAP){
        	if (dendro != null){
        	    int x = e.getX();
        	    int ribotypeX = treePanel.getRibotypeX();
        	    int tempX;

        	    if (x >= (ribotypeX - 1) && x <= (ribotypeX + 1)){
        	        //this is over border1
        	        adjustBorder1 = true;
        	        treePanel.setMoveBorder1(true);
        	    }//if
        	    else{
        	        //test out the label borders
        	        for (int i=0; i < dendro.getRoot().getNumLabels(); i++){
        	            tempX = treePanel.getLabelBorderValue(i);
        	            if (x >= (tempX - 1) && x <= (tempX + 1)){
        	                adjustLabelBorder[i] = true;
        	                treePanel.setMoveLabelBorder(true, i);
        	            }//if
        	        }//for
        	    }//else
        	}//if
		}
    }//mousePressed

    //method to handle when the user has finished clicking on the panel
    public void mouseReleased(MouseEvent e){
        //test to see if there is even a tree associated with the panel yet
        if (dataOrMast == DATAP){
        	if (dendro != null){
        	    //grab the coordinates of the mouse event
        	    int x = e.getX();
        	    int y = e.getY();

				//set up nodes to check which ones are selected if any
        	    TreeNode nodeAffected = null;
        	    TreeNode tempNode = dendro.getRoot();

        	    //temporary values to check again the mouse event values
        	    int tempX;
        	    int tempY;

        	    ImageIcon tempImage = new ImageIcon(dendro.getRandomChild().getImage());
        	    int adjustHeight = (tempImage.getIconHeight() / 2);

        	    //continue this loop until either a nodeAffected receives a value or you are done with the tree
        	    while(nodeAffected == null && tempNode != null){
        	        //get the coordinates of the tempNode
        	        tempX = tempNode.getX();
        	        tempY = tempNode.getY() + adjustHeight;

        	        //check around the coordinates to see if it is a node area
        	        if (y >= tempY && y <= (tempY + 5) && x >= tempX && x <= (tempX + 5))
        	            nodeAffected = tempNode;
        	        else{
        	            //check if you should check nodes above or below the node just checked
        	            if (y > tempY)
        	                tempNode = tempNode.getLeftNode();
        	            else
        	                tempNode = tempNode.getRightNode();
        	        }//else
        	    }//while

        	    //done searching through the tree so now apply changes if necessary
        	    if (nodeAffected != null){
        	        //if rotate button is selected then rotate children
        	        if (rotate.isSelected()){
        	            nodeAffected.swapChildren();
        	            dendro.setTreeOrder();
        	            treePanel.repaint();
        	        }//if

        	        //if font button is selected then show prompt to set font for either leaf or inner node
        	        else if (font.isSelected()){
        			            if (nodeAffected.isLeaf()){
        			                SetLeafProperties edit = new SetLeafProperties(nodeAffected, dendro, treePanel, null);
        			            }//if
        			            else{
        				                SetGroupProperties editG = new SetGroupProperties(nodeAffected, treePanel, null);
        			            }//else
        		        }//else if
        	    }//if
        	    else if (adjustBorder1){
        	        //adjust the ribotypeX value
        	        treePanel.setRibotypeX(x);
        	        adjustBorder1 = false;
        	        treePanel.setMoveBorder1(false);
        	        treePanel.repaint();
        	    }//else if
        	    else{
        	        //test out the label borders
        	        for (int i=0; i < dendro.getRoot().getNumLabels(); i++){
        	            if (adjustLabelBorder[i]){
        	                treePanel.setLabelBorderValue(x, i);
        	                adjustLabelBorder[i] = false;
        	                treePanel.setMoveLabelBorder(false, i);
        	                treePanel.repaint();
        	            }//if
        	        }//for
        	    }//else
        	}//if
		}
		else {
				if ((!rotate.isSelected())&&(!font.isSelected()))
				 	JOptionPane.showMessageDialog(null, "Choose Operation First!");
				 	
				if ((leftTree != null)||(rightTree !=null)){
				   	//grab the coordinates of the mouse event

				   	int x = e.getX();
				   	int y = e.getY();
					int side = 0;
					TreeNode tempNode=null;
					ImageIcon tempImage =  new ImageIcon();

					if (leftTreePanel !=null){				
						if (x<leftTreePanel.getWidth()){
							side = LEFTSIDE;
							tempNode = leftTree.getRoot();
							tempImage = new ImageIcon(leftTree.getRandomChild().getImage());
						}
					}
					if (rightTreePanel!=null) {
							if (x<rightTreePanel.getWidth()){
								side = RIGHTSIDE;
								tempNode = rightTree.getRoot();
								tempImage = new ImageIcon(rightTree.getRandomChild().getImage());
							}
					}
				   	//set up nodes to check which ones are selected if any
				   	TreeNode nodeAffected = null;

				  	//temporary values to check again the mouse event values
				   	int tempX;
				   	int tempY;
				   	int adjustHeight = (tempImage.getIconHeight() / 2);
				   	
				    //continue this loop until either a nodeAffected receives a value or you are done with the tree
				    while(nodeAffected == null && tempNode != null){
				       	//get the coordinates of the tempNode
				       	tempX = tempNode.getX();
				        tempY = tempNode.getY() + adjustHeight;

						//check around the coordinates to see if it is a node area
				       	if ((y >= (tempY-3)) && (y <= (tempY + 3)) && (x >= (tempX-3)) && (x <= (tempX + 3)))
				        		nodeAffected = tempNode;
				       	else{
				        	   	//check if you should check nodes above or below the node just checked
				        	   	if (y > tempY)
				        	       		tempNode = tempNode.getLeftNode();
				        	   	else
				        	          	tempNode = tempNode.getRightNode();
				      	}//else
				 	}//while

				    //done searching through the tree so now apply changes if necessary
				    if (nodeAffected != null){
				     		//if rotate button is selected then rotate children
				     		
				       		if (rotate.isSelected()){
				        	   		nodeAffected.swapChildren();
				        	   		if (side == LEFTSIDE){
				        	       		leftTree.setTreeOrder();
				        	       		leftTreePanel.repaint();
				        	       		leftTree.setCoords(leftTreePanel, LEFTSIDE);
				        	       		connectPanel.setLeftTree(leftTree);
				        	       		connectPanel.repaint();
				        	       	}
				        	       	else {
				        	       			rightTree.setTreeOrder();
				        	       			rightTreePanel.repaint();
				        	       			rightTree.setCoords(rightTreePanel, RIGHTSIDE);
				        	       			connectPanel.setRightTree(rightTree);
				        	       			connectPanel.repaint();
				        	       	}

				       		}//if

				        	//if font button is selected then show prompt to set font for either leaf or inner node
				        	//need modify here
				        	else if (font.isSelected()){
				        			if (nodeAffected.isLeaf()){
				        				if (side == LEFTSIDE){
				        			       	SetLeafProperties edit = new SetLeafProperties(nodeAffected, leftTree, null, leftTreePanel);
				        			   	}
				        			   	else if (side == RIGHTSIDE){
				        			       		SetLeafProperties edit = new SetLeafProperties(nodeAffected, rightTree, null, rightTreePanel);
				        				}
				        			}//if
				        			else{
				        			   		if (side == LEFTSIDE){	
				        				      	SetGroupProperties editG = new SetGroupProperties(nodeAffected, null, leftTreePanel);
				        				    }
				        				    else if (side == RIGHTSIDE){
				        				    		SetGroupProperties editG = new SetGroupProperties(nodeAffected, null, rightTreePanel);	
				        			   		}
				        			   	}//else
				        		}//else if
				    }//if
				    
				}
				
        	}//else
    }//mouseReleased

}//TreePanelListener