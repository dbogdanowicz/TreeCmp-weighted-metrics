package treecmp.treeanalyzer;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.print.*;

public class DisplayDataSet extends JPanel implements Printable, Serializable{

    private JPanel entireDisplay;
    private Tree dendro;
    private TreePanel treePanel;
    private int initPos=0;
    private String panelName;
    private String panelPath;
    public boolean saved=false;
    public boolean exported = false;

    public DisplayDataSet(){
        super(new BorderLayout());

        treePanel = new TreePanel();
        treePanel.setBackground(Color.white);
        treePanel.setPreferredSize(new Dimension(800, 550));
        treePanel.setDataSet(this);

        JScrollPane test1 = new JScrollPane(treePanel);

        this.add(test1, BorderLayout.CENTER);
        this.setSize(800,550);
        this.setVisible(true);
        panelName = new String();

    }//default constructor

    //This method creates the tree structure from the file
    public boolean createTree(File fileName){
        String fileLine;
        String treeStructure = "";
        String nodeLabels = "";
        BufferedReader br;
        boolean readLabels=false;
        boolean readStructure=false;
        Set labelSet=new HashSet();
        LabelName labelName = new LabelName();
        boolean oneTree = false;

        String picFilePath = fileName.getParent();


        try{
            br = new BufferedReader( new FileReader(fileName));
            fileLine = br.readLine();

            readLabels = false;
            readStructure = false;

            //Move along the file reading 1 line at time
            while (fileLine != null){
			  if(!fileLine.trim().equals("")){
                if (fileLine.trim().equalsIgnoreCase("TRANSLATE")){
                    readLabels = true;
                    fileLine = new String();
                    fileLine = br.readLine();
                    continue;
                }//if
                else if (fileLine.lastIndexOf("UTREE PAUP_") != -1){
                    readLabels=false;
                    readStructure = true;
                    fileLine = fileLine.trim();
                    if (fileLine.indexOf("(")!=-1)
                    	fileLine = fileLine.substring(fileLine.indexOf("("));
                    else {
                    		fileLine = br.readLine();
                    		continue;
                    }

                }//else if
                else if (fileLine.lastIndexOf("ENDBLOCK;") != -1)
                    fileLine = fileLine.trim().substring(9);

                if((fileLine.lastIndexOf(");")!=-1)&&(!oneTree))
                	oneTree = true;
                else if ((fileLine.lastIndexOf(";")!=-1)&&(oneTree)){
						JOptionPane.showMessageDialog( this, "DataSet is not allowed to contain more than one tree topology!", "", JOptionPane.ERROR_MESSAGE);
                		br.close();
                		return false;
                }

                if (readStructure){
                	//remove white spaces in file line
                	int i=0;
                	int newLength = 0;
                	int j=0;

                	fileLine = fileLine.trim();
                	while(i<fileLine.length()){
                		if (fileLine.charAt(i) != ' ')
                			newLength++;
                		i++;
                	}

                	if (newLength==fileLine.length())
                    	treeStructure += fileLine.trim();
                    else {
                    		char []fileChar = new char[newLength];
                    		i=0;
                    		j=0;
                    		while(i<fileLine.length()){
                    			if(fileLine.charAt(i)!=' '){
                    				fileChar[j]=fileLine.charAt(i);
                    				j++;
                    			}
                    			i++;
                    		}

                    		fileLine = new String(fileChar);
                    		treeStructure += fileLine.trim();
                    }
                }
                else if ((readLabels)&&(fileLine.length()!=0)){
                //----------new ------------------------
                		if (fileLine.trim().equals(";")){}
                		else {
                			labelName=new LabelName();
                    		StringTokenizer st = new StringTokenizer(fileLine);

                    		labelName.num=st.nextToken();
                    		String temp=new String(st.nextToken());
                    		if (temp.endsWith(",")||temp.endsWith(";"))
                    			temp=temp.substring(0, temp.length()-1);
                    		labelName.name=temp;

            				Iterator it = labelSet.iterator();
            				while(it.hasNext()){
								LabelName memLabel = new LabelName();
								memLabel = (LabelName)it.next();
								if (memLabel.name.equals(temp)){
									String name = new String();
									name = fileName.getName();
							    	JOptionPane.showMessageDialog( this, "Leaves Are Not Uniquely Labelled in "+name, "", JOptionPane.ERROR_MESSAGE);
							       	return false;
								}
							}

                    		labelSet.add(labelName);

                    		//nodeLabels += fileLine.trim();
						}
                     }
		  	  }
              //--------------------------------------------
              fileLine = br.readLine();

            }//while

            br.close();
            //dendro = new Tree(treeStructure, nodeLabels, picFilePath);

			//decide whether the tree is binary or not
			String trees = new String(treeStructure);
			int i=0;
			int leftPCount=0;
			int commaCount=0;

			while(i<trees.length()){
				if (trees.charAt(i) == '(')
					leftPCount++;
				if (trees.charAt(i) == ',')
					commaCount++;
				i++;
			}

			if ((leftPCount != commaCount)&&(leftPCount != (commaCount-1))){
				JOptionPane.showMessageDialog( this, "Not a binary tree!", "", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			else {
            	dendro = new Tree(treeStructure, labelSet, picFilePath);
            	dendro.setDepth(dendro.getRoot(), 0);
			}

			//for debug
            //dendro.displayTree();
            //-------------

            return true;
        }//try
        catch(Exception ioe){
            JOptionPane.showMessageDialog( this, "Error Opening File", "", JOptionPane.ERROR_MESSAGE);
            return false;
        }//catch
    }//createTree

    // This method sets the coordinates for the nodes and then calls repaint
    public void drawTree(){

        //figure out if the panel height needs to be adjusted
        TreeNode temp = dendro.getRandomChild();
        ImageIcon tempImage = new ImageIcon("" + temp.getLabel(0) + ".jpg");
        int imageHeight = tempImage.getImage().getHeight(null) + 1;
		//see if the image height is less than the default of 20
        if (imageHeight < 20)
            imageHeight = 20;
        int numChildren = dendro.getNumberOfChildren(dendro.getRoot(), 0);
        int panelHeight = imageHeight * (numChildren + 5);

        //see if the panelHeight is more than the default of 550
        if (panelHeight > 550){
            treePanel.setPreferredSize(new Dimension(800, panelHeight));
            dendro.setPanelHeight(panelHeight);
        }//if
        else{
            treePanel.setPreferredSize(new Dimension(800, 550));
            dendro.setPanelHeight(550);
        }//else
        //figure out the scale factor for the tree and assign it
        double treeScreenWidth = dendro.getMaxNodeLength(dendro.getRoot(), 0);
        double factor = 1;
        if (treeScreenWidth > 235)
            factor = 1.00 / ((double) treeScreenWidth / 235.00);
        else
            factor = 235.00 / (double) treeScreenWidth;

        dendro.setScaleFactor(factor);

        //call the routines to actually draw the structure
        dendro.setCoords(treePanel);

        treePanel.setTree(dendro);
        treePanel.repaint();
    }//drawTree()

    // This method assigns a reference to the rotate button
    public void setRotateButton(JButton r){
        treePanel.setRotateButton(r);
        saved = false;
    }//setRotateButton

    // This method assigns a reference to the font button
    public void setFontButton(JButton f){
        treePanel.setFontButton(f);
        saved = false;
    }//setFontButton

    // This method assigns a reference to the label header button
    public void setLabelHeaderButton(JButton h){
        treePanel.setLabelHeaderButton(h);
        saved = false;
    }//setLabelHeader

    // This method returns the tree associated with this DataSet
    public Tree getTree(){
        Tree temp = new Tree();
        temp = copy_tree(temp, dendro);

        return temp;

        //return dendro;
    }//getTree

    public Tree getExactTree(){
    	return dendro;
    }

    // This method assigns a tree to this DataSet
    public void setTree(Tree t){
    	dendro = new Tree();
    	dendro = copy_tree(dendro, t);
        //dendro = t;
    }//setTree(t)

    //---------------------new -------------------
    private Tree copy_tree(Tree freshTree, Tree oldTree)
    {
        freshTree = new Tree();
        TreeNode freshRoot = new TreeNode();
        TreeNode oldRoot = new TreeNode();
        oldRoot = oldTree.getRoot();
        initPos=0;
        freshRoot = setPosTree(oldRoot, freshRoot);

        freshRoot.setNumLabels(oldRoot.getNumLabels());
        freshTree.setRoot(freshRoot);
        freshTree.setLabelHeader(oldTree.getLabelHeaders());
        freshTree.setNumHeaders(oldTree.getNumHeaders());
        freshTree.setPicFilePath(oldTree.getPicFilePath());
        freshTree.setTreeOrder(oldTree.getOrder());
        freshTree.setLeafSetSize();
        freshTree.setScaleFactor(oldTree.getScaleFactor());
        freshTree.setPanelHeight(oldTree.getPanelHeight());
        freshTree.setPanelWidth(oldTree.getPanelWidth());

        return freshTree;
    }

    private TreeNode setPosTree(TreeNode iter, TreeNode node)

     {
	//TreeNode node=new TreeNode();
	TreeNode leftChild=new TreeNode();
	TreeNode rightChild=new TreeNode();

	if (iter!=null){

	    if (!iter.isLeaf()){
	        leftChild.setParent(node);
	        rightChild.setParent(node);
	        node.setGeneLabel(iter.getLeftNode().getGeneLabel()+iter.getRightNode().getGeneLabel());
		node.addLeafSet(iter.getLeftNode().getLeafSet());
		node.addLeafSet(iter.getRightNode().getLeafSet());
		node.setMyLeftNode(leftChild);
		node.setMyRightNode(rightChild);
		node.setLabelsEx(iter.getLabels());
		node.setImage(iter.getImage());
		node.setDepth(iter.getDepth());
		node.setStemLength(iter.getStemLength());
		node.setX(iter.getX());
		node.setY(iter.getY());
		node.setLabelFont(iter.getLabelFont());
		node.setFontSize(iter.getFontSize());
	        setPosTree(iter.getLeftNode(), leftChild);

	        setPosTree(iter.getRightNode(), rightChild);
	        node.setTotSize(leftChild.getTotSize()+rightChild.getTotSize()+1);
	   }

	   else {
			  //modified label -> name
	          //node.setLeaf(new String(iter.getGeneLabel()));
	          node.setLeafLabel(new String(iter.getGeneLabel()));
	          node.setLeafName(new String(iter.getGeneName()));

	          node.setTotSize(1);
	          node.setLabelsEx(iter.getLabels());
		  node.setImage(iter.getImage());
		  node.setDepth(iter.getDepth());
		  node.setStemLength(iter.getStemLength());
		  node.setX(iter.getX());
		  node.setY(iter.getY());
		  node.setLabelFont(iter.getLabelFont());
		  node.setFontSize(iter.getFontSize());
	          //node.setPos(initPos);

		}

	  node.setPos(initPos);
	  initPos++;

	}
	return node;

     }

     public void printPanel()
     {
         PrinterJob pj=PrinterJob.getPrinterJob();
		 pj.setPrintable(DisplayDataSet.this);
		 pj.printDialog();
		 try{
		 		pj.print();
		 }catch (Exception PrintException) {
		 		JOptionPane.showMessageDialog(null, "Error Printing File");
		}
     }

     public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException
     {
		 Graphics2D g2=(Graphics2D)g;
		 int fontHeight=g2.getFontMetrics().getHeight();
		 int fontDesent=g2.getFontMetrics().getDescent();
		 int PH=600;

		 //leave room for result info, blank line, tree info, blank line, blank line, page number
		 double pageHeight = pageFormat.getImageableHeight();
		 double pageWidth = pageFormat.getImageableWidth();
		 double panelWidth = (double)treePanel.getWidth();
		 double panelHeight = (double)treePanel.getHeight();
		 double scale = 1;
		 if (panelWidth>=pageWidth){
		 		scale = pageWidth/panelWidth;
		 }


		 double panelWidthOnPage=panelWidth*scale;

		 //double headerHeightOnPage = fontHeight*scale;
		 double headerHeightOnPage = fontHeight;


		 int totalNumPages = (int)Math.ceil((double)treePanel.getHeight()*scale/(double)pageHeight);

		 if (pageIndex>=totalNumPages){
		 		return NO_SUCH_PAGE;
		 }

		 g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
		 //g2.translate(0f, headerHeightOnPage);
		 //g2.translate(0f, -pageIndex*PH);

		 //If this piece of the table is smaller than the size available,
		 //clip to the appropriate bounds.

		 if (pageIndex + 1 != totalNumPages) {
		 	//g2.setClip(0, (int)(PH*pageIndex),
		 	//(int) Math.ceil(panelWidthOnPage),
		 	//(int) Math.ceil(PH));

		 	g2.setClip(0, (int)(pageHeight*pageIndex),
		 		(int) Math.ceil(panelWidthOnPage),
		 		(int) Math.ceil(pageHeight));
		 }

		 g2.scale(scale, scale);

		 //g2.scale(scale, 0.6);
		 disableDoubleBuffering(this);
		 treePanel.paint(g2);

		 enableDoubleBuffering(this);
		 g2.scale(1/scale, 1/scale);
		 //g2.scale(1/scale, 1.67);
		 //g2.translate(0f, pageIndex*PH);
		 //g2.translate(0f, -headerHeightOnPage);

		return Printable.PAGE_EXISTS;
     }

	/** The speed and quality of printing suffers dramatically if
		*  any of the containers have double buffering turned on.
		*  So this turns if off globally.
		*  @see enableDoubleBuffering
		*/
	public static void disableDoubleBuffering(Component c) {
		    RepaintManager currentManager = RepaintManager.currentManager(c);
		    currentManager.setDoubleBufferingEnabled(false);
	}

	/** Re-enables double buffering globally. */

	public static void enableDoubleBuffering(Component c) {
			RepaintManager currentManager = RepaintManager.currentManager(c);
		    currentManager.setDoubleBufferingEnabled(true);
    }

     public void setPanelName(String inName)
     {
     	panelName = inName;
     }

     public String getPanelName()
     {
     	return panelName;
     }

     public void setPanelPath(String inPath)
     {
     	panelPath = inPath;
     }

     public String getPanelPath()
     {
     	return panelPath;
     }

    //----------------------------------------------


    public TreePanel getTreePanel(){
        return treePanel;
    }//getTreePanel

}//class DisplayDataSet