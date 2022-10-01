package treecmp.treeanalyzer;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class MastLeafLoc
{
    public String name;
    public int yLoc;
    
    public MastLeafLoc()
    {
        name = new String();
    }
    
    public MastLeafLoc(String inName, int inLoc)
    {
        name = new String();
        name = inName;
        yLoc = inLoc;
    }
    
}