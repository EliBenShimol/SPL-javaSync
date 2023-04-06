package bgu.spl.mics;

import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private static MessageBusImpl instance;
	private HashMap <Class<? extends Message>, LinkedList<MicroService>> microServiceHashmap;
	private HashMap <Class<? extends Message>, Integer> roundHashMap;
	private HashMap <MicroService, LinkedList<Message>> queuesHashmap;
	private HashMap <Event, Future> futureHashMap;


	public MessageBusImpl(){
		microServiceHashmap=new HashMap<>();
		roundHashMap=new HashMap<>();
		queuesHashmap=new HashMap<>();
		futureHashMap=new HashMap<>();
	}
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if(!microServiceHashmap.containsKey(type)) {
			microServiceHashmap.put(type, new LinkedList<MicroService>());
			roundHashMap.put(type, 0);
		}
		if(microServiceHashmap.containsKey(type))
			if(!microServiceHashmap.get(type).contains(m))
				microServiceHashmap.get(type).add(m);
		register(m);



	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if(!microServiceHashmap.containsKey(type)) {
			microServiceHashmap.put(type, new LinkedList<MicroService>());
			roundHashMap.put(type, 0);
		}
		if(microServiceHashmap.containsKey(type))
			if(!microServiceHashmap.get(type).contains(m))
				microServiceHashmap.get(type).add(m);
		register(m);


	}

	@Override
	public  <T> void  complete(Event<T> e, T result) {
		if(futureHashMap.containsKey(e)) futureHashMap.get(e).resolve(result);

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if(microServiceHashmap.containsKey(b.getClass())) {
			for (int i = 0; i < microServiceHashmap.get(b.getClass()).size(); i++) {
				if(queuesHashmap.containsKey(microServiceHashmap.get(b.getClass()).get(i)))
					queuesHashmap.get(microServiceHashmap.get(b.getClass()).get(i)).add(b);
			}
		}


	}


	
	@Override
	public synchronized <T> Future<T> sendEvent(Event<T> e) {
		Future<T> f=null;
		if(microServiceHashmap.containsKey(e.getClass())){
			Integer i=roundHashMap.get(e.getClass());
			MicroService m=microServiceHashmap.get(e.getClass()).get(i);
			queuesHashmap.get(m).add(e);
			if(!futureHashMap.containsKey(e)) {
				f=new Future<T>();
				futureHashMap.put(e, f);
			}else
				f= (Future<T>) futureHashMap.get(e);
			i++;
			roundHashMap.remove(e.getClass());
			if(i==microServiceHashmap.get(e.getClass()).size())
				i=0;
			roundHashMap.put(e.getClass(), i);
		}
		return f;
	}

	@Override
	public void register(MicroService m) {
		if(!queuesHashmap.containsKey(m))
			queuesHashmap.put(m, new LinkedList<Message>());

	}

	@Override
	public void unregister(MicroService m) {
		if(queuesHashmap.containsKey(m))
			microServiceHashmap.remove(m);
		for(int i=0;i<microServiceHashmap.size();i++)
			if(microServiceHashmap.get(i).contains(m))
				microServiceHashmap.get(i).remove(m);


	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		Message message=null;
		if(!queuesHashmap.get(m).isEmpty())
			message=queuesHashmap.get(m).removeFirst();

		return message;
	}

	public static synchronized MessageBusImpl getInstance(){
		if(instance==null)
			instance=new MessageBusImpl();
		return instance;
	}

	

}
