package patient;

import hospital.CommonAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.AMSService;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.wrapper.ControllerException;
import symptons.Symptom;

import java.util.ArrayList;


public class PatientAgent extends Agent {

    public static String TYPE = "Agent";

    /**
     * List of symptoms the patient has.
     * Which may include fever, mulligrubs, back pain, heart palpitations, muscle aches, intestinal pain
     */
    private ArrayList<Symptom> symptoms;

    /**
     * Name of patient to be easy to find him
     */
    private String name;

    /**
     * Last exam that patient did
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

    /**
     * Patient agent listener for messages
     */
    private class WaiForMessage extends CyclicBehaviour {

        public WaiForMessage(Agent a){
            super(a);
        }

        @Override
        public void action() {
            System.out.println("Message to Patient Agent: " + myAgent.getLocalName());
            ACLMessage message = receive();
            if (message != null) {
                handleMessage(message);
            }
            else {
                block();
            }
        }

        private void handleMessage (ACLMessage message) {
            switch (message.getPerformative()) {
                case ACLMessage.INFORM:
                    System.out.println("INFORM");
                    break;
            }
        }
    }
    /************************************ Constructors ************************************/
/*
    public PatientAgent(ArrayList<String> symptoms, String name){

        for(String symptom : symptoms) {
            this.symptoms.add(new Symptom(symptom));
        }

        this.name = name;
        this.enterTime = System.currentTimeMillis();
    }
*/
    /*
    public PatientAgent(ArrayList<String> symptoms, String name){

        this.name = name;

        for(String symptom : symptoms){
            this.symptoms.add(new Symptom(symptom));
        }

        this.enterTime = System.currentTimeMillis();
    }
    */

    /************************************ Get functions ************************************/

    public ArrayList<Symptom> getSymptoms(){
        return symptoms;
    }

    public String getLastExam(){
        return lastExam;
    }

    /************************************ Set functions ************************************/

    public void setHealthState(){

        float s = 0, b = 0;

        for(Symptom symptom : symptoms){
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

        while(i < symptoms.size()){
            if(lastExam.compareTo(symptoms.get(i).getExam()) == 0){
                symptoms.remove(i);
                return true;
            }
            else
                i++;
        }

        return false;
    }

    /************************************ OVERRIDE FUNCTIONS ************************************/


    protected void setup() {
        System.out.println("Patient Agent " + getLocalName() + " started.");
        ServiceDescription sd  = new ServiceDescription();
        sd.setType(PatientAgent.TYPE);
        sd.setName(getLocalName());
        register(sd);

        // Inform common agent that new patient just arrived
        ACLMessage message = new ACLMessage();
        message.setSender(this.getAID());
        message.setContent(CommonAgent.NEW_PATIENT_MESSAGE);
        message.setPerformative(ACLMessage.INFORM);
        message.addReceiver(new AID("Common", AID.ISLOCALNAME));
        send(message);
    }

    void register(ServiceDescription sd) {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);

            triageConsultation();
            addBehaviour(new WaiForMessage(this));
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
            doDelete();
        }
    }

    protected void takeDown()
    {
        try {
            DFService.deregister(this);
        }
        catch (Exception e) {
            System.out.println(e.getCause());
        }
    }

    /************************************ SUPPORT FUNCTIONS ************************************/


    public void triageConsultation(){

        addBehaviour(new Triage(this));

        // Send message to Common Agent

        AMSAgentDescription[] agents;

        try {
            agents = AMSService.search(this, new AMSAgentDescription());
        }
        catch (Exception e) {
            System.out.println( "Problem searching AMS: " + e);
            e.printStackTrace();
            return;
        }

        ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
        msg.setContent("I need a traige consultation");

        for(AMSAgentDescription agent : agents) {
            if (agent.getName().toString().compareTo("Common") == 0)
                msg.addReceiver(agent.getName());
        }
        send(msg);

    }


    /************************************ BEHAVIORS  ************************************/


    public class Triage extends SimpleBehaviour {

        PatientAgent pa;

        private boolean done;

        public Triage(PatientAgent pa){
            this.pa = pa;
        }

        public void action() {
            done = false;
            ACLMessage msg= receive();
            if (msg!=null) {
                if(msg.getSender().getName().compareTo("Common") == 0 && msg.getPerformative() == ACLMessage.INFORM && msg.getContent().compareTo("triage") == 0){
                    pa.setHealthState();
                }
            }
            block();

            done = true;
        }

        @Override
        public boolean done() {
            return done;
        }
    }



    public class Treatment extends SimpleBehaviour {

        private boolean done;

        @Override
        public void action() {

        }

        @Override
        public boolean done() {
            return done;
        }
    }

}
