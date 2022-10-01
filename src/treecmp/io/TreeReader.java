/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.io;

import java.io.BufferedInputStream;
import pal.tree.Tree;

/**
 *
 * @author Damian
 */
public interface TreeReader {

    public Tree readNextTree();

    public int open();

    public void close();

    public int scan();

    public int getStep();

    public void setStep(int step);

    public int getNumberOfTrees();

    public int getEffectiveNumberOfTrees();
    
    public BufferedInputStream getInputStream();
}

