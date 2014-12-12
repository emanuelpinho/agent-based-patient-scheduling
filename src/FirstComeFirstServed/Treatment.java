package FirstComeFirstServed;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Emanuelpinho on 12/12/14.
 */
public class Treatment {

    private ArrayList<Integer> slots;

    private String symptom;

    public Treatment(String symptom){
        this.symptom = symptom;
        slots = new ArrayList<Integer>();
    }

    public int getAvailableSlot(ArrayList<Integer> patientSlots, int doctorSlot){



        Collections.sort(slots);
        int ideal = doctorSlot + 1;
        int maxTreatSlot = 0;
        int maxPatSlot = 0;

        if(slots.size() > 0)
            slots.get(slots.size() - 1);

        if(patientSlots.size() > 0)
            maxPatSlot = patientSlots.get(patientSlots.size() - 1);

        boolean founded = false;
        while(!founded){
            if(slots.contains(ideal) || patientSlots.contains(ideal)) {
                if (ideal > maxTreatSlot && ideal > maxPatSlot) {
                    founded = true;
                } else
                    ideal++;
            }
            else
                founded = true;
        }

        slots.add(ideal);
        return ideal;
    }
}
