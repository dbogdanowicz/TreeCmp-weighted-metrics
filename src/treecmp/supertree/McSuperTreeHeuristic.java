/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.supertree;

import treecmp.metric.MatchingClusterMetricOptRF;
import treecmp.metric.Metric;

/**
 *
 * @author Damian
 */
public class McSuperTreeHeuristic extends AbstractSuperTreeHeuristic{

    private Metric metric = new MatchingClusterMetricOptRF();

    public McSuperTreeHeuristic() {
        metric.setName("MC");
    }

    @Override
    public Metric getMetric() {
        return metric;
    }

}
