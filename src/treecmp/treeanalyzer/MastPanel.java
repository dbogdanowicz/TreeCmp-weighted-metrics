package treecmp.treeanalyzer;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import java.awt.print.*;
import java.text.NumberFormat;
import java.text.DecimalFormat;

public class MastPanel extends JPanel implements Printable,Serializable
{
    private MastTreePanel leftTreePanel;
    private MastTreePanel rightTreePanel;
    private ConnectPanel connectPanel;
    private JPanel resultPanel;
    private JPanel centerPanel;
    private JPanel selectPanel;
    private Tree leftTree;
    private Tree rightTree;
    private Tree mastTree;
	private Set mastTreeLeaf;
    private Tree []treeGroup;
    private Tree []mastGroup;
	private Set []mastSetGroup;
    private JComboBox mastCombo;
    private JComboBox leftTreeCombo;
    private JComboBox rightTreeCombo;
    private boolean leftChanged = false;
	private boolean leftChanged2=false;
    private boolean rightChanged = false;
	private boolean rightChanged2=false;
    private boolean mastChanged = false;
	private boolean mastChanged2=false;
    private final int BOTH = 0;
    private final int ONLYTREE = 1;
    private final int ONLYMAST = 2;
    private JTextField distanceValue;
    private final int LEFTSIDE = 0;
    private final int RIGHTSIDE = 1;
    private String []titleGroup=null;
    private boolean labelNum=true;
    private double treeDistance=0;
	private boolean initial = true;
	private String panelName;
	private String panelPath;
	public boolean saved = false;
	public boolean exported = false;
	private TreeNode realNode = new TreeNode();
	private TreePanelListener treeListener;
	public final static int PANELH = 900;
	private String treeDistanceText = new String();
	private JTextField simIndex;
	private String treeSimIndexText = new String();

