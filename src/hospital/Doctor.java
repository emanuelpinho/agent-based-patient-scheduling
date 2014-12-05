package hospital;

import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.util.*;


public class Doctor extends Agent {

    public static String TYPE = "Doctor";

    /**
     * Experience that the Doctor has in a specific treatment
     */
    private HashMap<String, Experience> exp = new HashMap<String, Experience>();


    private String name;

    /**
     * Patient may be busy if performing some treatment.
     * When busy, a patient won't bid for any treatment auction.
     */
    private boolean busy = false;

    /**
     * Returns patient\'s busy state
     * @return busy
     */
    public boolean isBusy() {
        return busy;
    }

    /**
     * Set patient\'s busy state
     * @param b Busy state
     */
    public void setBusy(boolean b) {
        this.busy = b;
    }

    public double getExp(String treatment){
        return exp.get(treatment).getExp();
    }

    public Doctor(){
        this.name = getLocalName();
        constructor();
    }

    public Doctor(String name){
        this.name = name;
        constructor();
    }

    public void incrementExp(String treatment){
        exp.get(treatment).incrementExp();
    }

    public void constructor(){
        exp.put("analysis", new Experience());
        exp.put("endoscopy", new Experience());
        exp.put("resonance", new Experience());
        exp.put("electrocardiogram", new Experience());
        exp.put("sonography", new Experience());
        exp.put("colonoscopy", new Experience());
    }

    protected void setup()
    {
        System.out.println("Doctor Agent " + this.name + " started.");
        ServiceDescription sd  = new ServiceDescription();
        sd.setType(Doctor.TYPE);
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
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
            doDelete();
        }
    }

    private class WaitForMessage extends CyclicBehaviour {

        String actualExam;

        public WaitForMessage(Agent a){
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

        private void handleMessage (ACLMessage message) {
            String m = message.getContent();
            String s = message.getSender().getLocalName();
            switch (message.getPerformative()) {
                case ACLMessage.REQUEST:
                    //System.out.println("REQUEST MESSAGE RECEIVED AT DOCTOR");
                    if(!isBusy()) {
                        double exp = getExp(s) * 10;
                        ACLMessage reply = message.createReply();
                        reply.setPerformative(ACLMessage.SUBSCRIBE);
                        reply.setContent(String.valueOf(exp));
                        send(reply);
                    }
                    break;
                case ACLMessage.ACCEPT_PROPOSAL:
                    //System.out.println("ACCEPT_PROPOSAL MESSAGE RECEIVED AT DOCTOR");
                    if (m.equals(Treatment.BEGIN_TREATMENT_MESSAGE)) {
                        setBusy(true);
                        actualExam = s;
                    }
                    break;
                case ACLMessage.AGREE:
                    //System.out.println("AGREE MESSAGE RECEIVED AT DOCTOR");
                    if (m.equals(Treatment.FINISH_TREATMENT_MESSAGE) && s.compareTo(actualExam) == 0) {
                        incrementExp(s);
                        setBusy(false);
                        actualExam = null;
                    }
                    break;
                case ACLMessage.INFORM:
                    //System.out.println("INFORM MESSAGE RECEIVED AT DOCTOR");
                    if (m.equals(Treatment.FINISH_TREATMENT_MESSAGE) && s.compareTo(actualExam) == 0) {
                        setBusy(false);
                        actualExam = null;
                    }
                    break;
            }
        }
    }

}
