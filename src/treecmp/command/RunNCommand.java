/**
 * This file is part of TreeCmp, a tool for comparing phylogenetic trees using
 * the Matching Split distance and other metrics. Copyright (C) 2011, Damian
 * Bogdanowicz
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package treecmp.command;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import treecmp.common.ProgressIndicator;
import treecmp.common.StatCalculator;
import treecmp.common.SummaryStatCalculator;
import pal.tree.Tree;
import treecmp.common.AlignWriter;
import treecmp.common.ReportUtils;
import treecmp.common.TreeCmpException;
import treecmp.io.ResultWriter;
import treecmp.io.FileTreeReader;
import treecmp.config.ActiveMetricsSet;
import treecmp.io.TreeReader;
import treecmp.metric.Metric;

public class RunNCommand extends Command {

    public RunNCommand(int paramNumber, String name) {
        super(paramNumber, name);
    }

    public RunNCommand(int paramNumber, String name, int paramValue) {
        super(paramNumber, name);
        this.param = paramValue;
    }

    @Override
    public void run() throws TreeCmpException {
        super.run();
        argsFileCompareExecute(reader, this.getParam(), out);
    }

    private void argsFileCompare(TreeReader reader, int spanSize, ResultWriter out, StatCalculator[] metrics) throws TreeCmpException {

        int numberOfArgs = getArgsNum(reader);

        out.init();
        reader.open();
        pal.tree.Tree tree1, tree2;
        ArrayList<Tree> tree_vec = new ArrayList<Tree>();
        String row = "";
        int k, num, base;
        int n = 0;
        double val = 0;
        int mSize = metrics.length;

        //initialize summary stat calculators
        SummaryStatCalculator[] sStatCalc = new SummaryStatCalculator[mSize];
        for (int i = 0; i < mSize; i++) {
            sStatCalc[i] = new SummaryStatCalculator(metrics[i]);
        }

        String head = ReportUtils.getHeaderRow(metrics);
        out.setText(head);
        out.write();

        int maxIt = numberOfArgs * spanSize;

        int counter = 1;
        ProgressIndicator progress = new ProgressIndicator();
        progress.setMaxVal(maxIt);
        progress.setPrintInterval(600);
        progress.setPrintPercentInterval(5.0);
        progress.init();

        int sectionTreeSize = spanSize+1;
        //System.out.println(head);
        num = 0;
        do {
            n = 0;
            tree_vec.clear();
            findSection(reader);
            do {
                tree1 = reader.readNextTree();
                if (tree1 != null) {
                    tree_vec.add(tree1);
                    n++;
                }
            } while (tree1 != null && n < sectionTreeSize);

            //comparing trees
            int N = tree_vec.size();

            if (N > 1) {
                for (k = 0; k < metrics.length; k++) {
                    metrics[k].clear();
                }
                tree1 = tree_vec.get(0);
                for (int i = 1; i < N; i++) {
                    tree2 = tree_vec.get(i);

                    for (k = 0; k < metrics.length; k++) {
                        val = metrics[k].getDistance(tree1, tree2);
                        sStatCalc[k].insertValue(val);
                    }
                    //print row statistic
                    base = num + 1;
                    row = ReportUtils.getResultRow(counter, base, base + i, metrics);
                    out.setText(row);
                    out.write();
                    progress.displayProgress(counter);
                    counter++;
                }
                num += spanSize;
            }
        } while (tree1 != null);

        //print summary data to file
        SummaryStatCalculator.printSummary(out, sStatCalc);

        reader.close();
        out.close();
    }

    public void argsFileCompareExecute(TreeReader reader, int spanSize, ResultWriter out) throws TreeCmpException {

        Metric[] metrics = ActiveMetricsSet.getActiveMetricsSet().getActiveMetricsTable();
        StatCalculator[] statsMetrics = new StatCalculator[metrics.length];

        for (int i = 0; i < metrics.length; i++) {
            statsMetrics[i] = new StatCalculator(metrics[i]);
        }
        argsFileCompare(reader, spanSize, out, statsMetrics);
    }

    private int getArgsNum(TreeReader reader) {
        reader.open();
        int arg = 0;
        while (findSection(reader)) {
            arg++;
        }
        reader.close();
        return arg;
    }

    private boolean findSection(TreeReader reader) {
        boolean found = false;
        int c = '/';
        int prev = -2;
        int i = -2;
        try {
            do {
                prev = i;
                i = reader.getInputStream().read();
            } while (i != -1 && !(i == c && prev == c));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (i == c && prev == c) {
            found = true;
        }
        return found;
    }
}