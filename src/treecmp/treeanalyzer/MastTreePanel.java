package treecmp.treeanalyzer;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;


/*
 * This class is the panel that holds the actual tree structure on the screen.
 */
public class MastTreePanel extends JPanel{

    private Tree dendro;							//tree associated with this panel
    public TreePanelListener treeListener;					//listener to catch mouse clicks
    private TreePanelMouseMotionListener mouseListener;				//listener to catch mouse movements
    private int ribotypeX;							//start X coordinate for ribotypes
    private int labelX;								//start X coordinate for labels
    private int labelBorder;							//start X coordinate for label borders
    private boolean moveBorder1;						//boolean for moving border1
    private boolean [] moveLabelBorder;						//boolean for moving label borders
    private final int LEFTSIDE = 0;
    private final int RIGHTSIDE = 1;
    private int side;
    private final int LABELLOC = 10;
    private int LABELNAMELOC = 20;
    private final int PANELWIDTH = 330;
    final static int POINTSIZE = 4;
    private int SCREENWIDTH = PANELWIDTH - 30;
    private Tree mast;
    private TreeNode mastRoot;
    private Set mastLeafSet;
    private boolean numOrName=true;
    //recover
    private int STRFONTSIZE = Font.PLAIN;
    //--------
    private int adjustWidth = 0;
    private int oldWidth = 0;
    private int maxGeneName = 0;
    public boolean start = false;

    /*
     * Default constructor for class, set references to null and create listener
     */
    public MastTreePanel(int inSide){
        super();
        treeListener = new TreePanelListener();
        mouseListener = new TreePanelMouseMotionListener();
        dendro = null;
        moveBorder1 = false;
        moveLabelBorder = new boolean[10];
        //default the values to false
        for (int i=0; i < 10; i++)
            moveLabelBorder[i] = false;
        side = inSide;
        treeListener.setMastTreePanel(this, side);
        //mouseListener.setMastPanel(this);
        addMouseListener(treeListener);
        //addMouseMotionListener(mouseListener);
        mast = new Tree();
        mastRoot = new TreeNode();
        mastLeafSet = new HashSet();

    }//default constructor

    //Method used to assign a tree to the panel
    public void setTree(Tree t){
        //assign tree to this panel and the listeners
        dendro = t;
        treeListener.setTrees(t, side);
        mouseListener.setTree(t);

        //calculate the initial ribotypeX and labelX
        ribotypeX = 250;
        TreeNode temp = dendro.getRandomChild();
        ImageIcon tempImage = new ImageIcon(temp.getImage());
        int tempWidth = tempImage.getImage().getWidth(null);
        if (tempWidth < 20)
            tempWidth = 270;
        labelX = tempWidth + ribotypeX + 5;
        labelBorder = 250;

    }//setTree

    //--------------------------new -----------------------------
    /* display mast tree also, which should be mapped on the compared tree.
     */

    public void setMastLeaf(Set leaf){
    	mastLeafSet = leaf;
    }

	public void setMastTree(Tree t){
    	mast = t;
		mastRoot = t.getRoot();
		mastLeafSet = mastRoot.getLeafSet();
    }
    public void setLabelName(boolean setName)
    {
        numOrName=setName;
    }

    //----------------------------------------------------------

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
    public int getLabelBorder(){
        return labelBorder;
    }//getLabelBorder

    //Method used to set the labelBorder array
    public void setLabelBorder(int newLabelBorders){
        labelBorder = newLabelBorders;
    }//setLabelBorder

