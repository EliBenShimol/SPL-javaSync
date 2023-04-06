package bgu.spl.mics.application;


import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.ContinueBroadcast;
import bgu.spl.mics.application.messages.StartBroadcast;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) throws IOException, InterruptedException {
        MessageBusImpl bus=MessageBusImpl.getInstance();
        Cluster c=Cluster.getInstance();
        LinkedList<Thread> threads=new LinkedList<Thread>();
        LinkedList<StudentService> studentServices=new LinkedList<StudentService>();
        LinkedList<GPUService> gpuServices=new LinkedList<GPUService>();
        LinkedList<CPUService> cpuServices=new LinkedList<CPUService>();
        LinkedList<ConferenceService> conferenceServices=new LinkedList<ConferenceService>();
        JsonParser jsonParser=new JsonParser();
        FileReader f=new FileReader("./"+args[0]);
        String output="./"+args[1];
        Object obj=jsonParser.parse(f);
        JsonObject j=(JsonObject) obj;

        JsonArray students=(JsonArray) j.get("Students");
        for(int i=0;i<students.size();i++) {
            JsonObject j2= (JsonObject) students.get(i);
            String name= j2.get("name").getAsString();
            String department=j2.get("department").getAsString();
            String stat=j2.get("status").getAsString();
            Student.Degree status;
            if(stat.equals("MSc"))
                status= Student.Degree.MSc;
            else
                status= Student.Degree.PhD;
            Student student=new Student(name, department, status);
            JsonArray models=(JsonArray) j2.get("models");
            for(int k=0;k<models.size();k++) {
                JsonObject j3 = (JsonObject) models.get(k);
                String name2 = j3.get("name").getAsString();
                String stat2=j3.get("type").getAsString();
                Data.Type type;
                if(stat2.equals("Images")||stat2.equals("images"))
                    type= Data.Type.Images;
                else if(stat2.equals("Text")||stat2.equals("text"))
                    type= Data.Type.Text;
                else
                    type= Data.Type.Tabular;
                int size = j3.get("size").getAsInt();
                Data d=new Data(type, size);
                Model model=new Model(name2, d, student);
            }
            StudentService studentService=new StudentService(student.getName(), student, i);
            Thread t=new Thread(studentService);
            threads.add(t);
            studentServices.add(studentService);
        }

        JsonArray gpus=(JsonArray) j.get("GPUS");
        for(int i=0;i<gpus.size();i++){
            String stat = gpus.get(i).getAsString();
            GPU.Type type;
            if(stat.equals("RTX3090"))
                type= GPU.Type.RTX3090;
            else if(stat.equals("RTX2080"))
                type= GPU.Type.RTX2080;
            else
                type= GPU.Type.GTX1080;
            GPU g=new GPU(type, i);
            c.addGpu(g);
            GPUService gpuService=new GPUService(stat, g);
            Thread t=new Thread(gpuService);
            threads.add(t);
            gpuServices.add(gpuService);
        }

        JsonArray cpus=(JsonArray) j.get("CPUS");
        for(int i=0;i<cpus.size();i++){
            int cores = cpus.get(i).getAsInt();
            CPU cpu=new CPU(cores);
            c.addCpu(cpu);
            CPUService cpuService=new CPUService(cores+" ", cpu);
            Thread t=new Thread(cpuService);
            threads.add(t);
            cpuServices.add(cpuService);
        }


        JsonArray conferences=(JsonArray) j.get("Conferences");
        for(int i=0;i<conferences.size();i++) {
            JsonObject j2 = (JsonObject) conferences.get(i);
            String name = j2.get("name").getAsString();
            int date = j2.get("date").getAsInt();
            ConferenceInformation con=new ConferenceInformation(name, date);
            ConferenceService conferenceService=new ConferenceService(name, con);
            Thread t=new Thread(conferenceService);
            threads.add(t);
            conferenceServices.add(conferenceService);
        }

        int tickTime = j.get("TickTime").getAsInt();
        int dur=j.get("Duration").getAsInt();
        TimeService timeService=new TimeService(tickTime, dur);
        Thread t=new Thread(timeService);
        threads.add(t);

        for(int i=0;i<threads.size();i++)
            threads.get(i).start();
        StoppingClass stop=new StoppingClass("stop", threads, studentServices, gpuServices, cpuServices, conferenceServices, output);
        Thread thread=new Thread(stop);
        thread.start();
        Thread.sleep(1000);
        ContinueBroadcast b=new ContinueBroadcast(-1);
        bus.sendBroadcast(b);

        //java -cp target/spl221ass2-1.0-jar-with-dependencies.jar bgu.spl.mics.application.CRMSRunner example_input.json output.jsom


    }
}
