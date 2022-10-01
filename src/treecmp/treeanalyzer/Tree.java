package treecmp.treeanalyzer;

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.lang.*;

public class Tree implements Serializable{
    private TreeNode root;
    private int childrenDone;
    private double scaleFactor;
    private int panelHeight;
    private int panelWidth;
    private String [] labelHeaders;
    private int numHeaders;
    private int labelHeaderBuffer;
    private String picFilePath;

    //----------------------new ----------------------
    private final static String LEFT=new String("left");
    private final static String RIGHT=new String("right");
    private final int LEFTSIDE = 0;
    private final int RIGHTSIDE =1;
    private final int PANELWIDTH = 325;
    private String []order;
    private int orderIndex=0;
    private boolean ordered = false;
    private int leafSetSize=0;
    private int iniPos=1;
    private String nameIndex = new String();
    private double lengthPro=1;
    //----------------------new ----------------------
    /* decide whether the tree is empty or not
     */
    private boolean empty()
    {
    	if (root == null) return true;
    	return false;
    }

    public Tree()
    {
    	//root = new TreeNode();
    	root = null;
    	childrenDone = 0;
    	scaleFactor = 1;
        panelHeight = 550;
        panelWidth = 700;
        labelHeaderBuffer = 35;
    }
    //-------------------------------------------------

    public Tree(Tree inTree)
	{
		root=new TreeNode(inTree.getRoot());
		childrenDone = 0;
		scaleFactor=1;
		panelHeight = 550;
		panelWidth = 700;
		labelHeaderBuffer = 35;
		if (inTree.getLabelHeaders()!=null)
			labelHeaders=new String[inTree.getLabelHeaders().length];
		labelHeaders=inTree.getLabelHeaders();
		if (inTree.getOrder()!=null)
			order=new String[inTree.getOrder().length];
		order=inTree.getOrder();
		leafSetSize=inTree.getLeafSize();
		numHeaders=inTree.getNumHeaders();
		lengthPro=inTree.lengthPro;
	}

