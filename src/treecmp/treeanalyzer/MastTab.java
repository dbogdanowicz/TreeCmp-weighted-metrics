package treecmp.treeanalyzer;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.awt.List;
import java.text.NumberFormat;
import java.text.DecimalFormat;

public class MastTab{
    private Tree treeU;		// Compared tree U
    private Tree treeT;		// Compared tree T
    private Stack mastTab[][];	// 2-dim table to store multiple mast of subtrees
    private final int SUBMAST = 8;	//# of mast of subtrees that we have to
    					// consider during the computation
    private int uLeng = 0;		// # of total nodes in tree U
    private int tLeng = 0;		// # of total nodes in tree T
    private Stack treeStk=new Stack();	// to store multiple mast tree
    final static int ROOT_ = 0;
    final static int LEFT_ = 1;
    final static int RIGHT_ = 2;
    final static String RIGHT_CH = "RIGHT";
    final static String LEFT_CH = "LEFT";
    private boolean tabFilled=false;
    private int initPos=0;
    private boolean comp2 = true;	//compare two trees, only need to record the maximum
    					//agreement subtree in the intermediate steps
    					//inluding fillTab and umast, but for the optimal
    					//answer of tree group with size > 2, you have to
    					//record all the trees created during intermediate
    					//steps with has the size > 2.
    					//The initial valuse set all compare as two trees comparison.
    //for debug
    private int treeNum=0;
    private final static boolean GROUPCOMP = false;	//for group comparison, comp2 is false
    private final static boolean TWOCOMP = true;	//for two tree comparison, comp2 is true
    private boolean debugTest=true;
    private Set[]prunedSet = new HashSet[2];
    private int totalLeaves = 0;
    private double treeDistance = 0;

    public MastTab()
    {
    	mastTab = null;
    	treeU = new Tree();
    	treeT = new Tree();
    }

    public MastTab(Tree inTreeU, Tree inTreeT)
    {
        treeU = inTreeU;
        treeT = inTreeT;
        intTab();
    }

    public MastTab(TreeNode rootU, TreeNode rootT)
    {
        treeU = new Tree();
        treeT = new Tree();
        treeU.setRoot(rootU);
        treeT.setRoot(rootT);
        intTab();
    }

    /* Initialize the mast table
     */

    private void intTab()
    {
        TreeNode rootU = treeU.getRoot();
        TreeNode rootT = treeT.getRoot();

        uLeng = rootU.getLeafSize()*2 - 1;
        tLeng = rootT.getLeafSize()*2 - 1;

        mastTab = new Stack[uLeng][tLeng];

        // Here we could do optimize later

        for(int i = 0; i<uLeng; i++){
            for(int j = 0;j<tLeng; j++){
            	mastTab[i][j] = new Stack();
            }
        }

     }

     /* fill in the table
      * @ param: void
      * @ return: void
      */

     public void fillTab()
     {
         TreeNode curU = new TreeNode();	//current node of tree U
         TreeNode curT = new TreeNode();	//current node of tree T
         TreeNode firstU = new TreeNode();	//Left most node of tree U
         TreeNode firstT = new TreeNode();	//Left most node of tree T
         Stack mastSub[] = new Stack[SUBMAST];
         boolean loop = true; 			// indicate finish one loop or not
         TreeNode mastElm = new TreeNode();	// an mast element which is going to be put in stack
		  Stack maxMastStk = new Stack();

         int uPos = 0;
         int tPos = 0;

         firstU = treeU.getRoot();
         firstT = treeT.getRoot();

         /* use postfix order to do computing */
         while(firstU.getLeftNode()!=null)
             firstU = firstU.getLeftNode();

         while(firstT.getLeftNode()!=null)
             firstT = firstT.getLeftNode();

         curU = firstU;
         curT = firstT;


         for (int i=0;i<SUBMAST;i++)
         	mastSub[i]=new Stack();

         /* Table lists in this way: a, b, ab, c, abc, d, e, de, abcde...*/

         while (curU!=null){
         	tPos = 0;
         	loop = true;

			while (loop){

          	    //may move it outside the 2nd loop

         	    if (curU.isLeaf()){
             		if (contain(curU, curT)){
             		    mastElm = new TreeNode();
             		    mastElm.makeLeaf(curU);
             		    mastTab[uPos][tPos].push(mastElm);
             		}
             	}
         	    else if (curT.isLeaf()){
                 	      if (contain(curT, curU)){
                 	      	  mastElm = new TreeNode();
                 	      	  mastElm.makeLeaf(curT);
                 	      	  mastTab[uPos][tPos].push(mastElm);
                 	      }
                         } else {
              	    	    	for(int i=0;i<SUBMAST;i++)
              	    	    		mastSub[i] = new Stack();

              	    	    	int uRightSize = curU.getRightNode().getTotSize();
              	    	    	int tRightSize = curT.getRightNode().getTotSize();

              	    	    	/* The max sub tree should be the maximum of the following six
              	    	    	 * items: #(Tb, Ux)+#(Tc, Uy), #(Tb, Uy)+#(Tc, Ux), #(Ta, Ux),
              	    	    	 * #(Ta, Uy), #(Tb, Uw), #(Tc, Uw)
              	    	    	 */

              	    	    	//------------------------------------------------------

              	    	    	mastSub[0] = getMast(uPos - uRightSize-1, tPos - tRightSize-1);
              	      	    	mastSub[1] = getMast(uPos - 1, tPos - 1);
              	    	    	mastSub[2] = getMast(uPos - uRightSize-1, tPos - 1);
              	    	    	mastSub[3] = getMast(uPos - 1, tPos - tRightSize-1);
              	    	    	mastSub[4] = getMast(uPos - uRightSize-1, tPos);
              	    	    	mastSub[5] = getMast(uPos - 1, tPos);
              	    	    	mastSub[6] = getMast(uPos, tPos - tRightSize-1);
              	    	    	mastSub[7] = getMast(uPos, tPos - 1);

              	    	        maxMastStk = new Stack();

								maxMastStk.addAll(max(mastSub));
								mastTab[uPos][tPos] = maxMastStk;

              	    	    }

              	    //test the intermediate result

              	    tPos++;
              	    if (curT.isRoot())
              	    	loop = false;
              	    curT = getNext(curT);

             	}// end of 2nd while

             	uPos++;
             	if (curU.isRoot())
             	    curU = null;
             	else {

             		curU = getNext(curU);
             	}
          }
          treeStk=push_stk(treeStk, resMast());
		  treeStk=filter_stk(treeStk);

          tabFilled=true;

     } // end of fill table

     /* simply put all the elements in inStk to container
      * no matter repeat or bigger or smaller.
      */

     private Stack copy_stk(Stack container, Stack inStk)
     {

         if (!inStk.empty())
	    for(int i=0;i<inStk.size();i++)
	    	container.addElement((TreeNode)inStk.elementAt(i));

	 return container;
     }

     private Stack filter_stk(Stack container)
	 {
		int i=0;
		while(i<container.size()){
			if (((TreeNode)container.elementAt(i)).getLeafSize()<3)
				container.remove(i);
			else i++;
		}

		return container;
	 }

     /* put the mast tree into stack, suppose the input stack has elements
      * with the same size. If the input stk has elemnts with size greater
      * than the container's elements, then clear the containter and assign
      * all the elements in input stck to container; if they are equal, assign
      * the unrepeated elements into container; if less than, then do nothing.
      */

