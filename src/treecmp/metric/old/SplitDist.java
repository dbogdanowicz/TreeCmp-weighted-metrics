/*
 * SplitDist.java
 *
 * Created on 10 luty 2008, 00:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package treecmp.metric.old;

/**
 *
 * @author Damian
 */
public class SplitDist {
    
    /** Creates a new instance of SplitDist */
    public SplitDist() {
    }
    
    //dist([A1|A2],[B1|B2])=0.5*min((A1 xor B1)+(A2 xor B2),(A1 xor B2)+(A2 xor B1)) 
    public static double getDist1(boolean[] split1,boolean[] split2)
    {
       
        int n=split1.length;
        int eq=0,neq=0,q;
        double metric=0.0; 
        
        for(int i=0;i<n;i++)
        {
            if(split1[i]==split2[i]) eq++;
            else neq++;
        }
        
       
        if(eq > neq) q=eq;
        else q=neq;
                
        return (double)(n-q); 
        
    }
    
    
    
}
