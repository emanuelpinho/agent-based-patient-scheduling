package patient;

import hospital.CommonAgent;
import hospital.Treatment;
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
import sun.jvm.hotspot.debugger.cdbg.Sym;
import symptons.Symptom;

import java.util.ArrayList;
import java.util.Scanner;


public class PatientAgent extends Agent {

    public static String TYPE = "Agent";
    public static String NEW_TRIAGE_MESSAGE = "TRIAGE";
    public static String NEW_UPDATE_MESSAGE = "UPDATE";
    public static String TRIAGE_MESSAGE = "TRIAGE";
    public static String REQUEST_TREATMENT = "REQUEST_TREATMENT";
    public static String NEW_DEAD_MESSAGE = "NEW_DEAD_MESSAGE";

    /**
     * List of symptoms the patient has.
     * Which may include fever, mulligrubs, back pain, heart palpitations, muscle aches, intestinal pain
     */
    private ArrayList<Symptom> symptoms = new ArrayList<Symptom>();

    /**
     * Name of patient to be easy to find him
     */
    private String name;


    private boolean dead;
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
     * Patient may be busy if performing some treatment.
     * When busy, a patient won't bid for any treatment auction.
     */
    private boolean busy = false;

    /************************************ Constructors ************************************/

    public PatientAgent(){
        triage();
    }

    public PatientAgent(String[] symp, String name){

        this.enterTime = System.currentTimeMillis();
        this.name = name;
        this.dead = false;

        for(String s : symp){
            this.symptoms.add(new Symptom(s));
        }
    }

    public void triage(){

        String option;
        Scanner scan = new Scanner(System.in);
        this.symptoms = new ArrayList<Symptom>();
        this.enterTime = System.currentTimeMillis();

        String[] symptoms = new String[] {"fever","mulligrubs", "back pain", "heart palpitations",
                "muscles aches", "intestinal pain"};

        System.out.println("What is your name?");
        option = scan.nextLine();
        this.name = option;

        for(String s : symptoms){
            System.out.println("Do you have " + s + "? (y or n) ");
            option = scan.nextLine();

            while(option.compareTo("y") != 0 && option.compareTo("n") != 0){
                System.out.println("Invalid choice.\n Do you have " + s + "? (y or n) ");
                option = scan.nextLine();
            }
            if(option.compareTo("y") == 0)
                this.symptoms.add(new Symptom(s));
        }

        System.out.println("\n\n-----------------------------------------------------------------------------\n\n");
    }


    /************************************ Get functions ************************************/

    /**
     * Returns patient\'s busy state
     * @return busy
     */
    public boolean isBusy() {
        return busy;
    }

    public double calculateBid(double time){
        if(healthState < 0){
            System.out.println("The patient " + getLocalName() + " died");
            dead = true;
        }
        double res;

        res = ((1-initialState) * time) + (Math.pow(decreaseRate,2)/2);

        return res;
    }


    /************************************ Set functions ************************************/

    /**
     * Initial health state "s" is equals to 1 - symptoms ( fever 0.25, mulligrubs 0.1, back pain 0.15,
     * heart palpitations 0.35, muscle aches 0.05, instestinal pain 0.2 )
     * Decrease Rate "b" is equals to 1 - symptoms (  fever 0.6, mulligrubs 0.3, back pain 0.3,
     * heart palpitations 0.6, muscle aches 0.2, instestinal pain 0.4 )
     * Exams that patient has to do are equals to the symptoms list in same order to the symptoms list
     */

    public void setHealthState(){

        float s = 1, b = 0;

        for(Symptom symptom : symptoms){
            s -= symptom.getHealth();
            b += symptom.getDecreaseRate();
        }

        this.decreaseRate = b;
        this.initialState = s;
        long timeInHospital = System.currentTimeMillis() - enterTime; // retira um valor de decrease rate por cada 1440000 ms

        this.healthState =  initialState-((decreaseRate/10000)*timeInHospital);

        //System.out.println("The health state of " + getLocalName() + " is: " + this.healthState);
    }

    public Boolean removeSymptom(){
        int i = 0;

        while (i < symptoms.size()){
            if (lastExam.compareTo(symptoms.get(i).getExam()) == 0){
                //System.out.println("The patient removed the symptom: " + symptoms.get(i).getExam());
                symptoms.remove(i);
                setHealthState();
                if (symptoms.size() == 0) {
                    long timeInHospital = System.currentTimeMillis() - enterTime;
                   System.out.println("Paciente com o nome: " + getLocalName() + " demorou " + timeInHospital + " a ser curado" );
                }
                return true;
            }
            else {
                i++;
            }
        }
        return false;
    }

    /**
     * Set patient\'s busy state
     * @param b Busy state
     */
    public void setBusy(boolean b) {
        this.busy = b;
    }

    public void setLastExam(String exam){
        lastExam = exam;
    }

