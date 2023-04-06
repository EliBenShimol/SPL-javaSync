package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DataPreProcessEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.GPU;

/**
 * CPU service is responsible for handling the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    private final MessageBusImpl m=MessageBusImpl.getInstance();
    private final Cluster c=Cluster.getInstance();
    private CPU cpu;
    private long proc;
    public CPUService(String name, CPU cpu) {
        super(name);
        this.cpu=cpu;
        proc=0;
    }

    @Override
    protected void initialize() {


        Callback<TickBroadcast> send3= c -> whenTick(c);
        TickBroadcast b3=new TickBroadcast();
        Class<TickBroadcast> type3 = (Class<TickBroadcast>) b3.getClass();
        this.subscribeBroadcast(type3, send3);

        Callback<TerminateBroadcast>send4= c -> terminate();
        TerminateBroadcast b2=new TerminateBroadcast();
        Class<TerminateBroadcast> type4 = (Class<TerminateBroadcast>) b2.getClass();
        this.subscribeBroadcast(type4, send4);



    }

    private void whenTick(TickBroadcast t) {
        cpu.processData();
        for(int i=0;i<cpu.getBatches().size();i++){
            if(cpu.getBatches().get(i).getCpuTimeToProcess()<=0) {
                proc++;
                c.retData(cpu.getBatches().get(i));
                cpu.getBatches().remove(i);
                i--;
            }
        }
    }

    public long getProc(){return cpu.getCountBatch();}
    public long getTime(){return cpu.getTime();}
}