	//default constructor
    public Tree(String treeStructure, Set nodeNames, String picPath){
        //set the root to null
        root = null;

        //assign the path for this directory
        picFilePath = picPath;

        //build the tree from the Nexus file
        String temp;
        String nexus = treeStructure;
        Stack nodeStack = new Stack();                      //stack to hold left nodes
        Stack leafStack = new Stack();                      //stack to hold any leaves
        TreeNode node1;
        TreeNode node2;
        TreeNode node3;
        int openParen;
        int closeParen;
        int comma;
        int colon;
        int endSubstring;
        double stemLength;
        String currentStack = "";
        scaleFactor = 1;
        panelHeight = 550;
        panelWidth = 700;
        labelHeaderBuffer = 35;
        //----------------new --------------------
        int pos=0;
        double minLength=1;
        //----------------------------------------
		//try{
        		//when the loop is done there should only be one node on the stack, the root
        		while (nexus != null){
            	temp = String.valueOf(nexus.charAt(0));

            	//compress white space
            	if ((temp.charAt(0)=='\t')||(temp.charAt(0)=='\r')||(temp.charAt(0)=='\n')||(temp.charAt(0)==' ')){
            		nexus = nexus.substring(1);
            		continue;
            	}

            	//if there is an "(" then create a new node and push it on the stack
            	if (temp.equalsIgnoreCase("(")){
                	node1 = new TreeNode();
                	node2 = new TreeNode();
                	nodeStack.push(node1);
                	currentStack = "node";
                	nexus = nexus.substring(1);
            	}//if

            	//if there is a "," then check to see if there are any leaves on the stack
            	else if (temp.equalsIgnoreCase(",")){
                		if (leafStack.isEmpty()){
                    	//there are no leaves so there must be a node to add
                    	node1 = (TreeNode)nodeStack.pop();
                    	node2 = (TreeNode)nodeStack.pop();
                    	//node2.setLeftNode(node1);

						if (node2.getLeftNode() == null){
                    		//-----------------new -------------------
                    		node1.setPos(pos);
                    		pos++;
                    		parChild(node2, node1, LEFT);
                    		//----------------------------------------

                    		nodeStack.push(node2);
                    		currentStack = "node";
                    		nexus = nexus.substring(1);
						}
						else {
								node1.setPos(pos);
								pos++;
								parChild(node2, node1, RIGHT);
								node3 = new TreeNode();
								node2.setPos(pos);
								node2.setStemLength(0);
								pos++;
								parChild(node3, node2, LEFT);

								nodeStack.push(node3);
								currentStack="node";
								nexus=nexus.substring(1);
							}
                	}//if (nodes.isEmpty())
                	else{
                    	//there is a leaf so add it in
                    	node1 = (TreeNode)nodeStack.pop();
                    	//node1.setLeftNode((TreeNode)leafStack.pop());

						if (node1.getLeftNode() == null){
	                    	//---------------new ---------------------
	                    	node2 = (TreeNode)leafStack.pop();
	                    	node2.setPos(pos);
	                    	pos++;
	                    	parChild(node1, node2, LEFT);
	                    	//----------------------------------------

	                    	nodeStack.push(node1);
	                    	currentStack = "node";
	                    	nexus = nexus.substring(1);
						}
						else {
								node2 = (TreeNode)leafStack.pop();
								node2.setPos(pos);
								node2.setStemLength(0);
								pos++;
								parChild(node1, node2, RIGHT);
								node3 = new TreeNode();
								node1.setPos(pos);
								pos++;
								parChild(node3, node1, LEFT);
								nodeStack.push(node3);
								currentStack="node";
								nexus=nexus.substring(1);
							}
                	}//else
            	}//else if (temp == ',')

            	//if there is a ")"  then check to see if there are any leaves and then add the new node
            	else if (temp.equalsIgnoreCase(")")){
                		if (leafStack.isEmpty()){
                    	//there are no leaves so there must be a node to add
                    	node2 = (TreeNode)nodeStack.pop();
                    	node1 = (TreeNode)nodeStack.pop();
                    	//node1.setRightNode(node2);

                    	//-----------------new ----------------
                    	node2.setPos(pos);
                    	pos++;
                    	parChild(node1, node2, RIGHT);
                    	//-------------------------------------

                    	nodeStack.push(node1);
                    	currentStack = "node";
                    	if (nexus.length() > 1)
                        	nexus = nexus.substring(1);
                    	else
                        	nexus = null;
                	}//if
                	else{
                    	//there is a leaf so add it in
                    	node1 = (TreeNode)nodeStack.pop();
                    	//node1.setRightNode((TreeNode)leafStack.pop());

                    	//--------------------new ----------------
                    	node2 = (TreeNode) leafStack.pop();
                    	node2.setPos(pos);
                    	pos++;
                    	parChild(node1, node2, RIGHT);
                    	//----------------------------------------

                    	nodeStack.push(node1);
                    	currentStack = "node";
                    	if (nexus.length() > 1)
                        	nexus = nexus.substring(1);
                    	else
                        	nexus = null;
                	}//else
            	}//else if (temp == ')')

            	//if there is a ":" then add this stem value to the most recent node
            	else if (temp.equalsIgnoreCase(":")){
                		//Figure out which stack to pop from
                		if (currentStack.equalsIgnoreCase("node"))
                    		node1 = (TreeNode) nodeStack.pop();
                		else
                    		node1 = (TreeNode) leafStack.pop();

                		openParen = nexus.indexOf("(");
                		closeParen = nexus.indexOf(")");
                		comma = nexus.indexOf(",");
                		endSubstring = smallest(openParen, closeParen, comma);
                		stemLength = Double.parseDouble(nexus.substring(1, endSubstring));
                		node1.setStemLength(stemLength);

						if (stemLength<minLength)
							minLength = stemLength;

                		//Figure out which stack to push onto
                		if (currentStack.equalsIgnoreCase("node"))
                    		nodeStack.push(node1);
                		else
                    		leafStack.push(node1);

                		nexus = nexus.substring(endSubstring);
            		}//else if (temp == ":")

            		else if (temp.equalsIgnoreCase(";")){
                			nexus = null;
            			}//else if (temp = ";")

            			//otherwise this is just text so get the string value and add it to the leaf stack
            		else{
                			//this is a leaf
                			node1 = new TreeNode();
                			openParen = nexus.indexOf("(");
                			closeParen = nexus.indexOf(")");
                			comma = nexus.indexOf(",");
                			colon = nexus.indexOf(":");
                			endSubstring = smallest(openParen, closeParen, comma, colon);

                			//modified label -> name
                			//node1.setLeaf(nexus.substring(0, endSubstring));
                			node1.setLeafLabel(nexus.substring(0, endSubstring));
                			node1.setLabelColor(Color.black);

                			//---------------------new ----------------------
                			//modified label -> name
                			//node1.addLeaf(node1.getGeneLabel());
               				node1.setTotSize(1);
                			/* try to find the Gene Name for each node */
                			Iterator it=nodeNames.iterator();
                			LabelName labelName=new LabelName();
                			if (!nodeNames.isEmpty())
                    			while(it.hasNext()){
                    				labelName=(LabelName)it.next();
                    				if (labelName.num.equals(node1.getGeneLabel())){
                    	    			//modified label -> name
                    	    			//node1.setGeneName(new String(labelName.name));
                    	    			node1.setLeafName(new String(labelName.name));
                    	    			node1.addLeaf(node1.getGeneName());
                    	    			break;
                    				}
                    			}
                				//-----------------------------------------------

                				ImageIcon image = new ImageIcon(picFilePath + "/" + node1.getGeneName() + ".jpg");
                				if (image != null){
                    				node1.setImage(picFilePath + "/" + node1.getGeneName() + ".jpg");
                				}//if
                				leafStack.push(node1);
                				currentStack = "leaf";
                				nexus = nexus.substring(endSubstring);
            		}//else
        		}//while

        		//now set the root
        		root = (TreeNode)nodeStack.pop();

        		//--------------new ------------------
        		root.setPos(pos);
        		root.setStemLength(0);
        		//------------------------------------

        		labelHeaders = new String[10];
        		for(int i=0;i<10;i++)
            		labelHeaders[i]=new String();

        		labelHeaders[0] = "Leaf";
       		 	labelHeaders[1] = "Ribotype";
        		labelHeaders[2] = "Comment";

				numHeaders = 3;
        		root.setNumLabels(3);
        		leafSetSize = root.getLeafSize();
        		setTreeOrder();

        		lengthPro = minLength;

        		//for debug
        		//System.out.println("Length proportion: "+lengthPro);

     //   	} catch (Exception e){
     //   			throw new Exception();
     //   	}

    }//constructor Tree

