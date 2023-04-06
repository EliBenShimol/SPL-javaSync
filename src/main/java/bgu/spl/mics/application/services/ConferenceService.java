package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.ConferenceInformation;
import bgu.spl.mics.application.objects.Model;

import java.util.LinkedList;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConferenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    private MessageBusImpl m=MessageBusImpl.getInstance();
    private ConferenceInformation con;
    private LinkedList<Model> names;
    public ConferenceService(String name, ConferenceInformation con) {
        super(name);
        this.con=con;
        names=new LinkedList<Model>();
    }

    @Override
    protected void initialize() {
        Callback<TickBroadcast> send3= c -> endConference(c);
        TickBroadcast b3=new TickBroadcast();
        Class<TickBroadcast> type3 = (Class<TickBroadcast>) b3.getClass();
        this.subscribeBroadcast(type3, send3);

        Callback<TerminateBroadcast>send4= c -> terminate();
        TerminateBroadcast b2=new TerminateBroadcast();
        Class<TerminateBroadcast> type4 = (Class<TerminateBroadcast>) b2.getClass();
        this.subscribeBroadcast(type4, send4);

        Callback<PublishResultsEvent>send= c -> getModel(c);
        PublishResultsEvent e=new PublishResultsEvent();
        Class<PublishResultsEvent> type = (Class<PublishResultsEvent>) e.getClass();
        this.subscribeEvent(type, send);



    }


    public void endConference(TickBroadcast temp) throws InterruptedException {
        if(temp.getTime()==con.getDate()){
            PublishConferenceBroadcast b=new PublishConferenceBroadcast(names);
            this.sendBroadcast(b);
        }
    }
    public void getModel(PublishResultsEvent e){
        names.add(e.getModel());
        this.complete(e, e.getModel());
    }

    public int getDate(){return con.getDate();}

    public LinkedList<Model> getNames(){return names;}
}
