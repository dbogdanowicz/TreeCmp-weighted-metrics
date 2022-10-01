/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.supertree;

import treecmp.metric.MastBinMetric;
import treecmp.metric.Metric;

/**
 *
 * @author Damian
 */
public class MastSuperTreeHeuristic extends AbstractSuperTreeHeuristic{

    private Metric metric = new MastBinMetric();

    public MastSuperTreeHeuristic() {
        metric.setName("MAST");
    }

    @Override
    public Metric getMetric() {
        return metric;
    }

}
