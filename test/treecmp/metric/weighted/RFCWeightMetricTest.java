/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.metric.weighted;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import pal.tree.Tree;
import treecmp.metric.RFClusterMetric;
import treecmp.test.util.TreeCreator;

/**
 *
 * @author Damian
 */
public class RFCWeightMetricTest {
    
    @Test
    public void testDistance() {
        
        RFCWeightMetric instance = new RFCWeightMetric();
        RFClusterMetric rc = new RFClusterMetric();
            
        Tree su1 = TreeCreator.getWeightedSimpleUnitT1();
        Tree su2 = TreeCreator.getWeightedSimpleUnitT2();
        double rc1 = rc.getDistance(su1, su2);
        double inst1 = instance.getDistance(su1, su2);
        assertEquals(rc1, inst1, 0.0);     
        
       
    }
}
