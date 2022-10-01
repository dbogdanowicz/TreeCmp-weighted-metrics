package treecmp.treeanalyzer;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.awt.List;
import java.text.NumberFormat;
import java.text.DecimalFormat;

public class GordonMast{

    private Tree []treeGroup;
    private Tree []reRootGroup;
    private int treeNum;
    private String []leafArray;
    private Set leafSet;
    private String rootLeaf;
    private int rootLeafPos = 0;
    private Lcat []lcats;
    private int leafSetSize=0;
    private Stack mastLcat;
    public Set mastSet=new HashSet();		//The final answer
    private int lcatNumTwo;
	private int lcatNum;
    private static int LEFT=0;
    private static int RIGHT=1;
    private static int NOP=-1;
    private int mastSize=0;
    private int orderPos = -1;

    private int rec_submast=0;

    private int []lcatArray;
    private LinkedList pOrderList = new LinkedList();
    private Set lSet = new HashSet();
    private int orderMax;
	private String LEFTC = "L";
	private String RIGHTC = "R";
	private String PRUNEC = "P";
	private Stack sideStack = new Stack();
	private Stack nodeStack = new Stack();
	private double treeDistance;
	private int totalLeaves = 0;

    public GordonMast()
    {
    }

    public GordonMast(Tree []inTreeGroup)
    {
        setTreeGroup(inTreeGroup);
    }

    public void setTreeGroup(Tree []inTreeGroup)
    {
  	treeNum = inTreeGroup.length;
	orderMax = (int)(Math.pow(2, treeNum)-1);
   	treeGroup = new Tree[treeNum];
   	reRootGroup = new Tree[treeNum];
        for(int i=0;i<treeNum;i++){
            treeGroup[i]=new Tree();
            treeGroup[i]=inTreeGroup[i];
        }
        if (inTreeGroup!=null){
        	leafSet = inTreeGroup[0].getRoot().getLeafSet();
        	leafArray=new String[leafSet.size()];
        	Iterator it=leafSet.iterator();
        	int i=0;
        	while(it.hasNext()){
        	    leafArray[i]=(String)it.next();
        	    i++;
        	}
        	leafSetSize = leafArray.length;
        	rootLeaf=new String();
        	mastLcat = new Stack();
        }
    }

    /* if leaf set are the same return true
     * else return false
     */

    private boolean checkLeafSet(Tree []tGroup)
    {
	if (tGroup!=null){
		for(int i=1;i<tGroup.length;i++){
		    Set tempSet = new HashSet();
		    tempSet = tGroup[i].getRoot().getLeafSet();
		    if (leafSet.equals(tempSet))
			continue;
		    else
			return false;
		}
		return true;
	}
	return false;
    }

    /* get one lcat tuple
     */
    private int []getLcatTuple(int leaf1, int leaf2, Tree []tGroup, int rootPos)
    {
         int []lcatT=new int[treeNum];

         if ((leaf1 == rootPos )||(leaf2 == rootPos))
             return null;
         else {
         	String l1 = leafArray[leaf1];
         	String l2 = leafArray[leaf2];

         	for(int i=0;i<treeNum;i++){
         	    TreeNode temp = new TreeNode();
         	    temp = tGroup[i].getRoot();
         	    boolean loop=true;
         	    while(loop){
         	        if (!temp.isLeaf()){
         	            Set rightLeafSet = temp.getRightNode().getLeafSet();
         	            Set leftLeafSet = temp.getLeftNode().getLeafSet();
         	            if (rightLeafSet.contains(l1)&&rightLeafSet.contains(l2)){
         	            	temp = temp.getRightNode();
         	            	continue;
         	            }
         	            else if (leftLeafSet.contains(l1)&&leftLeafSet.contains(l2)){
         	            		temp = temp.getLeftNode();
         	            		continue;
         	                 }
         	                 else {
         	                 	lcatT[i]=temp.getPos();
         	                 	break;
         	                 }
         	        }
         	   }  //end of while
    		}  //end of for
    	}   //end of else

    	return lcatT;
    }

    /* create lcat tuples for the rooted group
     */

    private void createLca()
    {
    	lcatNumTwo=leafSetSize*(leafSetSize-1)/2;

    	lcatNum = lcatNumTwo + leafSetSize;
    	int leaf1 = 0;
    	int leaf2 = 1;

    	lcats = new Lcat[lcatNum];

    	for(int i=0;i<lcatNumTwo;i++){
    	    lcats[i]=new Lcat();
    	    lcats[i].leaf1 = leaf1;
    	    lcats[i].leaf2 = leaf2;
    	    int []tempLcat = new int[treeNum];
    	    tempLcat = getLcatTuple(leaf1, leaf2, reRootGroup, rootLeafPos);
    	    lcats[i].fillLcat(tempLcat);

    	    //that means, this tuple is the biggest
			if (tempLcat == null){
    	    	lcats[i].NPre = true;
				tempLcat = new int[treeNum];
				for(int j=0;j<treeNum;j++)
					tempLcat[j]=1;
				lcats[i].fillLcat(tempLcat);
			}
    	    else
    	    	lcats[i].NPre = false;

    	    leaf2++;
    	    if (leaf2==leafSetSize){
    	    	leaf1++;
    	    	leaf2=leaf1+1;
    	    }


    	}
	int []tempLcat = new int[treeNum];
	for(int i=0;i<treeNum;i++)
	    tempLcat[i]=0;

	for(int i=lcatNumTwo;i<lcatNum;i++){

	    lcats[i]=new Lcat();

	    lcats[i].leaf1=i-lcatNumTwo;

	    lcats[i].leaf2=i-lcatNumTwo;

	    lcats[i].fillLcat(tempLcat);

	    lcats[i].NPre=false;

	}
    }

