package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {

    public enum Status {
        preTrained, Training, Trained, Tested;
    }
    public enum Result {
        None, Good, Bad;
    }
    String name;
    Data data;
    Student student;
    Status status;
    Result results;

    public Model(String name, Data data, Student student){
        this.name=name;
        this.data=data;
        this.student=student;
        this.status=Status.preTrained;
        this.results=Result.None;
        this.student.addModel(this);

    }
    public String getName() {
        return name;
    }
    public Data getData(){
        return data;
    }
    public Status getStatus(){
        return status;
    }
    public Student getStudent(){
        return student;
    }
    public Result getResults(){
        return results;
    }
    public void changeResult(boolean ans){
        if(ans)
            this.results=Result.Good;
        else
            this.results=Result.Bad;
    }

    public void setStatus(Status s){
        this.status=s;
    }



}
