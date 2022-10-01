/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.metric.weighted;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import pal.tree.Tree;
import treecmp.metric.MatchingClusterMetricScaled;
import treecmp.metric.MatchingSplitMetricO3;
import treecmp.test.util.TreeCreator;

/**
 *
 * @author Damian
 */
public class MatchingClusterScaledWeightMetricTest {
    
    @Test
    public void testDistance() {
        
        MatchingClusterScaledWeightMetric instance = new MatchingClusterScaledWeightMetric();
        MatchingClusterMetricScaled mcs = new MatchingClusterMetricScaled();
            
        Tree su1 = TreeCreator.getWeightedSimpleUnitT1();
        Tree su2 = TreeCreator.getWeightedSimpleUnitT2();
        double mcs1 = mcs.getDistance(su1, su2);
        double inst1 = instance.getDistance(su1, su2);
        assertEquals(mcs1, inst1, 0.0);     
        
       
    }
}