    /* get the leaves do not belong to the node according to its position
     */
    private void getOutLeaf(TreeNode node, int pos)
    {
         if (node!=null){
             if (node.getPos()==pos){
            	lSet = new HashSet();
            	lSet.addAll(leafSet);
            	lSet.removeAll(node.getLeafSet());
             }
             else {
         	    getOutLeaf(node.getRightNode(), pos);
         	    getOutLeaf(node.getLeftNode(), pos);
             }
         }
     }

    private int getIndex(String leaf)
    {
        for(int i=0;i<leafArray.length;i++)
        	if (leafArray[i].equals(leaf))
        		return i;
    	return -1;
    }

    private void getSingleOrder(int treeIndex, int leafIndex, TreeNode node)
    {
		if (node != null){
			if (node.getPos()==treeIndex){

				if (node.isLeaf())
					orderPos = -1;

				else if (node.getLeftNode().getLeafSet().contains(leafArray[leafIndex]))

							orderPos = LEFT;

					 else if (node.getRightNode().getLeafSet().contains(leafArray[leafIndex]))

			     				orderPos = RIGHT;
			}
			else {
        			getSingleOrder(treeIndex, leafIndex, node.getLeftNode());
       				getSingleOrder(treeIndex, leafIndex, node.getRightNode());
			}
		}
    }

    private int getOrder(int lcatIndex, int leaf)
    {
    	int order=0;		/* Use a binary number to represent order:
    				 * Example: 4 trees; order = 13(bin: 1101)
    				 */
	for(int i=0;i<treeNum;i++){
		getSingleOrder(lcats[lcatIndex].lcats[i], leaf, reRootGroup[i].getRoot());

		order=order*2+orderPos;
	}

        return order;
    }

    private int getLocation(int leaf)
	{
	    return (leaf+lcatNumTwo);
	}

    private void push_order(int []hostLcat, int []decdLcat, int order, int subMast, int leaf1, int leaf2, boolean lessbig)
    {
	POrder pOrder = new POrder();
    	int j=0;

    	boolean sameLcat = false;

    	for(j=0;j<pOrderList.size();j++){
	    pOrder = (POrder) pOrderList.get(j);
    	    if (pOrder.sameLcat(hostLcat)){
    		sameLcat = true;
    	    	break;
    	    }
	}
    	String leafName1 = leafArray[leaf1];

    	String leafName2 = leafArray[leaf2];

    	if (!sameLcat){
	    pOrder = new POrder();
	    pOrder.iniLcat(hostLcat, lessbig);

	    pOrder.addDecend(decdLcat, order, subMast, leafName1, leafName2);
    	    pOrderList.add(pOrder);
	}
	else {
    		((POrder)pOrderList.get(j)).addDecend(decdLcat, order, subMast, leafName1, leafName2);
	}
    }

    /* Leaf2 is the leaf which demonstrate the mast attribute of the lcat
     */

    private void push_lcat(int leaf1, int leaf2, int decend)
    {
        for(int i=0;i<lcats.length;i++){
            if ((lcats[i].leaf1==leaf1&&lcats[i].leaf2==leaf2)||
            	(lcats[i].leaf1==leaf2&&lcats[i].leaf2==leaf1)){

            	    int order;
            	    order=getOrder(i, leaf2);

    	    	    int []tempLcat = lcats[i].lcats;
    	    	    boolean lessbig = lcats[i].lessbig;

    	    	    int []decdLcat = new int[treeNum];

		    for(int j=0;j<decdLcat.length;j++)
		    	decdLcat[j]=-leaf2;
    	    	    int subMastSize = 1;

		    //for the leaf, you know the mast size is 1
		    push_order(tempLcat, decdLcat, order,1, leaf2, leaf2, lessbig);

					order = (int)Math.pow(2, treeNum) - 1 - order;
					decdLcat = lcats[decend].lcats;
					int decdLeaf1 = lcats[decend].leaf1;
					int decdLeaf2 = lcats[decend].leaf2;
		    subMastSize = lcats[decend].mastSize;

		    //for the sub tree, you don't know the mast size
		    push_order(tempLcat, decdLcat, order,0, decdLeaf1, decdLeaf2, lessbig);
            	    break;
            }
        }
    }

    /* get Partial order of the LCAT tuples
     */

