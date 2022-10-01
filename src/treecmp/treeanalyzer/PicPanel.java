package treecmp.treeanalyzer;

import java.io.*;
import java.awt.*;
import javax.swing.*;

public class PicPanel extends JPanel{
	
	private Tree dendro;
	private int picPanelUnit;
	private int picsDone;
	
	public PicPanel(){
		super();
		dendro = null;
		picPanelUnit = 0;
		picsDone = 0;
	}//PicPanel constructor
	
	public void setTree(Tree t){
		dendro = t;
	}//setTree(t)
	
	public void setPanelUnit(int unit){
		picPanelUnit = unit;
	}//setPicPanelUnit(unit)

	public void drawPicsHelper(Graphics g, TreeNode node){
		if (node.isLeaf()){
			//it is a leaf so draw the ribotype
			ImageIcon tempImage = new ImageIcon(node.getImage());
			if (node.getY() <= 20){
				//g.drawImage(node.getImage(), 2, node.getY(), null);
				g.drawImage(tempImage.getImage(), 2, node.getY(), null);
			}//if
			else{
				//g.drawImage(node.getImage(), 2, node.getY() - (node.getImage().getHeight(null) / 2), null);
				g.drawImage(tempImage.getImage(), 2, node.getY() - (tempImage.getImage().getHeight(null)/2), null);
			}//else
			picsDone++;
		}//if
		else{
			//this is an inner node, move on to children
			drawPicsHelper(g, node.getRightNode());
			drawPicsHelper(g, node.getLeftNode());
		}//else
	}//drawPicsHelper(g, node)

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if (dendro != null){
			picsDone = 0;
			drawPicsHelper(g, dendro.getRoot());
		}//if
	}//paintComponent
	
}//PicPanel