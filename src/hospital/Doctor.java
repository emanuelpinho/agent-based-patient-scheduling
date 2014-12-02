package hospital;

import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.util.Map;
import java.util.Scanner;


public class Doctor extends Agent {

    public static String TYPE = "Doctor";

    /**
     * Experience that the Doctor has in a specific treatment
     */
    private Map<String, Experience> exp;

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
        constructor();
        System.out.println("Doctor Agent " + getLocalName() + " started.");
        ServiceDescription sd  = new ServiceDescription();
        sd.setType(Treatment.TYPE);
        sd.setName(getLocalName());
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
            switch (message.getPerformative()) {
                case ACLMessage.REQUEST:
                    System.out.println("REQUEST MESSAGE RECEIVED");
                    if(!isBusy()) {
                        String t = message.getSender().getName();
                        double exp = getExp(t) * 10;
                        ACLMessage reply = message.createReply();
                        reply.setPerformative(ACLMessage.SUBSCRIBE);
                        reply.setContent(String.valueOf(exp));
                        send(reply);
                    }
                    break;
                case ACLMessage.ACCEPT_PROPOSAL:
                    System.out.println("ACCEPT_PROPOSAL MESSAGE RECEIVED");
                    if (m.equals(Treatment.BEGIN_TREATMENT_MESSAGE)) {
                        setBusy(true);
                        actualExam = message.getSender().getName();
                    }
                    break;
                case ACLMessage.AGREE:
                    System.out.println("AGREE MESSAGE RECEIVED");
                    String t = message.getSender().getName();
                    if (m.equals(Treatment.FINISH_TREATMENT_MESSAGE) && t.compareTo(actualExam) == 0) {
                        incrementExp(message.getSender().getName());
                        setBusy(false);
                        actualExam = null;
                    }
                    break;
            }
        }
    }

}
