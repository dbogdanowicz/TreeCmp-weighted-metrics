package treecmp.treeanalyzer;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LabelHeaders extends JFrame{

    private Tree dendro;
    private TreePanel panel;
    private String [] labelHeaders;
    private JTextField [] labelFields;
    
    public LabelHeaders(Tree t, TreePanel p){
        super("Label Headers");
        dendro = t;
        panel = p;
        labelHeaders = new String[10];
        labelFields = new JTextField[10];
        
        //default all the information to empty string
        for (int i=0; i < 10; i++)
            labelHeaders[i] = "";
        
        //load the current label header information from the tree
        for (int i=0; i < dendro.getRoot().getNumLabels(); i++){
            labelHeaders[i] = dendro.getLabelHeader(i);
        }//for
        
        Container c = getContentPane();
        c.setLayout(new GridLayout(12, 2));
        
        JLabel tempLabel;
        JTextField tempField;
        JPanel temp;
        
        tempLabel = new JLabel("Set Label Headers. Leave blank to exclude.");
        temp = new JPanel(new FlowLayout());
        temp.add(tempLabel);
        c.add(temp);
        
        for (int i=0; i < 10; i++){
            //set up the panel for the Label Headers
            tempLabel = new JLabel("Label Header " + (i + 1));
            tempField = new JTextField(labelHeaders[i], 25);
            temp = new JPanel(new FlowLayout());
            temp.add(tempLabel);
            temp.add(tempField);
            c.add(temp);
            labelFields[i] = tempField;
        }//for
        
        //create the OK button for the form and assign the listener
        JButton ok = new JButton("OK");
        ok.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e){
                    UpdateLabelHeaders();
                    dispose();
                }//actionPerformed
            }//ActionListener
        );
        
        //create the Cancel button for the form and assign the listener
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e){
                    dispose();
                }//actionPerformed
            }//ActionListener
        );
        
        //add the ok and cancel buttons to the frame
        temp = new JPanel(new FlowLayout());
        temp.add(ok);
        temp.add(cancel);
        c.add(temp);
        
        setResizable(false);
        setSize(560, 500);
        show();
        
    }//default constructor
    
    //This method will actually update the label headers for the tree
    public void UpdateLabelHeaders(){
        String value;
        String [] newLabelHeaders = new String [10];
        int newNumLabels = 10;
        
        //find out how many fields were populated
        for (int i=1; i < 10; i++){
            value = labelFields[i].getText();
            if (value.equalsIgnoreCase("")){
                newNumLabels = i;
                break;
            }//if
        }//for
                
        //assignt the new label header strings
        for (int i=0; i < 10; i++){
            newLabelHeaders[i] = labelFields[i].getText();
        }//for
        
        //assign the new number of label headers
        dendro.setNumHeaders(newNumLabels);
        dendro.setLabelHeader(newLabelHeaders);
        
        //repaint the screen to show the changes made
        panel.repaint();
        
    }//UpdateLabelHeaders

}//LabelHeaders class
