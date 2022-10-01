package treecmp.treeanalyzer;

import java.awt.*;
import javax.swing.*;
import java.lang.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import java.util.Set;

public class MastWinShell
{
    //public boolean selectAll;
    //public int selectFile;
    //public Set stringSet;
    public boolean okOrNot;
    MastWin mastWin;

    public MastWinShell(Tree []dataSetGroup, Tree []loadFileGroup, TreeAnalyzer treeAnalyzer, JButton r, JButton f, JButton h)
    {
        mastWin = new MastWin(dataSetGroup, loadFileGroup, treeAnalyzer, r, f, h);
    }

    /*
    public int getSelectFile()

    {

   	return selectFile;

    }


    public boolean getSelectAll()

    {

   	return selectAll;

    }


    public Set getTreeSet()

    {

    	return stringSet;

    }



    public boolean getConfirm()
    {
        return okOrNot;
    }
    */

    private class MastWin extends JFrame
    {
      private JRadioButton fromDataSet, fromFile;
      private JCheckBox allGroup;
      private JRadioButton useNum, useName;
      private JList list;
      private DefaultListModel listModel;

      private Tree []treeGroup;
      private boolean selectAll=true;		//select all group or part of tree group

      private int selectFile;			//select from which file

      private Set stringSet;			//set to store the number of trees to be compared

      final static int FROMDATASET = 0;
	  final static int LOADFILE = 1;

      private Tree []dataSet;
      private Tree []loadFile;
      private String []dataSetTitle;
      private String []loadFileTitle;
      private String []title;
      private boolean numOrName=false;		//true: if use nume; false: if use name
      private JTextField fileField;
      private File selectedFile;
      private String []compareTrees;
      private int allIndex[];
      private final String DATASETTYPE = "d";
      private final String MASTTYPE = "m";
      private final String NEXTYPE = "n";
      private JButton browserButton;