     private Stack push_stk(Stack container, Stack resMastStk)
     {
          TreeNode temp1;
          TreeNode temp2;
          TreeNode temp3;
          TreeNode temp4;
          boolean eq=false;

		  if (!resMastStk.isEmpty())
              if (!container.isEmpty()){
                  temp1=(TreeNode)container.elementAt(0);
                  temp2=(TreeNode)resMastStk.elementAt(0);

                  if (temp2.getLeafSize()>temp1.getLeafSize()){
                      // input tree is the largest, so all the stored
              	      container.clear();
              	      // trees need pop out
              	      container.addAll(resMastStk);
                  }
                  else if (temp2.getLeafSize()==temp1.getLeafSize())
              		    for(int i=0;i<resMastStk.size();i++){
              		        eq=false;
              		        temp3=(TreeNode)resMastStk.elementAt(i);
              		        for(int j=0;j<container.size();j++){
              		    	    temp4=(TreeNode)container.elementAt(j);
              		    	    if (temp4.getLeafSet().equals(temp3.getLeafSet())){
              		    	        eq=true;
              		    	        break;
              		    	    }
              		        }
              		        if (!eq)
              		    	    container.push((TreeNode)resMastStk.elementAt(i));
              		     }
              }
              else {
          	    	container.addAll(resMastStk);
          	  }

          return container;
     }

	 /* push element into stack, only choose the bigger and not repeat one.
	  */

     private Stack push_stkBig(Stack container, Stack resMastStk)
     {
          TreeNode temp3;
          TreeNode temp4;
          boolean insert=true;

          if (!resMastStk.isEmpty())
              if (!container.isEmpty()){

                  for(int i=0;i<resMastStk.size();i++){
              		insert=true;
              		temp3=(TreeNode)resMastStk.elementAt(i);
              		int j=0;
              		while(j<container.size()){
              			temp4=(TreeNode)container.elementAt(j);
              		    	if (temp4.getLeafSet().equals(temp3.getLeafSet())){
              		    	 	insert=false;
              		    	        break;
              		    	}
							else if (temp4.getLeafSet().containsAll(temp3.getLeafSet())){
										insert=false;
										break;
							} else if (temp3.getLeafSet().containsAll(temp4.getLeafSet())){
										container.remove(j);
							}
							else j++;
              		}  //end of while
              		if (insert)
              		    	container.push((TreeNode)resMastStk.elementAt(i));
              	  }
              }
              else {
          	    container.addAll(resMastStk);
          	    }

          return container;
     }
	 private Stack push_stkBig(Stack container, TreeNode tree)
     {
         TreeNode temp1;
         boolean insert=true;

         if (container.isEmpty())
         	container.push(tree);
         else {
         	int j=0;
         	while(j<container.size()){
               	    temp1=(TreeNode)container.elementAt(j);
              	    if (temp1.getLeafSet().equals(tree.getLeafSet())){
              		insert=false;
              		break;
              	    }
					else if (temp1.getLeafSet().containsAll(tree.getLeafSet())){
								insert=false;
								break;
					} else if (tree.getLeafSet().containsAll(temp1.getLeafSet())){
								container.remove(j);
					} else j++;
              	}
                if (insert)
              	    container.push(tree);
          }

          return container;
      }
	 /* push all the unrepeated elements into the container, no
      * matter the size is bigger or smaller.
      */

     private Stack push_stkGrp(Stack container, Stack resMastStk)
     {
          TreeNode temp3;
          TreeNode temp4;
          boolean insert=true;

          if (!resMastStk.isEmpty())
              if (!container.isEmpty()){

                  for(int i=0;i<resMastStk.size();i++){
              		insert=true;
              		temp3=(TreeNode)resMastStk.elementAt(i);
              		int j=0;
              		while(j<container.size()){
              			temp4=(TreeNode)container.elementAt(j);
              		    	if (temp4.getLeafSet().equals(temp3.getLeafSet())){
              		    	 	insert=false;
              		    	        break;
              		    	}
              		    	else j++;
              		}  //end of while
              		if (insert)
              		    	container.push((TreeNode)resMastStk.elementAt(i));
              	  }
              }
              else {
          	    container.addAll(resMastStk);
          	    }

          return container;
     }

     private Stack push_stkGrp(Stack container, TreeNode tree)
     {
         TreeNode temp1;
         boolean insert=true;

         if (container.isEmpty())
         	container.push(tree);
         else {
         	int j=0;
         	while(j<container.size()){
               	    temp1=(TreeNode)container.elementAt(j);
              	    if (temp1.getLeafSet().equals(tree.getLeafSet())){
              		insert=false;
              		break;
              	    }
              	    else j++;
              	}
                if (insert)
              	    container.push(tree);
          }

          return container;
      }
     /* push a single tree node into container, suppose the container's
      * elements have the same number of size, check greater and repeat
      */

     private Stack push_stk(Stack container, TreeNode tree)
     {
         TreeNode temp1;
         boolean eq=false;

         if (container.isEmpty())
         	container.push(tree);
         else {
         	temp1=(TreeNode)container.elementAt(0);

         	if (temp1.getLeafSize()<tree.getLeafSize()){
         		container.clear();
         		container.push(tree);
         	}
         	else if (temp1.getLeafSize()== tree.getLeafSize()){
         	  	for(int j=0;j<container.size();j++){
               		    temp1=(TreeNode)container.elementAt(j);
              		    if (temp1.getLeafSet().equals(tree.getLeafSet())){
              		    	eq=true;
              		        break;
              		    }
              	  	}
                  	if (!eq)
              			container.push(tree);
              	    }
          }

          return container;
      }


     /* get the mast tree stored in the stack
      * here we only pop out one tree, later can be improved
      */

     private TreeNode pop_stk()
     {
        if (!treeStk.isEmpty())
             return (TreeNode)treeStk.elementAt(0);

        return null;
     }

     /* print all the umast trees stored in the stack
      */

     public void print_stk()
     {
         TreeNode node;
         Tree t=new Tree();
         System.out.println("Umast stack size: "+treeStk.size());
         TreeNode temp=new TreeNode();

      	 if (!treeStk.empty()){
      	     node=(TreeNode)treeStk.elementAt(0);
      	     System.out.println("UMAST Tree "+" has leaves:");
      	     for (int i=0;i<treeStk.size();i++)
   				System.out.print("|| "+((TreeNode)treeStk.elementAt(i)).getLeafSize());
   	     	System.out.println();
      	     for (int i=0;i<treeStk.size();i++){
				node = new TreeNode();
   				node = (TreeNode)treeStk.elementAt(i);
   				System.out.print("-- "+node.getGeneLabel()+"\n");
				Set mastLfSet = new HashSet();
				mastLfSet = node.getLeafSet();
				Iterator it = mastLfSet.iterator();
				while(it.hasNext())
					System.out.print((String)it.next()+"  ");
				System.out.println();

			}
   	     	System.out.println();
      	 }
      	 else System.out.println("Empty UMAST");

     }


     /* After create the mast tree, you need to set position and

      * all the other information, such as parent, total size
	  * into the new mast tree.

      */


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

	        setPosTree(iter.getLeftNode(), leftChild);

	        setPosTree(iter.getRightNode(), rightChild);
	        node.setTotSize(leftChild.getTotSize()+rightChild.getTotSize()+1);
		//node.setPos(initPos);

		//leftChild.setParent(node);
		//rightChild.setParent(node);

		//node.getLeftNode().setParent(node);

