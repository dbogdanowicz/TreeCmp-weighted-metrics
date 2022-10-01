package treecmp.treeanalyzer;

import java.io.*;
import javax.swing.*;
import java.awt.*;

/*
 * This class is the panel that holds the actual tree structure on the screen.
 */
public class TreePanel extends JPanel{

    public Tree dendro;							//tree associated with this panel
    private TreePanelListener treeListener;					//listener to catch mouse clicks
    private TreePanelMouseMotionListener mouseListener;				//listener to catch mouse movements
    private int ribotypeX;							//start X coordinate for ribotypes
    private int labelX;								//start X coordinate for labels
    private int [] labelBorder;							//start X coordinate for label borders
    private boolean moveBorder1;						//boolean for moving border1
    private boolean [] moveLabelBorder;						//boolean for moving label borders
	private DisplayDataSet dataSet;

    /*
     * Default constructor for class, set references to null and create listener
     */
    public TreePanel(){
        super();
        treeListener = new TreePanelListener();
        mouseListener = new TreePanelMouseMotionListener();
        dendro = null;
        moveBorder1 = false;
        moveLabelBorder = new boolean[10];
        //default the values to false
        for (int i=0; i < 10; i++)
            moveLabelBorder[i] = false;
        treeListener.setTreePanel(this);
        mouseListener.setTreePanel(this);
        addMouseListener(treeListener);
        addMouseMotionListener(mouseListener);
    }//default constructor

    //Method used to assign a tree to the panel
    public void setTree(Tree t){
        //assign tree to this panel and the listeners
        dendro = t;
        treeListener.setTree(t);
        mouseListener.setTree(t);

        //calculate the initial ribotypeX and labelX
        ribotypeX = 250;
        TreeNode temp = dendro.getRandomChild();
        ImageIcon tempImage = new ImageIcon(temp.getImage());
        int tempWidth = tempImage.getImage().getWidth(null);
        if (tempWidth < 20)
            tempWidth = 270;
        labelX = tempWidth + ribotypeX + 5;
        labelBorder = new int [10];

        for (int i=0; i < 10; i++)
            labelBorder[i] = labelX + (50 * i);
    }//setTree

	public void setDendro(Tree t){
		dendro = t;
		treeListener.setTree(t);
		mouseListener.setTree(t);
	}
	
    //Method used to assign the rotate button
    public void setRotateButton(JButton r){
        treeListener.setRotateButton(r);
    }//setRotateButton

    //Method used to assign the font button
    public void setFontButton(JButton f){
        treeListener.setFontButton(f);
    }//setFontButton

    //Method used to assign the label header button
    public void setLabelHeaderButton(JButton h){
        treeListener.setLabelHeaderButton(h);
    }//setLabelHeaderButton

    //Method used to return the ribotypeX
    public int getRibotypeX(){
        return ribotypeX;
    }//getRibotypeX

    //Method used to set the ribotypeX
    public void setRibotypeX(int newRibotypeX){
        ribotypeX = newRibotypeX;
    }//setRibotypeX

    //Method used to return the moveBorder1 value
    public boolean getMoveBorder1(){
        return moveBorder1;
    }//getMoveBorder1

    //Method used to set the moveBorder1 value
    public void setMoveBorder1(boolean value){
        moveBorder1 = value;
    }//setMoveBorder1

    //Method used to return the labelBorder array
    public int [] getLabelBorder(){
        return labelBorder;
    }//getLabelBorder

    //Method used to set the labelBorder array
    public void setLabelBorder(int [] newLabelBorders){
        labelBorder = newLabelBorders;
    }//setLabelBorder

    //Method used to set the label border x value for a label
    public void setLabelBorderValue(int newLabelBorderX, int index){
        labelBorder[index] = newLabelBorderX;
    }//setLabelBorder

    //Method used to return the label border x value of a label
    public int getLabelBorderValue(int index){
        return labelBorder[index];
    }//getLabelBorder

    //Method used to set the MoveLabelBorder value of a label
    public void setMoveLabelBorder(boolean value, int index){
        moveLabelBorder[index] = value;
    }//setMoveLabelBorder

    //Method used to return the MoveLabelBorder value of a label
    public boolean getMoveLabelBorder(int index){
        return moveLabelBorder[index];
    }//getMoveLabelBorder

