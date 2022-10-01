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

package treecmp.command;

import treecmp.common.StartTreeType;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import pal.misc.IdGroup;
import pal.tree.Tree;
import pal.tree.TreeUtils;
import treecmp.common.DifferentLeafSetUtils;
import treecmp.common.NodeUtilsExt;
import treecmp.common.ReportUtils;
import treecmp.common.StatCalculator;
import treecmp.common.SummaryStatCalculator;
import treecmp.common.TimeDate;
import treecmp.common.TreeCmpException;
import treecmp.common.TreeCmpUtils;
import treecmp.config.IOSettings;
import treecmp.io.ResultWriter;
import treecmp.io.FileTreeReader;
import treecmp.io.TreeReader;
import treecmp.metric.Metric;
import treecmp.random.RandomTreeGenerator;
import treecmp.supertree.AbstractSuperTreeHeuristic;
import treecmp.supertree.MastSuperTreeHeuristic;
import treecmp.supertree.McSuperTreeHeuristic;
import treecmp.supertree.MpSuperTreeHeuristic;
import treecmp.supertree.NsSuperTreeHeuristic;
import treecmp.supertree.RfSuperTreeHeuristic;
import treecmp.supertree.SuperTreeInputData;
import treecmp.supertree.SuperTreeResultData;
import treecmp.supertree.TtSuperTreeHeuristic;

public class RunSuperTreeCommand extends Command {

    private String heuristicName;
    private StartTreeType searchType;
    private String startTreeFile = null;
    private int startTreeNum = 0;
    public RunSuperTreeCommand(int paramNumber, String name, String heuristicName, StartTreeType type, String startTreeFile, int startTreeNum) {
        super(paramNumber, name);
        this.heuristicName = heuristicName;
        this.searchType = type;
        this.startTreeFile = startTreeFile;
        this.startTreeNum = startTreeNum - 1;
    }

    @Override
    public void run() throws TreeCmpException{
        super.run();

        out.init();
        reader.open();
        
        AbstractSuperTreeHeuristic stHeuristic = getStHeuristic();
        superTreeSearchExecute(reader, out, stHeuristic);
        
        reader.close();
        out.close();

    }

    private AbstractSuperTreeHeuristic getStHeuristic() {

        AbstractSuperTreeHeuristic heuristic = null;

        if (heuristicName.equals("rc")) {
            heuristic = new RfSuperTreeHeuristic();
        } else if (heuristicName.equals("mc")) {
            heuristic = new McSuperTreeHeuristic();
        } else if (heuristicName.equals("tt")) {
            heuristic = new TtSuperTreeHeuristic();
        } else if (heuristicName.equals("ns")) {
            heuristic = new NsSuperTreeHeuristic();
        } else if (heuristicName.equals("mp")) {
            heuristic = new MpSuperTreeHeuristic();
        }  else if (heuristicName.equals("mast")) {
            heuristic = new MastSuperTreeHeuristic();
        }
        return heuristic;
    }
  