    //----------------------new ---------------------

    /* To build the relationship between two nodes
     * @ param: inPar:parent, inChild: child, lOrR: left or right child
     * @ return: void
     */

    private void parChild(TreeNode inPar, TreeNode inChild, String lOrR)
    {
    	inChild.setParent(inPar);
        inPar.setTotSize(inPar.getTotSize()+inChild.getTotSize());
        inPar.addLeafSet(inChild.getLeafSet());
        inPar.setGeneLabel(inPar.getGeneLabel() + inChild.getGeneLabel());

        if (lOrR.equalsIgnoreCase(LEFT)) inPar.setMyLeftNode(inChild);
        else inPar.setMyRightNode(inChild);
    }

    public static void parentChild(TreeNode inPar, TreeNode inChild, String lOrR)
	    {
	    	inChild.setParent(inPar);
	        inPar.setTotSize(inPar.getTotSize()+inChild.getTotSize());
	        inPar.addLeafSet(inChild.getLeafSet());
	        inPar.setGeneLabel(inPar.getGeneLabel() + inChild.getGeneLabel());

	        if (lOrR.equalsIgnoreCase(LEFT)) inPar.setMyLeftNode(inChild);
	        else inPar.setMyRightNode(inChild);
    }
    //-----------------------------------------------

    //return the smallest value of the 4 values
    public int smallest(int value1, int value2, int value3, int value4){
        int small1 = smallest(value1, value2);
        int small2 = smallest(value3, value4);
        int small3 = smallest(small1, small2);
        return small3;
    }//smallest(value1, value2, value3, value4)