    //Method used to return the label border x value of a label
    public int getLabelBorderValue(){
        return labelBorder;
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
    public void drawTree(Graphics g, TreeNode node, int d){
        g.setColor(Color.black);
        ImageIcon tempImage;
        int stemX;
        int stemY;
        int adjustHeight;

        if (!node.isLeaf()){

			node.setX(node.getX()+d);
        	//g.fillRect(node.getX()-POINTSIZE/2, node.getY()-POINTSIZE/2, POINTSIZE, POINTSIZE);

            //calculate the adjusted height based on the image
            tempImage = new ImageIcon(dendro.getRandomChild().getImage());
            adjustHeight = (tempImage.getIconHeight() / 2);

            //now draw the lines connecting parent to child
            if (mast!=null){
                Set rightSet = new HashSet();
                rightSet = node.getRightNode().getLeafSet();

				if (intersect(rightSet, mastLeafSet)){
               	    g.setColor(new Color(50,100,255));
					if (side == LEFTSIDE){
						g.fillRect(node.getX()-1, node.getRightNode().getY() + adjustHeight, 2, node.getY() - node.getRightNode().getY());
						g.fillRect(node.getX(), node.getRightNode().getY() + adjustHeight-1, node.getRightNode().getX()+d-node.getX(), 2);
					}
					else {
							g.fillRect(node.getX()-1, node.getRightNode().getY() + adjustHeight, 2, node.getY() - node.getRightNode().getY());
							g.fillRect(node.getRightNode().getX()+d, node.getRightNode().getY() + adjustHeight-1, node.getX() - (node.getRightNode().getX()+d), 2);
					}

				}
				else {
						g.drawLine(node.getX(), node.getY() + adjustHeight, node.getX(), node.getRightNode().getY() + adjustHeight);
						g.drawLine(node.getX(), node.getRightNode().getY() + adjustHeight, node.getRightNode().getX()+d, node.getRightNode().getY() + adjustHeight);
				}

				g.setColor(Color.black);

                Set leftSet = new HashSet();
                leftSet = node.getLeftNode().getLeafSet();

                if (intersect(leftSet, mastLeafSet)){
                    g.setColor(new Color(50,100,255));

					if (side == LEFTSIDE){
						g.fillRect(node.getX()-1, node.getY() + adjustHeight, 2, node.getLeftNode().getY() - node.getY());
						g.fillRect(node.getX(), node.getLeftNode().getY() + adjustHeight-1, node.getLeftNode().getX()+d-node.getX(), 2);
					}
					else {
							g.fillRect(node.getX()-1, node.getY() + adjustHeight, 2, node.getLeftNode().getY() - node.getY());
							g.fillRect(node.getLeftNode().getX()+d, node.getLeftNode().getY() + adjustHeight-1, node.getX() - (node.getLeftNode().getX()+d), 2);
					}
				}

                g.drawLine(node.getX(), node.getY() + adjustHeight, node.getX(), node.getLeftNode().getY() + adjustHeight);
				g.drawLine(node.getX(), node.getLeftNode().getY() + adjustHeight, node.getLeftNode().getX()+d, node.getLeftNode().getY() + adjustHeight);

            	g.setColor(Color.black);
            }

			g.fillRect(node.getX()-POINTSIZE/2, node.getY()-POINTSIZE/2, POINTSIZE, POINTSIZE);

            /*
            //draw the length of the branch
            g.setColor(Color.red);
            g.setFont(new Font("Stems", Font.PLAIN, 10));

            stemX = ((node.getRightNode().getX() + node.getX()) / 2) - 5;
            stemY = node.getRightNode().getY() + adjustHeight;
            g.drawString(String.valueOf(node.getRightNode().getStemLength()), stemX, stemY);

            stemX = ((node.getLeftNode().getX() + node.getX()) / 2) - 5;
            stemY = node.getLeftNode().getY() + adjustHeight;
            g.drawString(String.valueOf(node.getLeftNode().getStemLength()), stemX, stemY);
	    */

            //call method on the children draw the two children
            drawTree(g, node.getLeftNode(), d);
            drawTree(g, node.getRightNode(), d);
        }//else
        else {
				node.setX(node.getX()+d);
		}
    }//drawTreeHelper

    private boolean intersect(Set leafSet1, Set leafSet2)
    {
        Iterator it1 = leafSet1.iterator();

        while(it1.hasNext()){
        	if (leafSet2.contains((String)it1.next()))
        		return true;
        }
        return false;
    }
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
    public void drawLabel(Graphics g, TreeNode node, int side){
        if (node.isLeaf()){
            String tempLabel;

            //now go ahead and draw the labels
            tempLabel = node.getLabel(0);
            //modified label -> name
            //if (mastLeafSet.contains(node.getGeneLabel()))
            System.out.println("Start or not:?"+start);

            if (!start){
            	if (mastLeafSet.contains(node.getGeneName()))
            		node.setLabelColor(Color.blue);
            	else node.setLabelColor(Color.black);
			}

			g.setColor(node.getLabelColor());

            //need recover
            g.setFont(new Font(node.getFont().getFontName(), node.getFont().getStyle(), node.getFontSize()));
            //-----------------

            if (side == LEFTSIDE){
                //recover
            	g.drawString(tempLabel, this.getWidth() - LABELLOC - tempLabel.length()*STRFONTSIZE, node.getY() + 5);
            	//-------
            }
            else if (side == RIGHTSIDE)
            		g.drawString(tempLabel, LABELLOC, node.getY()+5);

        }//if
        else{
            //call method on the children draw the two children
            drawLabel(g, node.getLeftNode(), side);
            drawLabel(g, node.getRightNode(), side);
        }//else
    }

    public void drawLabelName(Graphics g, TreeNode node, int side){
        if (node.isLeaf()){
            String tempLabel;

            //now go ahead and draw the labels
            /* the label name is put in labels[3] */
            tempLabel = node.getLabel(3);
            //modified label -> name
            //if (mastLeafSet.contains(node.getGeneLabel()))
            if (!start){
				if (mastLeafSet.contains(node.getGeneName()))
	        	    node.setLabelColor(Color.blue);
            	else node.setLabelColor(Color.black);
			}

			g.setColor(node.getLabelColor());

            LABELNAMELOC=tempLabel.length();

			if (tempLabel.length()>maxGeneName)
					maxGeneName = tempLabel.length();

            //need recover
            g.setFont(new Font(node.getFont().getFontName(), node.getFont().getStyle(), node.getFontSize()));
            //--------

            if (side == LEFTSIDE){
            	//recover
            	//g.drawString(tempLabel, this.getWidth() - LABELLOC - LABELNAMELOC * STRFONTSIZE, node.getY() + 5);
            	g.drawString(tempLabel, this.getWidth() - LABELLOC - LABELNAMELOC*6, node.getY() + 5);
            	//-------
            }
            else if (side == RIGHTSIDE)
            		g.drawString(tempLabel, LABELLOC, node.getY()+5);

        }//if
        else{
            //call method on the children draw the two children
            drawLabelName(g, node.getLeftNode(), side);
            drawLabelName(g, node.getRightNode(), side);
        }//else
    }


    //override this method in order to get the Graphics content, then call drawTreeHelper
    public void paintComponent(Graphics g){
        super.paintComponent(g);				//Get the graphics content

        //only attempt to draw tree if the tree is not null
        if (dendro != null){
            //draw the tree
            //int treeScreenWidth = dendro.getMaxNodeLength(dendro.getRoot(), 0);
            double treeScreenWidth = dendro.getPreferredSize(dendro.getRoot());
            double factor = 1;

			//draw the labels
			//recover
			STRFONTSIZE = 7;
			//-----------

			if (numOrName)
			   	drawLabel(g, dendro.getRoot(), side);
			else
			   	drawLabelName(g, dendro.getRoot(), side);

			if (numOrName){
			      	if (SCREENWIDTH>PANELWIDTH-LABELNAMELOC)
			            	SCREENWIDTH = PANELWIDTH - LABELNAMELOC - LABELLOC;
			}
			else {
						//recover
						SCREENWIDTH = PANELWIDTH - STRFONTSIZE*maxGeneName - LABELLOC;
						//-------
			}

			if (treeScreenWidth > SCREENWIDTH)
			     	factor = 1.0/((double)treeScreenWidth/SCREENWIDTH);
			else
			      	factor = (double)SCREENWIDTH/(double)treeScreenWidth;

			dendro.setScaleFactor(factor);

			dendro.setCoords(this, side);

			adjustWidth = this.getWidth() - PANELWIDTH;

			if (side == RIGHTSIDE)
				adjustWidth = 5;

            drawTree(g, dendro.getRoot(), adjustWidth);

            //draw the background for the ribotypes
            //g.setColor(Color.gray);
            //g.fillRect(ribotypeX, 0, labelBorder[0] - ribotypeX, this.getHeight());

            //draw the ribotypes
            //drawRibotype(g, dendro.getRoot());

            //draw the background for the labels
            //g.setColor(Color.white);
            //g.fillRect(labelX,0, this.getWidth() - labelX, this.getHeight());

            //draw the label headers
            g.setColor(Color.black);

            //need recover
            g.setFont(new Font("Headers", Font.PLAIN, 10));
            //-----------

			String s = dendro.getLabelHeader(0);

            if (side == LEFTSIDE){
       			//recover
            	g.drawString(dendro.getLabelHeader(0), this.getWidth() - s.length()*STRFONTSIZE - LABELLOC, 23);
            	//-------
            }
            else if (side == RIGHTSIDE)
            		g.drawString(dendro.getLabelHeader(0), LABELLOC, 23);


            /*
            //draw the borders
            int tempPanelHeight = dendro.getPanelHeight();
            g.setColor(Color.blue);
            g.drawLine(ribotypeX - 1, 0, ribotypeX - 1, tempPanelHeight);
            g.drawLine(ribotypeX, 0, ribotypeX, tempPanelHeight);
            g.drawLine(ribotypeX + 1, 0, ribotypeX + 1, tempPanelHeight);
            */

            /*
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
            */

        }//if
    }//paintComponent

	public Tree getTree()
	{
		return dendro;
	}
}//class MastTreePanel