    private void partialOrder(Lcat []inLcats)
    {
        for(int i=0;i<inLcats.length;i++){
             if(inLcats[i].NPre)
             	continue;
             else {
             	    boolean skip=false;

  		    for(int j=0;j<inLcats[i].lcats.length;j++)
  		    	if ((inLcats[i].lcats[j]==2)||(inLcats[i].lcats[j]==0)){
  		    	    if (inLcats[i].lcats[j]==2)
  		    	    	lcats[i].lessbig = true;
  		    	    skip=true;

  		    	    break;

  		    	}
  		    if (skip)
  		    	continue;

  		    	int order;
              	    getOutLeaf(reRootGroup[0].getRoot(),inLcats[i].lcats[0]);

              	    //Find the demonstrating leaf, which is common to all the trees

              	    Set retainSet = new HashSet();
              	    retainSet.addAll(lSet);
              	    for(int j=1;j<inLcats[i].lcats.length;j++){
              	    	getOutLeaf(reRootGroup[j].getRoot(), inLcats[i].lcats[j]);
   			retainSet.retainAll(lSet);
   		    }

   		    /*for debug---------------------
   		    if (retainSet!=null){
              	    	System.out.println("Retain Set Size: "+retainSet.size());
              	    	Iterator it = retainSet.iterator();
              	    	while(it.hasNext()){
              	    	    System.out.print(it.next()+" ");
              	    	}
              	    	System.out.println();
              	    }
              	    else System.out.println("Null retain Set");
              	    */
              	    //------------------------------

   		    if (retainSet==null)
   		    	continue;
   		    else {
   		    	    Iterator it=retainSet.iterator();
   		    	    while(it.hasNext()){
			    	String leafName = (String)it.next();
   		    	        int index = getIndex(leafName);

				//all the lcat less than biggest, so don't put biggest into list
				if (index!=rootLeafPos)
					push_lcat(inLcats[i].leaf1,index,i);

   		    	    }

   		    } //end of else
   		}  //end of else
   	}  //end of for


   }

     private int createSubMast(int orderIndex1, int orderIndex2, int lcatIndex)
     {
         if (lcats[lcatIndex].mastSize!=0){
       	    int currentSize=lcats[orderIndex1].mastSize+lcats[orderIndex2].mastSize;
       	    if (mastSize<1)
			//if (mastSize<lcats[lcatIndex])
       	        return -1;
       	    else {
       	    	    Iterator it1=lcats[orderIndex1].decends.iterator();
       	    	    Iterator it2=lcats[orderIndex2].decends.iterator();

       	    	    Set tempSet3=new HashSet();

       	    	    while(it1.hasNext()){
       	        	Set tempSet1=new HashSet();
       	        	tempSet1=(HashSet)it1.next();

       	        	Set tempSet2=null;
       	        	while(it2.hasNext()){
       	            	    tempSet2=new HashSet();
       	            	    tempSet2=(HashSet)it2.next();
       	            	    tempSet2.addAll(tempSet1);
       	               	}
       	               	tempSet3.add(tempSet2);
       	            }
       	            if (mastSize==lcats[lcatIndex].mastSize)
       	            	lcats[lcatIndex].mastSet.addAll(tempSet3);
       	            else if (mastSize>lcats[lcatIndex].mastSize){
       	            	    lcats[lcatIndex].mastSet.clear();
       	            	    lcats[lcatIndex].mastSet.addAll(tempSet3);
       	            	    lcats[lcatIndex].mastSize=currentSize;
       	          	 }
       	    }
        } //end of if

        return 0;
    }


    private Set getRootedMast()
    {
        Iterator it=mastLcat.iterator();

        int []lcatArray=new int[mastLcat.size()];
        int i=0;

        while(it.hasNext()){
            lcatArray[i]=((Integer)it.next()).intValue();
            i++;
        }
        for(i=0;i<lcatArray.length;i++){
            int lcatIndex=lcatArray[i];
        }
	return null;
    }

    public void displayLcat()
    {
        for(int i=0;i<lcats.length;i++)
            lcats[i].display(leafArray);
    }

    private boolean same(int []lcat, int []temp)
    {
	if (lcat.length!=temp.length)
		return false;
	else {
		for(int i=0;i<lcat.length;i++)
			if (lcat[i]!=temp[i])
				return false;
	}
	return true;
    }
    private int belongMainLcat(int []temp)
    {
	for(int i=0;i<pOrderList.size();i++)
	{
		POrder pOrder = new POrder();
		pOrder = (POrder)pOrderList.get(i);
		int []lcat = pOrder.lcats;

		if (same(lcat, temp))
			return i;
		}

	return -1;
    }

    /* index: index of the pOrder in the pOrder list
     * we get the mast size for each POrder in the pOrderList
     */
    private void getSubMast(POrder pOrder, int index)
{

	for(int i=0;i<pOrder.decends.size();i++){
		DcedInfo dced = new DcedInfo();
		dced = (DcedInfo)pOrder.decends.get(i);

		int k=0;
		while(k<dced.dcedList.size()){
			DcedMast dcedMast = new DcedMast();
			dcedMast = (DcedMast)dced.dcedList.get(k);

			//it's not leaf, we have to get the sub mast first
			if (dcedMast.subMast==0){
				int j=belongMainLcat(dcedMast.lcat);
				if (j>=0){
					POrder p = new POrder();
					p = (POrder)pOrderList.get(j);
					if (p.mastSize==0){
						getSubMast(p, j);
					}
					dcedMast.subMast=((POrder)pOrderList.get(j)).mastSize;
				}
				else {
					//at least, it has two leaves
					dcedMast.subMast = 2;
				}
				((DcedMast)dced.dcedList.get(k)).subMast = dcedMast.subMast;
			}

			if(dcedMast.subMast>dced.subMastSize){
			    int m=0;
			    for(m=0;m<k;m++){

				/* the dcedMast before the current one should have a smaller size */

				//if (((DcedMast)dced.dcedList.get(0)).subMast<dcedMast.subMast){

					dced.dcedList.remove(0);
				//}
			    } //end of for
			    dced.subMastSize = dcedMast.subMast;
			    k=0;		//after removal, the current one should be the oth one, and always start from 1th.
			}
			else if (dcedMast.subMast<dced.subMastSize){

				dced.dcedList.remove(k);
				k--;
			}
			k++;
		}  //end of while

		((DcedInfo)((POrder)pOrderList.get(index)).decends.get(i)).dcedList=dced.dcedList;
	}  //end of for
    }