    public int smallest(int value1, int value2, int value3){
        int small1 = smallest(value1, value2);
        small1 = smallest(small1, value3);
        return small1;
    }//smallest(value1, value2, value3)

    //return the smallest value of the 2 values
    public int smallest(int value1, int value2){
        if (value1 == -1 && value2 != -1)
            return value2;
        else if (value2 == -1 && value1 != -1)
            return value1;
        else if (value1 < value2)
            return value1;
        else
            return value2;
    }//smallest(value1, value2)

    //routine to set the depth of each node
    public void setDepth(TreeNode node, int depth){
        if (!node.isLeaf()){
            setDepth(node.getLeftNode(), depth + 1);
            setDepth(node.getRightNode(), depth + 1);
        }//if
        node.setDepth(depth);
    }//setDepth()

    //return the entire depth of the tree
    public int getTreeDepth(){
        return getTreeDepthHelper(root, 0);
    }//getTreeDepth()

    //helper routine to get the depth of the entire tree
    public int getTreeDepthHelper(TreeNode node, int d){
        int tempDepth = d;
        if (node.isLeaf())
            tempDepth = d;
        else
            tempDepth = getTreeDepthHelper(node.getLeftNode(), d + 1);
        int tempDepth2 = d;
        if (node.isLeaf())
            tempDepth2 = d;
        else
            tempDepth2 = getTreeDepthHelper(node.getRightNode(), d + 1);
        if (tempDepth > tempDepth2)
            return tempDepth;
        else
            return tempDepth2;
    }//getTreeDepthHelper(node, d)

    //routine to set the x and y coordinates of all the nodes in the tree
    public void setCoords(TreePanel treePanel){
        TreeNode temp = getRandomChild();
        ImageIcon tempImage = new ImageIcon("" + temp.getLabel(0) + ".jpg");
        int panelUnit = tempImage.getImage().getHeight(null) + 1;
        if (panelUnit < 5)
            panelUnit = 20;

        childrenDone = 0;

        root.setPanelHeight(this.getPanelHeight());
        root.setPanelWidth(this.getPanelWidth());

        //setCoordsHelper(panelUnit, (treePanelWidth / treeDepth), root);
        //setCoordsHelper(panelUnit, root.getStemLength(), root, getPanelWidth(),LEFTSIDE);
		setCoordsHelper(panelUnit, 0, root, PANELWIDTH,LEFTSIDE);

    }//setCoords

    //-----------------------------new --------------------------------
    public void setCoords(MastTreePanel treePanel, int side ){
        TreeNode temp = getRandomChild();
        int panelUnit = 20;

        /*
		if (panelUnit*root.getLeafSet().size()>MastPanel.PANELH)
			panelUnit = (MastPanel.PANELH - 50)/ root.getLeafSet().size();

		if (panelUnit<10)
			panelUnit = 10;
		*/

        childrenDone = 0;

        root.setPanelHeight(this.getPanelHeight());
        root.setPanelWidth(this.getPanelWidth());

		if (side == LEFTSIDE)
        	setCoordsHelper(panelUnit, root.getStemLength(), root, getPanelWidth(), LEFTSIDE);
        else setCoordsHelper(panelUnit, root.getStemLength(), root, PANELWIDTH,RIGHTSIDE);
    }//setCoords
    //---------------------------------------------------------------------

