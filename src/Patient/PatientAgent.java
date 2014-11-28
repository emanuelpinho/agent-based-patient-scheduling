package patient;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.AMSService;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import symptons.Symptom;

import java.util.ArrayList;
import java.util.Scanner;


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
/*
    public PatientAgent(ArrayList<String> symptons, String name){

        for(String symptom : symptons) {
            this.symptons.add(new Symptom(symptom));
        }

        this.name = name;
        this.enterTime = System.currentTimeMillis();
    }
*/
    public PatientAgent(ArrayList<String> symptoms, String name){

        this.name = name;

        for(String symptom : symptoms){
            this.symptons.add(new Symptom(symptom));
        }

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

    /************************************ OVERRIDE FUNCTIONS ************************************/


    protected void setup()
    {
        ServiceDescription sd  = new ServiceDescription();
        sd.setType( "patient" );
        sd.setName(getLocalName());
        register(sd);


        while(symptons.size() > 0){
            addBehaviour(new Treatment());
        }

    }

    void register(ServiceDescription sd)
    {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            triageConsultation();
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

        AMSAgentDescription[] agents = null;

        try {
            agents = AMSService.search(this, new AMSAgentDescription());
        }
        catch (Exception e) {
            System.out.println( "Problem searching AMS: " + e);
            e.printStackTrace();
        }

        ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
        msg.setContent("I need a traige consultation");

        for(int i=0; i<agents.length;i++) {
            if (agents[i].getName().compareTo("Common") == 0)
                msg.addReceiver(agents[i].getName());
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
