package treecmp.treeanalyzer;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

//This class handles the mouse events from the treepanel and adjust the borders accordingly
public class TreePanelMouseMotionListener implements MouseMotionListener{

    private TreePanel treePanel;
    private Tree dendro;
    
    //default constructor
    public TreePanelMouseMotionListener(){
        dendro = null;
        treePanel = null;
    }//TreePanelMouseMotionListener()

    //assign the panel this associates with
    public void setTreePanel(TreePanel t){
        treePanel = t;
    }//setTreePanel(t)

    //assign the tree value
    public void setTree(Tree t){
        dendro = t;
    }//setTree(t)

	public void mouseMoved(MouseEvent e){
        if (dendro != null){
            //test to see if the mouse is over one of the borders
            int x = e.getX();

            int ribotypeX = treePanel.getRibotypeX();
            //int labelX = treePanel.getLabelX();
            int tempX;
            Cursor mouseIcon = null;

            //test out the label borders first
            for (int i=0; i < dendro.getRoot().getNumLabels(); i++){
                tempX = treePanel.getLabelBorderValue(i);
                if (x >= (tempX - 1) && x <= (tempX + 1)){
                    //mouse is over one of the label borders
                    mouseIcon = new Cursor(Cursor.E_RESIZE_CURSOR);
                    treePanel.setCursor(mouseIcon);
                }//if
            }//for

            if(x >= (ribotypeX - 1) && x <= (ribotypeX + 1)){
                //mouse is over border between tree and ribotype
                mouseIcon = new Cursor(Cursor.E_RESIZE_CURSOR);
                treePanel.setCursor(mouseIcon);
            }//else if
            else if (mouseIcon == null){
                //mouse is not over any border
                mouseIcon = new Cursor(Cursor.DEFAULT_CURSOR);
                treePanel.setCursor(mouseIcon);
            }//else
        }//if
    }//mouseMoved

    public void mouseDragged(MouseEvent e){
        if (dendro != null){
            int x = e.getX();

            if (treePanel.getMoveBorder1()){
                //border 1 is being adjusted
                treePanel.setRibotypeX(x);
                treePanel.repaint();
            }//if
            else{
                //test the other label borders
                for (int i=0; i < dendro.getRoot().getNumLabels(); i++){
                    if (treePanel.getMoveLabelBorder(i)){
                        treePanel.setLabelBorderValue(x, i);
                        treePanel.repaint();
                    }//if
                }//for
            }//else
        }//if
    }//mouseDragged
}//class TreePanelMouseMotionListener
