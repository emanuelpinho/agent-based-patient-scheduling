package hospital;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.AMSService;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import javafx.scene.Parent;
import patient.PatientAgent;
import symptons.Symptom;

import java.util.*;

public class Treatment extends Agent {

    public static String TYPE = "Treatment";
    public static String NEW_TREATMENT_MESSAGE = "TREATMENT_MESSAGE";
    public static String BEGIN_TREATMENT_MESSAGE = "BEGIN_TREATMENT_MESSAGE";
    public static String FINISH_TREATMENT_MESSAGE = "FINISH_TREATMENT_MESSAGE";

    private String name;

    protected AID doctorAID, patientAID;

    protected double doctorPropose, patientBid, timeOfTreatment;

    protected boolean busy;

    private ArrayList<AID> waitingList = new ArrayList<AID>();

    public Treatment(){
        constructor();
    }

    public Treatment(String name){
        this.name = name;
        this.timeOfTreatment = 0;
    }

    public void constructor(){
        busy = false;
        Scanner scan = new Scanner(System.in);

        System.out.println("What is the name of exam ? (analysis, endoscopy, resonance, electrocardiogram, " +
                "sonography or colonoscopy) ");
        String option = scan.nextLine();

        while(option.compareTo("analysis") != 0 && option.compareTo("endoscopy") != 0 &&
                option.compareTo("resonance") != 0 && option.compareTo("electrocardiogram") != 0 &&
                option.compareTo("sonography") != 0 && option.compareTo("colonoscopy") != 0){

            System.out.println("Invalid choice.\n What is the name of exam ? (analysis, endoscopy, resonance, " +
                    "electrocardiogram, sonography or colonoscopy) ");
            option = scan.nextLine();
        }
        this.name = option;
        this.timeOfTreatment = 0;
    }

    protected void setup()
    {
        System.out.println("Treatment Agent " + this.name + " started.");
        ServiceDescription sd  = new ServiceDescription();
        sd.setType(Treatment.TYPE);
        sd.setName(this.name);
        register(sd);
    }