    public void setCoordsHelper(int heightUnit, double parentStemLength, TreeNode node, int panelWidth, int side){
        int nodeChildren = node.getNumberOfChildren(0);
        double tempX = (parentStemLength + node.getStemLength()) * scaleFactor;

		//System.out.println("Scale: "+scaleFactor+" tempX: "+tempX);
        //for debug

        //------------

	if (side == LEFTSIDE){
        	node.setX((int)tempX+2);
        }
        else if (side == RIGHTSIDE){
        	node.setX((int) (panelWidth - tempX));
        }

        if (node.isLeaf()){
            node.setY((heightUnit * childrenDone) + labelHeaderBuffer);
            childrenDone++;
        }//if
        else{
            //this is an inner node
            int lowY = (heightUnit * (node.getRightNode().getNumberOfChildren(0) + childrenDone - 1));
            int hightY = (heightUnit * (node.getRightNode().getNumberOfChildren(0) + childrenDone));
            node.setY(((lowY + hightY) / 2) + labelHeaderBuffer);

            //call on child nodes
            setCoordsHelper(heightUnit, (node.getStemLength() + parentStemLength), node.getRightNode(), panelWidth, side);
            setCoordsHelper(heightUnit, (node.getStemLength() + parentStemLength), node.getLeftNode(), panelWidth, side);
        }//else

        //System.out.println("Node: "+node.getGeneName()+" X: "+node.getX()+" Y: "+node.getY());
    }//setCoordsHelper

    public double getMaxNodeLength(TreeNode node, double LengthSoFar){
        LengthSoFar += node.getStemLength();
        if (node.isLeaf())
            return LengthSoFar;
        else{
            double leftMaxLength = getMaxNodeLength(node.getLeftNode(), LengthSoFar);
            double rightMaxLength = getMaxNodeLength(node.getRightNode(), LengthSoFar);
            if (leftMaxLength > rightMaxLength)
                return leftMaxLength;
            else
                return rightMaxLength;
        }//else
    }//getMaxNodeLength

    //This method returns the root node of the tree
    public TreeNode getRoot(){
        return root;
    }//getRoot()

    //This method grabs a random child, first left child
    public TreeNode getRandomChild(){
        TreeNode child = root;
        while (!child.isLeaf())
            child = child.getLeftNode();
        return child;
    }//getRandomChild

    public int getNumberOfChildren(TreeNode node, int childrenSoFar){
        if (node.isLeaf())
            return childrenSoFar + 1;
        else{
            int leftChildren = getNumberOfChildren(node.getLeftNode(), childrenSoFar);
            int rightChildren = getNumberOfChildren(node.getRightNode(), childrenSoFar);
            return leftChildren + rightChildren;
        }//else
    }//getNumberOfChildren

    public void setScaleFactor(double newFactor){
        scaleFactor = newFactor;
    }//setScaleFactor

    public double getScaleFactor(){
        return scaleFactor;
    }//getScaleFactor

    public void setPanelHeight(int newHeight){
        panelHeight = newHeight;
    }//setPanelHeight

    public int getPanelHeight(){
        return panelHeight;
    }//getPanelHeight

    public void setPanelWidth(int newWidth){
        panelWidth = newWidth;
    }//setPanelWidth

    public int getPanelWidth(){
        return panelWidth;
    }//getPanelWidth

    //get the Label header at this index
    public String getLabelHeader(int index){
        return labelHeaders[index];
    }//getLabelHeader

	public String []getLabelHeaders()
	{
		return labelHeaders;
	}

    //assign the new label headers
    public void setLabelHeader(String [] newLabelHeaders){
        if (newLabelHeaders!=null){
            int length = newLabelHeaders.length;
            labelHeaders=new String[length];
            for (int i=0; i < length; i++){
            	labelHeaders[i]=new String();
           	labelHeaders[i] = newLabelHeaders[i];
            }
        }
    }//setLabelHeader

	public void setNumHeaders(int i){
        numHeaders = i;
        root.setNumLabels(i);
    }//setNumHeaders

