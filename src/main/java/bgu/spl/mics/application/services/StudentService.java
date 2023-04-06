package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.LinkedList;
import java.util.Random;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    private MessageBusImpl m=MessageBusImpl.getInstance();
    private Student s;
    private int countP;
    private int countR;
    private int index;
    private LinkedList<Model> pubModels;
    public StudentService(String name, Student s, int i) {
        super(name);
        this.s=s;
        countP=0;
        countR=0;
        index=i;
        pubModels=new LinkedList<Model>();
    }

    @Override
    protected void initialize(){

        Callback<ContinueBroadcast>send= c -> decideAction(c);
        ContinueBroadcast e=new ContinueBroadcast(0);
        Class<ContinueBroadcast> type = (Class<ContinueBroadcast>) e.getClass();
        this.subscribeBroadcast(type, send);


        Callback<PublishConferenceBroadcast>send2= c -> getPublic(c);
        PublishConferenceBroadcast b3=new PublishConferenceBroadcast();
        Class<PublishConferenceBroadcast> type2 = (Class<PublishConferenceBroadcast>) b3.getClass();
        this.subscribeBroadcast(type2, send2);

        Callback<TerminateBroadcast>send4= c -> terminate();
        TerminateBroadcast b2=new TerminateBroadcast();
        Class<TerminateBroadcast> type4 = (Class<TerminateBroadcast>) b2.getClass();
        this.subscribeBroadcast(type4, send4);


        Callback<StopBroadcast>send7= c -> decideBroadCast(c);
        StopBroadcast b7=new StopBroadcast();
        Class<StopBroadcast> type7 = (Class<StopBroadcast>) b7.getClass();
        this.subscribeBroadcast(type7, send7);
    }

    private void decideBroadCast(StopBroadcast c) {
        TerminateBroadcast t=new TerminateBroadcast();
        m.sendBroadcast(t);
    }

    public void decideAction(ContinueBroadcast m2) throws InterruptedException {
        if(m2.getI()==index||m2.getI()==-1) {
            if (s.getModels() > 0) {
                Model model = s.sendModel();
                Future<Model> f;
                TrainModelEvent<Model> e = new TrainModelEvent<Model>(model);
                f = this.sendEvent(e);
                model = f.get();
                Random random=new Random();
                int rand=random.nextInt(10)+1;
                if(s.getStatus()== Student.Degree.MSc){
                    if(rand>4)
                        model.changeResult(true);
                    else
                        model.changeResult(false);
                }
                else{
                    if(rand>2)
                        model.changeResult(true);
                    else
                        model.changeResult(false);
                }
                if (model.getResults() == Model.Result.Good) {
                    countP++;
                    pubModels.add(model);
                    PublishResultsEvent<Model> p = new PublishResultsEvent<Model>(model);
                    m.sendEvent(p);
                }
                ContinueBroadcast con = new ContinueBroadcast(index);
                m.sendBroadcast(con);
            }
        }

    }

    public void getPublic(PublishConferenceBroadcast temp){
        for(int i=0;i<temp.getNames().size();i++) {
            boolean ans=false;
            for (int j = 0; j < s.getMyM().size(); j++) {
                if (s.getMyM().get(j).equals(temp.getNames().get(i).getName()))
                    ans=true;
            }
            if(!ans)
                countR++;
        }
    }

    public int getCountP(){
        return countP;
    }

    public int getCountR(){
        return countR;
    }

    public Student getS(){return s;}

    public LinkedList<Model> getPubModels(){return pubModels;}


}
