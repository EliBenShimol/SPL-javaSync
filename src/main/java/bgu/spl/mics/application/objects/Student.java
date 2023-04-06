package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;
    private LinkedList<Model> models;
    private LinkedList<String> myM;

    public Student(String name, String department, Degree status){
        this.name=name;
        this.department=department;
        this.status=status;
        this.publications=0;
        this.papersRead=0;
        models=new LinkedList<Model>();
        myM=new LinkedList<String>();
    }
    public void addPublications(int publications){
        this.publications+=publications;
    }
    public void addPapersRead(int papersRead){
        this.papersRead+=papersRead;
    }
    public void addModel(Model m){
        models.add(m);
        myM.add(m.getName());
    }

    public String getName() {
        return name;
    }
    public String getDepartment(){
        return department;
    }
    public Degree getStatus(){
        return status;
    }
    public int getPublications(){
        return publications;
    }
    public int getPapersRead(){
        return papersRead;
    }
    public Model sendModel(){
        Model ans=models.removeFirst();
        return ans;
    }

    public int getModels(){
        return models.size();
    }

    public LinkedList<String> getMyM(){
        return myM;
    }

}
