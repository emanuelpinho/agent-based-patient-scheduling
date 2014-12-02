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

    protected boolean busy;

    private ArrayList<AID> waitingList = new ArrayList<AID>();

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
        constructor();
        System.out.println("Treatment Agent " + getLocalName() + " started.");
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
            addBehaviour(new WaitingListBehaviour(this));
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
            doDelete();
        }
    }

    public void setTimeOfTreatment(double doctorTime){
        timeOfTreatment = doctorTime + 500;
    }

    public void resetValues(){
        doctorPropose = 0;
        patientBid = 0;
        timeOfTreatment = 0;
        doctorAID = null;
        patientAID = null;
    }

    private void askDoctors(DFAgentDescription dfd, ServiceDescription sd){
        sd.setType(Doctor.TYPE);

        dfd.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, dfd);
            ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
            message.setContent(Treatment.NEW_TREATMENT_MESSAGE);
            for(DFAgentDescription a : result){
                message.addReceiver(a.getName());
            }
            send(message);
            Thread.sleep(500);
        } catch (FIPAException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initBiding(DFAgentDescription dfd, ServiceDescription sd){
        sd.setType(PatientAgent.TYPE);

        dfd.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, dfd);
            ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
            setTimeOfTreatment(doctorPropose);
            message.setContent(String.valueOf(timeOfTreatment));
            for(DFAgentDescription a : result){
                if(waitingList.contains(a.getName()))
                    message.addReceiver(a.getName());
            }
            send(message);
            Thread.sleep(500);
        } catch (FIPAException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initTreatment(DFAgentDescription dfd, ServiceDescription sd, AID doctor, AID patient){
        dfd.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, dfd);
            ACLMessage message = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
            message.setContent(Treatment.BEGIN_TREATMENT_MESSAGE);
            for(DFAgentDescription a : result){
                if(a.getName().equals(doctor) || a.getName().equals(patient))
                    message.addReceiver(a.getName());
            }
            send(message);
            Thread.sleep((long) timeOfTreatment);

            //FINISH TREATMENT

            message.setPerformative(ACLMessage.AGREE);
            message.setContent(Treatment.FINISH_TREATMENT_MESSAGE);
            send(message);
        } catch (FIPAException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class WaitingListBehaviour extends CyclicBehaviour {

        public WaitingListBehaviour(Agent a){
            super(a);
        }

        @Override
        public void action() {
            if (waitingList.size() > 0) {
                AID doctor, patient;
                DFAgentDescription dfd = new DFAgentDescription();
                ServiceDescription sd  = new ServiceDescription();
                askDoctors(dfd, sd);
                if(doctorAID == null)
                    block();
                doctor = doctorAID;
                initBiding(dfd, sd);
                if(patientAID == null)
                    block();
                patient = patientAID;
                busy = true;
                initTreatment(dfd, sd, doctor, patient);
                busy = false;
                resetValues();
            }
            block();
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
            else {
                block();
            }
        }

        private void handleMessage (ACLMessage message) {
            Double m;
            AID agent = message.getSender();

            switch (message.getPerformative()) {
                case ACLMessage.SUBSCRIBE:
                    m = Double.parseDouble(message.getContent());
                    System.out.println("SUBSCRIBE MESSAGE RECEIVED");
                    if(m > doctorPropose && !busy){
                        doctorPropose = m;
                        doctorAID = agent;
                    }
                    break;
                case ACLMessage.PROPOSE:
                    m = Double.parseDouble(message.getContent());
                    System.out.println("PROPOSE MESSAGE RECEIVED");
                    if(m > patientBid && !busy){
                        patientBid = m;
                        patientAID = agent;
                    }
                    break;
                case ACLMessage.REQUEST:
                    System.out.println("REQUEST MESSAGE RECEIVED - " + message.getContent());
                    if(name.equals(message.getContent())) {
                        waitingList.add(message.getSender());
                    }
                    break;
            }
        }
    }
}
