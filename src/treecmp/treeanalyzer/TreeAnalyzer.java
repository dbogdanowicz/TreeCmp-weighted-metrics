package treecmp.treeanalyzer;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.List;

public class TreeAnalyzer extends JFrame{

    private DisplayDataSet currentDataSet;
    final JButton rotate;
    final JButton font;
    final JButton header;
    final JButton print;
    //------------------new --------------------
    final JButton mast;
    private MastWinShell mastWinShell;
    final static int FROMDATASET = 0;
    final static int LOADFILE = 1;
    private boolean selectAll;
    private int selectFile;
    private Set treeSet = new HashSet();
    private int mastIndex=1;		// index for mast label, start from 1
    public List panelType=new List();	// indicate the types of all the current panels in the tab
    private MastPanel currentMast;
    private final String MASTSUFFIX=".mst";
    private final String DATASUFFIX = ".dat";
    private final String NEXSUFFIX = ".nex";
    private final String DATASETTYPE = "d";
    private final String MASTTYPE = "m";
    private final String NEXTYPE = "n";
    private boolean firstOpenFile = true;
    private File currentPath = new File("");
    //------------------------------------------

    public JTabbedPane mainDisplay;
    private TreeAnalyzer clone;

    public TreeAnalyzer(){
        super("Tree Analyzer");

        currentDataSet = new DisplayDataSet();
        Container c;

        mainDisplay = new JTabbedPane();

        JToolBar toolBar = new JToolBar();
        rotate = new JButton(new ImageIcon("rotate.gif"));
        rotate.setToolTipText("Rotate Children");

        font = new JButton(new ImageIcon("font.gif"));
        font.setToolTipText("Set Color");

        header = new JButton(new ImageIcon("header.gif"));
        header.setToolTipText("Set Label Headers");

        print = new JButton(new ImageIcon("print.gif"));
        print.setToolTipText("Print");

	//-------------------new ------------------------
	mast = new JButton(new ImageIcon("mast.gif"));
	mast.setToolTipText("Maximum Agreement Subtree");
	clone = this;
	currentMast = new MastPanel();
	//-----------------------------------------------

        rotate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	if ((panelType.getItem(mainDisplay.getSelectedIndex()).equals(DATASETTYPE))||
            		(panelType.getItem(mainDisplay.getSelectedIndex()).equals(NEXTYPE))){
            			((DisplayDataSet)mainDisplay.getSelectedComponent()).saved = false;
            			currentDataSet.saved = false;
            	}
            	else {
            			((MastPanel)mainDisplay.getSelectedComponent()).saved = false;
            			currentMast.saved = false;
            	}

				rotate.setSelected(true);
				font.setSelected(false);
				/*
                if (rotate.isSelected())
                    rotate.setSelected(false);
                else{
                    font.setSelected(false);
                    rotate.setSelected(true);
                    header.setSelected(false);
                    //-----------------new ------------------
                    mast.setSelected(false);
           	    //---------------------------------------
                }//else
                */

            }//actionPerformed
        });

        font.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){

            	if ((panelType.getItem(mainDisplay.getSelectedIndex()).equals(DATASETTYPE))||
            		(panelType.getItem(mainDisplay.getSelectedIndex()).equals(NEXTYPE))){
            			currentDataSet.saved = false;
            			((DisplayDataSet)mainDisplay.getSelectedComponent()).saved = false;
            	}
            	else  {
            			currentMast.saved = false;
            			((MastPanel)mainDisplay.getSelectedComponent()).saved = false;
            	}

				font.setSelected(true);
				rotate.setSelected(false);
				/*
                if (font.isSelected())
                    font.setSelected(false);
                else{
                    font.setSelected(true);
                    rotate.setSelected(false);
                    header.setSelected(false);
                    //------------------new ------------------
                    mast.setSelected(false);
                    //----------------------------------------

                }//else
                */

            }//actionPerformed
        }//ActionListener
        );

        //---------------------------new ------------------------------------
        mast.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){

                Tree []tempTreeGroup = getDataSetTree();

                mastWinShell=new MastWinShell(getDataSetTree(), null, clone, rotate, font, header);
                //only success can you input the tab
				//panelType.add(MASTTYPE);

            }//actionPerformed()
        }//ActionListener
        );
        //---------------------------------------------------------------------

        header.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){

            	if ((panelType.getItem(mainDisplay.getSelectedIndex()).equals(DATASETTYPE))||
            		(panelType.getItem(mainDisplay.getSelectedIndex()).equals(NEXTYPE))){
            			currentDataSet.saved = false;
            			((DisplayDataSet)mainDisplay.getSelectedComponent()).saved = false;
            	}
            	else  {
            			currentMast.saved = false;
            			((MastPanel)mainDisplay.getSelectedComponent()).saved = false;
            	}

            	if ((panelType.getItem(mainDisplay.getSelectedIndex()).equals(DATASETTYPE))||
            		(panelType.getItem(mainDisplay.getSelectedIndex()).equals(NEXTYPE))){
                    currentDataSet = (DisplayDataSet) mainDisplay.getSelectedComponent();
                    Tree temp = currentDataSet.getExactTree();
                    if (temp != null){
						//Only do this if a tree is associated
                    	LabelHeaders labelHeader = new LabelHeaders(temp, currentDataSet.getTreePanel());

                    }//if
                }
            }//actionPerformed
        }//ActionListener
        );

        print.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
            	if ((panelType.getItem(mainDisplay.getSelectedIndex()).equals(DATASETTYPE))
            		||(panelType.getItem(mainDisplay.getSelectedIndex()).equals(NEXTYPE))){
                    currentDataSet = (DisplayDataSet) mainDisplay.getSelectedComponent();
                    try{
                        //PrintUtilities.printComponent(currentDataSet.getTreePanel());
                        currentDataSet.printPanel();
                        print.setSelected(false);
                        font.setSelected(false);
                        rotate.setSelected(false);
                        //-----------------new -------------------
                        mast.setSelected(false);
                        //----------------------------------------

                    }//try
                    catch(Exception ex){
                        JOptionPane.showMessageDialog(null, "Error Printing File");
                    }//catch
                }
                else {
                	currentMast = (MastPanel)mainDisplay.getSelectedComponent();
                	try{
						currentMast.printPanel();
						//PrintUtilities printer = new PrintUtilities(currentMast.getCenterPanel());
						print.setSelected(false);
                		font.setSelected(false);
                		rotate.setSelected(false);
                		mast.setSelected(false);
                	}
                	catch(Exception ex){
                		JOptionPane.showMessageDialog(null, "Error printing file");
                	}
                }
            }//actionPerformed()
        }//ActionListener
        );

        //Add all the buttons to the toolbar
        toolBar.add(rotate);
        toolBar.add(font);
        toolBar.add(header);

	//--------------------new ---------------
        toolBar.add(mast);
        //---------------------------------------

        toolBar.add(print);

        //create and setup the menu bar
        JMenuBar menu = new JMenuBar();
        setJMenuBar(menu);

        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);

        JMenuItem menuNew = new JMenuItem("New");
        menuNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        menuNew.addActionListener( new ActionListener()
            {
                public void actionPerformed(ActionEvent e){

                    /* Open a .nex (nexus file), .dat (DataSet Object) or a .mst(MAST Object)
                     * Here we only allow open nexus file contain one tree topology
                     */

                    JFileChooser openFile;
                    if (firstOpenFile){
                    	openFile = new JFileChooser();
                    	firstOpenFile = false;
                    }
                    else {
                    	    openFile = new JFileChooser(currentPath);
                    }

                    ExampleFileFilter nexFilter = new ExampleFileFilter("nex", "Nexus files");
                    nexFilter.addExtension("txt");
                    ExampleFileFilter datFilter = new ExampleFileFilter("dat", "DataSet files");
                    ExampleFileFilter mstFilter = new ExampleFileFilter("mst", "MAST files");

                    openFile.addChoosableFileFilter(datFilter);
                    openFile.addChoosableFileFilter(mstFilter);
                    openFile.setFileFilter(nexFilter);
                    int val = openFile.showOpenDialog(TreeAnalyzer.this);

                    if (val == JFileChooser.APPROVE_OPTION){
                    	String fileNamePart = openFile.getName(openFile.getSelectedFile());
                    	String filePath = openFile.getSelectedFile().getPath();

                    	if (openFile.getFileFilter().equals(nexFilter)){
                    		//open a .nex file
                    		DisplayDataSet newTabPane = new DisplayDataSet();
                    		newTabPane.setRotateButton(rotate);
                    		newTabPane.setFontButton(font);
                    		newTabPane.setLabelHeaderButton(header);
                    		boolean treeDone = newTabPane.createTree(openFile.getSelectedFile());
 							if (treeDone){
 								currentDataSet = newTabPane;
                            	currentDataSet.drawTree();
                    			mainDisplay.addTab(fileNamePart, newTabPane);
                    			int index = mainDisplay.getTabCount();
                    			mainDisplay.setSelectedIndex(index-1);
                    			panelType.add(NEXTYPE);
                    			currentPath = new File("");
                            	currentPath = openFile.getCurrentDirectory();
                            	newTabPane.setPanelName(fileNamePart);
                            	newTabPane.setPanelPath(filePath);
                            	newTabPane.saved = false;
                    		}
                    	}
                    	else
                    	if (openFile.getFileFilter().equals(datFilter)){
                    		//open a .dat file
                    		Tree temp = new Tree();
                    		try{
                                    FileInputStream fileInput = new FileInputStream(openFile.getSelectedFile());
                                    ObjectInputStream input = new ObjectInputStream(fileInput);
                                    temp = (Tree) input.readObject();
                                    DisplayDataSet newTabPane = new DisplayDataSet();
                    				newTabPane.setRotateButton(rotate);
                    				newTabPane.setFontButton(font);
                    				newTabPane.setLabelHeaderButton(header);
                    				mainDisplay.addTab(fileNamePart, newTabPane);
                    				int index = mainDisplay.getTabCount();
                    				mainDisplay.setSelectedIndex(index-1);
                    				panelType.add(DATASETTYPE);
                                    currentDataSet = (DisplayDataSet) mainDisplay.getSelectedComponent();
                            		currentDataSet.setTree(temp);
                            		currentDataSet.drawTree();
                            		currentPath = new File("");
                            		currentPath = openFile.getCurrentDirectory();
                            		newTabPane.setPanelName(fileNamePart);
                            		newTabPane.setPanelPath(filePath);
                            		newTabPane.saved = false;
                            		newTabPane.exported = true;
                            }//try
                            catch(Exception ex){
                                    JOptionPane.showMessageDialog(null, "Error Loading File.");
                     				ex.printStackTrace();
                            }

                    	}
                    	else
                    	if (openFile.getFileFilter().equals(mstFilter)){
                    		//open a .mst file

                    		try{
                               		FileInputStream fileInput = new FileInputStream(openFile.getSelectedFile());
                                    ObjectInputStream input = new ObjectInputStream(fileInput);
                                    Tree []treeGroup = (Tree[]) input.readObject();
                                    String []titleGroup = (String []) input.readObject();
                                    Set []mastGroup = (Set []) input.readObject();
                                    String labelNum = (String) input.readObject();
                                    double distance = Double.parseDouble((String)input.readObject());
                                    String distanceText = (String)input.readObject();
                                    String simIndexText = (String)input.readObject();
                                    String panelName = (String) input.readObject();

                                    boolean numOrName;
                                    if (labelNum.equals("true"))
                                   		numOrName = true;
                                    else numOrName = false;

                                    MastPanel mastPanel = new MastPanel(treeGroup, mastGroup, titleGroup, numOrName, distance, distanceText,simIndexText);
                                    mastPanel.setRotateButton(rotate);
                                    mastPanel.setFontButton(font);
                                    mastPanel.setLabelHeaderButton(header);

                    				mastPanel.setFontButton(font);
                    				mastPanel.drawTree();
                                    mainDisplay.addTab(panelName, mastPanel);

                                    int index = mainDisplay.getTabCount();
                                    mainDisplay.setSelectedIndex(index-1);

                                    panelType.add(MASTTYPE);
                                    mastIndex++;
                                    currentMast = (MastPanel) mainDisplay.getSelectedComponent();
                                    mastPanel.saved = true;
                                    mastPanel.exported = true;
                                    mastPanel.setPanelName(fileNamePart);
                                    mastPanel.setPanelPath(filePath);
                                    //mainDisplay.setComponentAt(index, mastPanel);
                          	}//try
                            catch(Exception ex){
                           		JOptionPane.showMessageDialog(null, "Error Loading File.");
                            }//catch

                    	}
                    	else {
                    			JOptionPane.showMessageDialog(null, "Unrecognized file type!");
                    	}
                    }


                }//actionPerformed
            }//ActionListener
        );

        JMenuItem close = new JMenuItem("Close");
        close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        close.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e){
                    int index = mainDisplay.getSelectedIndex();
                    dealSaveFiles();
                    if (index!=-1){
                    	mainDisplay.remove(index);
                    	panelType.remove(index);
                    }

                }//actionPerformed
            }//ActionListener
        );

        JMenuItem save = new JMenuItem("Save");
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        save.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e){
                		dealSaveFiles();

                }//actionPerformed
            }//ActionListener
        );

        JMenuItem printItem = new JMenuItem("Print");
        printItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        printItem.addActionListener( new ActionListener()
            {
                public void actionPerformed(ActionEvent e){
                    if ((panelType.getItem(mainDisplay.getSelectedIndex()).equals(DATASETTYPE))
					     ||(panelType.getItem(mainDisplay.getSelectedIndex()).equals(NEXTYPE))){
					    	currentDataSet = (DisplayDataSet) mainDisplay.getSelectedComponent();
					      	try{
					         	//PrintUtilities.printComponent(currentDataSet.getTreePanel());
					          	currentDataSet.printPanel();
					          	print.setSelected(false);
					          	font.setSelected(false);
					          	rotate.setSelected(false);
					           	//-----------------new -------------------
					         	mast.setSelected(false);
					          	//----------------------------------------

					      	}//try
					      	catch(Exception ex){
					         	JOptionPane.showMessageDialog(null, "Error Printing File");
					       	}//catch
					}
					else {
					      	currentMast = (MastPanel)mainDisplay.getSelectedComponent();
					       	try{
								currentMast.printPanel();
								font.setSelected(false);
					            rotate.setSelected(false);
					            mast.setSelected(false);
					       	}
					        catch(Exception ex){
					           	JOptionPane.showMessageDialog(null, "Error printing file");
					       	}
                	}
                }//actionPerformed
            }//ActionListener
        );

        JMenuItem exit = new JMenuItem("Exit");
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        exit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e)
                {System.exit(0);}
            }
        );

	//Add the file menu actions to the file menu
        file.add(menuNew);
        file.add(close);
        file.add(save);
        file.add(printItem);
        file.add(exit);

        //add the file section to the menu bar
        menu.add(file);

        //assign the buttons to the dataset
        currentDataSet.setRotateButton(rotate);
        currentDataSet.setFontButton(font);
        currentDataSet.setLabelHeaderButton(header);

        setSize(800,600);

        c = getContentPane();
        c.setLayout(new BorderLayout());
        c.add(toolBar, BorderLayout.NORTH);
        c.add(mainDisplay, BorderLayout.CENTER);
        c.setSize(800,550);
        c.setVisible(true);
        setVisible(true);

    }//default constructor

	private void dealSaveFiles()
	{
		try{
				int index = mainDisplay.getSelectedIndex();
           		//It is loaded from a .nex file, create a new .dat file
                if (panelType.getItem(mainDisplay.getSelectedIndex()).equals(NEXTYPE)){
                	currentDataSet = (DisplayDataSet) mainDisplay.getSelectedComponent();
                    Tree temp = currentDataSet.getTree();
                    if (!currentDataSet.saved){
                    	int val = JOptionPane.showConfirmDialog(null, "Save Data", "Information will be lost if not saved. Do you want to save first?", JOptionPane.YES_NO_OPTION);
                        if (val == JOptionPane.NO_OPTION){
                        	//No save required
                     	}//if
                        else{  //save needed
                            	if (!currentDataSet.exported){

                               		//need to create a new .dat file
                               		JFileChooser saveFile = new JFileChooser(currentPath);
                                	ExampleFileFilter filter = new ExampleFileFilter("dat", "Data Set");
                               		saveFile.setFileFilter(filter);

                               		//val = saveFile.showDialog(TreeAnalyzer.this, "Save");
									val = saveFile.showSaveDialog(TreeAnalyzer.this);
									File tempFile = new File("");
                               		if(val == JFileChooser.APPROVE_OPTION){
                                    	try{
                                    		File saveName = saveFile.getSelectedFile();
                                    		String path = saveName.getPath();
                                    		if (saveName.getName().indexOf('.')==-1){
                                    			tempFile = new File(path+DATASUFFIX);
                                    			path = path.concat(DATASUFFIX);
                                    		}
                                    		else tempFile = saveName;

                                            FileOutputStream fileOutput = new FileOutputStream(tempFile);
                                            ObjectOutputStream output = new ObjectOutputStream(fileOutput);
                                            output.writeObject(currentDataSet.getTree());
                                            output.flush();
                                            output.close();
                                            currentDataSet.exported = true;
                                    		currentDataSet.setPanelName(saveName.getName());
                                    		currentDataSet.setPanelPath(path);
                                    		currentDataSet.saved = true;
                                    	}//try
                                   		catch(Exception ex){
                                       		JOptionPane.showMessageDialog(null, "Error Saving File");
                                            System.out.println(ex.toString());
                                    	}//catch
                             		}//if approve

                            	} //else exported
                            	else {  //.dat file has already been created
                            			String fileName = new String(currentDataSet.getPanelPath());
                            			File saveName = new File(fileName);
                            			FileOutputStream fileOutput = new FileOutputStream(saveName);
                                    	ObjectOutputStream output = new ObjectOutputStream(fileOutput);
                                   		output.writeObject(currentDataSet.getTree());
                                   		output.flush();
                                   		output.close();
                                   		currentDataSet.saved = true;
                           		}


                    	}//if need save
                  	}
					else {
							//it's already been saved
					}
              	}  //end of if
               	else
                //If it is loaded from a data set, just save to the original file
                if (panelType.getItem(mainDisplay.getSelectedIndex()).equals(DATASETTYPE)){
                  		currentDataSet = (DisplayDataSet) mainDisplay.getSelectedComponent();
                       	Tree temp = currentDataSet.getTree();
                        if (!currentDataSet.saved){
							try{
								int val = JOptionPane.showConfirmDialog(null, "Save Data", "Information will be lost if not saved. Do you want to save first?", JOptionPane.YES_NO_OPTION);
                            	if (val == JOptionPane.NO_OPTION){
                                    //No save required
                            	}//if
                            	else{  //save needed
                                		String filePath = new String(currentDataSet.getPanelPath());
                            			File saveName = new File(filePath);
                            			FileOutputStream fileOutput = new FileOutputStream(saveName);
                                    	ObjectOutputStream output = new ObjectOutputStream(fileOutput);
                                    	output.writeObject(currentDataSet.getTree());
                                    	output.flush();
                                    	output.close();
                                		currentDataSet.saved = true;
                                }
                          	}//try
                           	catch(Exception ex){
                                    JOptionPane.showMessageDialog(null, "Error Saving File");
                                   	System.out.println(ex.toString());
                           	}//catch
                   		}//else
						else {
								//it's already been saved
						}
                 	}  //end of if
                   	//else if it a mast panel
                  	else {
                        	currentMast = (MastPanel) mainDisplay.getSelectedComponent();
                            Tree []temp = currentMast.getTreeGroup();
                            //there is a tree structure there so prompt to save
                            if (!currentMast.saved){
                            	if (!currentMast.exported){
                            		int val = JOptionPane.showConfirmDialog(null, "Save Data", "Information will be lost if not saved. Do you want to save first?", JOptionPane.YES_NO_OPTION);
                            		if (val == JOptionPane.NO_OPTION){
                                      	//No save required
                            	   	}//if
                            	    else{
                                    		//Save needed
                                    		JFileChooser saveFile = new JFileChooser(currentPath);
                                    		ExampleFileFilter filter = new ExampleFileFilter("mst", "Mast File");
                                    		saveFile.setFileFilter(filter);

                                    		val = saveFile.showDialog(TreeAnalyzer.this, "Save");

                                    		if(val == JFileChooser.APPROVE_OPTION){
                                    		    try{
                                    		    	File saveName = saveFile.getSelectedFile();
                                    		    	File tempFile = new File("");
                                    		    	String path = saveName.getPath();
                                    		    	if (saveName.getName().indexOf('.')==-1){
                                    					tempFile = new File(path+MASTSUFFIX);
                                    					path = path.concat(MASTSUFFIX);
                                    				}
                                    				else tempFile = saveName;

                                            		Tree []treeGroup = currentMast.getTreeGroup();
   					    							Set []mastSetGroup = currentMast.getMastSetGroup();
   					    							String []titleGroup = currentMast.getTitleGroup();
   					    							boolean numOrName = currentMast.getNumOrName();
   					    							double distance = currentMast.getDistance();
   					    							String distanceText = currentMast.getDistanceText();
   					    							String simIndexText = currentMast.getSimIndexText();
   					    							String labelNum=new String();
   					    							if (numOrName)
   					    		    					labelNum="true";
   					    							else labelNum="false";

                            		    			FileOutputStream fileOutput = new FileOutputStream(tempFile);
                            		    			ObjectOutputStream output = new ObjectOutputStream(fileOutput);
                            		    			output.writeObject(treeGroup);
                            		    			output.writeObject(titleGroup);
                            		    			output.writeObject(mastSetGroup);
                            		    			output.writeObject(labelNum);
                            		    			output.writeObject(Double.toString(distance));
                            		    			output.writeObject(distanceText);
                            		    			output.writeObject(simIndexText);

                            		    		    String panelName = new String();
                            		    		    panelName = saveName.getName();
                            		    		    int p = panelName.indexOf('.');

                            		    		    if (p!=-1)
                            		    		    	panelName = panelName.substring(0, p);

                            		    		    output.writeObject(panelName);
                                            		output.flush();
                                            		output.close();
                                            		currentMast.saved = true;
                                            		currentMast.exported = true;
                                            		currentMast.setPanelName(saveName.getName());
                                            		currentMast.setPanelPath(path);
                                    		    }//try
                                        	    catch(Exception ex){
                                            		JOptionPane.showMessageDialog(null, "Error Saving File");
                                            		System.out.println(ex.toString());
                                    		    }//catch
                                    		}//if approve
                                   	} //if need save
                            	}//if did not export
                            	else { //just write to the exported .mst file
                            				String fileName = new String(currentMast.getPanelPath());
                            				File saveName = new File(fileName);
                            				FileOutputStream fileOutput = new FileOutputStream(saveName);
                                    		ObjectOutputStream output = new ObjectOutputStream(fileOutput);
                                    		Tree []treeGroup = currentMast.getTreeGroup();
   					    					Set []mastSetGroup = currentMast.getMastSetGroup();
   					    					String []titleGroup = currentMast.getTitleGroup();
   					    					boolean numOrName = currentMast.getNumOrName();
   					    					double distance = currentMast.getDistance();
   					    					String distanceText = currentMast.getDistanceText();
   					    					String simIndexText = currentMast.getSimIndexText();
   					    					String labelNum=new String();
   					    					if (numOrName)
   					    		    			labelNum="true";
   					    					else labelNum="false";
                            		    	output.writeObject(treeGroup);
                            		    	output.writeObject(titleGroup);
                            		    	output.writeObject(mastSetGroup);
                            		    	output.writeObject(labelNum);
                            		    	output.writeObject(Double.toString(distance));
                            		    	output.writeObject(distanceText);
                            		    	output.writeObject(simIndexText);
                                            output.flush();
                                            output.close();

                                    		currentMast.saved = true;
                                    		//currentMast.setPanelName(saveName.getName());
                                    		//currentMast.setPanelPath(saveName.getPath());
                                }//else export
                            }//else save
                  	} //else mast
               	}//try
                catch(Exception ex){
                        //catch null pointer exception
               	}
    }

    //get the tree group of current data set
    private Tree []getDataSetTree()
    {
        Tree []treeGroup;
        DisplayDataSet indexDataSet;
        Tree temp;

        int treeNum;
        //treeNum = mainDisplay.getTabCount();
        treeNum = 0;
        for(int i=0;i<panelType.getItemCount();i++)
        	if (panelType.getItem(i).equals(DATASETTYPE)||panelType.getItem(i).equals(NEXTYPE))
        		treeNum++;
        treeGroup = new Tree[treeNum];
        int i=0;
        int index=0;

        while (i<mainDisplay.getTabCount()){
            if (panelType.getItem(i).equals(DATASETTYPE)||panelType.getItem(i).equals(NEXTYPE)){
                indexDataSet = new DisplayDataSet();
                indexDataSet = (DisplayDataSet)mainDisplay.getComponentAt(i);
                temp = new Tree();
                temp = indexDataSet.getTree();
                treeGroup[index]=new Tree();
                treeGroup[index] = temp;
                index++;
            }
            i++;
        }

        return treeGroup;
    }

    private void createMast()
    {
        Tree []treeGroup;
        DisplayDataSet indexDataSet;
        Tree temp;

        if (selectAll){
        	if (selectFile == FROMDATASET){
        		int treeNum=0;

        		for(int i=0;i<panelType.getItemCount();i++)
        		    if (panelType.getItem(i).equals(DATASETTYPE))
        			treeNum++;
        		treeGroup = new Tree[treeNum];
        		int i=0;
        		int index = 0;
        		while (i<treeNum){
   			    if (panelType.getItem(i).equals(DATASETTYPE)){
        			indexDataSet = new DisplayDataSet();
        			indexDataSet = (DisplayDataSet)mainDisplay.getComponentAt(i);
        			temp = new Tree();
        			temp = indexDataSet.getTree();
        			treeGroup[index]=new Tree();
        			treeGroup[index] = temp;
        			index++;
        		    }
        		    i++;
        		}
        	}

        }
        else {
        	if (selectFile == FROMDATASET){
        		int treeNum = treeSet.size();
        		treeGroup = new Tree[treeNum];
        		Iterator it = treeSet.iterator();
        		int i=0;
        		while(it.hasNext()){
        		    int j=Integer.parseInt((String)it.next());
        		    indexDataSet=new DisplayDataSet();
        		    indexDataSet = (DisplayDataSet)mainDisplay.getComponentAt(j);
        		    temp = new Tree();
        		    temp = indexDataSet.getTree();
        		    treeGroup[i]=new Tree();
        		    treeGroup[i]=temp;
        		    i++;
        		}
        	}

        }

    }

    public void addMastPanel(MastPanel mastPanel)
    {
        String temp = Integer.toString(mastIndex);
        mainDisplay.addTab("Mast"+temp, mastPanel);
        mastIndex++;
    }

    public void setMastIndex(int inIndex)
    {
        mastIndex = inIndex;
    }

    public int getMastIndex()
    {
        return mastIndex;
    }

    public void setPanelType(String type)
    {
        panelType.add(type);
    }

    public void setCurrentPanel()
    {
       int index = mainDisplay.getTabCount();
       mainDisplay.setSelectedIndex(index-1);
    }

	public void setWaitCursor()
	{
		Cursor hourGlassCursor = new Cursor(Cursor.WAIT_CURSOR);
        setCursor(hourGlassCursor);
    }

    public void setNormalCursor()
    {
    	Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        setCursor(normalCursor);
    }

    public static void main(String args[]){
        TreeAnalyzer app = new TreeAnalyzer();

        app.addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                    {System.exit(0);}
                }
            );
    }//main

}//class TreeAnalyzer