    //after get the order information, you need to get the sub-mast for the sub-tree
    private void computeMast(POrder pOrder, int index)
    {
    	/* We only compute the ones haven't been computed.
    	 */


	if (pOrder.mastSize==0){

            int comOrderMaxSize = 0;
	    int []orderPairList = new int[pOrder.decends.size()/2];	//order pair size should be half of the order list size

	    for(int i=0;i<orderPairList.length;i++)
		orderPairList[i]=-1;		//it is impossible for the order to be a minus number

	    //first get all the orderPair for this pOrder
	    int fillInNum = 0;
	    boolean alThere = false;

	    for(int i=0;i<pOrder.decends.size(); i++){
		int order = ((DcedInfo)pOrder.decends.get(i)).dOrder;
		alThere = false;
		for(int j=0;j<fillInNum;j++){
			if ((order == orderPairList[j])||((order +orderPairList[j])== orderMax)){
				alThere = true;
				break;
			}
		}
		if (alThere){
			continue;
		}
		else {
			orderPairList[fillInNum]=order;
			fillInNum++;
		}
	    }

	    //get the mast size for all the orders, we don't consider mast set now, compute it later.
	    for(int opl=0;opl<orderPairList.length;opl++){
		int order = orderPairList[opl];
		int index1 = -1;
		int index2 = -1;

		//find the position of orders of the specific order pair
		for(int i=0;i<pOrder.decends.size();i++){
			int tempOrder = ((DcedInfo)pOrder.decends.get(i)).dOrder;
			if ((tempOrder == order)||((tempOrder + order) == orderMax)){
				if (index1 == -1)
					index1 = i;
				else {
					index2 = i;
					break;
				}
			}
		}

	    	DcedInfo dced1 = new DcedInfo();
		dced1 = (DcedInfo)pOrder.decends.get(index1);
		DcedInfo dced2 = new DcedInfo();
		dced2 = (DcedInfo)pOrder.decends.get(index2);

		//deal with order1, we first check on the dcedlist level, we didn't modify the porderlist
		//remove the dcedMast with a smaller size
		int k1=0;
		while(k1<dced1.dcedList.size()){
			DcedMast dcedMast1 = new DcedMast();
			dcedMast1 = (DcedMast)dced1.dcedList.get(k1);

			if (dcedMast1.subMast ==0){
				int i1 = belongMainLcat(dcedMast1.lcat);
				if (i1>=0){
					POrder subPOrder = new POrder();
					subPOrder = (POrder)pOrderList.get(i1);
					if (subPOrder.mastSize==0)
						computeMast(subPOrder, i1);
					dcedMast1.subMast = ((POrder)pOrderList.get(i1)).mastSize;
				}
				else dcedMast1.subMast = 2;

				((DcedMast)dced1.dcedList.get(k1)).subMast = dcedMast1.subMast;

			}  //end of if

			if (dcedMast1.subMast>dced1.subMastSize){

				//remove the first k1-1 elements
				for(int m=0;m<k1;m++)
					dced1.dcedList.remove(0);
				dced1.subMastSize = dcedMast1.subMast;

				k1=0;
			}
			else if (dcedMast1.subMast < dced1.subMastSize){

				//remove itself
				dced1.dcedList.remove(k1);
				k1--;
			}
			k1++;

		} //end of while

		//deal with order2
		int k2=0;
		while(k2<dced2.dcedList.size()){
			DcedMast dcedMast2 = new DcedMast();
			dcedMast2 = (DcedMast)dced2.dcedList.get(k2);

			if (dcedMast2.subMast ==0){
				int i2 = belongMainLcat(dcedMast2.lcat);
				if (i2>=0){
					POrder subPOrder = new POrder();
					subPOrder = (POrder)pOrderList.get(i2);
					if (subPOrder.mastSize==0)
						computeMast(subPOrder, i2);
					dcedMast2.subMast = ((POrder)pOrderList.get(i2)).mastSize;
				}
				else
					dcedMast2.subMast = 2;

				((DcedMast)dced2.dcedList.get(k2)).subMast = dcedMast2.subMast;
			}  //end of if

			if (dcedMast2.subMast>dced2.subMastSize){

				//remove the first k1-1 elements
				for(int m=0;m<k2;m++)
					dced2.dcedList.remove(0);
				dced2.subMastSize = dcedMast2.subMast;

				k2=0;
			}
			else if (dcedMast2.subMast < dced2.subMastSize){

				//remove itself
				dced2.dcedList.remove(k2);
				k2--;
			}
			k2++;

		} //end of while

		//We get the sub mast size for each order pair
		int tempComSize = dced1.subMastSize + dced2.subMastSize;

		if (tempComSize > comOrderMaxSize){

			//keep the orders in the pOrder, and remove the former ones
			comOrderMaxSize = tempComSize;
			((POrder)pOrderList.get(index)).decends.set(index1, dced1);
			((POrder)pOrderList.get(index)).decends.set(index2, dced2);

			for(int former = 0;former < opl;former++){
				if (orderPairList[former]==-1)
					continue;
				else {
					int remIndex = 0;
					int remNum = 0;
					while(remIndex<pOrder.decends.size()){
						int remOrder = ((DcedInfo)pOrder.decends.get(remIndex)).dOrder;
						if  ((remOrder == orderPairList[former])||((remOrder +orderPairList[former])==orderMax)){
							((POrder)pOrderList.get(index)).decends.remove(remIndex);
							remNum++;
							remIndex--;
							//only two orders can be removed once
							if (remNum==2){
								orderPairList[former]=-1;
								break;
							}
						}

						remIndex++;
					}
				}
			}

		}
		else if (tempComSize < comOrderMaxSize){
				((POrder)pOrderList.get(index)).decends.remove(index1);
				if (index1<index2)
					((POrder)pOrderList.get(index)).decends.remove(index2-1);
				else ((POrder)pOrderList.get(index)).decends.remove(index2);
				orderPairList[opl]=-1;
		}
		else if (tempComSize == comOrderMaxSize){
			((POrder)pOrderList.get(index)).decends.set(index1, dced1);
			((POrder)pOrderList.get(index)).decends.set(index2, dced2);
		}
	    }

	    //put it back to pOrderList
	    ((POrder)pOrderList.get(index)).mastSize = comOrderMaxSize;
	    ((POrder)pOrderList.get(index)).orderPair = new int[orderPairList.length];

	    for(int i=0;i<orderPairList.length;i++)
		((POrder)pOrderList.get(index)).orderPair[i]=orderPairList[i];
	}

    }

