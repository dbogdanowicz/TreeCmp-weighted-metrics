package treecmp.treeanalyzer;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.awt.List;

class DcedInfo{
    int dOrder=0;
    int subMastSize=0;
	LinkedList dcedList=new LinkedList();
    boolean matched=false;
    
    public void display()
    {
        System.out.println("Order: "+dOrder+" ,SubMastSize: "+subMastSize);
        Iterator it = dcedList.iterator();
        
        while (it.hasNext()){
        	int dced[]=((DcedMast)it.next()).lcat;
        	for(int j=0;j<dced.length;j++)
        		System.out.print(dced[j]+"---");
        	System.out.println();
        }
    }
}

public class Lcat
{
    public int leaf1;
    public int leaf2;
    public int []lcats;
    public boolean NPre;	//biggest lcat, so no predecence
    public Set mastSet;		//The leaf set of mast of the current one
    public int mastSize=0;	//The mast size
    public Stack decends=new Stack();	/* the smaller lcats of the current one
    					 * Each element of stack is a DcedInfo object
    					 * The hashCode is the dOrder with a set of unrepeated mast
    					 * and size
    					 */
    public boolean done=false;
    public boolean lessbig = false;
    		
    public Lcat()
    {
    }
    
    public Lcat(int groupSize)
    {
        lcats = new int[groupSize];
    }
    
    public void fillLcat(int []inLcats)
    {
        if (inLcats!=null)
            if (lcats!=null){
        	if (lcats.length!=inLcats.length){
        		//System.out.println("Size differ!");
        	}
        	else {
        		for(int i=0;i<inLcats.length;i++)
        			lcats[i]=inLcats[i];
        	}
            }
            else {
        	lcats=new int[inLcats.length];
        	for(int i=0;i<inLcats.length;i++)
        		lcats[i]=inLcats[i];
            }
    }
    
    /* use a set of linked list to represent the subtree
     * with various direction
     * direct (0 0 1 1) mast1--mast2--mast3--..
     * direct (1 1 0 0) mast4--mast5--mast6--..
     * direct (0 1 0 1) mast7--mast8--mast9--..
     * direct (1 0 1 0) mast10--mast11--mast12--..
     */
     
    public void addDecend(int decend, int order)
    {
        boolean existOrder=false;
        int i=0;
        
        for(i=0;i<decends.size();i++){
            if (((DcedInfo)decends.elementAt(i)).dOrder==order){
            	existOrder=true;
            	break;
            }
        }
        if (existOrder) {
            ((DcedInfo)decends.elementAt(i)).dcedList.add(new Integer(decend));
        }    
        else {
        	DcedInfo dcedInfo=new DcedInfo();
        	dcedInfo.dcedList.add(new Integer(decend));
        	dcedInfo.dOrder=order;
        	decends.push(dcedInfo);
        }
    } 
    
    public void display(String []leafArray)
    {
        
        if (NPre) {
            System.out.println("["+leafArray[leaf1]+","+leafArray[leaf2]+"] Biggest");
        }
        else {
            System.out.print("["+leafArray[leaf1]+","+leafArray[leaf2]+"] ("+lcats[0]);
            for(int i=1;i<lcats.length;i++)
                System.out.print(", "+lcats[i]);
            System.out.println(")");
        }
    }
                  
}
     