    public MastPanel()
    {
        super(new BorderLayout());
        leftTreePanel = new MastTreePanel(LEFTSIDE);
        //leftTreePanel.setPreferredSize(new Dimension(325, PANELH));
        leftTreePanel.setBackground(Color.white);
        rightTreePanel = new MastTreePanel(RIGHTSIDE);
        //rightTreePanel.setPreferredSize(new Dimension(325, PANELH));
        rightTreePanel.setBackground(Color.white);
	connectPanel = new ConnectPanel();
	//connectPanel.setPreferredSize(new Dimension(100, PANELH));

	centerPanel = new JPanel();
	//centerPanel.setSize(800, PANELH);
	centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
	centerPanel.add(leftTreePanel);
	centerPanel.add(connectPanel);
	centerPanel.add(rightTreePanel);
	JScrollPane scrollBar = new JScrollPane(centerPanel);

	JLabel mastName = new JLabel("MAST  ");
	mastCombo = new JComboBox();

	JPanel upPanel = new JPanel();
	upPanel.setLayout(new BoxLayout(upPanel, BoxLayout.X_AXIS));
	upPanel.add(mastName);
	upPanel.add(mastCombo);
	upPanel.add(Box.createRigidArea(new Dimension(50,0)));

	JLabel distanceName = new JLabel("Distance  ");
	distanceValue = new JTextField(5);

	JLabel simIndexName = new JLabel("  Similarity / Dissimilarity  ");
	simIndex = new JTextField(10);
	
	JPanel downPanel = new JPanel();
	downPanel.setLayout(new BoxLayout(downPanel, BoxLayout.X_AXIS));
	downPanel.add(Box.createRigidArea(new Dimension(100,0)));
	downPanel.add(distanceName);
	downPanel.add(distanceValue);
	downPanel.add(Box.createRigidArea(new Dimension(20,0)));
	downPanel.add(simIndexName);
	downPanel.add(simIndex);
	downPanel.add(Box.createRigidArea(new Dimension(50,0)));
	
	JPanel resultPanel = new JPanel();
	resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.X_AXIS));
	resultPanel.add(downPanel);
	resultPanel.add(upPanel);
	EtchedBorder loweredetched;
	loweredetched = (EtchedBorder) BorderFactory.createEtchedBorder();
	//loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

	TitledBorder title;
	title = BorderFactory.createTitledBorder(loweredetched, "Result");
	title.setTitleJustification(TitledBorder.CENTER);
	resultPanel.setBorder(title);

	JLabel tree1Name = new JLabel("Tree1 ");
	JLabel vs = new JLabel("vs. ");
	JLabel tree2Name = new JLabel("Tree2 ");
	leftTreeCombo = new JComboBox();
	rightTreeCombo = new JComboBox();

	selectPanel = new JPanel();
	selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.X_AXIS));
	selectPanel.add(Box.createRigidArea(new Dimension(180, 0)));
	selectPanel.add(tree1Name);
	selectPanel.add(leftTreeCombo);
	selectPanel.add(Box.createRigidArea(new Dimension(70, 0)));
	selectPanel.add(vs);
	selectPanel.add(Box.createRigidArea(new Dimension(70, 0)));
	selectPanel.add(tree2Name);
	selectPanel.add(rightTreeCombo);
	selectPanel.add(Box.createRigidArea(new Dimension(180,0)));

	add(resultPanel, BorderLayout.NORTH);
	add(scrollBar, BorderLayout.CENTER);
	add(selectPanel, BorderLayout.SOUTH);

	setVisible(true);
	setSize(800, 550);

	panelName = new String();

	/*
	treeLisener = new TreePanelListener();
	treeLisener.setMastPanel(this);
	addMouseListener(treeListener);
	*/
	leftTreePanel.treeListener.setConnectPanel(connectPanel);
	rightTreePanel.treeListener.setConnectPanel(connectPanel);
    }

    public void setTreeGroup(Tree []treeGroup, Tree []mastGroup, String []title, boolean numOrName, double distance, String distanceText, String simIndexText)
    {
    	leftTree=new Tree();
		rightTree=new Tree();
		this.treeGroup = treeGroup;
        this.mastGroup = mastGroup;

        int treeGroupSize = treeGroup.length;
        int mastGroupSize = mastGroup.length;

        String []treeSelect =  new String[treeGroupSize];
        String []mastSelect = new String[mastGroupSize];

        for(int i=0;i<treeGroupSize;i++){
            leftTreeCombo.addItem(title[i]);
            rightTreeCombo.addItem(title[i]);
        }

        for(int i=0;i<mastGroupSize;i++){
            mastSelect[i]=Integer.toString(i);
            mastCombo.addItem(mastSelect[i]);
        }

        if (treeGroup!=null){
            leftTree = treeGroup[0];
            rightTree = treeGroup[1];

	    getOrderedTree(leftTree, rightTree);
	    leftTree.setTreeOrder();
	    rightTree.setTreeOrder();
	    //treeGroup[0].setTreeOrder();
	    //treeGroup[1].setTreeOrder();
            leftTreePanel.setTree(leftTree);
            rightTreePanel.setTree(rightTree);
        }

        if (mastGroup!=null){
        	mastTree = mastGroup[0];
        }

        ComboHandler comboHandler = new ComboHandler();
        leftTreeCombo.addItemListener(comboHandler);
        rightTreeCombo.addItemListener(comboHandler);
        rightTreeCombo.setSelectedIndex(1);
        mastCombo.addItemListener(comboHandler);

        if (treeGroup.length==2){
        	//distanceValue.setText(Double.toString(distance));
        	distanceValue.setText(distanceText);
        	treeDistance = distance;
        	treeDistanceText = distanceText;
        	simIndex.setText(simIndexText);
        	treeSimIndexText = simIndexText;
        }
        else {
                if (mastGroup!=null){
        	    	//distanceValue.setText(Integer.toString(leftTree.getRoot().getLeafSize()-mastGroup[0].getRoot().getLeafSize()));
					distanceValue.setText(distanceText);
					treeDistanceText = distanceText;
					simIndex.setText(simIndexText);
					treeSimIndexText = simIndexText;
				}
        }

        distanceValue.setEditable(false);

        leftTreePanel.setMastTree(mastGroup[0]);
        rightTreePanel.setMastTree(mastGroup[0]);
		connectPanel.setTree(leftTree, rightTree, mastGroup[0]);
        leftTreePanel.setLabelName(numOrName);
        rightTreePanel.setLabelName(numOrName);

        titleGroup = new String[title.length];
        for(int i=0;i<title.length;i++){
        	titleGroup[i] = new String();
            titleGroup[i]=title[i];
        }

        labelNum=numOrName;
   }

    private class ComboHandler implements ItemListener
    {
         public void itemStateChanged(ItemEvent e)
         {
			 if (e.getSource() == leftTreeCombo){
			 	/*
			 	if (!initial)
			 		System.out.println("left tree changed");
				*/
				if (!leftChanged2)
					leftChanged2=true;
				else

				 	leftChanged = true;

			 }
			 if (e.getSource() == rightTreeCombo){
				 /*
				 if (!initial)
					 System.out.println("right tree changed");
				 */
				 if (!rightChanged2)
					 rightChanged2=true;
				 else
					rightChanged = true;
			 }
			 if (e.getSource() == mastCombo){
				 /*
				 if(!initial)
					 System.out.println("mast tree changed");
				 */
				 if (!mastChanged2)
					 mastChanged2=true;
				 else
					mastChanged = true;
			 }

             drawTree();
         }
    }

	public void setTree(Tree []treeGroup, Set []mastSet, String []title, boolean numOrName, double distance, String distanceText, String simIndexText)
    {
    	leftTree=new Tree();
		rightTree=new Tree();
		this.treeGroup = treeGroup;
        this.mastSetGroup = mastSet;
		mastTreeLeaf = mastSetGroup[0];

        int treeGroupSize = treeGroup.length;
        int mastGroupSize = mastSet.length;

        String []treeSelect =  new String[treeGroupSize];
        String []mastSelect = new String[mastGroupSize];

        for(int i=0;i<treeGroupSize;i++){
            leftTreeCombo.addItem(title[i]);
            rightTreeCombo.addItem(title[i]);
        }

        for(int i=0;i<mastGroupSize;i++){
            mastSelect[i]=Integer.toString(i);
            mastCombo.addItem(mastSelect[i]);
        }

        if (treeGroup!=null){
            leftTree = treeGroup[0];
            rightTree = treeGroup[1];

	    getOrderedTree(leftTree, rightTree);
	    leftTree.setTreeOrder();
	    rightTree.setTreeOrder();
	    //treeGroup[0].setTreeOrder();
	    //treeGroup[1].setTreeOrder();

            leftTreePanel.setTree(leftTree);
            rightTreePanel.setTree(rightTree);
        }

		/* old code
        if (mastGroup!=null){
        	mastTree = mastGroup[0];
        }
		*/

        ComboHandler comboHandler = new ComboHandler();
        leftTreeCombo.addItemListener(comboHandler);
        rightTreeCombo.addItemListener(comboHandler);
        rightTreeCombo.setSelectedIndex(1);
        mastCombo.addItemListener(comboHandler);

        if (treeGroup.length==2){
        	//distanceValue.setText(Double.toString(distance));
			distanceValue.setText(distanceText);
        	treeDistance = distance;
        	treeDistanceText = distanceText;
        	simIndex.setText(simIndexText);
        	treeSimIndexText = simIndexText;
        	
        }
        else {
        	if (mastSet!=null){
        	    //distanceValue.setText(Integer.toString(leftTree.getRoot().getLeafSize()-mastSet[0].size()));
				distanceValue.setText(distanceText);
				treeDistanceText = distanceText;
				simIndex.setText(simIndexText);
				treeSimIndexText = simIndexText;
			}

        }

        distanceValue.setEditable(false);
		simIndex.setEditable(false);
		//new code
        leftTreePanel.setMastLeaf(mastSetGroup[0]);
        rightTreePanel.setMastLeaf(mastSetGroup[0]);
		connectPanel.setTree(leftTree, rightTree, mastSetGroup[0]);
        //---------

		leftTreePanel.setLabelName(numOrName);
        rightTreePanel.setLabelName(numOrName);

        titleGroup = new String[title.length];
        for(int i=0;i<title.length;i++){
        	titleGroup[i] = new String();
            titleGroup[i]=title[i];
        }

        labelNum=numOrName;
   }

	public MastPanel(Tree []treeGroup, Tree []mastGroup, String []title, boolean numOrName, double distance, String distanceText, String simIndexText)
    {
	this();
	setTreeGroup(treeGroup, mastGroup, title, numOrName, distance, distanceText, simIndexText);
	initial=false;
	leftChanged = false;
	leftChanged2=false;
    rightChanged = false;
	rightChanged2=false;
    mastChanged = false;
	mastChanged2=false;


	if (treeGroup.length>0){
		int maxLength = 0;
		for(int i=0;i<treeGroup.length;i++)
			if (treeGroup[i].getRoot().getLeafSet().size()>maxLength)
				maxLength = treeGroup[i].getRoot().getLeafSet().size();

		int panelUnit = (centerPanel.getHeight() - 50)/ maxLength;
		if (panelUnit<20){
			int height = 20 * maxLength + 50;
			centerPanel.setPreferredSize(new Dimension(800, height));
			connectPanel.setPreferredSize(new Dimension(100, height));
			leftTreePanel.setPreferredSize(new Dimension(325, height));
			rightTreePanel.setPreferredSize(new Dimension(325, height));
		}
		else {
				centerPanel.setPreferredSize(new Dimension(800, PANELH));
				connectPanel.setPreferredSize(new Dimension(100, PANELH));
				leftTreePanel.setPreferredSize(new Dimension(325, PANELH));
				rightTreePanel.setPreferredSize(new Dimension(325, PANELH));
		}
	}

    }

	public MastPanel(Tree []treeGroup, Set []mastGroup, String []title, boolean numOrName, double distance, String distanceText, String simIndexText)
    {
	this();
	setTree(treeGroup, mastGroup, title, numOrName, distance, distanceText, simIndexText);
	initial=false;
	leftChanged = false;
	leftChanged2=false;
    rightChanged = false;
	rightChanged2=false;
    mastChanged = false;
	mastChanged2=false;

    if (treeGroup.length>0){
    	int maxLength = 0;
		for(int i=0;i<treeGroup.length;i++){
			if (treeGroup[i].getRoot().getLeafSet().size()>maxLength)
				maxLength = treeGroup[i].getRoot().getLeafSet().size();
		}

		int panelUnit = (centerPanel.getHeight() - 50)/ maxLength;
		if (panelUnit<20){

			int height = 20 * maxLength + 50;

			//System.out.println("Height: "+height);

			int width = this.getWidth();
			centerPanel.setPreferredSize(new Dimension(800, height));
			connectPanel.setPreferredSize(new Dimension(100, height));
			leftTreePanel.setPreferredSize(new Dimension(325, height));
			rightTreePanel.setPreferredSize(new Dimension(325, height));
		}
		else {
				centerPanel.setPreferredSize(new Dimension(800, PANELH));
				connectPanel.setPreferredSize(new Dimension(100, PANELH));
				leftTreePanel.setPreferredSize(new Dimension(325, PANELH));
				rightTreePanel.setPreferredSize(new Dimension(325, PANELH));
		}
	}

    }

    public String getDistanceText()
    {
		return treeDistanceText;
	}

	public String getSimIndexText()
	{
		return treeSimIndexText;
	}
	
   private int transfer(boolean input)
   {
       if (input)
       	    return 1;
       return 0;
   }

   public void drawTree()
   {
	   /*
	   if (initial&&initial2&&(leftChanged||rightChanged||mastChanged))
		   	initial2=false;

		   else
		   	if (initial&&(!initial2)&&(leftChanged||rightChanged||mastChanged))

		   initial=false;
	   */

	   if (leftChanged&&(!initial)){

	   	int select = leftTreeCombo.getSelectedIndex();

	   	leftTree=new Tree();
	   	leftTree = treeGroup[select];

	   	int select2= rightTreeCombo.getSelectedIndex();

	   	rightTree=new Tree();

	   	rightTree=treeGroup[select2];

	   	getOrderedTree(leftTree, rightTree);

	   	leftTree.setTreeOrder();

	   	rightTree.setTreeOrder();

	   	treeGroup[select].setTreeOrder();

	   	treeGroup[select2].setTreeOrder();

	   	leftTreePanel.setTree(leftTree);
	   	rightTreePanel.setTree(rightTree);

	   	connectPanel.setLeftTree(leftTree);

	   	connectPanel.setRightTree(rightTree);
           	leftTreePanel.repaint();

           	rightTreePanel.repaint();
           	connectPanel.repaint();
	   	leftChanged = false;

	   	leftChanged2=false;
	}

	if (rightChanged&&(!initial)){

	    int select = rightTreeCombo.getSelectedIndex();
	    rightTree = new Tree();
		rightTree = treeGroup[select];
		int select2= leftTreeCombo.getSelectedIndex();
		leftTree = new Tree();
		leftTree = treeGroup[select2];

	    getOrderedTree(leftTree, rightTree);
		leftTree.setTreeOrder();
		rightTree.setTreeOrder();
		treeGroup[select].setTreeOrder();
		treeGroup[select2].setTreeOrder();

		leftTreePanel.setTree(leftTree);
	    rightTreePanel.setTree(rightTree);
		connectPanel.setLeftTree(leftTree);
	    connectPanel.setRightTree(rightTree);
            rightTreePanel.repaint();
			leftTreePanel.repaint();
            connectPanel.repaint();
            rightChanged = false;
			rightChanged2=false;
	}
	if (mastChanged&&(!initial)){
		//System.out.println("make re-assign mast");
	    int mastSelect = mastCombo.getSelectedIndex();
	    mastTreeLeaf = new HashSet();
	    mastTreeLeaf = mastSetGroup[mastSelect];
		leftTreePanel.setMastLeaf(mastTreeLeaf);
	    rightTreePanel.setMastLeaf(mastTreeLeaf);
	    connectPanel.setMastLeaf(mastTreeLeaf);
            leftTreePanel.repaint();
            rightTreePanel.repaint();
            connectPanel.repaint();
	    mastChanged = false;
		mastChanged2=false;

 	}
    }

    public Tree []getTreeGroup()
    {
        return treeGroup;
    }

    public Tree []getMastGroup()
    {
    	return mastGroup;
    }

	public Set []getMastSetGroup()
	{
		return mastSetGroup;
	}

    public String []getTitleGroup()
    {
        return titleGroup;
    }

    public boolean getNumOrName()
    {
        return labelNum;
    }

    public double getDistance()
    {
        return treeDistance;
    }

    private void setVisTree()
    {
        Tree []orderedTree = new Tree[2];
        orderedTree = getOrderedTree(leftTree, rightTree);
        leftTree = new Tree();
        rightTree = new Tree();
        leftTree = orderedTree[0];
        rightTree = orderedTree[1];
        leftTreePanel.setTree(leftTree);
        rightTreePanel.setTree(rightTree);
        connectPanel.setLeftTree(leftTree);
        connectPanel.setRightTree(rightTree);
    }

    public Tree []getOrderedTree(Tree leftTree, Tree rightTree)
    {
        Tree []orderedTree;
        orderedTree = new Tree[2];
        orderedTree[0]=new Tree();
        orderedTree[1]=new Tree();
        LinkedList nodePosList = new LinkedList();
        LinkedList sideList = new LinkedList();

        TreeNode leftRoot = new TreeNode();
        leftRoot=leftTree.getRoot();

        TreeNode rightRoot = new TreeNode();
        rightRoot = rightTree.getRoot();

		TreeNode tempNode;

        int d1 = compMetric(leftTree.getOrder(), rightTree.getOrder(), mastTreeLeaf);

		int iteration = 0;

		if (d1!=0){
			nodePosList.add(Integer.toString(leftRoot.getPos()));
			sideList.add("l");

			nodePosList.add(Integer.toString(rightRoot.getPos()));
			sideList.add("r");

			while ((d1!=0)&&(nodePosList.size()!=0)){
				String []orderMod;
				int d2=0;
				if (sideList.get(0).equals("l")){
					tempNode = new TreeNode();
					realNode = new TreeNode();
					getPosNode(leftRoot, Integer.parseInt((String)nodePosList.get(0)));
					tempNode = realNode;

					orderMod = treeReOrder(leftRoot, Integer.parseInt((String)nodePosList.get(0)), leftTree.getOrder());
					d2 = compMetric(orderMod, rightTree.getOrder(), mastTreeLeaf);
					if (d2<d1){
						//leftNode.swapChildren();
						swapSubTree(leftRoot, Integer.parseInt((String)nodePosList.get(0)));
						d1 = d2;
					}
					nodePosList.remove(0);
					sideList.remove(0);
					if (!tempNode.getLeftNode().isLeaf()){
						nodePosList.add(Integer.toString(tempNode.getLeftNode().getPos()));
						sideList.add("l");
					}
					if (!tempNode.getRightNode().isLeaf()){
						nodePosList.add(Integer.toString(tempNode.getRightNode().getPos()));
						sideList.add("l");
					}
				}
				else {
						tempNode = new TreeNode();
						realNode  = new TreeNode();
						getPosNode(rightRoot, Integer.parseInt((String)nodePosList.get(0)));
						tempNode = realNode;
						orderMod = treeReOrder(rightRoot, Integer.parseInt((String)nodePosList.get(0)), rightTree.getOrder());
						d2 = compMetric(orderMod, leftTree.getOrder(), mastTreeLeaf);
						if (d2<d1){
								//rightNode.swapChildren();
								swapSubTree(rightRoot, Integer.parseInt((String)nodePosList.get(0)));
								d1 = d2;
						}
						nodePosList.remove(0);
						sideList.remove(0);
						if (!tempNode.getLeftNode().isLeaf()){
							nodePosList.add(Integer.toString(tempNode.getLeftNode().getPos()));
							sideList.add("r");
						}
						if (!tempNode.getRightNode().isLeaf()){
							nodePosList.add(Integer.toString(tempNode.getRightNode().getPos()));
							sideList.add("r");
						}
				}
			} //end of while

			leftTree.setCoords(leftTreePanel, LEFTSIDE);
			rightTree.setCoords(rightTreePanel,RIGHTSIDE);
			orderedTree[0].setRoot(leftRoot);
			orderedTree[1].setRoot(rightRoot);
		}
		else {
				leftTree.setCoords(leftTreePanel,LEFTSIDE);
				rightTree.setCoords(rightTreePanel,RIGHTSIDE);
		}
        return orderedTree;
    }

	private void swapSubTree(TreeNode root, int pos)
	{
		if (!root.isLeaf()){
			if (root.getPos()!=pos){
				swapSubTree(root.getLeftNode(), pos);
				swapSubTree(root.getRightNode(), pos);
			}
			else {
					root.swapChildren();
			}
		}
	}

	private void getPosNode(TreeNode root, int pos)
	{
		if (root.getPos() == pos)
			realNode = root;
		else {
				if (!root.isLeaf()){
					getPosNode(root.getLeftNode(), pos);
					getPosNode(root.getRightNode(), pos);
				}
			}
	}

    private String []treeReOrder(TreeNode node, int pos, String []order)
    {
        TreeNode temp = new TreeNode();

        String []reOrder;

		getPosNode(node, pos);
		temp = realNode;
        reOrder=new String[order.length];

        for(int i=0;i<reOrder.length;i++)
        	reOrder[i]=new String();

        int leftSize = temp.getLeftNode().getLeafSize();
        int rightSize = temp.getRightNode().getLeafSize();

        while(!temp.isLeaf())
        	temp=new TreeNode(temp.getLeftNode());
        //modifide label -> name
        //String startLeaf = temp.getGeneLabel();
		String startLeaf = temp.getGeneName();

        int startPos=-1;
        for(int i=0;i<order.length;i++)
        	if (order[i].equals(startLeaf)){
        		startPos=i;
        		break;
        	}
 	int endPos = startPos + leftSize + rightSize;

	//modified label -> name
	//System.out.println("node name:"+node.getGeneLabel()+",start pos: "+startPos+",right side:"+rightSize+",left side: "+leftSize+",end pos: "+endPos);
 	//System.out.println("node name:"+node.getGeneName()+",start pos: "+startPos+",right side:"+rightSize+",left side: "+leftSize+",end pos: "+endPos);

 	for(int i=0;i<startPos;i++)
 		reOrder[i]=order[i];
 	for(int i=startPos;i<startPos+rightSize;i++)
 		reOrder[i]=order[i+leftSize];
 	for(int i=startPos+rightSize;i<endPos;i++)
 		reOrder[i]=order[i-rightSize];
 	for(int i=endPos;i<order.length;i++)
 		reOrder[i]=order[i];

 	return reOrder;
    }

	/* old one
    private int compMetric(String []order1, String []order2)
    {
        int distance=0;

        for(int i=0;i<order1.length;i++){
        	String str = order1[i];
         	int d2=getIndex(order2, str);
         	if (d2!=-1)
         	    distance += (d2-i)*(d2-i);
        }
        return distance;
    }
	*/

	private int compMetric(String []order1, String []order2, Set currentMast)
	{
		int distance = 0;			// The number of order edges that intersect
									// The optimal value of distance is "0"
									// The smaller, the better
		int left[] = new int[currentMast.size()];
		int right[] = new int[currentMast.size()];

		int index=0;

		for(int i=0;i<order1.length;i++){
			String str = order1[i];
			if (currentMast.contains(str)){
				left[index]=i;
				int d2 = getIndex(order2, str);
				right[index]=d2;
				index++;
			}

		}

		for(int i=0;i<left.length-1;i++){
			for(int j=i+1;j<left.length;j++){
				if(((left[i]<left[j])&&(right[i]>right[j]))||((left[i]>left[j])&&(right[i]<right[j])))
					distance++;
			}
		}

		return distance;
	}

    private int getIndex(String []order, String string)
    {
        for(int i=0;i<order.length;i++)
	    if(order[i].equals(string))
	    	return i;
	return -1;
    }

    public JPanel getCenterPanel()
    {
		return centerPanel;
	}

	public void printPanel()
	{
		PrinterJob pj=PrinterJob.getPrinterJob();
		pj.setPrintable(MastPanel.this);
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

     	//leave room for result info, blank line, tree info, blank line, blank line, page number
		double pageHeight = pageFormat.getImageableHeight()-2*fontHeight;
     	double pageWidth = pageFormat.getImageableWidth();
		double panelWidth = (double)centerPanel.getWidth();
		double scale = 1;
		if (panelWidth>=pageWidth){
			scale = pageWidth/panelWidth;
		}

		double panelWidthOnPage=panelWidth*scale;

		//double headerHeightOnPage = fontHeight*scale;
		double headerHeightOnPage = fontHeight;

		int totalNumPages = (int)Math.ceil((double)centerPanel.getHeight()*scale/(double)pageHeight);

		if (pageIndex>=totalNumPages){
			return NO_SUCH_PAGE;
		}

		//g2.translate(0, 0);
		g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
		//g2.drawString("Tree Distance: "+(treeDistance), (int)pageWidth/2-35,(int)pageFormat.getImageableY()+50);
		//g2.drawString("Page: "+(pageIndex+1), (int)pageWidth/2-35,
		//			(int)(pageHeight-50));

		g2.translate(0f, headerHeightOnPage);
		g2.translate(0f, -pageIndex*pageHeight);

		//If this piece of the table is smaller than the size available,
		//clip to the appropriate bounds.

     	if (pageIndex + 1 != totalNumPages) {

			/*
			g2.setClip(0, (int)(PH*pageIndex),
		      	(int) Math.ceil(panelWidthOnPage),
		      	(int) Math.ceil(PH));
		   	*/

		   	g2.setClip(0, (int)(pageHeight*pageIndex),
					      	(int) Math.ceil(panelWidthOnPage),
		      			(int) Math.ceil(pageHeight));

     	}

		g2.scale(scale, scale);
		//g2.scale(scale, 0.6);
		disableDoubleBuffering(this);
		centerPanel.paint(g2);

		//this.paint(g2);
		enableDoubleBuffering(this);
		g2.scale(1/scale,1/scale);
		//g2.scale(1/scale, 1.67);
		g2.translate(0f, pageIndex*pageHeight);
		g2.translate(0f, -headerHeightOnPage);

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

    // This method assigns a reference to the rotate button
	public void setRotateButton(JButton r){
		leftTreePanel.setRotateButton(r);
	   	rightTreePanel.setRotateButton(r);
	   	saved = false;
	}//setRotateButton

	// This method assigns a reference to the font button
	public void setFontButton(JButton f){
	  	leftTreePanel.setFontButton(f);
	  	rightTreePanel.setFontButton(f);
	   	saved = false;
	}//setFontButton

	// This method assigns a reference to the label header button
	public void setLabelHeaderButton(JButton h){
	  	leftTreePanel.setLabelHeaderButton(h);
	  	rightTreePanel.setLabelHeaderButton(h);
	   	saved = false;
    }//setLabelHeader

	public MastTreePanel getLeftPanel(){
		return leftTreePanel;
	}

	public MastTreePanel getRightPanel(){
		return rightTreePanel;
	}
}






