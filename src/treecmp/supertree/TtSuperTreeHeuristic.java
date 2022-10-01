/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.supertree;

import treecmp.metric.Metric;
import treecmp.metric.TripletMetric;

/**
 *
 * @author Damian
 */
public class TtSuperTreeHeuristic extends AbstractSuperTreeHeuristic{

    private Metric metric = new TripletMetric();
    @Override
    public Metric getMetric() {
        return metric;
    }

    public TtSuperTreeHeuristic() {
        metric.setName("TT");
    }

}