    public void getPOrderMastSet(int index)
    {
        POrder pOrder = new POrder();
	pOrder = (POrder)pOrderList.get(index);
	int []orderPairList = new int[pOrder.orderPair.length];

	for(int i=0;i<pOrder.orderPair.length;i++)
		orderPairList[i]=pOrder.orderPair[i];

	//compute the mast set one order-pair by one order-pair
	for(int opl=0;opl<orderPairList.length;opl++){
		int order = orderPairList[opl];
		int index1 = -1;
		int index2 = -1;

		if (order == -1)
			continue;

		//find the position of orders of the specific order pair
		for(int i=0;i<pOrder.decends.size();i++){
			int tempOrder = ((DcedInfo)pOrder.decends.get(i)).dOrder;
			if ((tempOrder == order)||((tempOrder + order) == orderMax)){
				if (index1 == -1)
					index1 = i;
				else {
					index2 = i;
					break;
				}
			}
		}

		DcedInfo dced1 = new DcedInfo();
		dced1 = (DcedInfo)pOrder.decends.get(index1);
		DcedInfo dced2 = new DcedInfo();
		dced2 = (DcedInfo)pOrder.decends.get(index2);

		//compute its own mast set, each dcedMast has its own mast set{{,,,}, {,,,}, ... ,{,,,}}

		Iterator it1 = dced1.dcedList.iterator();
		Iterator it2 = dced2.dcedList.iterator();
		Set keepDced = new HashSet();
		while(it2.hasNext()){
			DcedMast dcedMast = new DcedMast();
			dcedMast = (DcedMast)it2.next();

			//when you push lcat the basic two leaf already in leafset
			//others are not there
			//put All the mast set of All the dcedMast into keepDced together
			if (dcedMast.mastSet.size()==1){
				int i2 = belongMainLcat(dcedMast.lcat);
				if (i2>=0){
					if (((POrder)pOrderList.get(i2)).mastSet.size()==0)
						getPOrderMastSet(i2);
					dcedMast.mastSet.clear();
					dcedMast.mastSet.addAll(((POrder)pOrderList.get(i2)).mastSet);
				}
			}
			keepDced.addAll(dcedMast.mastSet);
		}

		while(it1.hasNext()){
			DcedMast dcedMast1 = new DcedMast();
			dcedMast1 = (DcedMast)it1.next();

			if (dcedMast1.mastSet.size()==1){
				int i3 = belongMainLcat(dcedMast1.lcat);
				if (i3>=0){
					if (((POrder)pOrderList.get(i3)).mastSet.size()==0)
						getPOrderMastSet(i3);
					dcedMast1.mastSet.clear();
					dcedMast1.mastSet.addAll(((POrder)pOrderList.get(i3)).mastSet);
				}
			}

			Iterator it3 = keepDced.iterator();
			while(it3.hasNext()){
				Set tempSet1 = new HashSet();
				tempSet1.addAll((Set)it3.next());
				Iterator it4=dcedMast1.mastSet.iterator();
				while(it4.hasNext()){
					Set tempSet2 = new HashSet();
					tempSet2.addAll(tempSet1);
					tempSet2.addAll((Set)it4.next());
					((POrder)pOrderList.get(index)).mastSet.add(tempSet2);
				}
			}
		}
	}
    }

    // try to mast for each pOrder in the pOrderList
    public void finalMast()
    {
        int i=0;

        POrder pOrder = new POrder();

	LinkedList maxIndexList = new LinkedList();

	//put all of the biggest mast pOrder position in this list
	//so that some of the pOrder don't have to be computed
	int oldMastSize = mastSize;
	while(i<pOrderList.size()){
		int currentSize = ((POrder)pOrderList.get(i)).mastSize+1;

		if (currentSize > mastSize){
			mastSize = currentSize;
			maxIndexList.clear();
			maxIndexList.add(Integer.toString(i));
		}
		else if (currentSize == mastSize){
			maxIndexList.add(Integer.toString(i));
		}
		i++;
	}

	if (oldMastSize < mastSize)
		mastSet.clear();

	for(int j=0;j<maxIndexList.size();j++){
		int index = Integer.parseInt((String)maxIndexList.get(j));
		getPOrderMastSet(index);
		Set partialSet = new HashSet();
		partialSet = (Set)((POrder)pOrderList.get(index)).mastSet;
		Iterator it = partialSet.iterator();
		while(it.hasNext()){
			((Set)it.next()).add(rootLeaf);
		}
		mastSet.addAll(partialSet);
	}

        if (mastSize<=3){
            mastSize =3;
            for(i=0;i<lcats.length;i++){
                Lcat lt = new Lcat();
                lt = lcats[i];
                Set temp;
                if (lt.lessbig){
                   temp = new HashSet();
                   temp.add(leafArray[lt.leaf1]);
                   temp.add(leafArray[lt.leaf2]);
                   temp.add(rootLeaf);
                   mastSet.add(temp);
                }
            }
        }

    }

