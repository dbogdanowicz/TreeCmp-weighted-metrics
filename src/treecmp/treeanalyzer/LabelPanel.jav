import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class LabelPanel extends JPanel{
	private Tree dendro;
	private int labelPanelUnit;
	private int labelsDone;
	
	public LabelPanel(){
		super();
		dendro = null;
		labelsDone = 0;
	}//default constructor
	
	public void setTree(Tree t){
		dendro = t;
	}//setTree(t)

	public void setPanelUnit(int unit){
		labelPanelUnit = unit;
	}//setPanelUnit(unit)
	
	public void drawLabelHelper(Graphics g, TreeNode node){
		if (node.isLeaf()){
			//it is a leaf so show the label
			g.setColor(node.getLabelColor());
			if (node.getY() <= 20)
				g.drawString(node.getGeneLabel() + "\t" + node.getNodeLabel() + "\t" + node.getCommentLabel(), 5, (10 + node.getY()));
			else{
				//g.drawString(node.getGeneLabel() + "\t" + node.getNodeLabel() + "\t" + node.getCommentLabel(), 5, (node.getY() + (node.getImage().getHeight(null) / 4)));
				ImageIcon tempImage = new ImageIcon(node.getImage());
				g.drawString(node.getGeneLabel() + "\t" + node.getNodeLabel() + "\t" + node.getCommentLabel(), 5, (node.getY() + (tempImage.getImage().getHeight(null) / 4)));
			}//else
			labelsDone++;
		}//if
		else{
			//this is an inner node, move on to children
			drawLabelHelper(g, node.getRightNode());
			drawLabelHelper(g, node.getLeftNode());
		}//else
	}//drawLabelHelper(g, node)
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if (dendro != null){
			labelsDone = 0;
			drawLabelHelper(g, dendro.getRoot());
		}//if	
	}//paintComponent(g)
	
}//class LabelPanel