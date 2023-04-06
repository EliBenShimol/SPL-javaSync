package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.LinkedList;

public class PublishConferenceBroadcast implements Broadcast {
    private LinkedList<Model> names;
    public PublishConferenceBroadcast(){
        names=new LinkedList<Model>();

    }
    public PublishConferenceBroadcast(LinkedList<Model> names){
        this.names=new LinkedList<Model>();
        for(int i=0;i<names.size();i++)
            this.names.add(names.get(i));
    }

    public LinkedList<Model> getNames(){
        return names;
    }
}
