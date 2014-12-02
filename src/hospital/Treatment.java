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

import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Treatment extends Agent {

    public static String TYPE = "Treatment";
    public static String NEW_TREATMENT_MESSAGE = "TREATMENT_MESSAGE";

    private String name;

    protected AID doctorAID, patientAID;

    protected boolean busy;

    private PriorityQueue<AID> waitingList = new PriorityQueue<AID>();

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
        if(option.compareTo("analysis") == 0)
            this.name = option;
        else if(option.compareTo("endoscopy") == 0)
            this.name = option;
        else if(option.compareTo("resonance") == 0)
            this.name = option;
        else if(option.compareTo("electrocardiogram") == 0)
            this.name = option;
        else if(option.compareTo("sonography") == 0)
            this.name = option;
        else if(option.compareTo("colonoscopy") == 0)
            this.name = option;
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
            makeInAction();
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
            doDelete();
        }
    }

    public void makeInAction(){
        addBehaviour(new FindPeople(this));


        Timer timer = new Timer();
        timer.schedule(new requestTask(), 500);

        /*
        onAction parBehaviour = new onAction(this, ParallelBehaviour.WHEN_ALL);
        parBehaviour.addSubBehaviour(new WaitForMessage(this));
        parBehaviour.addSubBehaviour(new FindPeople(this));

        addBehaviour(parBehaviour);
        */
    }
/*
    private class onAction extends ParallelBehaviour {

        Treatment t;

        public onAction(Treatment a, int endCondition) {
            super(a, endCondition);
            this.t = a;
        }

        public int onEnd() {
            reset();
            t.addBehaviour(this);
            return super.onEnd();
        }
    }
*/
    class requestTask extends TimerTask {
        public void run() {

        }
    }

    private class FindPeople extends SimpleBehaviour{

        private Treatment t;

        private boolean done;

        public FindPeople(Treatment a) {
            super(a);
            this.t = a;
        }

        private void sendMessage(DFAgentDescription dfd, ServiceDescription sd) {
            dfd.addServices(sd);
            try {
                DFAgentDescription[] result = DFService.search(t, dfd);
                ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                message.setContent(Treatment.NEW_TREATMENT_MESSAGE);
                for(DFAgentDescription a : result){
                    message.addReceiver(a.getName());
                }
                send(message);
                done = true;
            } catch (FIPAException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void action() {
            t.busy = true;
            done = false;
            DFAgentDescription dfd = new DFAgentDescription();
            ServiceDescription sd  = new ServiceDescription();

            sd.setType(Doctor.TYPE);
            sendMessage(dfd, sd);

            sd.setType(PatientAgent.TYPE);
            sendMessage(dfd, sd);

            done = true;
        }

        @Override
        public boolean done() {
            return done;
        }
    }

    private class WaitingListBehaviour extends CyclicBehaviour {

        public WaitingListBehaviour(Agent a){
            super(a);
        }

        @Override
        public void action() {
            if (waitingList.size() > 0) {
                AID patient = waitingList.poll();
                System.out.println("CALL AND TREAT PATIENT"); // TODO
            }
        }
    }

    private class WaitForMessage extends CyclicBehaviour {
        private double doctorPropose, patientBid;

        public WaitForMessage(Treatment a) {
            super(a);
            doctorPropose = 0;
            patientBid = 0;
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
                    if(m > doctorPropose){
                        doctorPropose = m;
                        doctorAID = agent;
                    }
                    break;
                case ACLMessage.PROPOSE:
                    m = Double.parseDouble(message.getContent());
                    System.out.println("PROPOSE MESSAGE RECEIVED");
                    if(m > patientBid){
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
