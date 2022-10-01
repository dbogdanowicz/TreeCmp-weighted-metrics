/*
 * BiparteMetric.java
 *
 * Created on 2 maj 2007, 21:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package treecmp.metric.old;
import pal.tree.*;
import java.util.*;
import pal.misc.IdGroup;
import treecmp.common.AlignInfo;
import treecmp.metric.Metric;
/**
 *
 * @author Damian
 */
public class BiparteMetric implements Metric {
   
   private String name="Bipart";
    
    
    /** Creates a new instance of BiparteMetric */
    public BiparteMetric() {
    }
 
    
   
private static HashMap<Integer,HashSet< TreeSet<Integer>>> getLcaMap(Tree tree,IdGroup id)
{
    
   HashMap<Integer,HashSet< TreeSet<Integer>>> map=new HashMap<Integer,HashSet< TreeSet<Integer>>>();
   Node n1,n2,nLca;
   int i,j;
   Integer idLca,id1,id2;
   TreeSet<Integer> idSet;
   int size=tree.getExternalNodeCount();
   
   
   //struktura nr wezla wewnetrzengo->{{1,2},{3,4},{6,7}}
   for(i=0;i<size;i++)
   {
       n1=tree.getExternalNode(i);
       id1=(Integer)id.whichIdNumber(n1.getIdentifier().getName());
       for(j=i+1;j<size;j++)
       {
           n2=tree.getExternalNode(j);
           id2=(Integer)id.whichIdNumber(n2.getIdentifier().getName());
           
           //szukamy najblizszego wspolnego przodka
           nLca=NodeUtils.getFirstCommonAncestor(n1,n2);
           idLca=(Integer)nLca.getNumber();
           idSet=new TreeSet<Integer>();
           idSet.add(id1);
           idSet.add(id2);
           putInMap(idLca,idSet,map);
       }
       
       
   }
   
    
    
   return map; 
}
    

private static void putInMap(Integer id,TreeSet<Integer> idSet,HashMap<Integer,HashSet< TreeSet<Integer>>> map )
{
      
    
    HashSet< TreeSet<Integer>> idsSet;
     
    if(map.containsKey(id))
    {
        idsSet=map.get(id);
    }else
    {
        idsSet=new HashSet< TreeSet<Integer>>();
        map.put(id,idsSet);
    }
    
    idsSet.add(idSet);
    
    
}

private static int intersectionSize(Set s1,Set s2)
{
    
    int size=0;
    int size1=s1.size();
    int size2=s2.size();
    Set larger,smaller;
    
    if(size1>size2)
    {
        larger=s1;
        smaller=s2;
    }else
    {
      larger=s2;
      smaller=s1;  
        
    }
    
      for (Iterator it=larger.iterator(); it.hasNext(); ) {
        Object element = it.next();
        if(smaller.contains(element)) size++;
      }

    
    return size;
}

private static int xorSize(Set s1,Set s2)
{
    
    int size=0;
    int size1=s1.size();
    int size2=s2.size();
    size=size1+size2-2*intersectionSize(s1,s2);

    
    return size;
}


public static double getBiparteDistance(Tree tree1,Tree tree2){
    
    int interSize,size;
    int [] matching;
    double metric,sum;
    
    HashMap<Integer,HashSet< TreeSet<Integer>>> map1 =getLcaMap(tree1,TreeUtils.getLeafIdGroup(tree1));
    HashMap<Integer,HashSet< TreeSet<Integer>>> map2 =getLcaMap(tree2,TreeUtils.getLeafIdGroup(tree1));
    HashMap<Integer,HashSet< TreeSet<Integer>>> larger,smaller;
   int leafNumber=tree1.getExternalNodeCount();
   int size1= map1.size();
   int size2= map2.size();
   int i,j;
   if(size2 > size1)
   {
       larger=map2;
       smaller=map1;
       size=size2;
   }else
   {
       larger=map1;
       smaller=map2;
       size=size1;
   }
   double weights [][]=new double[size][size]; 
   
   BipartiteMatcher matcher=new  BipartiteMatcher(size);
   for(i=0;i<size;i++)
   {
       for(j=0;j<size;j++)
       {
           matcher.setWeight(i,j,0.0);
           weights[i][j]=0.0;
       }
       
   }
   
   
   
   
   i=0;
    for (Iterator<HashSet< TreeSet<Integer>>> it1=map1.values().iterator(); it1.hasNext(); ) {
        HashSet< TreeSet<Integer>> value1 = it1.next();
        j=0; 
        for (Iterator<HashSet< TreeSet<Integer>>> it2=map2.values().iterator(); it2.hasNext(); ) {
            HashSet< TreeSet<Integer>> value2 = it2.next();
            
            interSize=intersectionSize(value1,value2);
            matcher.setWeight(i,j,(double)interSize);
            weights[i][j]=(double)interSize;
            j++;
         }   
         i++;
   }

   
   matching =matcher.getMatching();
   sum=0.0;
   for(i=0;i<matching.length;i++)sum+=weights[i][matching[i]] ;
   metric=(double)leafNumber*((double)leafNumber-1.0)/2.0-sum;
   return metric;
}

public static double getBiparteDistance2(Tree tree1,Tree tree2){
    
    int interSize,size;
    int [] matching;
    double metric,sum;
    
    HashMap<Integer,HashSet< TreeSet<Integer>>> map1 =getLcaMap(tree1,TreeUtils.getLeafIdGroup(tree1));
    HashMap<Integer,HashSet< TreeSet<Integer>>> map2 =getLcaMap(tree2,TreeUtils.getLeafIdGroup(tree1));
    HashMap<Integer,HashSet< TreeSet<Integer>>> larger,smaller;
   int leafNumber=tree1.getExternalNodeCount();
   int size1= map1.size();
   int size2= map2.size();
   int i,j;
   if(size2 > size1)
   {
       larger=map2;
       smaller=map1;
       size=size2;
   }else
   {
       larger=map1;
       smaller=map2;
       size=size1;
   }
   double weights [][]=new double[size][size]; 
   
   BipartiteMatcher matcher=new  BipartiteMatcher(size);
   for(i=0;i<size;i++)
   {
       for(j=0;j<size;j++)
       {
           matcher.setWeight(i,j,0.0);
           weights[i][j]=0.0;
       }
       
   }
   
   
   
   
   i=0;
    for (Iterator<HashSet< TreeSet<Integer>>> it1=map1.values().iterator(); it1.hasNext(); ) {
        HashSet< TreeSet<Integer>> value1 = it1.next();
        j=0; 
        for (Iterator<HashSet< TreeSet<Integer>>> it2=map2.values().iterator(); it2.hasNext(); ) {
            HashSet< TreeSet<Integer>> value2 = it2.next();
            
            interSize=-xorSize(value1,value2);
            matcher.setWeight(i,j,(double)interSize);
            weights[i][j]=(double)interSize;
            j++;
         }   
         i++;
   }

   
   matching =matcher.getMatching();
   sum=0.0;
   for(i=0;i<matching.length;i++)sum+=weights[i][matching[i]] ;
   
   //mnozymy prze 1/2
   metric=-sum*0.5;
   return metric;
}

public static double getBiparteSplitDistance(Tree tree1,Tree tree2){
    
     
    int [] matching;
    int i,j;
    double metric,sum,w;
    
        
    SplitSystem s1=SplitUtils.getSplits(tree1);
    IdGroup idGroup = s1.getIdGroup();
    SplitSystem s2 = SplitUtils.getSplits(idGroup, tree2);
   
    int size=s1.getSplitCount();
    
    
   double weights [][]=new double[size][size]; 
   
   BipartiteMatcher matcher=new  BipartiteMatcher(size);
   for(i=0;i<size;i++)
   {
       for(j=0;j<size;j++)
       {
           matcher.setWeight(i,j,0.0);
           weights[i][j]=0.0;
       }
       
   }
   
   for(i=0;i<size;i++)
   {
       for(j=0;j<size;j++)
       {
            w=-SplitDist.getDist1(s1.getSplit(i),s2.getSplit(j));
            matcher.setWeight(i,j,w);
            weights[i][j]=w;
       }
       
   }
   
    
    
//    String split1=s1.toString();
//    String split2=s2.toString();
//   
//    System.out.println("Split1:" +s1+" Split2:"+s2 +"" +"\n");
   
   
   matching =matcher.getMatching();
   sum=0.0;
   for(i=0;i<matching.length;i++)sum+=weights[i][matching[i]] ;
   
   metric=-sum;
   
   return metric;
}


    public String getName()
    {
        return this.name;   
    }
    
    public double getDistance(Tree t1, Tree t2)
    {
        return BiparteMetric.getBiparteDistance2(t1,t2);   
    }

    public String getCommandLineName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setCommandLineName(String commandLineName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getDescription() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setDescription(String description) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void initData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isRooted() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isDiffLeafSets() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public AlignInfo getAlignment() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
