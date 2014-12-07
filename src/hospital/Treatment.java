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
            Thread.sleep(100);
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
            Thread.sleep(100);
        } catch (FIPAException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void finishTreatment(DFAgentDescription dfd, ServiceDescription sd, AID doctor, AID patient){
        dfd.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, dfd);
            ACLMessage message = new ACLMessage(ACLMessage.AGREE);
            message.setContent(Treatment.FINISH_TREATMENT_MESSAGE);
            for(DFAgentDescription a : result){
                if(a.getName().equals(doctor) || a.getName().equals(patient))
                    message.addReceiver(a.getName());
            }
            send(message);
            waitingList.remove(patient);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    private void acceptProposal(DFAgentDescription dfd, ServiceDescription sd, AID person){
        dfd.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, dfd);
            ACLMessage message = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
            message.setContent(Treatment.BEGIN_TREATMENT_MESSAGE);
            for(DFAgentDescription a : result){
                if(a.getName().equals(person))
                    message.addReceiver(a.getName());
            }
            send(message);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    private void releaseDoctor(DFAgentDescription dfd, ServiceDescription sd, AID doctor){
        dfd.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, dfd);
            ACLMessage message = new ACLMessage(ACLMessage.INFORM);
            message.setContent(Treatment.FINISH_TREATMENT_MESSAGE);
            for(DFAgentDescription a : result){
                if(a.getName().equals(doctor))
                    message.addReceiver(a.getName());
            }
            send(message);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    private class WaitingListThread extends Thread{

        public void run() {
            addBehaviour(new CyclicBehaviour() {
                @Override
                public void action() {
                    if(waitingList.size() > 0){
                        AID doctor, patient;
                        DFAgentDescription dfd = new DFAgentDescription();
                        ServiceDescription sd = new ServiceDescription();
                        askDoctors(dfd, sd);
                        if (doctorAID == null)
                            block();
                        doctor = doctorAID;
                        acceptProposal(dfd, sd, doctor);
                        initBiding(dfd, sd);
                        if (patientAID == null) {
                            releaseDoctor(dfd, sd, doctor);
                            block();
                        }
                        patient = patientAID;
                        busy = true;
                        acceptProposal(dfd, sd, patient);
                        try {
                            Thread.sleep((long) timeOfTreatment);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        finishTreatment(dfd, sd, doctor, patient);
                        busy = false;
                        resetValues();
                    }
                    block();
                }
            });
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
                    //System.out.println("SUBSCRIBE MESSAGE RECEIVED AT TREATMENT");
                    m = Double.parseDouble(message.getContent());
                    if(m > doctorPropose && !busy){
                        doctorPropose = m;
                        doctorAID = agent;
                    }
                    break;
                case ACLMessage.PROPOSE:
                    m = Double.parseDouble(message.getContent());
                    //System.out.println("PROPOSE MESSAGE RECEIVED AT TREATMENT");
                    if(m > patientBid && !busy){
                        patientBid = m;
                        patientAID = agent;
                    }
                    break;
                case ACLMessage.REQUEST:
                    //System.out.println("REQUEST MESSAGE RECEIVED AT TREATMENT - " + message.getContent());
                    if(name.equals(message.getContent())) {
                        waitingList.add(message.getSender());
                    }
                    break;
            }
        }
    }
}
