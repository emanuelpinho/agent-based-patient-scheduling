package FirstComeFirstServed;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Emanuelpinho on 12/12/14.
 */
public class Patient {

    private ArrayList<String> symptoms;

    double timeToCompletTreatment;

    private ArrayList<Integer> slots;

    String name;


    private float healthState, initialState, decreaseRate;

    private long enterTime;

    public Patient(String[] symp, String name) {

        for (String s : symp) {
            this.symptoms.add(s);
        }
        timeToCompletTreatment = 0;
        this.name = name;
        slots = new ArrayList<Integer>();
        symptoms = new ArrayList<String>();
        enterTime = System.currentTimeMillis();
    }

    public ArrayList<String> getSymptoms(){
        return symptoms;
    }

    public ArrayList<Integer> getSlots(){
        Collections.sort(slots);
        return slots;
    }

    public void addSlot(int slot){
        slots.add(slot);
    }

    public void refreshHealthState(){
        setHealthState();
        long timeInHospital = System.currentTimeMillis() - enterTime;


        if(healthState < 0){
            System.out.println("O paciente " + name + " morreu devido a excesso de tempo");
        }
        else
            System.out.println("O paciente " + name + " demorou " + timeInHospital + " a ser curado");

    }

    public void setHealthState(){

        float s = 1, b = 0;

        for(String symptom : symptoms){
            if(symptom.compareTo("fever") == 0){
                s -= 0.25;
                b += 0.6;
            }
            else if(symptom.compareTo("mulligrubs") == 0){
                s -= 0.1;
                b += 0.3;
            }
            else if(symptom.compareTo("back pain") == 0){
                s -= 0.15;
                b += 0.3;
            }
            else if(symptom.compareTo("muscles aches") == 0){
                s -= 0.05;
                b += 0.2;
            }
            else if(symptom.compareTo("heart palpitations") == 0){
                s -= 0.35;
                b += 0.6;
            }
            else if(symptom.compareTo("intestinal pain") == 0){
                s -= 0.2;
                b += 0.4;
            }
        }

        this.decreaseRate = b;
        this.initialState = s;
        long timeInHospital = System.currentTimeMillis() - enterTime; // retira um valor de decrease rate por cada 1440000 ms

        this.healthState =  initialState-((decreaseRate/300000)*timeInHospital);
    }
}