      private MastWin(final Tree []dataSetGroup, final Tree []loadFileGroup, final TreeAnalyzer treeAnalyzer, final JButton r, final JButton f, final JButton h)
      {
        super("Create Mast Tree");

        dataSetTitle = new String[dataSetGroup.length];
        int index=0;
        for(int i=0;i<treeAnalyzer.mainDisplay.getTabCount();i++){
        	if (treeAnalyzer.panelType.getItem(i).equals(DATASETTYPE)||treeAnalyzer.panelType.getItem(i).equals(NEXTYPE)){
            	String substr = new String(treeAnalyzer.mainDisplay.getTitleAt(i));

            	dataSetTitle[index]=substr;
            	index++;
           	}
        }

        JPanel buttonPanel=new JPanel();
        JButton okButton = new JButton("OK");
        okButton.addActionListener(
            new ActionListener(){
            	public void actionPerformed(ActionEvent e)
            	{
            	   //create mast

            	   okOrNot = true;

            	    Cursor hourGlassCursor = new Cursor(Cursor.WAIT_CURSOR);
        			setCursor(hourGlassCursor);
        			dataSet=dataSetGroup;
            	   //loadFile=loadFileGroup;


            	   if (selectFile==LOADFILE){
            	       loadFile = createLoadTrees(selectedFile);
            	       if (loadFile == null){
						   Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
							setCursor(normalCursor);
							dispose();
						}
            	   }
            	   treeGroup = createMast(dataSet, loadFile);
            	   if (treeGroup.length <=1){
            	   		JOptionPane.showMessageDialog(null,

        		    			"Wrong tree number for comparison!",

        		    			"Inane error",
                                    		JOptionPane.ERROR_MESSAGE);
                   }
                   else {
            	   			Set []mastGroup;
            	   			double treeDistance = 0;
            	   			String distanceText = new String();
            	   			String simIndexText = new String();
            	   			MastTab mast;
				   			Stack mastStack;

            	   			// new code
            	   			GordonMast gMast;
            	   			Set mastSet = new HashSet();
            	   			//------------

            	   			if (treeGroup.length>2){

            	        		/* old code
            	   	  			mast=new MastTab();
            	          		mastStack = mast.treeGroupMast(treeGroup);
            	        		*/

            	        		gMast = new GordonMast(treeGroup);
            	        		gMast.getUMAST();
            	        		mastSet = gMast.mastSet;
            	        		treeDistance = gMast.treeDis();
            	        		distanceText = gMast.getDistanceText();
            	        		simIndexText = gMast.getSimIndexText();
            	   			}
            	   			else {
					     			/* Here need some modification */
            	    	 			mast = new MastTab(treeGroup[0], treeGroup[1]);
            	    	 			mastStack = mast.umast();

            	    	 			if (mastStack.size()!=0){
            	    	 				for(int j=0;j<mastStack.size();j++)
            	    	 					mastSet.add(((TreeNode)mastStack.elementAt(j)).getLeafSet());

            	   		 				//debug later
            	   		 				treeDistance = mast.treeDis();
            	   		 				distanceText = mast.getDistanceText();
            	   		 				simIndexText = mast.getSimIndexText();
            	   		 			}
            	   			}

            	   			if (mastSet.size()!=0){
            	   				Iterator it = mastSet.iterator();
            	   				int i=0;
            	   				mastGroup = new Set[mastSet.size()];
            	   				while(it.hasNext()){
            	   					mastGroup[i] = new HashSet();
            	   					mastGroup[i] = (HashSet)it.next();
            	   					i++;
            	   				}

            	   				MastPanel mastPanel = new MastPanel(treeGroup, mastGroup, title, numOrName, treeDistance, distanceText, simIndexText);
            	   				mastPanel.saved = false;
            	   				mastPanel.exported = false;
            	   				mastPanel.setRotateButton(r);
                        		mastPanel.setFontButton(f);
                        		mastPanel.setLabelHeaderButton(h);
		   						treeAnalyzer.addMastPanel(mastPanel);
		   						treeAnalyzer.panelType.add(MASTTYPE);
     		   					treeAnalyzer.setCurrentPanel();
     		   				}
     		   				else {
     		   						JOptionPane.showMessageDialog(null,
									"Empty MAST!",
									"Inane error",
                                    JOptionPane.ERROR_MESSAGE);
     		   				}
     		   		}

     		   		dispose();
            	 }
            }
        );

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(
            new ActionListener(){
            	public void actionPerformed(ActionEvent e)
            	{
            	    //cancel the selection
            	    okOrNot = false;
            	    dispose();
            	}
            }
        );

        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(Box.createRigidArea(new Dimension(200, 0)));
        buttonPanel.add(okButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(20,0)));
        buttonPanel.add(cancelButton);

	Container c;

	c=getContentPane();

	BorderLayout layout = new BorderLayout();
        c.add(fileSelect(), layout.NORTH);

	c.add(treeSelect(), layout.CENTER);

	c.add(buttonPanel, layout.SOUTH);
	setSize(520, 320);

	setVisible(true);
    }

    private boolean equalSize(Tree []tempTreeGroup)
    {
        int treeSize = 0;

        treeSize = tempTreeGroup[0].getRoot().getLeafSize();

        for(int i=1;i<tempTreeGroup.length;i++){
        	int s = tempTreeGroup[i].getRoot().getLeafSize();

        	if (s!=treeSize)
        		return false;
        }

        return true;
    }