    public int getNumHeaders(){
        return numHeaders;
    }//getNumHeaders

    //----------------------new -----------------------
    private void disTree(TreeNode node, int depth)
    {
    	//System.out.println("PosX:"+node.getX()+", "+"PosY:"+node.getY());

    	if (node == null) System.out.println("GENE: "+node.getGeneLabel()+"PosX:"+node.getX()+", "+"PosY:"+node.getY());
    	else {
    	    	if (!node.isLeaf()){
			System.out.println(node.getGeneLabel() + " pos: " + node.getPos()+" totsize: "+node.getTotSize());
    	    		if (node.isRoot())
    	    	       	    System.out.println("It is root");
    	    	  	else System.out.println("Parent "+node.getParent().getGeneLabel());
    	    		node.printLeafSet();

    	    		disTree(node.getLeftNode(), depth + 1);
    	    		disTree(node.getRightNode(), depth + 1);

    	    	}
    	    	else {
						System.out.println(node.getGeneLabel() + "("+node.getGeneName()+")"+"  is a leaf"+" pos: "+node.getPos());
    	    	  	if (node.getParent() == null) System.out.println("Single leaf");

    	    	  	    else System.out.println("Parent "+node.getParent().getGeneLabel());
    	    	  	node.printLeafSet();
    	     	}

    	}
    }


    public void displayTree()
    {
        System.out.println();
        System.out.println("DISPLAY TREE");
        System.out.println();

        disTree(root, 0);
    }

    public void setRoot(TreeNode inNode)
    {
        if (root == null)
            root = new TreeNode();
        root = inNode;
    }

    public double getPreferredSize(TreeNode node){
        if (node.isLeaf())
            return node.getStemLength();
        else{
            double leftMaxLength = getPreferredSize(node.getLeftNode());
            double rightMaxLength = getPreferredSize(node.getRightNode());
            if (leftMaxLength > rightMaxLength)
                return (leftMaxLength+node.getStemLength());
            else
                return (rightMaxLength+node.getStemLength());
        }//else
    }//getPreferredSize

    private void setOrder(TreeNode node)
    {
        if (orderIndex<leafSetSize){
            if (node.isLeaf()){
				//modified label -> name
            	//order[orderIndex] = node.getGeneLabel();
            	order[orderIndex] = node.getGeneName();
            	orderIndex++;
            }
            else {
            	    setOrder(node.getLeftNode());
            	    setOrder(node.getRightNode());
            }
        }
    }

    public void setTreeOrder()
    {
        leafSetSize = root.getLeafSize();
        order = new String[leafSetSize];

        for(int i=0;i<leafSetSize;i++)
            order[i]=new String();

        orderIndex=0;
        setOrder(root);

        ordered = true;
    }

    public void setTreeOrder(String []inOrder)
    {
        order = new String[inOrder.length];
        for(int i=0;i<order.length;i++)
        	order[i]=inOrder[i];
    }

    public String []getOrder()
    {
        //if (!ordered)
        //	setTreeOrder();
        return order;
    }

	public void displayOrder()
	{
		for(int i=0;i<order.length;i++)
			System.out.print(order[i]+"  ");
		System.out.println();
	}

	public int getLeafSize()
	{
		return root.getLeafSize();
	}

    public void setPicFilePath(String filePath)
    {
        picFilePath = filePath;
    }

    public String getPicFilePath()
    {
        return picFilePath;
    }

    public int getLeafSetSize()
    {
        return leafSetSize;
    }

    public void setLeafSetSize()
    {
        if (root!=null)
            leafSetSize=root.getLeafSize();
    }

    public void displayNegTree()
    {
        TreeNode node=new TreeNode();
        node=root;
        System.out.println();
        System.out.println("DISPLAY NEGATIVE TREE");
        System.out.println();

        while(!node.isLeaf())
            node=node.getLeftNode();
        while (!node.isRoot()){
            System.out.println(node.getGeneLabel() + " pos: " + node.getPos());
    	    System.out.println("Parent "+node.getParent().getGeneLabel());

	    node=getNext(node);
	}
	System.out.println(node.getGeneLabel() + " pos: " + node.getPos());
	System.out.println("It is a root");
    }