    private void superTreeSearchExecute(TreeReader reader, ResultWriter out, AbstractSuperTreeHeuristic stHeuristic) throws TreeCmpException {

        long start = System.currentTimeMillis();
        if (searchType != StartTreeType.CREATE_TREE_FILE) {
            String msg = getMsgWithTmie("Start of searching...");
            System.out.println(msg);
            out.setText(msg);
            out.write();
        }

        Tree tree;
        List<Tree> inputTrees = new ArrayList<Tree>();
        List<IdGroup> idGroups = new ArrayList<IdGroup>();
        while ((tree = reader.readNextTree()) != null) {
            inputTrees.add(tree);
            idGroups.add(TreeUtils.getLeafIdGroup(tree));
        }
        reader.close();
        // FIXME!!!
        IdGroup idGroup = null;
        //IdGroup idGroup = TreeCmpUtils.mergeIdGroups(idGroups);
        if (searchType != StartTreeType.CREATE_TREE_FILE) {
            String msg = String.format("Metric name: %s, start tree type: %s", heuristicName, searchType.name());
            System.out.println(msg);
            out.setText(msg);
            out.write();
        }
        String msg;
        Tree initStartTree = null;
        switch (searchType) {
            case RANDOM:
                RandomTreeGenerator randomTreeGenerator = new RandomTreeGenerator(idGroup);
                initStartTree = randomTreeGenerator.generateYuleTree(true);
                break;
            case TREE_FROM_DATA:
                initStartTree = inputTrees.get(startTreeNum);
                msg = String.format("Number of start tree in data file: %d", startTreeNum+1);
                System.out.println(msg);
                out.setText(msg);
                out.write();
                break;
            case CUSTOM:
                /* we read only one tree from input file
                the rest of the file is ignored */
                FileTreeReader startTreeReader = new FileTreeReader(startTreeFile);
                startTreeReader.open();
                initStartTree = startTreeReader.readNextTree();
                startTreeReader.close();
                break;
            case CREATE_TREE_FILE:
                msg = getMsgWithTmie("Preparing list of pruned trees...");
                System.out.println(msg);
                FileTreeReader baseTreeReader = new FileTreeReader(startTreeFile);
                baseTreeReader.open();
                Tree baseTree = baseTreeReader.readNextTree();
                baseTreeReader.close();
                printPrunedTrees(baseTree, inputTrees);
                msg = getMsgWithTmie("List of pruned trees has been written.");
                System.out.println(msg);
                return;
        }
        msg = getMsgWithTmie("Checking starting  tree..");
        System.out.println(msg);
        out.setText(msg);
        out.write();

        msg = getMsgWithTmie("Start tree: " + NodeUtilsExt.treeToSimpleString(initStartTree, false) +";");
        System.out.println(msg);
        out.setText(msg);
        out.write();
      
        if (!TreeCmpUtils.isBinary(initStartTree, true)){
            msg = "Start tree is not binary. Starting resolving multifurcations at random....";
            System.out.println(msg);
            out.setText(msg);
            out.write();
            //Tree startTree = TreeCmpUtils.makeBinary(initStartTree, true, RandomProcessType.YULE);
            //initStartTree = startTree;
        } else {
            msg = "Start tree is binary";
            System.out.println(msg);
            out.setText(msg);
            out.write();
        }
        
        IdGroup initTreeIdGroup = TreeUtils.getLeafIdGroup(initStartTree);
        List<String> commonIds = DifferentLeafSetUtils.getCommonLeaves(initTreeIdGroup, idGroup);
        int initTreeIdNum = initTreeIdGroup.getIdCount();
        int commonIdNum = commonIds.size();
        int mergedIdNum = idGroup.getIdCount();
        if (initTreeIdNum > commonIdNum){
            throw new UnsupportedOperationException("Start tree contains new taxa - not supported yet");
        }
        msg = String.format("Number of taxa in start tree: %d ", initTreeIdNum);
        System.out.println(msg);
        out.setText(msg);
        out.write();
        msg = String.format("Number of all different taxa in data file: %d ", mergedIdNum);
        System.out.println(msg);
        out.setText(msg);
        out.write();

        if (mergedIdNum > initTreeIdNum) {/*initTreeIdNum == commonIdNum */
            msg = "Start tree does not contain all required taxa. Start extending the tree...";
            System.out.println(msg);
            out.setText(msg);
            out.write();
            RandomTreeGenerator randomTreeGenerator = new RandomTreeGenerator(idGroup);
            Tree startTree = randomTreeGenerator.generateYuleTree(initStartTree, true);
            initStartTree = startTree;
            
        } else {
            msg = "Start tree contains exaclty all required taxa (and no additional ones). No further prepocessing is needed.";
            System.out.println(msg);
            out.setText(msg);
            out.write();
        }
  
        SuperTreeInputData stInputData = new SuperTreeInputData();
        stInputData.setInputTrees(inputTrees);
        stInputData.setStartTree(initStartTree);

        SuperTreeResultData stResultData = stHeuristic.runSearch(stInputData, out);
        System.out.println("Dist: " + stResultData.getDist());
        System.out.println("Tree: " + NodeUtilsExt.treeToSimpleString(stResultData.getSuperTree(), false));

        msg = getMsgWithTmie("End of searching...");
        System.out.println(msg);
        out.setText(msg);
        out.write();
        long end = System.currentTimeMillis();
        double timeInSeconds = ((double) end - (double) start) / (double) 1000;

        msg = getMsgWithTmie(String.format(Locale.US, "Searching took: %.2f s", timeInSeconds));
        System.out.println(msg);
        out.setText(msg);
        out.write();

        IOSettings.getIOSettings().setPruneTrees(true);
        IOSettings.getIOSettings().setGenSummary(true);
        Metric[] metrics = {
            new RfSuperTreeHeuristic().getMetric(),
            new McSuperTreeHeuristic().getMetric(),
            new TtSuperTreeHeuristic().getMetric(),
            new NsSuperTreeHeuristic().getMetric(),
            new MpSuperTreeHeuristic().getMetric(),
            new MastSuperTreeHeuristic().getMetric()
        };

        StatCalculator[] statsMetrics = new StatCalculator[metrics.length];
        for (int i = 0; i < metrics.length; i++) {
            statsMetrics[i] = new StatCalculator(metrics[i]);
        }

        String separator = "----------";
        out.setText(separator);
        out.write();
        printSprs(stResultData);
        out.setText(separator);
        out.write();
        String header1 = "Distances to initial tree: " + NodeUtilsExt.treeToSimpleString(initStartTree, false);
        out.setText(header1);
        out.write();
        printDistances(initStartTree, inputTrees, statsMetrics);

        out.setText(separator);
        out.write();
        String header2 = "Distances to best tree: " + NodeUtilsExt.treeToSimpleString(stResultData.getSuperTree(), false);
        out.setText(header2);
        out.write();
        printDistances(stResultData.getSuperTree(), inputTrees, statsMetrics);

    }

