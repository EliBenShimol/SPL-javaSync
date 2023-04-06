package bgu.spl.mics.application.objects;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	private static Cluster instance;
	private LinkedList<GPU> gpus;
	private LinkedList<CPU> cpus;
	private HashMap<DataBatch, Integer>dataBatchHashMap;
	private int currCpu;


	public Cluster(){
		gpus=new LinkedList<GPU>();
		cpus=new LinkedList<CPU>();
		dataBatchHashMap=new HashMap<>();
	}

	public void sending(DataBatch d, int send, int index){
		CPU c=cpus.get(send);
		c.getBatch(d);
		dataBatchHashMap.put(d, index);
	}
	public  int getCurrCpu(){
		int ans=currCpu;
		currCpu++;
		if(currCpu==cpus.size())
			currCpu=0;
		return ans;
	}
	public void retData(DataBatch d){
		if(dataBatchHashMap.containsKey(d)) {
			int index = dataBatchHashMap.get(d);
			GPU g=null;
			for (int i = 0; i < gpus.size(); i++)
				if (gpus.get(i).getIndex() == index) {
					g = gpus.get(i);
					g.getProcessedBatch(d);
				}
			if(g==null)
				try {
					throw new Exception("no gpu found");
				} catch (Exception e) {
					e.printStackTrace();
				}
		}


	}

	public void addGpu(GPU g){
		gpus.add(g);
	}
	public void addCpu(CPU c){
		cpus.add(c);
	}


	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		if(instance==null)
			instance=new Cluster();
		return instance;
	}

}