    private void getExistedSubMast()
    {
        for(int i=0;i<pOrderList.size();i++){
		POrder pOrder = new POrder();
		pOrder = (POrder)pOrderList.get(i);
		for(int j=0;j<pOrder.decends.size();j++){
			DcedInfo dcedInfo = new DcedInfo();
			dcedInfo = (DcedInfo)pOrder.decends.get(j);
			for(int k=0;k<dcedInfo.dcedList.size();k++){
				DcedMast dcedMast = new DcedMast();
				dcedMast = (DcedMast)dcedInfo.dcedList.get(k);
				if (dcedMast.subMast == 0){
					int index=belongMainLcat(dcedMast.lcat);
					if (index<0)
						((DcedMast)((DcedInfo)((POrder)pOrderList.get(i)).decends.get(j)).dcedList.get(k)).subMast = 2;
				}
			}
		}
	}
    }

	private void prune_tree(TreeNode node, Set commonSet)
	{

		if (!node.isLeaf()){
			sideStack.push(LEFTC);
			prune_tree(node.getLeftNode(), commonSet);

			if (!node.getRightNode().isLeaf())
				sideStack.push(RIGHTC);
			prune_tree(node.getRightNode(), commonSet);
		}
		else {
				if (commonSet.contains(node.getGeneName())){
					//if it a left child leaf
					if (node.getLeafSet().equals(node.getParent().getLeftNode().getLeafSet())){
						TreeNode leftN = new TreeNode();
						leftN.makeLeaf(node);
						nodeStack.push(leftN);
					}
					else {
							TreeNode rightN = new TreeNode();
							rightN.makeLeaf(node);

							TreeNode interN = new TreeNode();

							if (!sideStack.peek().equals(PRUNEC)){
								TreeNode leftN = new TreeNode();
								leftN = (TreeNode)nodeStack.pop();
								sideStack.pop();

								Tree.parentChild(interN, leftN, "left");
								Tree.parentChild(interN, rightN, "right");
							}
							else {
									interN = rightN;
									sideStack.pop();
							}

							if (!sideStack.empty()){
								while (sideStack.peek().equals(RIGHTC)||
										sideStack.peek().equals(PRUNEC)){
									if (sideStack.peek().equals(PRUNEC)){
										sideStack.pop();
										//sideStack.pop();
									}
									else {

										sideStack.pop();

										if (sideStack.peek().equals(PRUNEC)){

											sideStack.pop();
										}
										else {
												sideStack.pop();

												TreeNode leftN = new TreeNode();
												leftN = (TreeNode)nodeStack.pop();

												rightN = new TreeNode();
												rightN = interN;

												interN = new TreeNode();
												Tree.parentChild(interN, leftN, "left");
												Tree.parentChild(interN, rightN, "right");
										}
									}

									//reach the root
									if (sideStack.empty())
										break;

								}  //end of while
							}

							nodeStack.push(interN);

					}
				}
				//else if the leaf should be pruned
				else {
						if (node.getLeafSet().equals(node.getParent().getLeftNode().getLeafSet())){
							sideStack.pop();
							sideStack.push(PRUNEC);
						}
						else {
								//if right leaf was pruned
								if (sideStack.peek().equals(LEFTC)){
									//lift the left side if it is not pruned
									sideStack.pop();

									if (!sideStack.empty()){
										if (sideStack.peek().equals(RIGHTC)){
											TreeNode interN = new TreeNode();
											TreeNode rightN = new TreeNode();
											TreeNode leftN = new TreeNode();

											rightN = (TreeNode)nodeStack.pop();
											sideStack.pop();

											if (sideStack.peek().equals(LEFTC)){
												leftN = (TreeNode)nodeStack.pop();
												sideStack.pop();

												Tree.parentChild(interN, leftN, "left");
												Tree.parentChild(interN, rightN, "right");
											}
											else {
													sideStack.pop();
													interN = rightN;
											}

											if (!sideStack.empty()){
												while(sideStack.peek().equals(RIGHTC)||
														sideStack.peek().equals(PRUNEC)){
													if (sideStack.peek().equals(PRUNEC))
														sideStack.pop();
													else {

															sideStack.pop();

															if (sideStack.peek().equals(PRUNEC)){

																sideStack.pop();
															}
															else {
																sideStack.pop();

																leftN = new TreeNode();
																leftN = (TreeNode)nodeStack.pop();

																rightN = new TreeNode();
																rightN = interN;

																interN = new TreeNode();
																Tree.parentChild(interN, leftN, "left");
																Tree.parentChild(interN, rightN, "right");
															}
													}
													//reach the root
													if (sideStack.empty())
														break;
												}  //end of while

											}
											nodeStack.push(interN);
										}
									}
								}

								//if the left leaf is also pruned
								else {
										boolean loop = true;

										while (loop){
											if (sideStack.peek().equals(PRUNEC)){
											//both of left and right leaves are pruned
												sideStack.pop();
												if (sideStack.peek().equals(RIGHTC)){
													sideStack.pop();
													if (sideStack.peek().equals(PRUNEC))
														continue;
													else if (sideStack.peek().equals(LEFTC)){
															//lift and merge
															sideStack.pop();

															TreeNode interN = new TreeNode();
															interN = (TreeNode)nodeStack.pop();

															if (!sideStack.empty()){
																while(sideStack.peek().equals(RIGHTC)||
																	sideStack.peek().equals(PRUNEC)){
																	if (sideStack.peek().equals(PRUNEC))
																		sideStack.pop();
																	else {
																			sideStack.pop();

																			if (sideStack.peek().equals(PRUNEC)){

																				sideStack.pop();
																			}
																			else {
																					sideStack.pop();

																					TreeNode leftN = new TreeNode();
																					leftN = (TreeNode)nodeStack.pop();

																					TreeNode rightN = new TreeNode();
																					rightN = interN;

																					interN = new TreeNode();
																					Tree.parentChild(interN, leftN, "left");
																					Tree.parentChild(interN, rightN, "right");
																			}
																	}

																	if (sideStack.empty())
																		break;

																} //end of while
																nodeStack.push(interN);

																break;
															}

															nodeStack.push(interN);
														}
												}
												else if (sideStack.peek().equals(LEFTC)){
															sideStack.pop();
															sideStack.push(PRUNEC);
															break;
												}
											}
									} //end of while
								}	//end of else

						}
				}
		}
	}

