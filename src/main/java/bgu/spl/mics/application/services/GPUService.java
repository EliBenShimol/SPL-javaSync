package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    private final MessageBusImpl m=MessageBusImpl.getInstance();
    private final Cluster c=Cluster.getInstance();
    private GPU g;
    private HashMap<Model, Event> eventHashMap;
    private LinkedList<Model> models;
    private long dataProcessed;
    private long time;

    public GPUService(String name, GPU g) {
        super(name);
        this.g=g;
        eventHashMap=new HashMap<>();
        models=new LinkedList<Model>();
        dataProcessed=0;
    }

    @Override
    protected void initialize() {
        Callback<TestModelEvent>send2= c -> whenTest(c);
        TestModelEvent e2=new TestModelEvent();
        Class<TestModelEvent> type2 = (Class<TestModelEvent>) e2.getClass();
        this.subscribeEvent(type2, send2);


        Callback<TickBroadcast>send3= c -> whenTick(c);
        TickBroadcast b3=new TickBroadcast();
        Class<TickBroadcast> type3 = (Class<TickBroadcast>) b3.getClass();
        this.subscribeBroadcast(type3, send3);

        Callback<TerminateBroadcast>send4= c -> terminated();
        TerminateBroadcast b2=new TerminateBroadcast();
        Class<TerminateBroadcast> type4 = (Class<TerminateBroadcast>) b2.getClass();
        this.subscribeBroadcast(type4, send4);

        Callback<TrainModelEvent>send= c -> whenEvent(c);
        TrainModelEvent e=new TrainModelEvent();
        Class<TrainModelEvent> type = (Class<TrainModelEvent>) e.getClass();
        this.subscribeEvent(type, send);

    }


    public void terminated(){
        this.terminate();
    }




    public void whenTick(TickBroadcast temp){
        if(g.getModel()!=null){
            g.processData();
            if(g.getProcessedDataBatches().size()>0) {
                for (int i = 0; i < g.getProcessedDataBatches().size(); i++) {
                    time++;
                    if (g.getProcessedDataBatches().get(i).getGpuTimeToProcess() <= 0) {
                        g.removeBatch(i);
                        i--;
                        dataProcessed++;
                        g.addData();
                    }
                }
            }
            if((g.getProcessedDataBatches().size()+g.getSentDate())<g.getNumberOfBatches()&&g.getCountData()<g.getBatches().size()){
                int i=g.getProcessedDataBatches().size()+g.getSentDate();
                for(;i<g.getNumberOfBatches();i++){
                    int currC=g.activeCpu();
                    c.sending(g.getBatches().get(g.getCountData()), currC, g.getIndex());
                    g.addSentData();
                }
            }
            if(g.getCountData()==g.getBatches().size()) {
                Model model=g.getModel();
                model.setStatus(Model.Status.Trained);
                eventHashMap.remove(m);
                g.finishProcessing();
                if(!models.isEmpty()) {
                    g.fetchModel(models.removeFirst());
                }
                this.complete(eventHashMap.get(model), model);
            }
        }

    }

    public void whenEvent(Event event){
        time++;
        event.getModel().setStatus(Model.Status.Training);
        eventHashMap.put(event.getModel(), event);
        models.addLast(event.getModel());
        if(g.getModel()==null) {
            g.fetchModel(models.removeFirst());
        }
        g.buildBatches();
        dataProcessed+=event.getModel().getData().getSize()/2000;
        time+=event.getModel().getData().getSize()/2000;


    }

    public void whenTest(TestModelEvent t){
        Model model=t.getModel();
        model.changeResult(true);
        model.setStatus(Model.Status.Tested);
        this.complete(t, model);
    }

    public long getDataProcessed(){
        return dataProcessed;
    }

    public long getTime(){return time;}

}
