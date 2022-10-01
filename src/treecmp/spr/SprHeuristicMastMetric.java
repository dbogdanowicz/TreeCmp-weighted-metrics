/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.spr;

import treecmp.metric.MastMetric;
import treecmp.metric.Metric;

/**
 *
 * @author Damian
 */
public class SprHeuristicMastMetric extends SprHeuristicBaseMetric{

 @Override
protected Metric getMetric(){
    return new MastMetric();
 }
}
