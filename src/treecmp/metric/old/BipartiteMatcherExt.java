/*
 * BipartiteMatcherExt.java
 *
 * Created on 24 listopad 2007, 23:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package treecmp.metric.old;
/**
 *
 * @author Damian
 */
public class BipartiteMatcherExt{
    
    /** Creates a new instance of BipartiteMatcherExt */
    
     double[][] _weights;
     int size;
    
    public BipartiteMatcherExt() {
       size=-1;
    }
   
     public BipartiteMatcherExt(int n) {
       
         size=n;
        _weights=new double[n][n];
    }
    
   
     
     public void setWeight(int i, int j, double w)
     {
        _weights[i][j]=w;
              
     }
     
     double getMaximumWeightPM()
     {
         
        BipartiteMatcher matcher = new BipartiteMatcher(size);
        for(int i=0;i<size;i++)
        {
            for(int j=0;j<size;j++)
            {
                matcher.setWeight(i,j,_weights[i][j]);
            
            }
       
        }
         
        int[] matching =matcher.getMatching();
        double sum=0.0;
        for(int i=0;i<matching.length;i++)sum+=_weights[i][matching[i]] ; 
        
        return sum;
     }
     
     double getMinimumWeightPM()
     {
  
        BipartiteMatcher matcher = new BipartiteMatcher(size);
        for(int i=0;i<size;i++)
        {
            for(int j=0;j<size;j++)
            {
                matcher.setWeight(i,j,-_weights[i][j]);
            
            }
       
        }
         
        int[] matching =matcher.getMatching();
        double sum=0.0;
        for(int i=0;i<matching.length;i++)sum+=_weights[i][matching[i]] ; 
                    
        return sum;
     }
     
}
