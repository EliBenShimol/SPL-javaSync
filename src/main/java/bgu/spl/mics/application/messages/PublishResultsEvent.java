package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;
import com.sun.org.apache.xpath.internal.operations.Mod;

public class PublishResultsEvent<T> implements Event<T> {
    private Model m;

    public PublishResultsEvent(){
        m=null;
    }
    public PublishResultsEvent(Model m){
        this.m=m;
    }
    public String getName(){
        return m.getName();
    }
    @Override
    public Model getModel() {
        return m;
    }
}
