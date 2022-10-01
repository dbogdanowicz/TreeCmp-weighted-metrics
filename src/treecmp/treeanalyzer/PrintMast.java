package treecmp.treeanalyzer;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

public class PrintMast extends MastPanel
{
    public PrintMast()
    {
        super();
    }

    public PrintMast(MastPanel mastPanel)
    {
        super();

        Tree []myTreeGroup=mastPanel.getTreeGroup();
        Tree []myMastGroup = mastPanel.getMastGroup();
        String []myTitle = mastPanel.getTitleGroup();
        boolean myNum=mastPanel.getNumOrName();
        double distance=mastPanel.getDistance();
        String distanceText = mastPanel.getDistanceText();
        String simIndexText = mastPanel.getSimIndexText();

        setTreeGroup(myTreeGroup, myMastGroup, myTitle, myNum, distance, distanceText, simIndexText);
        setSize(1500, 550);
        setVisible(true);
        repaint();
    }
}