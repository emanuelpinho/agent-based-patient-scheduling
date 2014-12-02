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
     * Patient may be busy if performing some treatment.
     * When busy, a patient won't bid for any treatment auction.
     */
    private boolean busy = false;

    /************************************ Constructors ************************************/

    public void triage(){

        String option;
        Scanner scan = new Scanner(System.in);
        this.symptoms = new ArrayList<Symptom>();
        this.enterTime = System.currentTimeMillis();

        String[] symptoms = new String[] {"fever","mulligrubs", "back pain", "heart palpitations",
                "muscles aches", "intestinal pain"};

        this.name = getLocalName();

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

    public ArrayList<Symptom> getSymptoms(){
        return symptoms;
    }

    public String getLastExam(){
        return lastExam;
    }

    /**
     * Returns patient\'s busy state
     * @return busy
     */
    public boolean isBusy() {
        return busy;
    }

    public double calculateBid(double time){
        double res;

        res = (initialState * time) + (Math.pow(decreaseRate,2)/2);

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

        float s = 0, b = 0;

        for(Symptom symptom : symptoms){
            s += symptom.getHealth();
            b += symptom.getDecreaseRate();
        }

        this.decreaseRate = b;
        this.initialState = s;
        long timeInHospital = System.currentTimeMillis() - enterTime; // retira um valor de decrease rate por cada 1440000 ms

        this.healthState =  initialState-((decreaseRate/300000)*timeInHospital);

        System.out.println("Health state is: " + this.healthState);
    }

    public Boolean removeSymptom(){
        int i = 0;

        while (i < symptoms.size()){
            if (lastExam.compareTo(symptoms.get(i).getExam()) == 0){
                symptoms.remove(i);
                setHealthState();
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

        triage();

        System.out.println("Patient Agent " + getLocalName() + " started.");
        ServiceDescription sd  = new ServiceDescription();
        sd.setType(PatientAgent.TYPE);
        sd.setName(getLocalName());
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

        PatientAgent pa;

        public WaitForMessage(PatientAgent a){
            super(a);
            this.pa = a;
        }

        @Override
        public void action() {
            ACLMessage message = receive();
            if (message != null) {
                handleMessage(message);
            }
            else {
                block();
            }
        }

        private void handleMessage (ACLMessage message) {
            String m = message.getContent();
            String common = message.getSender().getName();

            switch (message.getPerformative()) {
                case ACLMessage.INFORM:
                    System.out.println("INFORM MESSAGE RECEIVED");
                    if (m.equals(PatientAgent.NEW_TRIAGE_MESSAGE)) {

                        // patient is unknown to Common Agent, add him to waitingTriagePatients
                        if (common.compareTo("Common") == 0) {
                            pa.setHealthState();
                        }
                    }
                    break;
                case ACLMessage.INFORM_REF:
                    System.out.println("INFORM_REF MESSAGE RECEIVED");
                    if (m.equals(PatientAgent.NEW_UPDATE_MESSAGE)) {

                        if (common.compareTo("Common") == 0) {
                            pa.removeSymptom();
                            setBusy(false);
                        }
                    }
                    break;
                case ACLMessage.REQUEST:
                    System.out.println("REQUEST MESSAGE RECEIVED");
                    if(!isBusy()) {
                        double time = Double.parseDouble(m);
                        double bid = calculateBid(time);
                        ACLMessage reply = message.createReply();
                        reply.setPerformative(ACLMessage.PROPOSE);
                        reply.setContent(String.valueOf(bid));
                        send(reply);
                    }
                    break;
                case ACLMessage.ACCEPT_PROPOSAL:
                    System.out.println("ACCEPT_PROPOSAL MESSAGE RECEIVED");
                    if (m.equals(Treatment.BEGIN_TREATMENT_MESSAGE)) {
                        setBusy(true);
                    }
                    break;
                case ACLMessage.AGREE:
                    System.out.println("AGREE MESSAGE RECEIVED");
                    if (m.equals(Treatment.FINISH_TREATMENT_MESSAGE)) {
                        visitCommonAgent();
                    }
                    break;
            }
        }
    }
}