    //Recursive method used to actually draw the tree
    public void drawTree(Graphics g, TreeNode node){
        g.setColor(Color.black);
        ImageIcon tempImage;
        int stemX;
        int stemY;
        int adjustHeight;

        if (node.isLeaf()){
            //draw the node
            g.fillRect(node.getX(), node.getY(), 5, 5);
        }//if
        else{
            //calculate the adjusted height based on the image
            tempImage = new ImageIcon(dendro.getRandomChild().getImage());
            adjustHeight = (tempImage.getIconHeight() / 2);

            //draw the node itself
            g.fillRect(node.getX(), node.getY() + adjustHeight, 5, 5);

            //now draw the lines connecting parent to child
            g.drawLine(node.getX(), node.getY() + adjustHeight, node.getX(), node.getRightNode().getY() + adjustHeight);
            g.drawLine(node.getX(), node.getRightNode().getY() + adjustHeight, node.getRightNode().getX(), node.getRightNode().getY() + adjustHeight);
            g.drawLine(node.getX(), node.getY() + adjustHeight, node.getX(), node.getLeftNode().getY() + adjustHeight);
            g.drawLine(node.getX(), node.getLeftNode().getY() + adjustHeight, node.getLeftNode().getX(), node.getLeftNode().getY() + adjustHeight);

            //draw the length of the branch
            g.setColor(Color.red);
            //temporary modify
            //recover
            g.setFont(new Font("Stems", Font.PLAIN, 10));
			//------

            stemX = ((node.getRightNode().getX() + node.getX()) / 2) - 5;
            stemY = node.getRightNode().getY() + adjustHeight;
            g.drawString(String.valueOf(node.getRightNode().getStemLength()), stemX, stemY);

            stemX = ((node.getLeftNode().getX() + node.getX()) / 2) - 5;
            stemY = node.getLeftNode().getY() + adjustHeight;
            g.drawString(String.valueOf(node.getLeftNode().getStemLength()), stemX, stemY);

            //call method on the children draw the two children
            drawTree(g, node.getLeftNode());
            drawTree(g, node.getRightNode());
        }//else
    }//drawTreeHelper

    //Recursive method used to actually draw the ribotype images
    public void drawRibotype(Graphics g, TreeNode node){
        if (node.isLeaf()){
            //draw the ribotype
            ImageIcon tempImage;
            tempImage = new ImageIcon(node.getImage());
            g.drawImage(tempImage.getImage(), ribotypeX + 5, node.getY() - (tempImage.getIconHeight() / 2), null);
        }//if
        else{
            //call method on the children draw the two children
            drawRibotype(g, node.getLeftNode());
            drawRibotype(g, node.getRightNode());
        }//else
    }//draw Ribotype

    //Recursive method used to actually draw the labels
    public void drawLabel(Graphics g, TreeNode node){
        if (node.isLeaf()){
            String tempLabel;

            //now go ahead and draw the labels
            for(int i=0; i < node.getNumLabels(); i++){
            	if (i==0)
            		tempLabel = node.getLabel(3);
            	else
                	tempLabel = node.getLabel(i);
                g.setColor(node.getLabelColor());
                //recover
                g.setFont(new Font(node.getFont().getFontName(), node.getFont().getStyle(), node.getFontSize()));
                g.drawString(tempLabel, labelBorder[i] + 5, node.getY() + 5);
            }//for

        }//if
        else{
            //call method on the children draw the two children
            drawLabel(g, node.getLeftNode());
            drawLabel(g, node.getRightNode());
        }//else
    }//draw Ribotype


    //override this method in order to get the Graphics content, then call drawTreeHelper
    public void paintComponent(Graphics g){
        super.paintComponent(g);				//Get the graphics content

        //only attempt to draw tree if the tree is not null
        if (dendro != null){
            dendro.setCoords(this);

            //draw the tree
            drawTree(g, dendro.getRoot());

            //draw the background for the ribotypes
            g.setColor(Color.gray);
            g.fillRect(ribotypeX, 0, labelBorder[0] - ribotypeX, this.getHeight());

            //draw the ribotypes
            drawRibotype(g, dendro.getRoot());

            //draw the background for the labels
            g.setColor(Color.white);
            g.fillRect(labelX,0, this.getWidth() - labelX, this.getHeight());

            //draw the label headers
            g.setColor(Color.black);
            //recover
            g.setFont(new Font("Headers", Font.PLAIN, 10));
            for (int i=0; i < dendro.getNumHeaders(); i++){
                g.drawString(dendro.getLabelHeader(i), labelBorder[i] + 5, 23);
            }//for

            //draw the labels
            drawLabel(g, dendro.getRoot());

            //draw the borders
            int tempPanelHeight = dendro.getPanelHeight();
            g.setColor(Color.blue);
            g.drawLine(ribotypeX - 1, 0, ribotypeX - 1, tempPanelHeight);
            g.drawLine(ribotypeX, 0, ribotypeX, tempPanelHeight);
            g.drawLine(ribotypeX + 1, 0, ribotypeX + 1, tempPanelHeight);

            //draw the label borders
            for (int i=0; i < dendro.getNumHeaders(); i++){
                //set the color for the border, first one should be blue, others transparent
                if (i == 0)
                    g.setColor(Color.blue);
                else
                    g.setColor(Color.white);

                //draw the actual line
                g.drawLine(labelBorder[i] - 1, 0, labelBorder[i] - 1, tempPanelHeight);
                g.drawLine(labelBorder[i], 0, labelBorder[i], tempPanelHeight);
                g.drawLine(labelBorder[i] + 1, 0, labelBorder[i] + 1, tempPanelHeight);
            }//for

            dataSet.saved = false;
        }//if
    }//paintComponent

    public void setDataSet(DisplayDataSet inDataSet)
    {
    	dataSet = inDataSet;
    }
}//class TreePanel