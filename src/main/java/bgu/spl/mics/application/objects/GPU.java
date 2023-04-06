package bgu.spl.mics.application.objects;

import com.sun.org.apache.xpath.internal.operations.Mod;

import java.util.LinkedList;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {


    /**
     * Enum representing the type of the GPU.
     */
    public enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
    private Model model;
    private int index;
    private final Cluster c = Cluster.getInstance();
    private int whereToSend;
    private LinkedList<DataBatch> dataBatches;
    private LinkedList<DataBatch> processedDataBatches;
    private int numberOfBatches;
    private int timeToProcessBatch;
    private int countData;
    private int sentDate;

    public GPU(Type type, int index) {
        this.type = type;
        this.index=index;
        model = null;
        whereToSend = -1;
        if(type==Type.RTX3090) {
            numberOfBatches = 32;
            timeToProcessBatch=1;
        }
        else if(type==Type.RTX2080) {
            numberOfBatches = 16;
            timeToProcessBatch=2;
        }
        else{
            numberOfBatches = 8;
            timeToProcessBatch=4;
        }
        countData=0;
        sentDate=0;
        processedDataBatches=new LinkedList<DataBatch>();
        dataBatches=new LinkedList<DataBatch>();


    }

    public int activeCpu() {
        whereToSend = c.getCurrCpu();
        return whereToSend;
    }

    public void buildBatches(){
            Data temp = model.getData();
            int start=1;
            int size=temp.getSize();
            int numOfBatches=size/1000;
            for(int i=0;i<numOfBatches;i++){
                DataBatch add=new DataBatch(temp, start);
                dataBatches.add(add);
                start+=1000;
        }
    }

    public void finishProcessing(){
        whereToSend=-1;
        dataBatches=new LinkedList<DataBatch>();
        model=null;
        countData=0;
        sentDate=0;
    }

    public void getProcessedBatch(DataBatch d){
        sentDate-=1;
        d.setGpuTimeToProcess(timeToProcessBatch);
        processedDataBatches.add(d);

    }
    public void processData(){
        if(!processedDataBatches.isEmpty()) {
            for (int i = 0; i < processedDataBatches.size(); i++) {
                processedDataBatches.get(i).reduceGpuT();
            }
        }
    }
    public void fetchModel(Model mo){
            model = mo;
            model.setStatus(Model.Status.Training);
    }

    public LinkedList<DataBatch> getBatches(){
        return dataBatches;
    }

    public LinkedList<DataBatch> getProcessedDataBatches(){
        return processedDataBatches;
    }
    public void removeBatch(int i){processedDataBatches.remove(i);}
    public Model getModel(){return model;}
    public int getNumberOfBatches(){return numberOfBatches;}
    public int getCountData(){return countData;}
    public void addData() {
        if(countData<dataBatches.size())
            countData++;
    }
    public int getSentDate(){return sentDate;}
    public void addSentData(){sentDate++;}
    public int getIndex(){return index;}

}
