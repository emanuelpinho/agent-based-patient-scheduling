package hospital;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import patient.PatientAgent;
import symptons.Symptom;

import java.util.ArrayList;

/**
 * This agent is responsible to do first consultation and update health state after each exam
 */
public class CommonAgent extends Agent {

    public static String TYPE = "Common";
    public static String NEW_PATIENT_MESSAGE = "NEW PATIENT";
    public static String UPDATE_PATIENT_MESSAGE = "UPDATE PATIENT";

    private ArrayList<AID> waitingTriagePatients = new ArrayList<AID>();


    protected void setup()
    {
        System.out.println("Common Agent " + getLocalName() + " started.");
        ServiceDescription sd  = new ServiceDescription();
        sd.setType(CommonAgent.TYPE);
        sd.setName(getLocalName());   // REPLACE to "Common" ? And if exists more than one?
        register(sd);
    }

    void register(ServiceDescription sd)
    {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd );
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

    /**
     * Common agent listener for messages
     */
    private class WaiForMessage extends CyclicBehaviour {

        public WaiForMessage(Agent a){
            super(a);
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

        /**
         * Handle messages sent to Common Agent
         * Currently the Common Agent listens for new patient arrivals
         * @param message Message received
         */
        private void handleMessage (ACLMessage message) {
            AID patient = message.getSender();
            String m = message.getContent();

            switch (message.getPerformative()) {
                case ACLMessage.SUBSCRIBE:
                    System.out.println("SUBSCRIBE MESSAGE RECEIVED");
                    if (m.equals(CommonAgent.NEW_PATIENT_MESSAGE)) {
                        // patient is unknown to Common Agent, add him to waitingTriagePatients
                        if (!waitingTriagePatients.contains(patient)) {
                            System.out.println("Add patient to watching list");
                            waitingTriagePatients.add(patient);

                            ACLMessage reply = message.createReply();
                            reply.setPerformative(ACLMessage.INFORM);
                            reply.setContent(PatientAgent.NEW_TRIAGE_MESSAGE);
                            send(reply);
                        }
                    }
                    break;
                case ACLMessage.REQUEST:
                    System.out.println("REQUEST MESSAGE RECEIVED");
                    if (m.equals(CommonAgent.UPDATE_PATIENT_MESSAGE)) {

                        // patient is unknown to Common Agent, add him to waitingTriagePatients
                        if (waitingTriagePatients.contains(patient)) {
                            ACLMessage reply = message.createReply();
                            reply.setPerformative(ACLMessage.INFORM_REF);
                            reply.setContent(PatientAgent.NEW_UPDATE_MESSAGE);
                            send(reply);
                        }
                    }
                    break;

            }
        }

    }

}
