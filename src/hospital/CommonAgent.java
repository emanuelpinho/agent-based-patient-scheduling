package hospital;

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

    /**
     * patient to do triage consultation
     */
    private PatientAgent patient;

    protected void setup()
    {
        ServiceDescription sd  = new ServiceDescription();
        sd.setType( "Common" );
        sd.setName( getLocalName() );
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


    public class WaiForMessage extends CyclicBehaviour {

        public WaiForMessage(Agent a){
            super(a);
        }

        @Override
        public void action() {
            System.out.println("Agent: " + myAgent.getLocalName());
            ACLMessage msg = receive();
            if (msg!=null) {
                System.out.println("Message received in : " + myAgent.getLocalName() + " -> " + msg.getContent()
                        + " from " + msg.getSender().getLocalName());

            }
            else
                block();
        }

        /**
         * Initial health state "s" is equals to 1 - symptoms ( fever 0.25, mulligrubs 0.1, back pain 0.15,
         * heart palpitations 0.35, muscle aches 0.05, instestinal pain 0.2 )
         * Decrease Rate "b" is equals to 1 - symptoms (  fever 0.6, mulligrubs 0.3, back pain 0.3,
         * heart palpitations 0.6, muscle aches 0.2, instestinal pain 0.4 )
         * Exams that patient has to do are equals to the symptoms list in same order to the symptoms list
         */
        public void triageConsultation(){
            patient.setHealthState();
        }

        public void updateHealthState(){
            patient.removeSymptom();
            patient.setHealthState();
        }
    }

}
