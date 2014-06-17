package de.braste.SPfB;

@SuppressWarnings("UnusedDeclaration")
public class HelperThread extends Thread {

    @Override public void run()
    {
        Runtime rt = Runtime.getRuntime();
        long isFree = rt.freeMemory();
        long wasFree;
        int i = 0;
        do {
            rt.runFinalization();
            rt.gc();
            wasFree = isFree;
            isFree = rt.freeMemory();
            i++;
        }
        while (isFree > wasFree && i <= 100);
    }
}