    private Tree []createLoadTrees(File fileName)
    {
        String fileLine;
        String treeStructure = "";
        String nodeLabels = "";
        BufferedReader br;
        boolean readLabels;
        boolean readStructure;
        Set labelSet=new HashSet();
        LabelName labelName = new LabelName();
        Tree []treeGroup;
        LinkedList treeList = new LinkedList();
        Tree dendro;
        LinkedList treeNameList = new LinkedList();

        String picFilePath = fileName.getParent();

        try{
            br = new BufferedReader( new FileReader(fileName));
            fileLine = br.readLine();

            readLabels = false;
            readStructure = false;

            //Move along the file reading 1 line at time
            while (fileLine != null){
         		if (!fileLine.trim().equals("")){
                	if (fileLine.trim().equalsIgnoreCase("TRANSLATE")){
                    	readLabels = true;
                    	fileLine = br.readLine();
                    	continue;
                	}//if
                	else if (fileLine.lastIndexOf("UTREE PAUP_") != -1){
                    		readLabels=false;
                    		readStructure = true;
                    		fileLine = fileLine.trim();
                    		treeStructure="";

                    		String s = new String();
                    		int p1 = fileLine.indexOf("UTREE") + 5;	//5 is the length of "UTREE"
                    		int p2 = fileLine.indexOf("=");
                    		s = fileLine.substring(p1, p2);
                    		s = s.trim();

                    		treeNameList.add(s);

                    		if (fileLine.indexOf("(")!=-1)
								fileLine = fileLine.substring(fileLine.indexOf("("));
							else {
							   		fileLine = br.readLine();
							       	continue;
                    		}
                    }//else if
                	else if (fileLine.lastIndexOf("ENDBLOCK;") != -1){
								//fileLine = fileLine.trim().substring(9);
                    			break;
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

                    	if (treeStructure.indexOf(';')!=-1){
                    		dendro = new Tree(treeStructure, labelSet, picFilePath);
            				dendro.setDepth(dendro.getRoot(), 0);
            				treeList.add(dendro);

            	    	}
            		}
                	else if (readLabels){
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
							    	   	dispose();
							    	   	return null;
									}
								}

                    			labelSet.add(labelName);
                    			//nodeLabels += fileLine.trim();
                     		}
                			//--------------------------------------------
						}
                }

