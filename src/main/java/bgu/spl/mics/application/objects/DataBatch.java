package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {

    private Data data;
    private int start_index;
    private int gpuTimeToProcess;
    private int cpuTimeToProcess;

    public DataBatch(Data data, int start_index){
        this.data=data;
        this.start_index=start_index;
        gpuTimeToProcess=-1;
        cpuTimeToProcess=-1;
    }

    public Data getData(){
        return data;
    }
    public int getStart_index(){
        return start_index;
    }

    public void setGpuTimeToProcess(int t){
        if(gpuTimeToProcess==-1)
            gpuTimeToProcess=t;
    }
    public int getGpuTimeToProcess(){
        return gpuTimeToProcess;
    }
    public void reduceGpuT(){
        gpuTimeToProcess-=1;
    }

    public void setCpuTimeToProcess(int t){
        if(data.getType()== Data.Type.Images)
            cpuTimeToProcess=t*4;
        else if(data.getType()== Data.Type.Text)
            cpuTimeToProcess=t*2;
        else
            cpuTimeToProcess=t;
    }
    public int getCpuTimeToProcess(){
        return cpuTimeToProcess;
    }
    public void reduceCpuT(){
        cpuTimeToProcess-=1;
    }



}