	private Tree reBuildTree(Tree inTree, Set commonSet)
	{
		//first, get the left most node of the inTree
		prune_tree(inTree.getRoot(), commonSet);

		Tree t = new Tree();
		t.setRoot((TreeNode)nodeStack.pop());
		t.setTreePos();

		nodeStack.clear();
		sideStack.clear();

		return t;
	}

	private int prune_leaf(Tree []treeGroup)
	{
		Set commonSet = new HashSet();
		Set s = new HashSet();
		commonSet.addAll(treeGroup[0].getRoot().getLeafSet());
		s.addAll(commonSet);

		//first get common leaf set of the tree group
		for(int i=1;i<treeGroup.length;i++){
			Set tempSet = new HashSet();
			tempSet = treeGroup[i].getRoot().getLeafSet();
			Iterator it = s.iterator();
			while(it.hasNext()){
				String leaf = new String();
				leaf = (String)it.next();
				if(!tempSet.contains(leaf))
					commonSet.remove(leaf);
			}
		}

		Set prunedSet = new HashSet();
		for(int i=0;i<treeGroup.length;i++){
			Set lSet = new HashSet();
			lSet = treeGroup[i].getRoot().getLeafSet();
			Iterator it = lSet.iterator();
			while(it.hasNext()){
				String str = new String();
				str = (String)it.next();
				if ((!commonSet.contains(str))&&(!prunedSet.contains(str))){
					prunedSet.add(str);
				}
			}
		}

		treeDistance = prunedSet.size() + commonSet.size();
		totalLeaves = prunedSet.size() + commonSet.size();

		if (commonSet.size()==0)
			return -1;
		else {
				for(int i=0;i<treeGroup.length;i++){
					if (!treeGroup[i].getRoot().getLeafSet().equals(commonSet)){
						Tree t = new Tree();
						t = reBuildTree(treeGroup[i], commonSet);
						treeGroup[i] = t;
					}
				}

				//for debug
				//for(int i=0;i<treeGroup.length;i++){
				//	treeGroup[i].displayTree();
				//}
				//-------------
		}

		return 0;
	}

    public void getUMAST()
    {
    	int commonResult=2;

		/* Check leaf set, if they are not the same, prune the inconsistent
	 	 * leaves.
	 	 */

		//new ----
		//One question: When the tree Group changed, the actual tree cannot be pruned.


		if (!checkLeafSet(treeGroup)){
			commonResult= prune_leaf(treeGroup);
			leafSet = treeGroup[0].getRoot().getLeafSet();
			leafSetSize = leafSet.size();
			Iterator it = leafSet.iterator();
			int i=0;
			leafArray = new String[leafSet.size()];

			while(it.hasNext()){
				leafArray[i] = new String();
				leafArray[i] = (String)it.next();
				i++;
			}
		}
		else {
				totalLeaves = treeGroup[0].getRoot().getLeafSet().size();
				treeDistance = treeGroup[0].getRoot().getLeafSet().size();
		}
		//--------

		if (commonResult!=-1){
			long t1 = System.currentTimeMillis();

			if (checkLeafSet(treeGroup)){
 	    		for(int i=0;i<leafArray.length;i++){
 	        		//re-root all the trees in the group, the same root

 	        		rootLeaf=leafArray[i];
 	        		//modified label -> name
 	        		String rootLabel = treeGroup[0].getNameIndex(rootLeaf);


					for(int j=0;j<reRootGroup.length;j++){
 	    				reRootGroup[j]=new Tree();
 	    				reRootGroup[j]=treeGroup[j].reRoot(rootLeaf, rootLabel);
 	    			}
 	    			rootLeafPos = i;
 	    			createLca();

 	    			//for debug---------
 	    			//displayLcat();
 	    			//------------------
 	    			pOrderList = new LinkedList();
 	    			partialOrder(lcats);

 	    			for(int m=0;m<pOrderList.size();m++){
 	    				POrder pOrder = new POrder();
 	    				pOrder = (POrder)pOrderList.get(m);
 	    				computeMast(pOrder, m);
 	    			}

 	    			for(int m=0;m<pOrderList.size();m++){
 	    				POrder pOrder = new POrder();
 	    				pOrder = (POrder)pOrderList.get(m);
 	    			}


 	    			finalMast();
 	    		}

			}
			else {
					//System.out.println("Trees cannot be compared, since they have different leaf set!");
			}
			long t2 = System.currentTimeMillis() - t1;
			//System.out.println("Time spent: "+(double)t2/1000);
		}
		//System.out.println("Finish get Gordon UMASt");
		if (!mastSet.isEmpty())
			treeDistance = treeDistance - ((Set)mastSet.iterator().next()).size();
    }

