package patient;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import symptons.Symptom;

import java.util.ArrayList;

/**
 * Created by Emanuelpinho on 18/11/14.
 */
public class PatientAgent extends Agent {

    /**
     * List of symptons that the patien has,
     * which may include fever, mulligrubs, back pain, heart palpitations, muscle aches, intestinal pain
     */
    private ArrayList<Symptom> symptons;

    /**
     * Name of patient to be easy to find him
     */
    private String name;

    /**
     * Last exam that patient do
     */
    private String lastExam;

    /**
     * Health of patient, follow the formula: h(t)= s-bt, where, t is time in hospital, s is inicial health state
     * and b is a decrease health state.
     */
    private float healthState;

    /**
     * initial state of health
     */
    private float initialState;

    /**
     * decrease rate of health state
     */
    private float decreaseRate;

    /**
     * Time that the patient enter in the hospital
     */
    private long enterTime;




    /************************************ Constructors ************************************/

    public PatientAgent(ArrayList<String> symptons, String name){

        for(String symptom : symptons) {
            this.symptons.add(new Symptom(symptom));
        }

        this.name = name;
        this.enterTime = System.currentTimeMillis();
    }


    /************************************ Get functions ************************************/

    public ArrayList<Symptom> getSymptons(){
        return symptons;
    }

    public String getLastExam(){
        return lastExam;
    }

    /************************************ Set functions ************************************/

    public void setHealthState(){

        float s = 0, b = 0;

        for(Symptom symptom : symptons){
            s += symptom.getHealth();
            b += symptom.getDecreaseRate();
        }

        this.decreaseRate = b;
        this.initialState = s;
        long timeInHospital = System.currentTimeMillis() - enterTime;

        this.healthState =  initialState-(decreaseRate/timeInHospital);
    }

    public Boolean removeSymptom(){
        int i = 0;

        while(i < symptons.size()){
            if(lastExam.compareTo(symptons.get(i).getExam()) == 0){
                symptons.remove(i);
                return true;
            }
            else
                i++;
        }

        return false;
    }

    protected void setup()
    {
        System.out.println("patient start");
        addBehaviour(new SendMessage());
    }

    public class SendMessage extends SimpleBehaviour {

        private Boolean done;

        @Override
        public void action() {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setContent( "Ping" );

            msg.addReceiver(new AID("Common", AID.ISLOCALNAME));

            send(msg);

            block();
        }

        @Override
        public boolean done() {
            return false;
        }
    }
}
