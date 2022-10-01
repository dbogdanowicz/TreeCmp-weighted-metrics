package treecmp.treeanalyzer;

import java.awt.*;
import java.io.*;
import java.util.*;

public class TreeNode implements Serializable{
    private TreeNode leftSubtree;
    private TreeNode rightSubtree;
    private boolean booLeaf;
    private String [] labels;
    private int numLabels;
    public int depth;
    private double stemLength;
    private int xPos;
    private int yPos;
    private Color labelColor;
    private Font labelFont;
    private int fontSize;
    private String imageName;
    private static int panelHeight;
    private static int panelWidth;

    //---------------------new --------------------
    private Set leafSet;		// the set of leaves in this subtree
    private int totNode;		// Total # of nodes in this subtree
					// including both leaves and interior
					// nodes.

    private TreeNode parent;		// The parent of this tree node
    private final static String LEFT = "LEFT";
    private final static String RIGHT = "RIGHT";
    private int pos;
    //-------------------------------------------

    //default constuctor
    public TreeNode(){
        leftSubtree = null;
        rightSubtree = null;
        parent = null;
        booLeaf = false;
        numLabels = 4;
        labels = new String[10];
        for (int i=0; i < 10; i++)
            labels[i] = "";
        panelHeight = 1;
        panelWidth = 1;
        imageName = null;
        //recover
        labelFont = Font.decode("Arial");
        //-------
        fontSize = 10;

        //----------------new --------------------
        leafSet = new HashSet();
        totNode = 1;		//This node is itself
        parent = null;
        stemLength=1;
        //-----------------------------------------

    }//default constructor

    //------------------------new -----------------------
    public TreeNode (TreeNode inNode) {
    	leftSubtree=new TreeNode();
    	leftSubtree = inNode.getLeftNode();
	rightSubtree=new TreeNode();
	rightSubtree = inNode.getRightNode();
    	booLeaf = inNode.isLeaf();
    	numLabels = inNode.getNumLabels();
    	labels = new String[10];
    	for (int i=0;i<10;i++)
    	    labels[i]=inNode.labels[i];

    	panelHeight = inNode.getPanelHeight();
    	panelWidth = inNode.getPanelWidth();
    	imageName = inNode.getImage();
    	leafSet = inNode.getLeafSet();
    	totNode = inNode.getTotSize();
	parent=new TreeNode();
	parent = inNode.getParent();
    	pos=inNode.getPos();
    	xPos = inNode.getX();
    	yPos = inNode.getY();
    	//recover
    	labelFont = inNode.getFont();
    	fontSize = inNode.getFontSize();
    	stemLength = inNode.getStemLength();
	    //-------
    }

    public void makeLeaf(TreeNode inNode)
    {
        labels[0] = new String(inNode.getGeneLabel());
		//modified label -> name
		labels[3] = new String(inNode.getGeneName());
		booLeaf = true;
		//modified label -> name
		leafSet.add(labels[3]);
		//leafSet.add(labels[0]);
    }

    //--------------------------------------------------------

    //return the left subtree
    public TreeNode getLeftNode(){
        return leftSubtree;
    }//getLeftNode

    //return the right subtree
    public TreeNode getRightNode(){
        return rightSubtree;
    }//getRightNode

    //assign leftChild to the left subtree
    public void setLeftNode(TreeNode leftChild){
        if (!booLeaf)
            leftSubtree = leftChild;
    }//setLeftNode

    //-------------------new ----------------
    public void setMyLeftNode(TreeNode leftChild)
    {
    	leftSubtree=new TreeNode();
    	leftSubtree = leftChild;
    }
    public void setMyRightNode(TreeNode rightChild)
    {
        rightSubtree=new TreeNode();
        rightSubtree = rightChild;
    }

    //----------------------------------------

    //assign rightChild to the left subtree
    public void setRightNode(TreeNode rightChild){
        if (!booLeaf)
            rightSubtree = rightChild;
    }//setRightNode

    //set this node to a leaf and assign the gene label
    /*
    public void setLeaf(String l){
        setLabel(l, 0);
        booLeaf = true;
        leafSet.add(l);
    }//setLeaf
	*/

	//modified label -> name
	public void setLeafLabel(String l){
		setLabel(l, 0);
		booLeaf = true;
	}

	public void setLeafName(String s){
		setLabel(s, 3);
		leafSet.add(s);
	}

    //return true if leaf and false if inner node
    public boolean isLeaf(){
        return booLeaf;
    }//isLeaf()

    //assign the depth as d
    public void setDepth(int d){
        depth = d;
    }//setDepth()

    //assign the label to the correct index
    public void setLabel(String l, int index){
        labels[index] = l;
    }//setLabel()

    //set the color for the label text
    public void setLabelColor(Color c){
        labelColor = c;
    }//setLabelColor(c)

    //---------------------new ------------------
    public void setGeneLabel(String l)
    {
    	labels[0]=l;
    }

    public String getGeneLabel()
    {
    	return labels[0];
    }

    public void setGeneName(String l)
    {
        labels[3]=l;
    }

    public String getGeneName()
    {
        return labels[3];
    }

    public String []getLabels()
    {
        return labels;
    }

    //set labels except the gene label
    public void setLabelsEx(String []labelsEx)
    {
         if (labelsEx.length>=10)
             for(int i=1;i<10;i++)
         	labels[i]=labelsEx[i];
    }

    //-------------------------------------------

    //return the color for the label text
    public Color getLabelColor(){
        return labelColor;
    }//getLabelColor()