    public double treeDis()
    {
    	return treeDistance;
    }

    public int totalLeaves()
    {
		return totalLeaves;
	}

	public String getDistanceText()
	{
		//distanceValue.setText(Double.toString(distance));
		String str = new String();
		NumberFormat formatter = new DecimalFormat("####.###");
		str = formatter.format(treeDistance);
		str = str.concat(" / ");
		str = str.concat(Integer.toString(totalLeaves));
		/*
		double percent = treeDistance/totalLeaves;
		str = str.concat(" ... ");
		str = str.concat(formatter.format(percent*100));
		str = str.concat("%");
		*/
		return str;
   	}

	public String getSimIndexText()
	{
	    String str = new String();
	    double dissimilar = (double)((treeDistance/totalLeaves)*100);
	    double similar = 100 - dissimilar;
	    double percent = similar/dissimilar;

	    if (treeDistance == totalLeaves)
	    	return "0 / 100";

	    if (treeDistance == 0)
	    	return "100 / 0";

	    NumberFormat formatter = new DecimalFormat("####.##");
		str = formatter.format(similar);
	    str = str.concat(" / ");
	    str = str.concat(new String(formatter.format(dissimilar)));
	    str = str.concat(" ...... ");
		str = str.concat(new String(formatter.format(percent)));

	    return str;
	}

    public static void main(String args[])
    {
        String importFile=new String();
        importFile=args[0];

        try{
                BufferedReader in = new BufferedReader(
                	new InputStreamReader(
                		new FileInputStream(importFile)));

                // Here should be modified, since maybe the tree topology is very
                // large and with multiple lines.
                // should be changed in tree.java

                char ch;
                int treeNum=0;
                int i=0,j=0;

                treeNum=Integer.parseInt(in.readLine());

                String t[]=new String[treeNum];
                LinkedList l = new LinkedList();


                i=0;
                int index = 0;

                String treeLabel = new String();
                boolean start = false;

                //first read labels
                String sep1 = new String(",");
                String sep2 = new String("-");

                while (i<treeNum){
                	String str = new String();
                	str = in.readLine();
                	//System.out.println(str);
                	//System.out.println("iter: "+i+","+treeNum);
                	//# is the seperator of different trees
        			if (str.equals("#")&&(!start)){
        				treeLabel = new String();
        				start = true;
        				continue;
        			}
        			if (str.equals("#")&&(start)){
        				//finish one label description of one tree
        			    //System.out.println("Parse"+treeLabel);
        			    StringTokenizer st = new StringTokenizer(treeLabel, ",");
        				Set s = new HashSet();
        				while(st.hasMoreTokens()){
        					String p = st.nextToken();
        					StringTokenizer st2 = new StringTokenizer(p, "-");
        					String q = st2.nextToken();
        					String r = st2.nextToken();
        					LabelName ln = new LabelName();
        					ln.num = q;
        					ln.name = r;
        					//System.out.println(ln.num+","+ln.name);

        					s.add(ln);
        				}
        				l.add(s);

        				treeLabel = new String();
        				i++;
        				start = false;
        			}
        			if (start){
        				treeLabel=treeLabel.concat(str);
        			}
        		}

        		//then read tree topologies

        		i=0;
        		//System.out.println("read tree");
        		while (i<treeNum){
                    ch=(char)in.read();
		            t[i]=new String();
                    while(ch!=';'){
                    	if ((ch!='\0')&&(ch!='\n')&&(ch!='\t')&&(ch!=' '))
                    	    t[i]=t[i]+ch;
                    	ch=(char)in.read();


                    }
                    t[i]=t[i]+ch;
                    i++;
                }


                in.close();

				for(i=0;i<l.size();i++){
				    Set member = new HashSet();
				    member = (Set)l.get(i);

				    Iterator it1 = member.iterator();
				    while(it1.hasNext()){
				    	LabelName ll = new LabelName();
				    	ll = (LabelName)it1.next();
				    	//System.out.print(ll.num+", "+ll.name+"  ");
				    }

				    System.out.println();
				}


                Tree tGroup[]=new Tree[treeNum];
				Set set=new HashSet();

				//for (i=0;i<treeNum;i++){
		    	//	System.out.println(t[i]);
		    	//	tGroup[i]=new Tree(t[i], (Set)(l.get(i)) , "");
				//	tGroup[i].displayTree();
				//}


		GordonMast gMast = new GordonMast(tGroup);
		gMast.getUMAST();

	   } catch(Exception e){
	   	System.out.println("Exception: "+e.getMessage());
                e.printStackTrace();
           }
    }


}
