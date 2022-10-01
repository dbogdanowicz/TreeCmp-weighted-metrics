/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.supertree;

import treecmp.metric.Metric;
import treecmp.metric.RFClusterMetric;

/**
 *
 * @author Damian
 */
public class RfSuperTreeHeuristic extends AbstractSuperTreeHeuristic{

    private Metric metric = new RFClusterMetric();

    @Override
    public Metric getMetric() {
        return metric;
    }

    public RfSuperTreeHeuristic() {
        metric.setName("RC");
    }

}
