package treecmp.treeanalyzer;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SetGroupProperties extends JFrame{

    private TreeNode node;
    private TreePanel panel;
    private MastTreePanel mPanel;
    private Color fontColor;
    private JComboBox colorCombo;
    private JComboBox fontCombo;
    private JComboBox fontSizeCombo;
    private String [] allFonts;

    //constructor
    public SetGroupProperties(TreeNode n, TreePanel p, MastTreePanel m){
        super("Group Properties");
        node = n;
        panel = p;
        mPanel = m;

        Container c = getContentPane();
        c.setLayout(new GridLayout(4,2));

        //Initialize the labels
        JLabel colorLbl = new JLabel("Color");
        JLabel fontLbl = new JLabel("Font");
        JLabel fontSizeLbl = new JLabel("Font Size");

        //Initialize the color array
        String [] allColors = new String [12];
        allColors[0] = "black";
        allColors[1] = "blue";
        allColors[2] = "cyan";
        allColors[3] = "darkGray";
        allColors[4] = "gray";
        allColors[5] = "green";
        allColors[6] = "lightGray";
        allColors[7] = "magenta";
        allColors[8] = "orange";
        allColors[9] = "pink";
        allColors[10] = "red";
        allColors[11] = "yellow";

        //Populate the Color Combo Box
        colorCombo = new JComboBox(allColors);

        //set up the combo box with the appropriate color highlighted
        if (fontColor == Color.black)
            colorCombo.setSelectedIndex(0);
        else if (fontColor == Color.blue)
            colorCombo.setSelectedIndex(1);
        else if (fontColor == Color.cyan)
            colorCombo.setSelectedIndex(2);
        else if (fontColor == Color.darkGray)
            colorCombo.setSelectedIndex(3);
        else if (fontColor == Color.gray)
            colorCombo.setSelectedIndex(4);
        else if (fontColor == Color.green)
            colorCombo.setSelectedIndex(5);
        else if (fontColor == Color.lightGray)
            colorCombo.setSelectedIndex(6);
        else if (fontColor == Color.magenta)
            colorCombo.setSelectedIndex(7);
        else if (fontColor == Color.orange)
            colorCombo.setSelectedIndex(8);
        else if (fontColor == Color.pink)
            colorCombo.setSelectedIndex(9);
        else if (fontColor == Color.red)
            colorCombo.setSelectedIndex(10);
        else if (fontColor == Color.yellow)
            colorCombo.setSelectedIndex(11);

        //Get all available fonts and populate the combo box
        allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fontCombo = new JComboBox(allFonts);

        //Figure out what index the correct font is
        int fontIndex = 0;
        for (int i=0; i < allFonts.length; i++){
            if (node.getFont().equals(Font.decode((String) fontCombo.getItemAt(i)))){
                fontIndex = i;
                break;
            }//if
        }//for

        //set up the combo box with the correct font highlighted
        fontCombo.setSelectedIndex(fontIndex);

        //Populate the font size combo box
        String [] fontSizes = new String [9];
        for (int i=0; i < 9; i++)
            fontSizes[i] = "" + (i + 8);

        fontSizeCombo = new JComboBox(fontSizes);

        //Figure out what index the correct font is
        fontSizeCombo.setSelectedIndex(node.getFontSize() - 8);

        //Create the OK button with listener
        JButton ok = new JButton("OK");
        ok.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e){
                    UpdateNodes(node);
                    dispose();
                }//actionPerformed
            }//ActionListener
        );

        //Create Cancel button with listener
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e){
                    dispose();
                }//actionPerformed
            }//ActionListener
        );

        //Now begin adding the different components to the screen
        JPanel temp = new JPanel(new FlowLayout());
        temp.add(colorLbl);
        c.add(temp);

        temp = new JPanel(new FlowLayout());
        temp.add(colorCombo);
        c.add(temp);

        temp = new JPanel(new FlowLayout());
        temp.add(fontLbl);
        c.add(temp);

        temp = new JPanel(new FlowLayout());
        temp.add(fontCombo);
        c.add(temp);

        temp = new JPanel(new FlowLayout());
        temp.add(fontSizeLbl);
        c.add(temp);

        temp = new JPanel(new FlowLayout());
        temp.add(fontSizeCombo);
        c.add(temp);

        temp = new JPanel(new FlowLayout());
        temp.add(ok);
        c.add(temp);

        temp = new JPanel(new FlowLayout());
        temp.add(cancel);
        c.add(temp);

        setResizable(false);
        setSize(560,200);
        show();
    }//SetGroupProperties constructor

    //This recursive method assigns the new values to all the affected nodes
    public void UpdateNodes(TreeNode n){
        if (n.isLeaf()){
            //this is a leaf so just assign the properties
            int colorIndex = colorCombo.getSelectedIndex();
            if (colorIndex == 0)
                n.setLabelColor(Color.black);
            else if (colorIndex == 1)
                n.setLabelColor(Color.blue);
            else if (colorIndex == 2)
                n.setLabelColor(Color.cyan);
            else if (colorIndex == 3)
                n.setLabelColor(Color.darkGray);
            else if(colorIndex == 4)
                n.setLabelColor(Color.gray);
            else if (colorIndex == 5)
                n.setLabelColor(Color.green);
            else if (colorIndex == 6)
                n.setLabelColor(Color.lightGray);
            else if (colorIndex == 7)
                n.setLabelColor(Color.magenta);
            else if (colorIndex == 8)
                n.setLabelColor(Color.orange);
            else if (colorIndex == 9)
                n.setLabelColor(Color.pink);
            else if (colorIndex == 10)
                n.setLabelColor(Color.red);
            else if (colorIndex == 11)
                n.setLabelColor(Color.yellow);

			//System.out.println("Colore selected: "+n.getLabelColor());

            n.setFont(Font.decode((String) fontCombo.getSelectedItem()));

            n.setFontSize(fontSizeCombo.getSelectedIndex() + 8);
        }//if
        else{
            //this is an inner node, call on children
            UpdateNodes(n.getRightNode());
            UpdateNodes(n.getLeftNode());
        }//else

        //Refresh the screen to show the changes
        if (panel != null)
       	 	panel.repaint();
       	else if (mPanel != null){
					mPanel.start = true;
       				mPanel.repaint();
			}

    }//UpdateNodes()

}//SetGroupProperties