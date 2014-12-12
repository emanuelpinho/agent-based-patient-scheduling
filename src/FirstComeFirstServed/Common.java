package FirstComeFirstServed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Emanuelpinho on 12/12/14.
 */
public class Common {

    private ArrayList<Doctor> doctors;

    private HashMap<String, Treatment> treatments;

    private static Queue<Patient> patients;

    public Common(){
        patients = new LinkedList<Patient>();
        treatments = new HashMap<String, Treatment>();
        doctors = new ArrayList<Doctor>();
    }

    public void addDoctor(){
         doctors.add(new Doctor());
    }

    public void addTreatment(String t){

        treatments.put(t, new Treatment(t));
    }

    public void addPatient(String[] symp, String name){
        patients.add(new Patient(symp, name));
    }

    public void init(){
        addDoctor();
        addDoctor();
        addDoctor();

        addTreatment("analysis");
        addTreatment("endoscopy");
        addTreatment("resonance");
        addTreatment("electrocardiogram");
        addTreatment("sonography");
        addTreatment("colonoscopy");

        addPatient(new String[] {"fever", "mulligrubs"}, "paciente");
        addPatient(new String[] {"mulligrubs"}, "paciente2");
        addPatient(new String[] {"back pain"}, "paciente3");
        addPatient(new String[] {"muscles aches"}, "paciente4");
        addPatient(new String[] {"heart palpitations"}, "paciente6");
        addPatient(new String[] {"intestinal pain"}, "paciente9");
    }

    public void run(){

        Patient p;
        Doctor d;
        Treatment t;
        int examSlot;

        while(patients.size() > 0){
            p = patients.remove();
            d = findAvailableDoctor();
            for(String s : p.getSymptoms()){
                try {
                    t = getTreatment(s);
                    examSlot = t.getAvailableSlot(p.getSlots(), d.getLastSlot());
                    d.setLastSlot(examSlot);
                    p.addSlot(examSlot);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            p.refreshHealthState();
        }
    }

    public Doctor findAvailableDoctor(){
        Doctor d = doctors.get(0);
        int slot = d.getLastSlot();

        for(int i = 1; i < doctors.size(); i++){
            if (doctors.get(i).getLastSlot() < slot){
                d = doctors.get(i);
            }
        }

        return d;
    }

    public Treatment getTreatment(String s) throws Exception {

        if(s.compareTo("fever") == 0){
            return treatments.get("analysis");
        }
        else if(s.compareTo("mulligrubs") == 0){
            return treatments.get("endoscopy");
        }
        else if(s.compareTo("back pain") == 0){
            return treatments.get("resonance");
        }
        else if(s.compareTo("muscles aches") == 0){
            return treatments.get("sonography");
        }
        else if(s.compareTo("heart palpitations") == 0){
            return treatments.get("electrocardiogram");
        }
        else if(s.compareTo("intestinal pain") == 0){
            return treatments.get("colonoscopy");
        }
        else throw new Exception();
    }

}