    /**
     * Send one message per symptom to the treatment agents
     */
    private void sendMessageToTreatments() {
        for(Symptom s : symptoms) {
            DFAgentDescription dfd = new DFAgentDescription();
            ServiceDescription sd  = new ServiceDescription();
            sd.setType(Treatment.TYPE);
            dfd.addServices(sd);
            try {
                DFAgentDescription[] result = DFService.search(this, dfd);
                ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                message.setContent(s.getExam());
                for(DFAgentDescription a : result){
                    message.addReceiver(a.getName());
                }
                send(message);
            } catch (FIPAException e) {
                e.printStackTrace();
            }
        }
    }

    /************************************ OVERRIDE FUNCTIONS ************************************/



    protected void setup() {
        System.out.println("Patient Agent " + getLocalName() + " started.");
        ServiceDescription sd  = new ServiceDescription();
        sd.setType(PatientAgent.TYPE);
        sd.setName(this.name);
        register(sd);

        // Inform common agent that new patient just arrived
        ACLMessage message = new ACLMessage();
        message.setSender(this.getAID());
        message.setContent(CommonAgent.NEW_PATIENT_MESSAGE);
        message.setPerformative(ACLMessage.SUBSCRIBE);
        message.addReceiver(new AID("Common", AID.ISLOCALNAME));   // If exists 2 Common Agents
        send(message);

        sendMessageToTreatments();
    }

    void register(ServiceDescription sd) {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            addBehaviour(new WaitForMessage(this));
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

    protected void visitCommonAgent(){
        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        message.setSender(this.getAID());
        message.setContent(CommonAgent.UPDATE_PATIENT_MESSAGE);
        message.addReceiver(new AID("Common", AID.ISLOCALNAME));   // If exists 2 Common Agents
        send(message);
    }


    /************************************ BEHAVIORS  ************************************/


    /**
     * Patient agent listener for messages
     */

    private class WaitForMessage extends CyclicBehaviour {

        public WaitForMessage(Agent a){
            super(a);
        }

        @Override
        public void action() {
            ACLMessage message = receive();
            if (message != null) {
                handleMessage(message);
            }
            block();
        }

        private void handleMessage (ACLMessage message) {
            String m = message.getContent();
            String common = message.getSender().getLocalName();

            switch (message.getPerformative()) {
                case ACLMessage.INFORM:
                    //System.out.println("INFORM MESSAGE RECEIVED AT PATIENT BY - " + message.getSender().getLocalName());
                    if (m.equals(PatientAgent.NEW_TRIAGE_MESSAGE)) {
                        // patient is unknown to Common Agent, add him to waitingTriagePatients
                        if (common.compareTo("Common") == 0) {
                            setHealthState();
                        }
                    }
                    break;
                case ACLMessage.INFORM_REF:
                    //System.out.println("INFORM_REF MESSAGE RECEIVED AT PATIENT");
                    if (m.equals(PatientAgent.NEW_UPDATE_MESSAGE)) {
                        if (common.compareTo("Common") == 0) {
                            removeSymptom();
                            setBusy(false);
                        }
                    }
                    break;
                case ACLMessage.REQUEST:

                    if(!isBusy() && symptoms.size() != 0 && !dead) {
                        double time = Double.parseDouble(m);
                        double bid = calculateBid(time);
                        //System.out.println("REQUEST MESSAGE RECEIVED AT PATIENT AND ANSWER WITH " + bid + " value");

                        if(dead){
                            ACLMessage reply = message.createReply();
                            reply.setPerformative(ACLMessage.UNKNOWN);
                            reply.setContent(PatientAgent.NEW_DEAD_MESSAGE);
                            send(reply);
                        }
                        else {
                            ACLMessage reply = message.createReply();
                            reply.setPerformative(ACLMessage.PROPOSE);
                            reply.setContent(String.valueOf(bid));
                            send(reply);
                        }
                    }
                    break;
                case ACLMessage.ACCEPT_PROPOSAL:
                    //System.out.println("ACCEPT_PROPOSAL MESSAGE RECEIVED AT PATIENT");
                    if (m.equals(Treatment.BEGIN_TREATMENT_MESSAGE)) {
                        if(!isBusy()){
                            //System.out.println("PATIENT IS NOT BUSY");
                            //System.out.println("The Patient " + getLocalName() + " began the exam " + message.getSender().getLocalName());
                            setBusy(true);
                        }
                        else{
                            //System.out.println("PATIENT IS BUSY, SEARCH ANOTHER");
                            ACLMessage reply = message.createReply();
                            reply.setPerformative(ACLMessage.FAILURE);
                            send(reply);
                        }

                    }
                    break;
                case ACLMessage.AGREE:
                    //System.out.println("AGREE MESSAGE RECEIVED AT PATIENT");
                    if (m.equals(Treatment.FINISH_TREATMENT_MESSAGE)) {
                        setLastExam(message.getSender().getLocalName());
                        visitCommonAgent();
                    }
                    break;
            }
        }
    }
}
