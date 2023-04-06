package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class TrainModelEvent<T> implements Event<T> {
    private Model m;

    public TrainModelEvent(){
        m=null;
    }
    public TrainModelEvent(Model m){
        this.m=m;
    }

    @Override
    public Model getModel() {
        return m;
    }
}



