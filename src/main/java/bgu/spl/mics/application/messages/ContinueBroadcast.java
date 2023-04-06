package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class ContinueBroadcast implements Broadcast {
    private int i;


    public ContinueBroadcast(int i){
        this.i=i;
    }
    public int getI(){
        return i;
    }
}
