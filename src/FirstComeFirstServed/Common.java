package FirstComeFirstServed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;


public class Common {

    private ArrayList<Doctor> doctors;

    private HashMap<String, Treatment> treatments;

    private static Queue<Patient> patients;

    String option;

    public Common(String option){
        patients = new LinkedList<Patient>();
        treatments = new HashMap<String, Treatment>();
        doctors = new ArrayList<Doctor>();
        this.option = option;
    }

    public void addDoctor(String name){
         doctors.add(new Doctor(name));
    }

    public void addTreatment(String t){

        treatments.put(t, new Treatment(t));
    }

    public void addPatient(String[] symp, String name){
        patients.add(new Patient(symp, name));
    }

    public void init(){
        addDoctor("doctor1");
        addDoctor("doctor2");
        addDoctor("doctor3");

        addTreatment("analysis");
        addTreatment("endoscopy");
        addTreatment("resonance");
        addTreatment("electrocardiogram");
        addTreatment("sonography");
        addTreatment("colonoscopy");

        if(option.compareTo("3") == 0) {
            addPatient(new String[]{"fever"}, "paciente");
            addPatient(new String[]{"mulligrubs"}, "paciente2");
            addPatient(new String[]{"back pain", "fever"}, "paciente3");
            addPatient(new String[]{"back pain"}, "paciente4");
            addPatient(new String[]{"heart palpitations"}, "paciente6");
            addPatient(new String[]{"fever"}, "paciente9");
            addPatient(new String[]{"intestinal pain", "heart palpitations"}, "paciente11");
            addPatient(new String[]{"mulligrubs"}, "paciente12");
            addPatient(new String[]{"mulligrubs", "back pain"}, "paciente13");
            addPatient(new String[]{"fever", "back pain"}, "paciente14");
            addPatient(new String[]{"fever"}, "paciente15");
            addPatient(new String[]{"heart palpitations", "back pain"}, "paciente16");
            addPatient(new String[]{"intestinal pain", "fever"}, "paciente17");
            addPatient(new String[]{"intestinal pain", "mulligrubs"}, "paciente18");
            addPatient(new String[]{"back pain"}, "paciente19");
        }
        else if(option.compareTo("2") == 0) {
            addPatient(new String[] {"fever", "mulligrubs", "back pain"}, "paciente");
            addPatient(new String[] {"mulligrubs", "heart palpitations", "intestinal pain"}, "paciente2");
            addPatient(new String[] {"back pain", "fever", "intestinal pain"}, "paciente3");
            addPatient(new String[] {"muscles aches", "back pain"}, "paciente4");
            addPatient(new String[] {"heart palpitations", "fever"}, "paciente6");
            addPatient(new String[] {"intestinal pain", "mulligrubs"}, "paciente9");

        }
        else{
            addPatient(new String[] {"fever", "mulligrubs"}, "paciente");
            addPatient(new String[] {"mulligrubs"}, "paciente2");
            addPatient(new String[] {"back pain"}, "paciente3");
            addPatient(new String[] {"muscles aches"}, "paciente4");
            addPatient(new String[] {"heart palpitations"}, "paciente6");
            addPatient(new String[] {"intestinal pain"}, "paciente9");
        }
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
                slot = doctors.get(i).getLastSlot();
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
