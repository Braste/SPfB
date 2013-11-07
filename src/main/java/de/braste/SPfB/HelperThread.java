package de.braste.SPfB;

public class HelperThread extends Thread {

    @Override public void run()
    {
        Runtime rt = Runtime.getRuntime();
        long isFree = rt.freeMemory();
        long wasFree = isFree;
        int i = 0;
        while (isFree > wasFree && i <= 100)
        {
            rt.runFinalization();
            rt.gc();
            wasFree = isFree;
            isFree = rt.freeMemory();
            i++;
        }
    }
}