                fileLine = br.readLine();
            }//while

            br.close();
            //dendro = new Tree(treeStructure, nodeLabels, picFilePath);

            treeGroup=new Tree[treeList.size()];

			for(int count=0;count<treeGroup.length;count++){
            	treeGroup[count]=(Tree)treeList.get(count);
            }

            title = new String[treeNameList.size()];

            for(int i=0;i<title.length;i++){
            	title[i]=new String();
            	title[i]=(String)treeNameList.get(i);
            }

            return treeGroup;
        }//try
        catch(Exception ioe){
            JOptionPane.showMessageDialog( this, "Error Opening File", "", JOptionPane.ERROR_MESSAGE);
            return null;
        }//catch
    }//createTree

    private Tree []createMast(Tree []dataSet, Tree []loadFile)
    {
        Tree []treeGroup = null;

        if (selectFile == FROMDATASET){
        	if (selectAll){
        		int treeNum = dataSet.length;
        		title = new String[treeNum];
        		treeGroup = new Tree[treeNum];

        		Object []titleObject;
        		titleObject = list.getSelectedValues();

        		for(int i=0;i<treeNum;i++){
        			treeGroup[i]=new Tree();
        			treeGroup[i] = dataSet[i];
        			title[i]=new String();
        			title[i] = titleObject[i].toString();
        		}
        	}
        	else {
        			int treeNum = list.getSelectedValues().length;
        			treeGroup = new Tree[treeNum];
        			int treeGroupIndex[] = new int[treeNum];
        			treeGroupIndex = list.getSelectedIndices();
        			int index=0;

        			Object []titleObject;
        			titleObject = list.getSelectedValues();
        			title = new String[treeNum];

        			try {
        				for(int i=0;i<treeNum;i++){
        					index = list.getSelectedIndices()[i];
        					treeGroup[i]=new Tree();
        					treeGroup[i]=dataSet[index];
        					title[i]=new String();
							title[i]=titleObject[i].toString();
        				}
        			} catch(Exception e){
        		    		JOptionPane.showMessageDialog(null,

        		    			"DataSet Tree number out of bound, please reselect.",

        		    			"Inane error",
                                    		JOptionPane.ERROR_MESSAGE);
                            e.printStackTrace();
                    }
            }
      	}
        else if (selectFile == LOADFILE){

        		int treeNum = loadFile.length;
        		treeGroup = new Tree[treeNum];

        		for(int i=0;i<treeNum;i++){
        			treeGroup[i]=new Tree();
        			treeGroup[i]=loadFile[i];
        		}
        	}

        return treeGroup;
    }

    private JPanel treeSelect()
    {
        JPanel treePanel=new JPanel();
        //ListListener listListener = new ListListener();

		treePanel.setLayout(new BoxLayout(treePanel, BoxLayout.Y_AXIS));

        // Create check box
        allGroup = new JCheckBox("All");
        allGroup.setSelected(true);

        //register for event
        TreeBoxHandler treeHandler = new TreeBoxHandler();
        allGroup.addItemListener(treeHandler);

        //create list
        listModel = new DefaultListModel();
        if (selectFile == FROMDATASET){
        	for(int i=0;i<dataSetTitle.length;i++)
        		listModel.addElement(dataSetTitle[i]);
        }
        else {
        		for(int i=0;i<loadFileTitle.length;i++)
        			listModel.addElement(loadFileTitle[i]);
        }
        list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        allIndex=new int[listModel.size()];
        for(int i=0;i<listModel.size();i++)
        	allIndex[i]=i;

        list.setSelectedIndices(allIndex);
        //list.addListSelectionListener(listListener);
        list.setVisibleRowCount(5);
        list.setEnabled(false);

        JScrollPane listScrollPane = new JScrollPane(list);

        treePanel.add(allGroup);
        treePanel.add(listScrollPane);

        treePanel.setBorder(BorderFactory.createTitledBorder("Select Trees"));

        return treePanel;
    }

    /*
    private class ListLisener implements ListSelectionListener
    {
    	 public void valueChanged(ListSelectionEvent e) {
        	if (e.getValueIsAdjusting() == false)
        		decide = true;
        }
    }
    */

    private class TreeBoxHandler implements ItemListener
    {
        public void itemStateChanged(ItemEvent e)
        {
			if (e.getStateChange() == ItemEvent.SELECTED){
				selectAll=true;
				list.setSelectedIndices(allIndex);
				list.setEnabled(false);

			}
			else {
					selectAll = false;
					list.setEnabled(true);
					list.setSelectedIndex(0);
			}
         }

    }

    private JPanel fileSelect()
    {
        ButtonGroup fileGroup;

        JPanel filePanel=new JPanel();
        filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.Y_AXIS));

        // Create radio buttons
        fromDataSet = new JRadioButton("From Current Data Set", true);
        filePanel.add(fromDataSet);
        fromFile = new JRadioButton("Load From Nexus File", false);
        filePanel.add(fromFile);

        JPanel browserPanel = new JPanel();
        browserPanel.setLayout(new BoxLayout(browserPanel, BoxLayout.X_AXIS));
        JLabel openLabel = new JLabel("open:");
        browserPanel.add(openLabel);

        fileField = new JTextField();
        fileField.setEditable(true);
        fileField.setSize(10, 5);
        fileField.addActionListener(
        	new ActionListener(){
        		public void actionPerformed(ActionEvent e)
        		{
        			String filePath = fileField.getText();
        			selectedFile = new File(filePath);
        		}
        	}
        );

        browserPanel.add(fileField);

        browserButton=new JButton("Browser..");
        browserPanel.add(browserButton);
        browserButton.addActionListener(
            new ActionListener(){
            	public void actionPerformed(ActionEvent e)
            	{
            		JFrame.setDefaultLookAndFeelDecorated(true);
        			JDialog.setDefaultLookAndFeelDecorated(true);

            	    JFileChooser openFile = new JFileChooser("Open File");
                    ExampleFileFilter filter = new ExampleFileFilter("txt", "Nexus files");
                    openFile.setFileFilter(filter);

                    int val = openFile.showOpenDialog(MastWin.this);
                    if (val == JFileChooser.APPROVE_OPTION){
                        selectedFile = openFile.getSelectedFile();
                        String filePath = selectedFile.getPath();
                        fileField.setText(filePath);

                    }
                }
            }
        );

        filePanel.add(browserPanel);

        filePanel.setBorder(BorderFactory.createTitledBorder("Choose File"));

        // register events
        FileButtonHandler fileHandler = new FileButtonHandler();
        fromDataSet.addItemListener(fileHandler);
        fromFile.addItemListener(fileHandler);

        // create logical relationship between JRadioButtons
        fileGroup = new ButtonGroup();
        fileGroup.add(fromDataSet);
        fileGroup.add(fromFile);

        JPanel namePanel = new JPanel();
        JLabel nameLabel = new JLabel("Show Label: ");
        namePanel.add(nameLabel);
        ButtonGroup nameGroup;
        useName = new JRadioButton("Gene Name", true);
        namePanel.add(useName);
        useNum = new JRadioButton("Gene Num", false);
        namePanel.add(useNum);
        numOrName = false;


        NameHandler nameHandler = new NameHandler();
        useNum.addItemListener(nameHandler);
        useName.addItemListener(nameHandler);

        nameGroup=new ButtonGroup();
        nameGroup.add(useNum);
        nameGroup.add(useName);
        namePanel.setBorder(BorderFactory.createLineBorder(Color.gray));
        JPanel comPanel=new JPanel();
        comPanel.setLayout(new BoxLayout(comPanel, BoxLayout.Y_AXIS));
        comPanel.add(filePanel);
        comPanel.add(namePanel);

        return comPanel;
    }

    private class NameHandler implements ItemListener
    {
        public void itemStateChanged(ItemEvent e)
        {
            if (e.getSource() == useNum){
            	numOrName = true;
            }
            else if (e.getSource() == useName) {
            		numOrName = false;
            	}
        }
    }

    private class FileButtonHandler implements ItemListener
    {
        public void itemStateChanged(ItemEvent e)
        {
			if (e.getSource() == fromDataSet){
				selectFile = FROMDATASET;
				browserButton.setEnabled(false);
				fileField.setEnabled(false);
				allGroup.setEnabled(true);
				allGroup.setSelected(true);

			}
			else if (e.getSource() == fromFile){
					selectFile=LOADFILE;
					browserButton.setEnabled(true);
					fileField.setEnabled(true);
					allGroup.setEnabled(false);
					list.setEnabled(false);

			}
         }

    }

	private void textParse(String content)
	{
		int i=0;
		int startN=0;
		int endN=0;
		boolean start=false;
		boolean range=false;
		char c;
		stringSet = new HashSet();
		String firstS=new String();

		if (content!=null){
			while(i<content.length()){
				c=content.charAt(i);
				//start of a number
				if ((Character.isDigit(c))&&(!start)){
					startN=i;
					start=true;
				}
				//end of a number
				else if (c==','){
							if (start){
								endN=i;
								String subStr=new String();
								subStr = content.substring(startN, endN);
								stringSet.add(subStr);
								if (range){
									int first = Integer.parseInt(firstS);
									int last = Integer.parseInt(subStr);
									for(int j=first+1;j<last;j++)
										stringSet.add(Integer.toString(j));
									range=false;
								}
								start=false;
							}
							//pop error format dialog
							else
								JOptionPane.showMessageDialog(null,
									"Tree select format error, please reselect.",
									"Inane error",
                                    JOptionPane.ERROR_MESSAGE);
				}
				//if it is a range
				else if (c=='-'){
						if (start){
								endN=i;
								String subStr=new String();
								subStr=content.substring(startN, endN);
								stringSet.add(subStr);
								range=true;
								start=false;
								firstS = new String();
								firstS = subStr;
						}
						else JOptionPane.showMessageDialog(null,
									"Tree select format error, please reselect.",
									"Inane error",
                                    JOptionPane.ERROR_MESSAGE);
				}
				i++;
			} // end of while
			if (start){

				endN=i;
				String subStr=new String();
				subStr = content.substring(startN, endN);
				stringSet.add(subStr);

				if (range){
					int first = Integer.parseInt(firstS);
					int last = Integer.parseInt(subStr);
					for(int j=first+1;j<last;j++)
						stringSet.add(Integer.toString(j));
					range=false;
				}
				start=false;
			}

		}
	}


   }
}