    void register(ServiceDescription sd)
    {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            addBehaviour(new WaitForMessage(this));
            addBehaviour(new WaitingListBehaviour(this, 1000));
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
            doDelete();
        }
    }

    public void setTimeOfTreatment(double doctorTime){
        timeOfTreatment = doctorTime*2 + 2000;
    }

    public void resetValues(){
        doctorPropose = 0;
        patientBid = 0;
        timeOfTreatment = 0;
        doctorAID = null;
        patientAID = null;
    }

    private enum State {
        LOOKING_FOR_PATIENTS,
        LOOKING_FOR_MEDICS,
        WAITING_FOR_MEDIC_APPROVAL,
        START_TREATMENT,
        FINISH_TREATMENT,
        LEAVE_DOCTOR,
        DISABLE_STATE
    }

    private State state = State.LOOKING_FOR_MEDICS;

    private class WaitingListBehaviour extends TickerBehaviour {



        private void sendMessage(String type, String content, int performative, AID receiver){
            DFAgentDescription dfd = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType(type);
            dfd.addServices(sd);
            try {
                DFAgentDescription[] result = DFService.search(Treatment.this, dfd);
                ACLMessage message = new ACLMessage(performative);
                message.setContent(content);
                if(receiver == null) {
                    for (DFAgentDescription a : result) {
                        message.addReceiver(a.getName());
                    }
                }
                else{
                    for(DFAgentDescription a : result){
                        if(a.getName().equals(receiver))
                            message.addReceiver(a.getName());
                    }
                }
                send(message);
            } catch (FIPAException e) {
                e.printStackTrace();
            }
        }

        public WaitingListBehaviour(Agent a, long interval) {
            super(a, interval);
        }

        protected void onTick() {
            switch (state) {
                case LOOKING_FOR_MEDICS:
                    if (waitingList.size() == 0) {
                        //System.out.println("No patients waiting");
                        return;
                    }
                    resetValues();
                    //System.out.println("Looking for doctors for treatment");
                    sendMessage(Doctor.TYPE, Treatment.NEW_TREATMENT_MESSAGE, ACLMessage.REQUEST, null);
                    state = State.WAITING_FOR_MEDIC_APPROVAL;
                    break;
                case WAITING_FOR_MEDIC_APPROVAL:
                    if (doctorAID != null) {
                        //System.out.println("Looking for medical approval to start treatment");
                        sendMessage(Doctor.TYPE, Treatment.BEGIN_TREATMENT_MESSAGE, ACLMessage.ACCEPT_PROPOSAL, doctorAID);
                        state = State.LOOKING_FOR_PATIENTS;
                    } else {
                        //System.out.println("Starting all over again");
                        state = State.LOOKING_FOR_MEDICS;
                    }
                    break;
                case LOOKING_FOR_PATIENTS:
                    //System.out.println("Looking for patients for the treatment");
                    setTimeOfTreatment(doctorPropose);
                    for (AID p : waitingList) {
                        sendMessage(PatientAgent.TYPE, String.valueOf(timeOfTreatment), ACLMessage.REQUEST, p);
                    }
                    state = State.START_TREATMENT;
                    break;
                case START_TREATMENT:
                    if (patientAID != null) {
                        //System.out.println("Telling patient to begin treatment");
                        sendMessage(PatientAgent.TYPE, Treatment.BEGIN_TREATMENT_MESSAGE, ACLMessage.ACCEPT_PROPOSAL, patientAID);
                        state = State.DISABLE_STATE;
                        addBehaviour(new WakerBehaviour(Treatment.this, (long) timeOfTreatment) {
                            @Override
                            protected void onWake() {
                                super.onWake();
                                System.out.println("Treatment: " + getLocalName() + " is over");
                                state = State.FINISH_TREATMENT;
                            }
                        });
                        return;
                    }

                    //System.out.println("Starting all over again");
                    sendMessage(Doctor.TYPE, Treatment.FINISH_TREATMENT_MESSAGE, ACLMessage.INFORM, doctorAID);
                    state = State.LOOKING_FOR_MEDICS;
                    break;

                case FINISH_TREATMENT:
                    //System.out.println("Telling doctor and patient that treatment is over");
                    sendMessage(PatientAgent.TYPE, Treatment.FINISH_TREATMENT_MESSAGE, ACLMessage.AGREE, patientAID);
                    sendMessage(Doctor.TYPE, Treatment.FINISH_TREATMENT_MESSAGE, ACLMessage.AGREE, doctorAID);
                    waitingList.remove(patientAID);

                    state = State.LOOKING_FOR_MEDICS;
                    break;
                case LEAVE_DOCTOR:
                    //System.out.println("Starting all over again");
                    sendMessage(Doctor.TYPE, Treatment.FINISH_TREATMENT_MESSAGE, ACLMessage.INFORM, doctorAID);
                    state = State.LOOKING_FOR_MEDICS;
                    break;
                default:
                    break;
            }
        }
    }

    private class WaitForMessage extends CyclicBehaviour {

        public WaitForMessage(Treatment a) {
            super(a);
        }

        @Override
        public void action(){
            ACLMessage message = receive();
            if (message != null) {
                handleMessage(message);
            }
            block();
        }

        private void handleMessage (ACLMessage message) {
            double m;
            AID agent = message.getSender();

            switch (message.getPerformative()) {
                case ACLMessage.SUBSCRIBE:
                    //System.out.println("SUBSCRIBE MESSAGE RECEIVED AT TREATMENT");
                    m = Double.parseDouble(message.getContent());
                    if (m > doctorPropose && !busy && state == State.WAITING_FOR_MEDIC_APPROVAL) {
                        doctorPropose = m;
                        doctorAID = agent;
                    }
                    break;
                case ACLMessage.PROPOSE:
                    m = Double.parseDouble(message.getContent());
                    //System.out.println("PROPOSE MESSAGE RECEIVED AT TREATMENT");
                    if (m > patientBid && !busy && state == State.START_TREATMENT) {
                        patientBid = m;
                        patientAID = agent;
                    }
                    break;
                case ACLMessage.REQUEST:
                    //System.out.println("REQUEST MESSAGE RECEIVED AT TREATMENT - " + message.getContent());
                    if (name.equals(message.getContent())) {
                        waitingList.add(message.getSender());
                    }

                case ACLMessage.CANCEL:
                    //System.out.println("CANCEL MESSAGE RECEIVED AT TREATMENT ");
                    state = State.LOOKING_FOR_MEDICS;
                    break;

                case ACLMessage.FAILURE:
                    //System.out.println("FAILURE MESSAGE RECEIVED AT TREATMENT ");
                    state = State.LEAVE_DOCTOR;
                    break;
            }
        }
    }
}
