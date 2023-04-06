package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	private MessageBusImpl m=MessageBusImpl.getInstance();
	private int tick;
	private int duration;
	private int timeLeft;

	public TimeService() {
		super("ticker");
	}

	public TimeService(int tick, int duration) {
		super("ticker");
		this.tick=tick;
		this.duration=duration;
		timeLeft=0;
	}

	@Override
	protected void initialize() {
		Callback<TickBroadcast>send3= c -> decideBroadCast(c);
		TickBroadcast b3=new TickBroadcast();
		Class<TickBroadcast> type3 = (Class<TickBroadcast>) b3.getClass();
		this.subscribeBroadcast(type3, send3);

		Callback<TerminateBroadcast>send4= c -> terminate();
		TerminateBroadcast b2=new TerminateBroadcast();
		Class<TerminateBroadcast> type4 = (Class<TerminateBroadcast>) b2.getClass();
		this.subscribeBroadcast(type4, send4);

		Callback<ContinueBroadcast>send= c -> decideBroadCast(c);
		ContinueBroadcast b=new ContinueBroadcast(0);
		Class<ContinueBroadcast> type = (Class<ContinueBroadcast>) b.getClass();
		this.subscribeBroadcast(type, send);

		
	}

	public void decideBroadCast(Broadcast temp) throws InterruptedException {
		if(timeLeft<=duration){
			TickBroadcast b=new TickBroadcast(timeLeft);
			timeLeft+=tick;
			Thread.sleep(tick);
			this.sendBroadcast(b);
		}
		else{
			StopBroadcast b=new StopBroadcast();
			this.sendBroadcast(b);
		}


	}

	public int getTick() {
		return tick;
	}

	public int getDuration() {
		return duration;
	}

	public int getTimeLeft() {
		return timeLeft;
	}
}
