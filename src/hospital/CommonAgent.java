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

    /**
     * Define if Agent is available
     */
    private Boolean free;

    private ACLMessage msg;

    private Logger myLogger = Logger.getMyLogger(getClass().getName());

    protected void setup()
    {

        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Common");
        sd.setName(getName());
        //sd.setOwnership("TILAB");
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            addBehaviour(new Triage(this));

        } catch (FIPAException e) {
            myLogger.log(Logger.SEVERE, "Agent " + getLocalName()+" - Cannot register with DF", e);
            doDelete();
        }
    }


    public class Triage extends CyclicBehaviour {

        private Boolean done;

        public Triage(Agent a){
            super(a);
        }

        @Override
        public void action() {
            System.out.println("Agent: " + myAgent.getLocalName());
            msg = receive();
            if (msg!=null)
                System.out.println(" - " + myAgent.getLocalName() + " <- " + msg.getContent());
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
