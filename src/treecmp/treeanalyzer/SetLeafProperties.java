package treecmp.treeanalyzer;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/*
 * This class creates a window for the user to assign properties for a leaf node on the tree
 */
public class SetLeafProperties extends JFrame{

    private TreeNode node;
    private Tree dendro;
    private TreePanel panel;
    private MastTreePanel mPanel;
    private String [] nodeLabels;
    private Color fontColor;
    private String [] labelHeaders;
    private JTextField [] newLabels;
    private JComboBox colorCombo;
    private JComboBox fontCombo;
    private JComboBox fontSizeCombo;
    private String [] allFonts;
    private String [] fontSizes;

    //Default constructor, initialize everything needed and show to the user
    public SetLeafProperties(TreeNode n, Tree t, TreePanel p, MastTreePanel m){
        super("Leaf Properties");
        node = n;
        dendro = t;
        mPanel = m;
        panel = p;
        nodeLabels = new String[node.getNumLabels()];				//array to hold the current label values
        labelHeaders = new String[node.getNumLabels()];				//array to hold the label headers
        newLabels = new JTextField[node.getNumLabels()];			//array to hold the new label values

        //Load current information into arrays
        for (int i=0; i < node.getNumLabels(); i++){
            nodeLabels[i] = node.getLabel(i);
            labelHeaders[i] = dendro.getLabelHeader(i);
        }//for

		//modified using leaf name instead of leaf number
		nodeLabels[0] = node.getGeneName();

        Container c = getContentPane();
        c.setLayout(new GridLayout(node.getNumLabels() + 4, 2));

        //setup the labels for the form
        JLabel colorLbl = new JLabel("Color");
        JLabel fontLbl = new JLabel("Font");
        JLabel fontSizeLbl = new JLabel("Font Size");
        JLabel styleLbl = new JLabel("Style");

        //setup the color array and create the combo box
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
        colorCombo = new JComboBox(allColors);

        //get the node's current color and make that the selected value in the combo box
        fontColor = node.getLabelColor();
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

        //grab all the possible fonts from the user's machine and put in an array and then create the combo box
        allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fontCombo = new JComboBox(allFonts);

        //grab the node's current font and make that the selected value in the combo box
        int fontIndex = 0;
        for (int i=0; i < allFonts.length; i++){
            if (node.getFont().equals(Font.decode((String) fontCombo.getItemAt(i)))){
                fontIndex = i;
                break;
            }//if
        }//for

        fontCombo.setSelectedIndex(fontIndex);

        //Populate the font size combo box
        String [] fontSizes = new String [9];
        for (int i=0; i < 9; i++)
            fontSizes[i] = "" + (i + 8);

        fontSizeCombo = new JComboBox(fontSizes);

        //Figure out what index the correct font is
        fontSizeCombo.setSelectedIndex(node.getFontSize() - 8);

        //create the OK button for the form and assign a listener to handle the user clicks
        JButton ok = new JButton("OK");
        ok.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e){
                    UpdateNode();					//assign new values to node
                    dispose();						//get rid of this form
                }//actionPerformed
            }//ActionListener
        );

        //create the Cancel button for the form and assign a listener to handle the user clicks
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e){
                    dispose();						//get rid of this form
                }//actionPerformed
            }//ActionListener
        );

        /*
         * The following lines create temporary panel to hold items so they do not grow to fill the area and then
         * add them to the container c
         */

        JLabel tempLabel;
        JTextField tempField;
        JPanel temp;
        for (int i=0; i < node.getNumLabels(); i++){
            tempLabel = new JLabel(labelHeaders[i]);
            tempField = new JTextField(nodeLabels[i], 20);
            temp = new JPanel(new FlowLayout());
            temp.add(tempLabel);
            c.add(temp);
            temp = new JPanel(new FlowLayout());
            temp.add(tempField);
            c.add(temp);
            newLabels[i] = tempField;
        }//for

        temp = new JPanel(new FlowLayout());
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
        setSize(560,320 + (node.getNumLabels() * 20));
        show();
    }//SetLeafProperties constructor


    //This method assigns the new properties to the desired node
    public void UpdateNode(){

        //update the new labels
        for (int i=1; i < node.getNumLabels(); i++){
            node.setLabel(newLabels[i].getText(), i);
        }//for

		node.setGeneName(newLabels[0].getText());

        //figure out what color is and assign it to the node
        int colorIndex = colorCombo.getSelectedIndex();
        if (colorIndex == 0)
            node.setLabelColor(Color.black);
        else if (colorIndex == 1)
            node.setLabelColor(Color.blue);
        else if (colorIndex == 2)
            node.setLabelColor(Color.cyan);
        else if (colorIndex == 3)
            node.setLabelColor(Color.darkGray);
        else if(colorIndex == 4)
            node.setLabelColor(Color.gray);
        else if (colorIndex == 5)
            node.setLabelColor(Color.green);
        else if (colorIndex == 6)
            node.setLabelColor(Color.lightGray);
        else if (colorIndex == 7)
            node.setLabelColor(Color.magenta);
        else if (colorIndex == 8)
            node.setLabelColor(Color.orange);
        else if (colorIndex == 9)
            node.setLabelColor(Color.pink);
        else if (colorIndex == 10)
            node.setLabelColor(Color.red);
        else if (colorIndex == 11)
            node.setLabelColor(Color.yellow);

        //assign the new font
        node.setFont(Font.decode((String) fontCombo.getSelectedItem()));

        //assign the new font size
        node.setFontSize(fontSizeCombo.getSelectedIndex() + 8);

        //refresh the screen to show the changes
        if (panel!=null)
        	panel.repaint();
        else if (mPanel!=null){
					mPanel.start = true;
        			mPanel.repaint();
				}

    }//UpdateNode

}//SetLeafProperties