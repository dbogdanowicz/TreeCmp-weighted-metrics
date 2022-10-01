package treecmp.treeanalyzer;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.awt.List;

class DcedMast
{
	

	public int []lcat;
	

	public int subMast = 0;
	public Set leafSet = new HashSet();
	public Set mastSet = new HashSet();
}
public class POrder
{
    public int []lcats;
    public Set mastSet= new HashSet();
    public int mastSize=0;
    public LinkedList decends = new LinkedList();
    public boolean lessbig=false;
    public int []orderPair;	//contain the order pair with current biggest mast size
    
    public POrder()
    {
    }
    
    public POrder(int groupSize)
    {
        lcats = new int[groupSize];
    }
    
    public void iniLcat(int []inLcats, boolean lb)
    {
        if (inLcats!=null)
            if (lcats!=null){
            	if (lcats.length!=inLcats.length){
            	}
            	else {
            		for (int i=0;i<inLcats.length;i++)
            			lcats[i]=inLcats[i];
            	}
            }
            else {
            	    lcats = new int[inLcats.length];
            	    for(int i=0;i<inLcats.length;i++)
            	    	lcats[i]=inLcats[i];
            }
            
        lessbig = lb;
    }
    
    /* use a set of linked list to represent the subtree
     * with various direction
     * direct (0 0 1 1) mast1--mast2--mast3--..
     * direct (1 1 0 0) mast4--mast5--mast6--..
     * direct (0 1 0 1) mast7--mast8--mast9--..
     * direct (1 0 1 0) mast10--mast11--mast12--..
     */
     
     public void addDecend(int decend[], int order, int subMast, String leaf1, String leaf2)
    {
        boolean existOrder=false;
        int i=0;
        
        for(i=0;i<decends.size();i++){
            if ((((DcedInfo)decends.get(i)).dOrder)==order){
            	existOrder=true;
            	break;
            }
        }
        if (existOrder) {  
			DcedInfo dced = new DcedInfo();
			dced = (DcedInfo)decends.get(i);
		
			Iterator it = dced.dcedList.iterator();
			boolean same = false;
			while(it.hasNext()){
				int []temp = ((DcedMast)it.next()).lcat;
				boolean sameArray = true;
				
				for(int j=0;j<temp.length;j++){
					if (temp[j]!=decend[j]){
						sameArray=false;
						break;
					}
				}
				
				if (sameArray){
					same=true;
					break;
				}
			}
			
			if (!same){
				
				DcedMast dcedMast = new DcedMast();
				
				dcedMast.lcat = decend;
				dcedMast.subMast = subMast;
				dcedMast.leafSet.add(leaf1);
				dcedMast.leafSet.add(leaf2);
				Set leafSet = new HashSet();
				leafSet.add(leaf1);	//you may change the leaf name into its position
				leafSet.add(leaf2);
				dcedMast.mastSet.add(leafSet);
				
				((DcedInfo)decends.get(i)).dcedList.add(dcedMast);
			
			}
        }    
        else {
        	DcedInfo dcedInfo=new DcedInfo();
			DcedMast dcedMast = new DcedMast();
			dcedMast.lcat = decend;
			dcedMast.subMast = subMast;
			dcedMast.leafSet.add(leaf1);
			dcedMast.leafSet.add(leaf2);
			
			dcedInfo.dcedList.add(dcedMast);
			
			Set leafSet = new HashSet();
			leafSet.add(leaf1);
			leafSet.add(leaf2);
			dcedMast.mastSet.add(leafSet);
        	dcedInfo.dOrder=order;
        	decends.add(dcedInfo);
        }
    }
         
    public void display()
    {
        
        System.out.println("Display Partial Order: ");
        System.out.print("Main Lcat: ");
        
        for(int i=0;i<lcats.length;i++)
        	System.out.print(lcats[i]);
		if (lessbig)
			System.out.print("(lessbig)");
		
        System.out.println();
        
        for(int i=0;i<decends.size();i++)
        	((DcedInfo)decends.get(i)).display();
		System.out.println("Mast Size: "+mastSize);
		System.out.println("Mast Set: ");
		
		Iterator it=mastSet.iterator();
		while(it.hasNext()){
			Set leafSet = new HashSet();
			leafSet=(HashSet)it.next();
			Iterator it2 = leafSet.iterator();
			while(it2.hasNext()){
				System.out.print((String)it2.next()+" ");
			}
			System.out.println();
		}
    }
    
    public boolean sameLcat(int []inLcat)
    {
        if (lcats.length!=inLcat.length)
        	return false;
        else {
        	boolean isSame = true;
        	for(int i=0;i<lcats.length;i++){
        	    if (lcats[i]!=inLcat[i]){
        		isSame = false;
        		continue;
        	    }
        	}
        	if (isSame)
        	    return true;
        }
        
        return false;
    } 
}        