    /* copy a subtree, the new tree has different address as the old one
     */
    private TreeNode copySubTree(TreeNode iter, TreeNode node)
    {
        TreeNode leftChild=new TreeNode();
	TreeNode rightChild=new TreeNode();

	if (iter!=null){

	    if (!iter.isLeaf()){
	        leftChild.setParent(node);
	        rightChild.setParent(node);
	        node.setGeneLabel(iter.getLeftNode().getGeneLabel()+iter.getRightNode().getGeneLabel());
		node.addLeafSet(iter.getLeftNode().getLeafSet());
		node.addLeafSet(iter.getRightNode().getLeafSet());
		node.setMyLeftNode(leftChild);
		node.setMyRightNode(rightChild);

	        copySubTree(iter.getLeftNode(), leftChild);

	        copySubTree(iter.getRightNode(), rightChild);
	        node.setTotSize(leftChild.getTotSize()+rightChild.getTotSize()+1);
	   }

	   else {

	          node.setLeafLabel(new String(iter.getGeneLabel()));
	          //modified label -> name
	          node.setLeafName(new String(iter.getGeneName()));
	          node.setTotSize(1);
		}

	}
	return node;
    }

    private TreeNode buildSubTree(TreeNode node1, TreeNode node2)
    {
        TreeNode freshRoot=new TreeNode();

        parChild(freshRoot, node1, LEFT);
        parChild(freshRoot, node2, RIGHT);

        return freshRoot;
    }

    /* re-root the tree according to the leaf inputed
     * the leaf works as the root. The original tree
     * is not changed, a new tree is created.
     */

    public Tree reRoot(String rootLeaf, String rootLabel)
    {
        TreeNode iter=root;
        boolean located=false;
        TreeNode reRootTreeNode=null;
        Tree reRootTree;

        if (!iter.isLeaf()&&(iter.getLeafSet().contains(rootLeaf))){
            while(!located){
                TreeNode leftNode = iter.getLeftNode();
                TreeNode rightNode = iter.getRightNode();

                if(leftNode.getLeafSet().contains(rootLeaf)){
                   	TreeNode temp2=new TreeNode();
             		temp2 = copySubTree(rightNode, temp2);
             		if (reRootTreeNode==null)
             		    reRootTreeNode=temp2;
             		else {
             			reRootTreeNode=buildSubTree(reRootTreeNode, temp2);
             	 	}

                        if (leftNode.isLeaf()){
                             TreeNode temp1=new TreeNode();
                             temp1.setLeafLabel(rootLabel);
                             temp1.setLeafName(rootLeaf);
                             reRootTreeNode=buildSubTree(reRootTreeNode, temp1);
                             located = true;
                        }
                        else {
                        	iter = leftNode;
                        	//continue;
                        }
                 }
                 else {
                 	TreeNode temp2 = new TreeNode();
                 	temp2 = copySubTree(leftNode, temp2);
                 	if (reRootTreeNode ==  null)
                 		reRootTreeNode = temp2;
                 	else {
                 		reRootTreeNode=buildSubTree(reRootTreeNode, temp2);
                 	}
                 	if (rightNode.isLeaf()){
                 		TreeNode temp1 = new TreeNode();
                 		//modified label -> name
                 		//temp1.setLeaf(rootLeaf);
                 		temp1.setLeafName(rootLeaf);
                 		temp1.setLeafLabel(rootLabel);
                 		reRootTreeNode=buildSubTree(reRootTreeNode, temp1);
                 		located = true;
                 	}
                 	else {
                 		iter=rightNode;
                 		//continue;
                 	}
         	}
         	/*
         	Tree t = new Tree();
         	t.setRoot(reRootTreeNode);
         	t.displayTree();
         	t.displayNegTree();
         	*/
           }   //end of while
           if (located){
           	reRootTree = new Tree();
           	iniPos=1;
  		setReRootPos(reRootTreeNode);
           	reRootTree.setRoot(reRootTreeNode);

           	return reRootTree;
           }
        }    // end of if

        //the leaf does not exist in the tree
        return null;

    }

