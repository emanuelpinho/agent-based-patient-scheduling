package FirstComeFirstServed;

import java.util.ArrayList;

/**
 * Created by Emanuelpinho on 12/12/14.
 */
public class Doctor {

    private double timeOfWork;

    public String getName() {
        return name;
    }

    private String name;

    private int lastSlot;

    public Doctor(String name){
        timeOfWork = 0;
        this.name = name;
    }

    public double getTimeOfWork() {
        return timeOfWork;
    }

    public int getLastSlot(){
        return lastSlot;
    }

    public void setLastSlot(int slot){
        lastSlot = slot;
    }


}
