package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class TestModelEvent<T> implements Event<T> {
    private Model m;

    public TestModelEvent(){
        m=null;
    }
    public TestModelEvent(Model m){
        this.m=m;
    }

    public void changeResultM(Boolean ans){
        m.changeResult(ans);
    }


    @Override
    public Model getModel() {
        return m;
    }
}