    /* This position is different from the one
     * used in MAST Tab, this is in-suffix: root position
     * is 1 and the deeper the bigger.
     */

    private void setReRootPos(TreeNode reRootNode)
    {
        if (!reRootNode.isLeaf()){
            reRootNode.setPos(iniPos);
            iniPos++;
            setReRootPos(reRootNode.getLeftNode());
            setReRootPos(reRootNode.getRightNode());
        }
        else reRootNode.setPos(0);
    }

    public void setTreePos(){
    	iniPos = 1;
    	setReRootPos(getRoot());
    }

	public String getNameIndex(String leafName)
	{
		getNameIndexIter(getRoot(), leafName);
		return nameIndex;
	}

	private void getNameIndexIter(TreeNode node, String leafName)
	{
		if (node!=null){
			if(node.isLeaf()){
				if (node.getGeneName().equals(leafName))
					nameIndex = node.getGeneLabel();
			}
			else {
					getNameIndexIter(node.getLeftNode(), leafName);
					getNameIndexIter(node.getRightNode(), leafName);
			}
		}
	}

    private static TreeNode getNext(TreeNode inNode)
     {
         TreeNode nextNode;

         if (inNode == null) return null;
         else {
         	if (inNode.isRoot()){
         	    return null;
         	}
         	else {
         		//if inNode is a left child, then go to its sibling's left most child

         		if (inNode.getGeneLabel().equals(inNode.getParent().getLeftNode().getGeneLabel())){
						if (!inNode.getGeneLabel().equals(inNode.getParent().getRightNode().getGeneLabel()))
							nextNode = getLeftMost(inNode.getParent().getRightNode());
						else {
								//left and right child has the same genelabel
         						if (inNode.getLeafSet().equals(inNode.getParent().getLeftNode().getLeafSet()))
         	    	    			nextNode = getLeftMost(inNode.getParent().getRightNode());
         	    	    		else
         	    	    			nextNode = inNode.getParent();
         	    	    }
         		}
         		else {
         			//else inNode is a right child, then go to its parent
         			nextNode = inNode.getParent();
         		}
         	}
         }

         return nextNode;
     }

     private static TreeNode getLeftMost(TreeNode inNode)
     {
         TreeNode nextNode;

         if (inNode == null) return null;
         else {
         	 nextNode = inNode;
         	 while(!nextNode.isLeaf()) nextNode = nextNode.getLeftNode();
         }

         return nextNode;
     }
    //-------------------------------------------

    public static void main (String args[]){
        String str=new String();
        str="((f,(((g,e),(d,c)),b)),a);";
        Set set=new HashSet();
        Tree tree=new Tree(str, set, "");
        System.out.println("Original tree");
        tree.displayTree();

        Tree t=new Tree();
        String rootLeaf=new String();
        rootLeaf="g";
        t=tree.reRoot(rootLeaf, "");
        System.out.println("Re-rooted tree(root: "+rootLeaf+")");

        t.displayTree();
        System.out.println();
        t.displayNegTree();

        /*
        System.out.print("Input a nexus sequence\n>>");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try{
            String treeFile = br.readLine();
            br.close();
            Tree test = new Tree(treeFile);
            test.setDepth(test.getRoot(), 0);

            //display the tree
            int depth = 1;
            //test.displayTree(test.getRoot(), depth);
            int height = test.getTreeDepth();
            System.out.println("Tree height = " + height);
            System.out.println("Number of children = " + test.getRoot().getNumberOfChildren(0));
        }//try
        catch(Exception e){}
        */
    }//main
}//class Tree