		//node.getRightNode().setParent(node);
	   }

	   else {
			  //modified label -> name
	          //node.setLeaf(new String(iter.getGeneLabel()));
	          node.setLeafLabel(new String(iter.getGeneLabel()));
	          node.setLeafName(new String(iter.getGeneName()));
	          node.setTotSize(1);
		  //node.setPos(initPos);

		}

	  node.setPos(initPos);
	  initPos++;

	}
	return node;

     }


     /* set the position information for all the nodes in the trees in the stack
      */
     private void pos_stk(Stack stk)
     {
         TreeNode root;
         TreeNode secRoot;
         TreeNode temp=new TreeNode();
         Stack newStk=new Stack();

         for(int i=0;i<stk.size();i++){
             root=new TreeNode();
             root=(TreeNode)stk.elementAt(i);
             initPos=0;
             secRoot=new TreeNode();
             secRoot=setPosTree(root, secRoot);
             temp=secRoot;
             newStk.push(secRoot);
         }
         stk.clear();
         stk=copy_stk(stk, newStk);
     }

     /* getImMast: is used to get the mast tree from the input two rows and treeT info
      * imRight, imLeft: info about the right subtree and left subtree
      * root: the current root
      * return : mast row for current root
      */

     private Stack[]getImMast(Stack []imRight, Stack []imLeft, TreeNode curU)
     {
         TreeNode curT = new TreeNode();	//current node of tree T
         TreeNode firstT = new TreeNode();	//Left most node of tree T
         Stack []mastRow=new Stack[tLeng];
         Stack []mastSub=new Stack[SUBMAST];
         TreeNode tempTree=new TreeNode();
         int tPos=0;

         boolean loop=true;

         //for debug-------------
         Tree temp=new Tree();
         //------------------------

         firstT=treeT.getRoot();

         while(firstT.getLeftNode()!=null)
             firstT = firstT.getLeftNode();

         for(int i=0;i<tLeng;i++)
             mastRow[i]=new Stack();

         for(int i=0;i<SUBMAST;i++)
             mastSub[i]=new Stack();

         curT=firstT;

         while (loop){
             if (curT.isLeaf()){
         		if (debugTest)

             		if (contain(curT, curU)){
             		    tempTree=new TreeNode();
             		    tempTree.makeLeaf(curT);
             		    mastRow[tPos].push(tempTree);
             		}
             		// else the mast is empty
             }
             else {
              	      for(int i=0;i<SUBMAST;i++)
              	          mastSub[i] = new Stack();

              	      int uRightSize = curU.getRightNode().getTotSize();
              	      int tRightSize = curT.getRightNode().getTotSize();

              	      /* The max sub tree should be the maximum of the following six
              	       * items: #(Tb, Ux)+#(Tc, Uy), #(Tb, Uy)+#(Tc, Ux), #(Ta, Ux),
              	       * #(Ta, Uy), #(Tb, Uw), #(Tc, Uw)
              	       */
              	      mastSub[0] = imLeft[tPos - tRightSize-1];
              	      mastSub[1] = imRight[tPos - 1];
              	      mastSub[2] = imLeft[tPos - 1];
              	      mastSub[3] = imRight[tPos - tRightSize-1];
              	      mastSub[4] = imLeft[tPos];
              	      mastSub[5] = imRight[tPos];
              	      mastSub[6] = mastRow[tPos - tRightSize-1];
              	      mastSub[7] = mastRow[tPos - 1];

              	      Stack maxMastStk = new Stack();

              	      maxMastStk.addAll(max(mastSub));

              	      mastRow[tPos] = maxMastStk;

            }

            tPos++;
            if (curT.isRoot())
                loop = false;

            else curT = getNext(curT);
        }	//end of while

        return mastRow;

    }		//end of getImMast


     /* According to the move operation, move root and construct tree based on interm, orig1,and orig2
      * in this program, we change the root of treeU
      * interm: the intermediate tree computed by the last root
      * imMast: the mast row computed by the last root
      * orig1, orig2: the original sub tree in the very first rooted tree
      * moveOp: move operation, including: LEFT_, RIGHT_, and ROOT_
      */

     private void moveRoot(TreeNode interm, Stack []imMast, TreeNode orig1, TreeNode orig2, int moveOp)
     {
     	 TreeNode preLeft;
     	 TreeNode preRight;
     	 TreeNode left=new TreeNode();
     	 TreeNode right=new TreeNode();
     	 TreeNode temp1=new TreeNode();
     	 TreeNode temp2=new TreeNode();
     	 TreeNode temp3=new TreeNode();
     	 Stack []imRight=new Stack[tLeng];
     	 Stack []imLeft=new Stack[tLeng];
   	 Stack []mastRow=new Stack[tLeng];	//store the result
   	 Stack []tempMast=new Stack[tLeng];	//store the temp result
   	 Stack []curMast=new Stack[tLeng];
   	 TreeNode treeUU=new TreeNode();
   	 Tree t=new Tree();

   	 for (int i=0;i<tLeng;i++){
   	     imRight[i]=new Stack();
   	     imLeft[i]=new Stack();
   	     mastRow[i]=new Stack();
   	     tempMast[i]=new Stack();
   	     curMast[i]=new Stack();
   	 }

         switch(moveOp){
         case ROOT_:
         	//do nothing to itself, only extend to its four neighbours

		if (!treeU.getRoot().getLeftNode().isLeaf()){

			preRight=new TreeNode(treeU.getRoot().getRightNode());
         		preLeft=new TreeNode(treeU.getRoot().getLeftNode().getRightNode());
   			left=new TreeNode(treeU.getRoot().getLeftNode().getLeftNode());

   	    		mastRow=getMastRow(uLeng-1-1);

   	    		moveRoot(preRight, mastRow, preLeft, left, LEFT_);	//left-left

   	    		moveRoot(preRight, mastRow, left, preLeft, RIGHT_);	//left-right
		}

		if (!treeU.getRoot().getRightNode().isLeaf()){
   	    		preLeft=new TreeNode(treeU.getRoot().getLeftNode());
   	    		preRight=new TreeNode(treeU.getRoot().getRightNode().getRightNode());
   	    		right=new TreeNode(treeU.getRoot().getRightNode().getLeftNode());
   	    		mastRow=getMastRow(preLeft.getTotSize()-1);

   	    		moveRoot(preLeft, mastRow, preRight, right, LEFT_);	//right-left

   	    		moveRoot(preLeft, mastRow, right, preRight, RIGHT_);	//right-right
		}

   	    	break;

   	  case  LEFT_:

   	  	//first deal with the current tree, and then extend to two neighbors
   		temp1=new TreeNode(interm);
   		temp2=new TreeNode(orig1);
   		temp3=new TreeNode(orig2);
		right=new TreeNode();
		treeUU=new TreeNode();

   	  	right.setChild(temp1, RIGHT_CH);
   	  	right.setChild(temp2, LEFT_CH);

   		for(int i=0;i<imMast.length;i++)
			imRight[i].addAll(imMast[i]);

		imLeft=getMastRow(orig1.getPos());

   		tempMast=getImMast(imRight, imLeft, right);

   		imLeft=getMastRow(orig2.getPos());

   		treeUU.setChild(right, RIGHT_CH);
   		treeUU.setChild(temp3, LEFT_CH);

		curMast=getImMast(tempMast, imLeft, treeUU);

   		//modified on May 27
   		if (!curMast[tLeng-1].empty()){
   			temp1=new TreeNode();
   			temp1=(TreeNode)curMast[tLeng-1].elementAt(0);
   		}

		curMast[tLeng-1]=filter_stk(curMast[tLeng-1]);


   		treeStk=push_stk(treeStk, curMast[tLeng-1]);

		if (!orig2.isLeaf()){

		    temp1=new TreeNode(orig2.getRightNode());
		    temp2=new TreeNode(orig2.getLeftNode());
   		    moveRoot(right, tempMast, orig2.getRightNode(), orig2.getLeftNode(), LEFT_);
   		    moveRoot(right, tempMast, temp2, temp1, RIGHT_);

   		}




   		break;

   	   case RIGHT_:

   		temp1=new TreeNode(interm);
   		temp2=new TreeNode(orig1);
   		temp3=new TreeNode(orig2);
   		left=new TreeNode();

   		treeUU=new TreeNode();

   		left.setChild(temp1, RIGHT_CH);
   		left.setChild(temp2, LEFT_CH);



   		for(int i=0;i<imMast.length;i++)

   		    imRight[i].addAll(imMast[i]);

		imLeft=getMastRow(orig1.getPos());

   		tempMast=getImMast(imRight, imLeft, left);

		imLeft=getMastRow(orig2.getPos());

   		treeUU.setChild(orig2, RIGHT_CH);
   		treeUU.setChild(left, LEFT_CH);

   		curMast=getImMast(tempMast, imLeft, treeUU);

   		//modified on May27
   		if (!curMast[tLeng-1].empty()){
   			temp1=new TreeNode();
   			temp1=(TreeNode)curMast[tLeng-1].elementAt(0);
   		}

		curMast[tLeng-1]=filter_stk(curMast[tLeng-1]);
   		treeStk=push_stk(treeStk, curMast[tLeng-1]);

   		if (!orig2.isLeaf()){

   		    temp1=new TreeNode(orig2.getRightNode());
		    temp2=new TreeNode(orig2.getLeftNode());
   		    moveRoot(left, tempMast, orig2.getRightNode(), orig2.getLeftNode(), LEFT_);
   		    moveRoot(left, tempMast, temp2, temp1, RIGHT_);
   		}

   		break;
   	    default:

   	    	break;
   	    }

       }	//end of moveRoot

     /* umast: get the mast tree for the unrooted tree, make use of the rooted version
      * @void: treeU, treeT; both of them are rooted at the center edge of the tree
      		we need to re-root the treeU
      * @return: umast

     		      old root (useless)
     			|
     			V
     	 new right-> --------
     	  	    |	     |<------preRight (preComputed right)
     new root -> ---*---*   -*---
     		|	|  |     |
     	  	*   	|<''''''''''''''' preLeft(preComputed left)
     new left->----	|  |	 |
   	     |	   |	|  |	 |
   	     a	   b    c  d     e

     */

     public Stack umast()
     {
     	//It should be done after fill in the table
     	if (!tabFilled)
     		fillTab();

     	Stack []imMast=new Stack[tLeng];

   	TreeNode temp=treeU.getRoot();

 	if (treeU.getRoot().getLeafSize()>2){
   	    long t1 = System.currentTimeMillis();
     	    moveRoot(temp, imMast, temp, temp, ROOT_);
     	    long t2 = System.currentTimeMillis() - t1;
	    //System.out.println("Time spent: "+(double)t2/1000);
     	}
     	//else the umast(treeU, treeT) is the same as mast(treeU, treeT)
     	else {
     		pos_stk(mastTab[uLeng-1][tLeng-1]);
     		return resMast();
     	     }

     	pos_stk(treeStk);
     	//print_stk();
     	return treeStk;
     }


     /* getNextt: to get the next node to be dealt with in the tree; use post
      * fix order.
      * @param: current treeNode
      * @return: next treeNode
      */
     private TreeNode getNext(TreeNode inNode)
     {
         //TreeNode nextNode = new TreeNode();
         TreeNode nextNode;

         if (inNode == null) return null;
         else {
         	if (inNode.isRoot()){
         	    nextNode = getLeftMost(inNode);
         	}
         	else {
         		//if inNode is a left child, then go to its sibling's left most child

         		if (inNode.getGeneLabel().equals(inNode.getParent().getLeftNode().getGeneLabel())){
         				if (!inNode.getGeneLabel().equals(inNode.getParent().getRightNode().getGeneLabel()))
							nextNode = getLeftMost(inNode.getParent().getRightNode());
						else {
								//left and right child has the same genelabel
         						if (inNode.getLeafSet().equals(inNode.getParent().getLeftNode().getLeafSet()))
         	    	    			nextNode = getLeftMost(inNode.getParent().getRightNode());
         	    	    		else
         	    	    			nextNode = inNode.getParent();
         	    	    }

         		}
         		else {
         			//else inNode is a right child, then go to its parent
         			nextNode = inNode.getParent();
         		}
         	}
         }

         return nextNode;
     }

     /* getLeftMost: get the left most child of the current node
      * @param: the current node
      * @return: the left most child
      */

     private TreeNode getLeftMost(TreeNode inNode)
     {
         //TreeNode nextNode = new TreeNode();
         TreeNode nextNode;

         if (inNode == null) return null;
         else {
         	 nextNode = inNode;
         	 while(!nextNode.isLeaf()) nextNode = nextNode.getLeftNode();
         }

         return nextNode;
     }

     /* fillLab: to get the postFix order of a tree
      * @ param: a tree node
      * @ return: an array contain the postFix order of all nodes in the tree
      */


     private void fillLab(TreeNode inNode)
     {
         TreeNode node=inNode;
         int pos=0;

	 node=inNode;

	 while (!node.isRoot()){

            node=getNext(node);
	 }


     }


     /* contain: to decide whether a leaf is in the leaf set of a subtree
      * @ param: inLeaf: input leaf
      * @ param: inTree: input tree
      * @ return: bool, true: in, false: not in
      */

     private boolean contain(TreeNode inLeaf, TreeNode inTree)
     {

            Set leafSet= new HashSet();
            String leafLabel = inLeaf.getGeneName();

            leafSet = inTree.getLeafSet();
            if(leafSet.contains(leafLabel))
            	return true;

            return false;
      }

      /* get the mast value for the current position in the table
       */

     private Stack getMast(int uPos, int tPos)
     {

         Stack stk=new Stack();

         //copy_stk(stk, mastTab[uPos][tPos]);

         stk.addAll(mastTab[uPos][tPos]);

         return stk;
     }

     /* get one row (or column) from the mast tabl
      */

     private Stack []getMastRow(int pos)
     {

         Stack []tempRow = new Stack[tLeng];

         for(int i=0;i<tLeng;i++){

             tempRow[i]=new Stack();

             tempRow[i].addAll(mastTab[pos][i]);
         }

         return tempRow;
     }

     /* get the mast value for the final one
     */

     public Stack resMast()
     {

         return mastTab[uLeng-1][tLeng-1];
     }

     /* getMast: to get the maximum size from the multiple candidate sub mast
      * @param: array of the 8 sub mast(stored in array just for convenient
      * @return: the tree of the desired mast
      */

      public Stack max(Stack mastArr[])
      {
            Stack maxMastStk=new Stack();

            TreeNode maxMast = null;

            int maxSize = 0;
            Integer sel = new Integer(0);	// which sub mast to chose
            int select;
            int temp = 0;
            int mastSize[];
            boolean oneSide = false;	// indicate whether the mast is from one side of a tree
            Stack selStk=new Stack();

            mastSize = new int[SUBMAST];

	    for (int i=0;i<SUBMAST;i++)
            	    if (mastArr[i].empty()) mastSize[i] = 0;
            	    else mastSize[i] = ((TreeNode)mastArr[i].elementAt(0)).getLeafSize();

	    	maxSize = mastSize[0] + mastSize[1];
            	temp = mastSize[2] + mastSize[3];

            	if (maxSize>0)
                    selStk.push(sel);

            	if (temp > maxSize){
            	    maxSize = temp;
            	    selStk.clear();
            	    sel = new Integer(2);
            	    selStk.push(sel);
            	}
            	else if ((temp == maxSize)&&(maxSize!=0)){
            		sel=new Integer(2);
            		selStk.push(sel);
            	}

            	for(int i = 4;i<mastArr.length;i++){

            	    temp = mastSize[i];

                    if (temp > maxSize){
                	maxSize = temp;
                	selStk.clear();
                	sel = new Integer(i);
                	selStk.push(sel);
                    }
                    else if ((temp== maxSize)&&(maxSize!=0)){
                	    sel = new Integer(i);
                	    selStk.push(sel);
                         }
            	}
            	if (maxSize == 0){
            		maxMastStk.clear();
                	return maxMastStk;
            	}

            maxMast = new TreeNode();

            // the first two cases, leaves of mast not in a single branch

            for(int i=0;i<selStk.size();i++){
                sel=(Integer)selStk.elementAt(i);
                select=sel.intValue();

                if ((select == 0) ||(select == 2)){
            	    if (mastArr[select].empty()){
            	        maxMastStk=push_stk(maxMastStk, mastArr[select+1]);
            	    }
            	    else if (mastArr[select + 1].empty()) {
            	    		maxMastStk=push_stk(maxMastStk, mastArr[select]);
            	    	 }

            	     	 else {

            	     	 	for (int j=0;j<mastArr[select].size();j++)
            	     	 	    for(int k=0;k<mastArr[select+1].size();k++){
            	     	 	        maxMast = new TreeNode();
            	     	 	        TreeNode leftT=new TreeNode((TreeNode)mastArr[select].elementAt(j));
									leftT.setParent(maxMast);
            	     	 	        maxMast.setMyLeftNode(leftT);

            	     	 	        TreeNode rightT=new TreeNode((TreeNode)mastArr[select+1].elementAt(k));
									rightT.setParent(maxMast);
            	     	 	        maxMast.setMyRightNode(rightT);

                 	    		maxMast.setGeneLabel(((TreeNode)mastArr[select].elementAt(j)).getGeneLabel().concat(((TreeNode)mastArr[select+1].elementAt(k)).getGeneLabel()));
                 	    		maxMast.addLeafSet(((TreeNode)mastArr[select].elementAt(j)).getLeafSet());
                 	    		maxMast.addLeafSet(((TreeNode)mastArr[select+1].elementAt(k)).getLeafSet());
                 	    		maxMastStk=push_stk(maxMastStk, maxMast);
                 	    	    }

                     	}
             	}
            	else {
            		maxMastStk=push_stk(maxMastStk, mastArr[select]);
            	      }

            } //end of for


            return maxMastStk;
      }

    private Set []checkLeafSet(Tree u, Tree t)
    {
    	Set result[] = new HashSet[2];
    	result[0] = new HashSet();
    	result[1] = new HashSet();

    	Set uSet = new HashSet();
		uSet = u.getRoot().getLeafSet();

		Set tSet = new HashSet();
		tSet = t.getRoot().getLeafSet();

		Iterator itu = uSet.iterator();
		Iterator itt = tSet.iterator();

		while(itu.hasNext()){
			String s = new String();
			s = (String)itu.next();

			if (!tSet.contains(s))
				result[0].add(s);
		}

		while(itt.hasNext()){
			String s = new String();
			s = (String)itt.next();

			if (!uSet.contains(s))
				result[1].add(s);
		}

		return result;
    }

     /* Get the tree-to-tree distance
      * @ param: treeStk, treeT, treeU
      * formula: d(T1, T2) = d1(T1, T2) + L(T1, T2)/(n*d1(T1, T2))
      * here T1 is treeT, T2 is treeU;
      *      d1(T1, T2) = n-#(T, U) -- the number of pruned leaves
      *      n - number of leaves
      *      L(T1, T2) = min(l(A)), where A is the obtained mast tree
      *      l(A) = sum (s(x,A)), where s is the length of the path between
      *   			  the leaves x1 and x2 in the tree A(x)
      * note: here we only consider the trees with same number of leaves.
      * @output: the tree-to-tree distance.
      */

     public double treeDis()
     {
	 	int fraction=1;
	 	double distance=0;
	 	int temp;
	 	TreeNode rootU=new TreeNode();
	 	TreeNode rootT=new TreeNode();

	 	rootU=treeU.getRoot();
	 	rootT=treeT.getRoot();

	 	if (!treeStk.empty()){
	 			prunedSet[0] = new HashSet();
	 			prunedSet[1] = new HashSet();

	 			prunedSet = checkLeafSet(treeU, treeT);

				totalLeaves = rootU.getLeafSet().size()+prunedSet[1].size();

				int pruneSize = 0;
	 			int commonLeaves = rootU.getLeafSize() - prunedSet[0].size();

	 			distance = (double)(commonLeaves - ((TreeNode)treeStk.elementAt(0)).getLeafSize());

	 			if ((prunedSet[0].size()!=0)||(prunedSet[1].size()!=0)){
	     	    	pruneSize = prunedSet[0].size()+prunedSet[1].size();
				}

	     	    if (distance == 0)
	     	    	return distance;
	     	    else {
	     	    		fraction=getPathSum((TreeNode)treeStk.elementAt(0), rootU, rootT);

	     	    		for (int i=1;i<treeStk.size();i++){
         	        		temp=getPathSum((TreeNode)treeStk.elementAt(i), rootU, rootT);

         	        		if (temp<fraction)
       			    			fraction=temp;
       		    		}
      					distance=distance+(double)fraction/(rootU.getLeafSize()*distance)+pruneSize;
      	 			}
      	 	}

		 treeDistance = distance;

      	 return distance;
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

      /* get the sum of path for all the pruned leaves
       */

      private int getPathSum(TreeNode mastTree, TreeNode rootU, TreeNode rootT)
      {
          String leaf=new String();
          Set leafSet=new HashSet();
          int path=0;
          int subPath=0;

          if (mastTree.getLeafSize()!=0){
              leafSet=mastTree.getLeafSet();
              Iterator it=rootU.getLeafSet().iterator();
              while (it.hasNext()){
                  leaf=(String)it.next();
                  if ((!leafSet.contains(leaf))&&(!prunedSet[0].contains(leaf))&&(!prunedSet[1].contains(leaf))){
                  	//that means the leaf in treeU is pruned
                  	subPath=getPath(leaf, mastTree, rootU, rootT);

      			path+=subPath;
      		   }
      	      }
      	  }

      	  return path;
      }

      /* get the path for a single pruned leaf in mast tree
       */

      private int getPath(String leaf, TreeNode mastTree, TreeNode rootU, TreeNode rootT)
      {
          TreeNode parU=new TreeNode();		//the parent node of leaf in tree U
          TreeNode parT=new TreeNode();   	//the parent node of leaf in tree T
          TreeNode iterU=new TreeNode();
          TreeNode iterT=new TreeNode();
          TreeNode iterM=new TreeNode();
          TreeNode commP=new TreeNode();	//common parent of sible U and sible T
          boolean loop=true;
          String sibleU;
          String sibleT;
          Set leftSet;
          Set rightSet;
          Set leftMSet;
          Set rightMSet;

          iterU=rootU;
          iterT=rootT;
          iterM=mastTree;

	  leftSet=new HashSet(getSible(iterU, leaf, iterM));
	  rightSet=new HashSet(getSible(iterT, leaf, iterM));

	  Iterator it=leftSet.iterator();
	  String pruned;

	  List pruneList=new List();

	  while(it.hasNext()){
	  	pruned=(String)it.next();

	  	if (!mastTree.getLeafSet().contains(pruned))
	  		pruneList.add(pruned);
	  }

	  for(int i=0;i<pruneList.getItemCount();i++)
	  	leftSet.remove((String)pruneList.getItem(i));

	  //Adjust the leftSet if leaf's sible is seperated into two subtrees
	  if (intersect(leftSet, mastTree.getLeftNode().getLeafSet())&&
	  		intersect(leftSet, mastTree.getRightNode().getLeafSet())&&
	  		!leftSet.equals(mastTree.getLeafSet())){
				Set replace = new HashSet();
				replace.addAll(mastTree.getLeafSet());
				replace.removeAll(leftSet);
				leftSet.clear();
				leftSet.addAll(replace);
		}


	  it=rightSet.iterator();
	  pruneList=new List();

	  while(it.hasNext()){
		pruned=(String)it.next();

	  	if (!mastTree.getLeafSet().contains(pruned))
	  		pruneList.add(pruned);
	  }

	  for(int i=0;i<pruneList.getItemCount();i++)
	  	rightSet.remove((String)pruneList.getItem(i));

	//Adjust the rightSet if leaf's sible is seperated into two subtrees
	if (intersect(rightSet, mastTree.getLeftNode().getLeafSet())&&
		  	intersect(rightSet, mastTree.getRightNode().getLeafSet())&&
		  	!rightSet.equals(mastTree.getLeafSet())){
				Set replace = new HashSet();
				replace.addAll(mastTree.getLeafSet());
				replace.removeAll(rightSet);
				rightSet.clear();
				rightSet.addAll(replace);
	}

	  while(true){
	  	if (!iterM.isLeaf()){
	  		leftMSet=iterM.getLeftNode().getLeafSet();
	  		rightMSet=iterM.getRightNode().getLeafSet();
	  		//common parent is in the left side
	  		if ((leftMSet.containsAll(leftSet))&&(leftMSet.containsAll(rightSet)))
	  		    iterM=iterM.getLeftNode();
	  		else if ((rightMSet.containsAll(leftSet))&&(rightMSet.containsAll(rightSet)))
	  				iterM=iterM.getRightNode();
	  		     else {
	  		     	    commP=iterM;
	  		     	    break;
	  		     }
	       }
	  }

	  int uPath=0;
	  int tPath=0;

	  List uList=new List();
	  List tList=new List();

	  uList=getPartialPath(leftSet, commP);
	  tList=getPartialPath(rightSet, commP);

	  boolean uStable=false;
	  boolean tStable=false;

	  if ((leftSet.equals(commP.getLeafSet()))&&(!commP.getLeafSet().equals(mastTree.getLeafSet())))
	  {
	  	uPath=2;
	  	uStable=true;
	  }
	  else if ((leftSet.equals(commP.getLeafSet()))&&(commP.getLeafSet().equals(mastTree.getLeafSet())))
	  	{
	  		uPath=1;
	  		uStable=true;
	  	}

	  if ((rightSet.equals(commP.getLeafSet()))&&(!commP.getLeafSet().equals(mastTree.getLeafSet())))
	  {
	  	tPath=2;
	  	tStable=true;
	  }
	  else if ((rightSet.equals(commP.getLeafSet()))&&(!commP.getLeafSet().equals(mastTree.getLeafSet())))
	  	{
	  		tPath=1;
	  		tStable=true;
	  	}

	  if (uStable&&tStable)
	  	return (uPath+tPath);
	  else if (uStable&&(!tStable))
	  		return (uPath+tList.getItemCount()+1);
	  else if ((!uStable)&&tStable)
	  		return (tPath+uList.getItemCount()+1);
	  else if ((!uStable)&&(!tStable)){
	  	     int i=0;
    	  	     while((i<uList.getItemCount())&&(i<tList.getItemCount())){
    	  	    	    if (((String)uList.getItem(i)).equals((String)tList.getItem(i)))
    	  	    	    	i++;
    	  	    	    else break;
    	  	     }
    	  	     int path=0;

    	  	     //because when they have same partial path, then the commp is e
    	  	     //no longer equal to mastTre, it has been moved downwards

    	  	     if ((commP.getLeafSet().equals(mastTree.getLeafSet()))&&(i==0))
    	  	     	path=uList.getItemCount()-i+tList.getItemCount()-i+1;
    	  	     else {
    	  	            //decide whether the inserted node has effect on the other one
    	  	            boolean insLayer1=false;
    	  	            boolean insLayer2=false;
    	  	            insLayer1 = increaseLay(commP, uList, rightSet);
    	  	            insLayer2 = increaseLay(commP, tList, leftSet);

    	  	            if (insLayer1||insLayer2)
    	  	            	path=uList.getItemCount()-i+tList.getItemCount()-i+3;
    	  	            else
    	  	     	        path=uList.getItemCount() - i + tList.getItemCount() - i + 2;
    	  	     }

    	  	     return path;
    	  	}

    	   return 0;
	  /*
	  if ((uPath==2)&&(tPath==2))
    	  	return 4;
    	  else if ((uPath==2)&&(tPath!=2))
    	  	    return (uPath+tList.getItemCount()+1);
    	       else if ((tPath==2)&&(uPath!=2))
    	  		return (tPath+uList.getItemCount()+1);
    	  	    else {
    	  	            int i=0;
    	  	    	    while((i<uList.getItemCount())&&(i<tList.getItemCount())){
    	  	    	    	if (((String)uList.getItem(i)).equals((String)tList.getItem(i)))
    	  	    	    		i++;
    	  	    	    	else break;
    	  	    	    }
    	  	    	    int path=0;
    	  	    	    path=uList.getItemCount() - i + tList.getItemCount() - i + 2;
    	  		    return path;
    	  		}
    	  */

      }

      private boolean increaseLay(TreeNode commP, List list, Set lowerSet)
      {
          TreeNode iterM=new TreeNode(commP);

          for (int i=0;i<list.getItemCount();i++){
          	if (list.getItem(i).equals("r"))
          		iterM=iterM.getRightNode();
          	else if (list.getItem(i).equals("l"))
          		iterM=iterM.getLeftNode();
          }

          if (iterM.getLeafSet().containsAll(lowerSet))
          	return true;

          return false;
      }

      private void printSet(Set leafSet)
      {
          Iterator iter=leafSet.iterator();

          while(iter.hasNext())
          	System.out.print(iter.next()+" ");
          System.out.println();
      }

      private List getPartialPath(Set leafSet, TreeNode iterM)
      {
          TreeNode temp=new TreeNode(iterM);
          TreeNode temp2=new TreeNode();
          TreeNode temp3=new TreeNode();

          List list=new List();
          int path=1;

          //printSet(leafSet);

          boolean loop=true;
       	  while((!temp.getLeafSet().equals(leafSet))&&loop){

	      if (temp.getLeftNode().getLeafSet().containsAll(leafSet)){
	      	    temp=temp.getLeftNode();
	      	    list.add("l");
	      }
	      else  if(temp.getRightNode().getLeafSet().containsAll(leafSet)){
	      	    	temp=temp.getRightNode();
	      	    	list.add("r");
	      	    }
	      	    else {
	      	    	   temp2=temp.getLeftNode();
	      	    	   temp3=temp.getRightNode();
	      	    	   Set complexSet=new HashSet();
	      	    	   Set tempSet1=new HashSet();
	      	    	   Set tempSet2=new HashSet();
	      	    	   if(!temp.isLeaf()){

	      	    	   	while(true){
	      	    	   	    temp2=temp.getLeftNode();
	      	    	   	    temp3=temp.getRightNode();


	      	    	   	    tempSet1=new HashSet(complexSet);
	      	    	   	    tempSet2=new HashSet(complexSet);
	      	    	   	    if (!temp2.isLeaf()){
	      	    	   	        tempSet1.addAll(temp2.getRightNode().getLeafSet());
	      	    	   	    	tempSet1.addAll(temp3.getLeafSet());
	      	    	   	    	tempSet2.addAll(temp2.getLeftNode().getLeafSet());
	      	    	   	    	tempSet2.addAll(temp3.getLeafSet());
	      	    	   	    	if ((tempSet1.containsAll(leafSet))||(tempSet2.containsAll(leafSet)))
	      	    	   	    	{
	      	    	   	    	    list.add("l");
	      	    	   	    	    loop=false;

	      	    	   	    	    if (tempSet1.containsAll(leafSet))
	      	    	   	    	    	list.add("l");
	      	    	   	    	    else list.add("r");

	      	    	   	    	    break;
	      	    	   	    	}
	      	    	   	    	else {
	      	    	   		        list.add("l");
	      	    	   		        temp=new TreeNode();
	      	    	   		        temp=temp.getLeftNode();
	      	    	   		        complexSet.addAll(temp3.getLeafSet());
	      	    	   		        continue;
	      	    	   		     }
	      	    	   	    }  //end of if

	      			    if (!temp3.isLeaf()){
	      			    	tempSet1.addAll(temp3.getRightNode().getLeafSet());
	      			        tempSet1.addAll(temp2.getLeafSet());

	      			        tempSet2.addAll(temp3.getLeftNode().getLeafSet());
	      			        tempSet2.addAll(temp2.getLeafSet());

	      			    	if ((tempSet1.containsAll(leafSet))||(tempSet2.containsAll(leafSet)))
	      				{
	      			    	    list.add("r");
	      			    	    loop=false;

	      			    	    if (tempSet1.containsAll(leafSet))
	      			    	    	list.add("l");
	      			    	    else list.add("r");

	      			    	    break;
	      			    	}
	      			    	else {
	      			     	    	list.add("r");
	      			     	    	temp=temp.getRightNode();
	      			     	    	complexSet.addAll(temp2.getLeafSet());
	      			     	    	continue;
	      			 	}

	  			    }  //end of if
	  			}   //end of while
	  		    }  //end of if temp is leaf
	  		}  //end of else

	  }	//end of while

	  return list;
      }

      private Set getSible(TreeNode root, String leaf, TreeNode iterM)
      {
          TreeNode sible=new TreeNode();
          boolean loop=true;
          TreeNode iter=new TreeNode();
          iter=root;
          Set empty=new HashSet();
          Set specSet=new HashSet();
          TreeNode specSible=new TreeNode();
   	  TreeNode inteSible=new TreeNode();

          specSible=root.getLeftNode();
          specSet=specSible.getLeafSet();


          if ((specSet.size()==1)&&(specSet.contains(leaf))){

          	inteSible=root.getRightNode().getLeftNode();
          	//inteSible=new TreeNode(iterM);

          	/*
          	specSible=new TreeNode();
          	specSible=root.getRightNode().getLeftNode();
          	specSet=new HashSet();
          	specSet=specSible.getLeafSet();

          	return specSet;
          	*/
          }
          else {
          	  specSible=new TreeNode();
          	  specSible=root.getRightNode();
          	  specSet=specSible.getLeafSet();
          	  if ((specSet.size()==1)&&(specSet.contains(leaf))){

          	  	inteSible=root.getLeftNode().getRightNode();
          	  	//inteSible=new TreeNode(iterM);

          	  	/*
          		specSible=new TreeNode();
          		specSible=root.getLeftNode().getRightNode();
          		specSet=new HashSet();
          		specSet=specSible.getLeafSet();

          		return specSet;
          		*/
          	  }
          	  else {
          		while(loop){


              		    if (!iter.isLeaf()){

          	    		if (iter.getLeftNode().getLeafSet().contains(leaf))
          			    iter=iter.getLeftNode();
          	    		else iter=iter.getRightNode();
              		    }

              		    else {
       		    		    sible=iter.getParent().getLeftNode();
       		    		    //modified label -> name
       		    		    //if (!sible.getGeneLabel().equals(leaf)){
       		    		    if (!sible.getGeneName().equals(leaf)){
       		    		    	inteSible=sible;
       		    		    	break;
       		    		    }

       					//return sible.getLeafSet();
       		    		    else {
       		    		    	    inteSible=iter.getParent().getRightNode();
       		    		    	    break;
       		    	    		    //return iter.getParent().getRightNode().getLeafSet();
       		    		    }
       	      		    }
       	  		}  //end of while
       	  	   }
       	  }


       	  if (intersect(inteSible, iterM)){

       	  	return inteSible.getLeafSet();
       	  }
       	  else {

       	  	loop=true;
       	  	TreeNode temp1=new TreeNode();
       	  	TreeNode temp2=new TreeNode();
       	  	temp1=new TreeNode(inteSible.getParent());
       	  	temp2=new TreeNode(temp1.getParent());
       	  	//temp1 is temp2's left node
       	  	if (temp2!=null){
       	  	    if (temp1.getLeafSet().equals(temp2.getLeftNode().getLeafSet()))
       	  		temp2=temp2.getRightNode();
       	  	    else temp2=temp2.getLeftNode();
       	  	}

       	  	//decide whether the sible is the real sible

       	  	/*
       	  	else {
       	  	        if (inteSible.getLeafSet().equals(temp1.getLeftNode().getLeafSet()))
       	  			temp2=temp1.getRightNode();
       	  		else temp2=temp1.getLeftNode();
       	  		if (!temp2.isLeaf())
       	  			temp2=temp2.getLeftNode();

       	  	}
       	  	*/


       	  	TreeNode temp4=new TreeNode();
       	  	while(loop){
       	  	    if (intersect(temp2, iterM)){
       	  	    	    if (!temp2.getLeafSet().containsAll(iterM.getLeafSet()))
       	  		        return temp2.getLeafSet();
       	  		    else {
       	  		           while(true){

       	  		    	   	if (!temp2.isLeaf()){
       	  		    	   		temp4=temp2.getRightNode();
       	  		    	   		if (intersect(temp4, iterM))
       	  		    	   			if (!temp4.getLeafSet().containsAll(iterM.getLeafSet()))
       	  		    	   				return temp2.getRightNode().getLeafSet();
       	  		    	   			else {
       	  		    	   				temp2=temp4;
       	  		    	   				break;
       	  		    	   			}
       	  		    	   		else temp2=temp2.getLeftNode();
       	  		    	   	}
       	  		    	        else {
       	  		    	        	if (intersect(temp2, iterM))
       	  		    	        		return temp2.getLeafSet();
       	  		    	        	else
       	  		    	        	    return empty;
       	  		    	             }
       	  		    	   }
       	  		    }
       	  	    }
       	  	    else {
       	  	 	    temp1=new TreeNode(temp2.getParent());
       	  	    	    if (temp1.getParent()==null){
       	  	    	    	   if (temp2.getLeafSet().equals(temp1.getLeftNode().getLeafSet()))
       	  	    	    	   	temp2=temp1.getRightNode();
       	  	    	    	   else temp2=temp1.getLeftNode();

       	  	    	           /*
       	  	    	           if (temp2.getLeafSet().equals(temp1.getLeftNode().getLeafSet()))
       	  	    	           	return temp1.getRightNode().getLeafSet();
       	  	    	           else return temp1.getLeftNode().getLeafSet();
       	  	    	           */
       	  	    	    }
       	  	    	    else {
       	  		       		temp2=new TreeNode(temp1.getParent());
       	  		       		//temp1 is temp2's left node
       	  		       		if (temp1.getLeafSet().equals(temp2.getLeftNode().getLeafSet()))
       	  			    	    temp2=temp2.getRightNode();
       	  		       		else temp2=temp2.getLeftNode();
       	  		    }
       	  	   }
       	  	}

       	  }

       	  return empty;
      }

      /* decide whether two sub trees intersect or not
       */

      private boolean intersect(TreeNode tree1, TreeNode tree2)
      {
          Set set1=new HashSet();
          Set set2=new HashSet();

          set1=tree1.getLeafSet();
          set2=tree2.getLeafSet();

          Iterator iter=set1.iterator();

          while(iter.hasNext()){
          	if (set2.contains((String)iter.next()))
          		return true;
          }

          return false;
      }

		/* decide whether two sets are intersect or not
		 */
		private boolean intersect(Set s1, Set s2)
		{
			Iterator it1 = s1.iterator();

			while(it1.hasNext()){
				if(s2.contains((String)it1.next()))
					return true;
			}

			return false;
		}

      /* get the point that need to increase value
       * e.g.: 1 2 5 3 4 then point is position 3--corrspond to number "3"
       */

      private int getPoint(int intArray[])
      {
          for(int i=intArray.length-2;i>=0;i--)
          	if (intArray[i]>=intArray[i+1])
          		continue;
          	else return i;
          return -1;
      }

      /* get the index from array according to the element value
       */

      private int getIndex(int intArray[], int element)
      {
          for(int i=0;i<intArray.length;i++)
              if (element==intArray[i])
              		return i;

          return -1;
      }

      private int getListSub(List intList, int element)
      {
          for(int i=0;i<intList.getItemCount();i++)
          	if (Integer.parseInt(intList.getItem(i))==element)
          		return i;
          return -1;
      }

      private List addInOrder(List intList, int element)
      {
          if (intList.getItemCount()==0)
          	intList.add(Integer.toString(element));
          else {
                int i=0;
          	for(i=0;i<intList.getItemCount();i++)
          	    //might have problem
          	    if (Integer.parseInt(intList.getItem(i))>element){
          	    	intList.add(Integer.toString(element), i);
          		break;
          	    }
          	if (i==intList.getItemCount())
          		intList.add(Integer.toString(element));
          }

          return intList;
      }

      public void printArr(int intArray[])
      {
          for (int i=0;i<intArray.length;i++)
          	System.out.print(intArray[i]+"  ");

          System.out.println();
      }

      /* For tree group(>2) comparison, should set comp2 as false
       */

      public void setCompFlag(boolean flag)
      {
          comp2 = flag;

      }

      public boolean getCompFlag()
      {
          return comp2;
      }

      /* Get the mast for a group of trees.
       * algorithm: 1.Pick two trees from the group and get mast1
       * 	    2.Pick an uncompared tree t from the group and
       * 	      compare mast1 with t and gete mast2
       *            3.Continue to pick tree and compare until there
       *              is no tree left.
       *            4.try all the possible order and do 1 to 4.
       * output: mast tree and distance for the group of trees.
       */

      public Stack treeGroupMast(Tree []treeGroup)
      {
          MastTab first2Mast;
          MastTab temp2Mast;
          Stack umastStk=new Stack();
          Stack tempStk1;
          Stack tempStk2;
          TreeNode tempTree1=new TreeNode();
          TreeNode tempTree2=new TreeNode();
          TreeNode tempComparedTree;
          TreeNode tempMast;

          //the first tree
          if (treeGroup.length>2)
              comp2 = GROUPCOMP;
          else comp2 = TWOCOMP;

          if (treeGroup.length>1){
              first2Mast = new MastTab(treeGroup[0], treeGroup[1]);
              if (!comp2)
       		  first2Mast.setCompFlag(GROUPCOMP);

              umastStk=new Stack();
              umastStk=copy_stk(umastStk, first2Mast.umast());
              int t=0;
       	      while( t<umastStk.size()){
       		  if (((TreeNode)umastStk.elementAt(t)).getLeafSize()<3)
       			umastStk.remove(t);
       		  else t++;
       	      }

              for(int i=2;i<treeGroup.length;i++){
                  tempComparedTree = new TreeNode();
                  tempComparedTree = (TreeNode)treeGroup[i].getRoot();
                  tempStk2=new Stack();
                  for(int j=0;j<umastStk.size();j++){
                  	tempMast=new TreeNode((TreeNode)umastStk.elementAt(j));
                  	temp2Mast = new MastTab(tempMast, tempComparedTree);
                  	temp2Mast.setCompFlag(GROUPCOMP);
          		tempStk1=new Stack();
          		tempStk1=copy_stk(tempStk1, temp2Mast.umast());
          		if (comp2){
          			if (!tempStk2.empty()){
       			    	    tempTree1=(TreeNode)tempStk1.elementAt(0);
       			            tempTree2=(TreeNode)tempStk2.elementAt(0);
       			            if (tempTree1.getLeafSize()>tempTree2.getLeafSize()){
       			           	tempStk2.clear();
       			           	tempStk2=copy_stk(tempStk2, tempStk1);
       			    	    }
       			    	    else if (tempTree1.getLeafSize()==tempTree2.getLeafSize())
       			           		tempStk2=push_stk(tempStk2, tempStk1);
       				}
       				else tempStk2=push_stk(tempStk2,tempStk1);
       			}
       			else {
       			       	 tempStk2=push_stkGrp(tempStk2, tempStk1);
       			     }
          	  }
          	  umastStk.clear();
          	  umastStk=copy_stk(umastStk, tempStk2);

          	  int m=0;
       		  while(m<umastStk.size()){
       			if (((TreeNode)umastStk.elementAt(m)).getLeafSize()<3)
       				umastStk.remove(m);
       			else m++;
       		  }

              } //end of for
          }  //end of if

       	  return umastStk;
      }

      /* print the mast table
       */
      public void printTab()
      {
          int i, j, k;

      		System.out.println("print mast table");
                for(i=0;i<uLeng;i++){
                    for(j=0;j<tLeng;j++)
                    	if (mastTab[i][j].empty())
          			System.out.print("empty  ");
          		else {
          		        System.out.print("(");
          			for(k=0;k<mastTab[i][j].size();k++)
          			    System.out.print(((TreeNode)mastTab[i][j].elementAt(k)).getGeneLabel()+",  ");
          			System.out.print(")  ");
          		}

          	    System.out.println();
          	}
       }

      private void printRow(Stack []imRow)

      {

          for(int i=0;i<imRow.length;i++){

              if (imRow[i].empty())

                  System.out.print("empty  ");

              else {

                  	System.out.print("(");
			for(int j=0;j<imRow[i].size();j++)

			    System.out.print(((TreeNode)imRow[i].elementAt(j)).getGeneLabel()+",  ");

			System.out.print(")  ");

		   }
	  }
	  System.out.println();


      }

      /* print one element of mast table
       */

      private void printTab(int uPos, int tPos)
      {
          if (mastTab[uPos][tPos] == null)
          	System.out.println("Element: "+uPos + "," + tPos + ": "+ "null");
          else System.out.println("Element: "+uPos+","+tPos+":  "+ ((TreeNode)mastTab[uPos][tPos].elementAt(0)).getGeneLabel());
      }

      /* print the tree element in the mast Table
       */

      private void printTree(TreeNode node)
      {
          Tree tree = new Tree();

          tree.setRoot(node);

          System.out.println("start to print tree");
          tree.displayTree();
          System.out.println("Finish print tree");
      }

        public static void main(String args[])
        {
            // These two strings contains the nexus format of tree files

            String t1 = new String();
            String t2 = new String();

            // This is the tree file contain nexus format

            String importFile = new String("file.nex");

            importFile=args[0];

            // The two trees need compared.

            Tree tree1;
            Tree tree2;
            Tree result = new Tree();

            MastTab mastTab;


            // read these two strings from input file

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
                String t[];


                treeNum=Integer.parseInt(in.readLine());


      		t=new String[treeNum];

      		for(i=0;i<treeNum;i++)
      		    t[i]=new String();

      		i=0;
                while (i<treeNum){
                    ch=(char)in.read();

                    while(ch!=';'){
                    	if ((ch!='\0')&&(ch!='\n')&&(ch!='\t')&&(ch!=' '))
                    	    t[i]=t[i]+ch;
                    	ch=(char)in.read();


                    }
                    t[i]=t[i]+ch;
                    i++;
                }

                in.close();

		Tree treeGroup[]=new Tree[treeNum];
		Set set=new HashSet();
		for (i=0;i<treeNum;i++){
		    System.out.println(t[i]);
		    treeGroup[i]=new Tree(t[i], set , "");

		}

		System.out.println("New tree: ");
		treeGroup[0].displayTree();
		System.out.println("New tree: ");
		treeGroup[1].displayTree();

		MastTab mast=new MastTab();
		mast.treeGroupMast(treeGroup);

		/*
		System.out.println(t[0]);
                System.out.println(t[1]);

                tree1=new Tree(t[0]);
                tree2=new Tree(t[1]);

                System.out.println("Begin to print tree1");

                tree1.displayTree();

                System.out.println("Begin to print tree2");

                tree2.displayTree();

                System.out.println("Begin to get mast tree");

                mastTab = new MastTab(tree1, tree2);

                mastTab.fillTab();

                System.out.println("Start to print mast rooted tree");

                TreeNode node=new TreeNode();

                System.out.println("MAST Stack has "+mastTab.resMast().size()+"  trees");

                Tree ttemp=new Tree();
                if (!mastTab.resMast().empty())
                    for(i=0;i<mastTab.resMast().size();i++){
                	node=(TreeNode)(mastTab.resMast().elementAt(i));
                	System.out.println("Get MAST "+node.getGeneLabel());
                	ttemp.setRoot(node);
                	ttemp.displayTree();
                    }


                //mastTab.printTab();



                //for debug--------------------------------------------
   		//System.out.println("Mast Tree has "+((TreeNode)mastTab.resMast().elementAt(0)).getLeafSize()+" leaves");
   		//-----------------------------------------------

                //result.displayTree();


		//for debug----------------------
		System.out.println();
		System.out.println("Start UMAST tree!");
		//------------------------------------

                mastTab.umast();

                //node=(TreeNode)mastTab.umast().elementAt(0);

                System.out.println("Get Umast Tree: ");


                //result.setRoot(node);

                //result.displayTree();

                mastTab.print_stk();

             	System.out.println("Tree-to-Tree distance");

             	double distance=mastTab.treeDis();

             	System.out.println(distance);
             	*/


                System.out.println("End!");

            }
            catch(Exception e){
            	System.out.println("Exception: "+e.getMessage());
                e.printStackTrace();
            }

       }
}










