package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

public class TickBroadcast implements Broadcast {

    private int time;

    public TickBroadcast(){
        time=0;
    }
    public TickBroadcast(int time){
        this.time=time;
    }

    public int getTime(){
        return time;
    }
}
