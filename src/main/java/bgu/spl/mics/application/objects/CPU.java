package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    private int cores;
    private LinkedList<DataBatch> batches;
    private int timeToProcess;
    private int countBatch;
    private long time;

    public CPU(int cores){
        this.cores=cores;
        batches=new LinkedList<DataBatch>();
        timeToProcess=32/cores;
        countBatch=0;
        time=0;
    }

    public void getBatch(DataBatch d){
        countBatch++;
        time++;
        d.setCpuTimeToProcess(timeToProcess);
        time+=d.getCpuTimeToProcess();
        batches.add(d);
    }

    public void processData(){
        time++;
        if(batches.size()>0) {
            for (int i = 0; i < batches.size(); i++) {
                time++;
                batches.get(i).reduceCpuT();
            }
        }

    }
    public LinkedList<DataBatch> getBatches(){
        return batches;
    }
    public int getCountBatch(){return countBatch;}
    public long getTime(){return time;}
}
