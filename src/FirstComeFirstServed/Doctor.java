package FirstComeFirstServed;

import java.util.ArrayList;

/**
 * Created by Emanuelpinho on 12/12/14.
 */
public class Doctor {

    private double timeOfWork;

    private int lastSlot;

    public Doctor(){
        timeOfWork = 0;
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
