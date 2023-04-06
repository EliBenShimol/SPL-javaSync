package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.StopBroadcast;
import bgu.spl.mics.application.messages.TerminateBroadcast;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class StoppingClass extends MicroService {
    /**
     * @param name the micro-service name (used mainly for debugging purposes -
     *             does not have to be unique)
     */

    private LinkedList<Thread> threads;
    private LinkedList<StudentService> studentServices;
    private LinkedList<GPUService> gpuServices;
    private LinkedList<CPUService> cpuServices;
    private LinkedList<ConferenceService> conferenceServices;
    private MessageBusImpl m=MessageBusImpl.getInstance();
    private String output;

    public StoppingClass(String name, LinkedList<Thread> threads, LinkedList<StudentService> studentServices, LinkedList<GPUService> gpuServices,
                         LinkedList<CPUService> cpuServices, LinkedList<ConferenceService> conferenceServices, String output) {
        super(name);
        this.threads=threads;
        this.studentServices=studentServices;
        this.gpuServices=gpuServices;
        this.cpuServices=cpuServices;
        this.conferenceServices=conferenceServices;
        this.output=output;
    }

    @Override
    protected void initialize() {

        Callback<StopBroadcast> send7= c -> {
            try {
                decideBroadCast(c);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        StopBroadcast b7=new StopBroadcast();
        Class<StopBroadcast> type7 = (Class<StopBroadcast>) b7.getClass();
        this.subscribeBroadcast(type7, send7);

    }

    private void decideBroadCast(StopBroadcast c) throws IOException {
        int gTime=0;
        int cTime=0;
        int batch=0;

        for(int i=0;i<gpuServices.size();i++)
            batch+=gpuServices.get(i).getDataProcessed();
        for(int i=0;i<gpuServices.size();i++)
            gTime+=+gpuServices.get(i).getTime();
        for(int i=0;i<cpuServices.size();i++)
            cTime+=cpuServices.get(i).getTime()+gTime/2;


        FileWriter writer=new FileWriter(output);
        writer.write("Students:");
        writer.append(System.lineSeparator());
        for(int i=0;i<studentServices.size();i++) {
            writer.write("   name:"+studentServices.get(i).getName());
            writer.append(System.lineSeparator());
            writer.write("   department:"+studentServices.get(i).getS().getDepartment());
            writer.append(System.lineSeparator());
            writer.write("   Status:"+studentServices.get(i).getS().getStatus());
            writer.append(System.lineSeparator());
            writer.write("   publications:"+studentServices.get(i).getCountP());
            writer.append(System.lineSeparator());
            writer.write("   PapersRead:"+studentServices.get(i).getCountR());
            writer.append(System.lineSeparator());
            writer.write("   TrainedModels:");
            writer.append(System.lineSeparator());
            for(int j=0;j<studentServices.get(i).getPubModels().size();j++) {
                writer.append(System.lineSeparator());
                writer.write("      name:" + studentServices.get(i).getPubModels().get(j).getName());
                writer.append(System.lineSeparator());
                writer.write("      data:");
                writer.append(System.lineSeparator());
                writer.write("         type:" + studentServices.get(i).getPubModels().get(j).getData().getType());
                writer.append(System.lineSeparator());
                writer.write("         size:" + studentServices.get(i).getPubModels().get(j).getData().getSize());
                writer.append(System.lineSeparator());
                writer.write("      Status:"+studentServices.get(i).getPubModels().get(j).getStatus());
                writer.append(System.lineSeparator());
                writer.write("      Results:"+studentServices.get(i).getPubModels().get(j).getResults());
                writer.append(System.lineSeparator());
            }
            writer.append(System.lineSeparator());
        }

        writer.write("Conferences:");
        writer.append(System.lineSeparator());
        for(int i=0;i<conferenceServices.size();i++){
            writer.write("   name:"+conferenceServices.get(i).getName());
            writer.append(System.lineSeparator());
            writer.write("   date:"+conferenceServices.get(i).getDate());
            writer.append(System.lineSeparator());
            writer.write("   Publications:");
            writer.append(System.lineSeparator());
            for(int j=0;j<conferenceServices.get(i).getNames().size();j++){
                writer.append(System.lineSeparator());
                writer.write("      name:"+conferenceServices.get(i).getNames().get(j).getName());
                writer.append(System.lineSeparator());
                writer.write("      data:");
                writer.append(System.lineSeparator());
                writer.write("         type:" + conferenceServices.get(i).getNames().get(j).getData().getType());
                writer.append(System.lineSeparator());
                writer.write("         size:" + conferenceServices.get(i).getNames().get(j).getData().getSize());
                writer.append(System.lineSeparator());
                writer.write("      Status:"+conferenceServices.get(i).getNames().get(j).getStatus());
                writer.append(System.lineSeparator());
                writer.write("      Results:"+conferenceServices.get(i).getNames().get(j).getResults());
                writer.append(System.lineSeparator());
            }
            writer.append(System.lineSeparator());
        }


        writer.write("cpuTimeUsed:"+cTime);
        writer.append(System.lineSeparator());
        writer.append(System.lineSeparator());
        writer.write("gpuTimeUsed:"+gTime);
        writer.append(System.lineSeparator());
        writer.append(System.lineSeparator());
        writer.write("batchesProcessed:"+batch);
        writer.append(System.lineSeparator());





        for(int i=0;i<threads.size();i++)
            threads.get(i).stop();
        this.terminate();
        writer.close();
    }
}
