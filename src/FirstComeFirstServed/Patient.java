package FirstComeFirstServed;

import symptons.Symptom;

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


    private float healthState;

    public Patient(String[] symp, String name) {

        symptoms = new ArrayList<String>();
        slots = new ArrayList<Integer>();
        symptoms = new ArrayList<String>();

        Collections.addAll(symptoms, symp);

        timeToCompletTreatment = 0;
        this.name = name;

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
        Collections.sort(slots);
        timeToCompletTreatment = slots.get(slots.size() - 1) * 7825;
        // this is 7 x 675 (delay of exchange of messages in our method + 2000( time for treatment ) + 1100 ( doctor experience )
        setHealthState();

        if(healthState < 0)
            System.out.println("O paciente " + name + " morreu devido a excesso de tempo");
        else
            System.out.println("O paciente " + name + " demorou " + timeToCompletTreatment + " a ser curado");

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

        float decreaseRate = b;
        float initialState = s;

        this.healthState = (float) (initialState-((decreaseRate/300000)*timeToCompletTreatment));
    }

    public String getName() {
        return name;
    }
}