    private String getMsgWithTmie(String msg) {
        return TimeDate.now() + ": " + msg;
    }

    private void printDistances(Tree refTree, List<Tree> trees, StatCalculator[] metrics) throws TreeCmpException {
        int mSize = metrics.length;
        int num = 1;
        String row = "";
        //initialize summary stat calculators
        SummaryStatCalculator[] sStatCalc = new SummaryStatCalculator[mSize];
        for (int i = 0; i < mSize; i++) {
            sStatCalc[i] = new SummaryStatCalculator(metrics[i]);
        }

        String head = ReportUtils.getHeaderRow(metrics, true);
        out.setText(head);
        out.write();

        for (Tree tree : trees) {
            for (int i = 0; i < metrics.length; i++) {
                double val = metrics[i].getDistance(refTree, tree);
                //summary
                sStatCalc[i].insertValue(val);
            }
            //set -1 to indicate ref tree mode
            row = ReportUtils.getResultRow(num, num, -1, metrics);
            out.setText(row);
            out.write();
            num++;
        }

        //print summary data to file
        SummaryStatCalculator.printSummary(out, sStatCalc);
    }

    private void printSprs(SuperTreeResultData stResultData) {
        List<Double> distList = stResultData.getDistList();
        int sprCount = 0;
        String header = "Spr iteration\t Distance";
        out.setText(header);
        out.write();
        for (Double d : distList) {
            String row = String.format(Locale.US, "%d\t%f", sprCount, d);
            out.setText(row);
            out.write();
            sprCount++;
        }

    }
    
    private void printPrunedTrees(Tree baseTree, List<Tree> inputTrees) {
        for (Tree inputTree : inputTrees) {
            Tree[] prunedTrees = DifferentLeafSetUtils.pruneTrees(baseTree, inputTree);
            out.setText(NodeUtilsExt.treeToSimpleString(prunedTrees[0], false)+";");
            out.write();
            out.setText(NodeUtilsExt.treeToSimpleString(prunedTrees[1], false)+";");
            out.write();
        }
    }
}
