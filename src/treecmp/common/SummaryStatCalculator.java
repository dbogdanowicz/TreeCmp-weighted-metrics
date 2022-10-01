/** This file is part of TreeCmp, a tool for comparing phylogenetic trees
    using the Matching Split distance and other metrics.
    Copyright (C) 2011,  Damian Bogdanowicz

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package treecmp.common;

import java.util.List;
import java.util.Locale;
import pal.tree.Tree;
import treecmp.io.ResultWriter;
import treecmp.config.IOSettings;


public class SummaryStatCalculator {


    private StatCalculator statCalc;
    private long count;
    private double sum;
    private double sq_sum;
    private double min;
    private double max;

    public SummaryStatCalculator() {

        this.count=0;
        this.sum=0.0;
        this.sq_sum=0.0;

        this.min=Double.MAX_VALUE;
        this.max=-Double.MAX_VALUE;
    }


    /**
     *
     * @param _met
     */
    public SummaryStatCalculator(StatCalculator statCalc) {

        //call non-parameter constructor
        this();
        this.statCalc = statCalc;
    }

 

   public void clear()
  {
        this.count=0;
        this.sum=0.0;
        this.sq_sum=0.0;

        this.min=Double.MAX_VALUE;
        this.max=-Double.MAX_VALUE;

    }


  public double getMax()
  {
   return this.max;
  }

  public double getMin()
  {
    return this.min;
  }

  public double getAvg()
  {
      double avg=Double.POSITIVE_INFINITY;
      if (count>0)
          avg=this.sum/(double)count;

      return avg;
  }

  public double getVariance()
  {
      double var=Double.POSITIVE_INFINITY;
      double avg;
      if (count>0)
      {
          avg=this.getAvg();
          var=this.sq_sum/(double)count-avg*avg;
      }
      return var;
  }

  public double getStd()
  {
        double std=Double.POSITIVE_INFINITY;
        double var;

        if(count>0)
        {
            var=this.getVariance();
            std=Math.sqrt(var);
        }
        return std;
  }


  public long getCount()
  {
      return this.count;
  }

  public void insertValue(double dist)
  {
        sum+=dist;
        count++;
        sq_sum+=dist*dist;

        if(dist<min) min=dist;
        if(dist>max) max=dist;

  }

    public String getName() {
        return this.statCalc.getName();
    }

    public String getCommandLineName() {
        return this.statCalc.getCommandLineName();
    }

    
    public List<Tree[]> getMaxDistTrees() {
        return this.statCalc.getMaxDistTrees();
    }

    public List<Tree[]> getMinDistTrees() {
        return this.statCalc.getMinDistTrees();
    }

    public static void printSummary(ResultWriter out, SummaryStatCalculator[] sStatCalc) {
        int size = sStatCalc.length;
        String separator = IOSettings.getIOSettings().getSSep();
        if (IOSettings.getIOSettings().isGenSummary()) {
            String line = "";

            line = "---------";
            out.setText(line);
            out.write();
            line = "Summary:";

            out.setText(line);
            out.write();

            //name-avg-std-min-max-count
            line = "Name" + separator;
            line += "Avg" + separator;
            line += "Std" + separator;
            line += "Min" + separator;
            line += "Max" + separator;
            line += "Count";

            out.setText(line);
            out.write();

            for (int i = 0; i < size; i++) {
                line = sStatCalc[i].getName() + separator;
                line += sStatCalc[i].getAvg() + separator;
                line += sStatCalc[i].getStd() + separator;
                line += sStatCalc[i].getMin() + separator;
                line += sStatCalc[i].getMax() + separator;
                line += sStatCalc[i].getCount();
                out.setText(line);
                out.write();
            }
        }
        if (IOSettings.getIOSettings().isExtreme()) {
            String line = "---------";
            out.setTextAndWrite(line);
            line = "Extreme trees:";
            out.setTextAndWrite(line);
            for (int i = 0; i < size; i++) {
                line = String.format(Locale.US, "Min dist trees for metric %s, dist: %f, num of pairs: %d",
                        sStatCalc[i].getName(), sStatCalc[i].getMin(), sStatCalc[i].getMinDistTrees().size());
                out.setTextAndWrite(line);
                printPairOfTreesList(out, sStatCalc[i].getMinDistTrees());

                line = String.format(Locale.US, "Max dist trees for metric %s, dist: %f, num of pairs: %d",
                        sStatCalc[i].getName(), sStatCalc[i].getMax(), sStatCalc[i].getMaxDistTrees().size());
                out.setTextAndWrite(line);
                printPairOfTreesList(out, sStatCalc[i].getMaxDistTrees());
            }
        }
    }

    private static void printPairOfTreesList(ResultWriter out, List<Tree[]> treePairList) {
        for (int i = 0; i < treePairList.size(); i++) {
            Tree t1 = treePairList.get(i)[0];
            Tree t2 = treePairList.get(i)[1];
            String line = String.format("Pair: %d", i + 1);
            out.setTextAndWrite(line);
            out.setTextAndWrite(NodeUtilsExt.treeToSimpleString(t1, false) + ";");
            out.setTextAndWrite(NodeUtilsExt.treeToSimpleString(t2, false) + ";");
        }
    }
}