    //set the number of labels for the nodes
    public void setNumLabels(int newNumLabels){
        if (newNumLabels > 0){
            numLabels = newNumLabels;
            if (!this.isLeaf()){
                //call this on both children
                this.getLeftNode().setNumLabels(newNumLabels);
                this.getRightNode().setNumLabels(newNumLabels);
            }//if
        }//if
    }//setNumLabels

    //return the number of labels associated with this node
    public int getNumLabels(){
        return numLabels;
    }//getNumLabels()

    //return the label at the given index
    public String getLabel(int index){
        return labels[index];
    }//getLabel()

    //set the path for the node image
    public void setImage(String path){
        imageName = path;
    }//setImage(path)

    //return the path for the node image
    public String getImage(){
        return imageName;
    }//getImage

    //set the stem Length
    public void setStemLength(double stem){
        stemLength = stem;
    }//setStemLength

    //Return the stem length
    public double getStemLength(){
        return stemLength;
    }//getStemLength

    //assign x coordinate for the node

    /*
    public void setX(int x){
        if (x < 20)
            xPos = 2;
        else if (x > (panelWidth - 10))
            xPos = panelWidth - 10;
        else
            xPos = x;
    }//setX()
	*/

	 public void setX(int x){
        xPos = x;
    }//setX()

    //assign the y coordinate for the node
    public void setY(int y){
        yPos = y;
    }//setY()

    //return the x coordinate
    public int getX(){
        return xPos;
    }//getX()

    //return the y coordinate
    public int getY(){
        return yPos;
    }//getY()

    //set the panel height value for the node
    public void setPanelHeight(int height){
        panelHeight = height;
    }//setPanelHeight(height)

    //return the panel height value of the node
    public int getPanelHeight(){
        return panelHeight;
    }//getPanelHeight()

    //set the panel width value for the node
    public void setPanelWidth(int width){
        panelWidth = width;
    }//setPanelWidth

    //return the panel width value of the node
    public int getPanelWidth(){
        return panelWidth;
    }//getPanelWidth

    //return the number of children this node has
    public int getNumberOfChildren(int children){
        if (this.isLeaf())
            return children + 1;
        else
            return (this.getLeftNode().getNumberOfChildren(children) + this.getRightNode().getNumberOfChildren(children));
    }//getNumberOfChildren(node, children)

    //rotate the children of this node
    public void swapChildren(){
        if (!isLeaf()){
            TreeNode temp = getRightNode();
            setRightNode(getLeftNode());
            setLeftNode(temp);
        }//if
    }//swapChildren

    //set the font value for this node
    public void setFont(Font fontValue){
        labelFont = fontValue;
    }//setFont()

    //return the font value of this node
    public Font getFont(){
        return labelFont;
    }//getFont()

    //set the font size for this node
    public void setFontSize(int size){
        fontSize = size;
    }//setFontSize

    //return the font siez of this node
    public int getFontSize(){
        return fontSize;
    }//getFontSize

    //------------------new --------------------------------

    public void addLeaf(String inLeaf){
	leafSet.add(inLeaf);
    }

    public void addLeafSet(HashSet inSet)
    {
	leafSet.addAll(inSet);
    }

    public boolean containLeaf(String inLeaf){
	return leafSet.contains(inLeaf);
    }

    public int getLeafSize(){
	return leafSet.size();
    }

    public HashSet getLeafSet(){
	return (HashSet)leafSet;
    }

    public void printLeafSet()
    {
	System.out.println("Leaf Set");

	if(leafSet.size()!=0){
	    Iterator it = leafSet.iterator();
	    while (it.hasNext()){
	    	System.out.print(it.next()+"  ");
	    }
	    System.out.println();
	}
	else System.out.println(" No leaf in the subtree!");
    }

    /* Total nodes(including internal node and leaves) are
     * created when build the tree.
     */
    public int getTotSize(){
	return totNode;
    }

    public void setTotSize(int inSize){
 	totNode = inSize;
    }

    public TreeNode getParent()
    {
 	return parent;
    }

    public void setParent(TreeNode inPare)
    {
 	parent=new TreeNode();
 	parent = inPare;
    }

    public boolean isRoot()
    {
 	if (parent == null) return true;
 	return false;
    }

    /* To build the relationship between two nodes
     * @ param: inPar:parent, inChild: child, lOrR: left or right child
     * @ return: void
     */
    public void setChild(TreeNode inChild, String lOrR)
    {
    	inChild.setParent(this);
        setTotSize(getTotSize()+inChild.getTotSize());
        addLeafSet(inChild.getLeafSet());
        setGeneLabel(getGeneLabel() + inChild.getGeneLabel());

        if (lOrR.equalsIgnoreCase(LEFT)) setLeftNode(inChild);
        else setRightNode(inChild);
    }

    public void setPos(int inPos)
    {
        pos=inPos;
    }

    public int getPos()
    {
  	return pos;
    }

    /* decide whether two sub trees intersect or not
     */

    public boolean intersect(TreeNode tree2)
    {
      	Set set1=new HashSet();
        Set set2=new HashSet();

        set1=getLeafSet();
        set2=tree2.getLeafSet();

        Iterator iter=set1.iterator();

        while(iter.hasNext()){
      	    if (set2.contains((String)iter.next()))
          	return true;
        }

        return false;
    }

    public int getDepth()
    {
        return depth;
    }

    public Font getLabelFont()
    {
        return labelFont;
    }

    public void setLabelFont(Font inFont)
    {
        labelFont=inFont;
    }

    //-------------------------------------------------------
}//class TreeNode