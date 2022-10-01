/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.supertree;

import treecmp.metric.Metric;
import treecmp.metric.NodalL2SplittedMetric;

/**
 *
 * @author Damian
 */
public class NsSuperTreeHeuristic extends AbstractSuperTreeHeuristic{

    private Metric metric = new NodalL2SplittedMetric();
    @Override
    public Metric getMetric() {
        return metric;
    }

    public NsSuperTreeHeuristic() {
        metric.setName("NS");
    }

}
