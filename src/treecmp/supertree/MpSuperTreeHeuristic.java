/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.supertree;

import treecmp.metric.MatchingPairMetric;
import treecmp.metric.Metric;

/**
 *
 * @author Damian
 */
public class MpSuperTreeHeuristic extends AbstractSuperTreeHeuristic{

    private Metric metric = new MatchingPairMetric();

    public MpSuperTreeHeuristic() {
        metric.setName("MP");
    }

    @Override
    public Metric getMetric() {
        return metric;
    }

}
