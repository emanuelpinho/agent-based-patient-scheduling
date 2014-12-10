package hospital;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
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

    protected boolean busy, doctorSearch, patientSearch;

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
        this.doctorSearch = true;
        this.patientSearch = true;

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
            WaitingListThread wl = new WaitingListThread();
            wl.start();
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
        doctorSearch = true;
        patientSearch = true;
    }

    private class WaitingListThread extends Thread{

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

        public void run() {
            Random ran = new Random();

            while( 1==1 ) {
                if (waitingList.size() > 0) {

                    sendMessage(Doctor.TYPE, Treatment.NEW_TREATMENT_MESSAGE, ACLMessage.REQUEST, null);

                    try {
                        Thread.sleep(ran.nextInt(1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    doctorSearch = false;

                    if (doctorAID != null) {

                        sendMessage(Doctor.TYPE, Treatment.BEGIN_TREATMENT_MESSAGE, ACLMessage.ACCEPT_PROPOSAL, doctorAID);

                        try {
                            Thread.sleep(600);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if(!doctorSearch) {

                            setTimeOfTreatment(doctorPropose);

                            for (AID p : waitingList)
                                sendMessage(PatientAgent.TYPE, String.valueOf(timeOfTreatment), ACLMessage.REQUEST, p);

                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if (patientAID != null) {

                                patientSearch = false;
                                sendMessage(PatientAgent.TYPE, Treatment.BEGIN_TREATMENT_MESSAGE, ACLMessage.ACCEPT_PROPOSAL, patientAID);

                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                if (!patientSearch) {

                                    busy = true;

                                    System.out.println("Patient " + patientAID + " win bidding at " + getLocalName() + " and wait " + timeOfTreatment);
                                    try {
                                        Thread.sleep((long) timeOfTreatment);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    sendMessage(PatientAgent.TYPE, Treatment.FINISH_TREATMENT_MESSAGE, ACLMessage.AGREE, patientAID);
                                    sendMessage(Doctor.TYPE, Treatment.FINISH_TREATMENT_MESSAGE, ACLMessage.AGREE, doctorAID);
                                    busy = false;
                                } else
                                    sendMessage(Doctor.TYPE, Treatment.FINISH_TREATMENT_MESSAGE, ACLMessage.INFORM, doctorAID);
                            }
                        }
                        else{
                            System.out.println("OK, i go to search another doctor");
                        }
                    }
                }

                System.out.println("END OF CYCLE");

                resetValues();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
                    if (m > doctorPropose && !busy && doctorSearch) {
                        doctorPropose = m;
                        doctorAID = agent;
                    }
                    break;
                case ACLMessage.PROPOSE:
                    m = Double.parseDouble(message.getContent());
                    //System.out.println("PROPOSE MESSAGE RECEIVED AT TREATMENT");
                    if (m > patientBid && !busy && patientSearch) {
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
                    doctorSearch = true;
                    break;

                case ACLMessage.FAILURE:
                    //System.out.println("FAILURE MESSAGE RECEIVED AT TREATMENT ");
                    patientSearch = true;
                    break;
            }
        }
    }
}
