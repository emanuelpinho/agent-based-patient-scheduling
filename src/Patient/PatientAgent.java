package patient;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.AMSService;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import symptons.Symptom;

import java.util.ArrayList;


public class PatientAgent extends Agent {

    /**
     * List of symptons that the patien has,
     * which may include fever, mulligrubs, back pain, heart palpitations, muscle aches, intestinal pain
     */
    private ArrayList<Symptom> symptons;

    /**
     * Name of patient to be easy to find him
     */
    private String name;

    /**
     * Last exam that patient do
     */
    private String lastExam;

    /**
     * Health of patient, follow the formula: h(t)= s-bt, where, t is time in hospital, s is inicial health state
     * and b is a decrease health state.
     */
    private float healthState;

    /**
     * initial state of health
     */
    private float initialState;

    /**
     * decrease rate of health state
     */
    private float decreaseRate;

    /**
     * Time that the patient enter in the hospital
     */
    private long enterTime;


    private Logger myLogger = Logger.getMyLogger(getClass().getName());




    /************************************ Constructors ************************************/

    public PatientAgent(ArrayList<String> symptons, String name){

        for(String symptom : symptons) {
            this.symptons.add(new Symptom(symptom));
        }

        this.name = name;
        this.enterTime = System.currentTimeMillis();
    }

    public PatientAgent(){

    }


    /************************************ Get functions ************************************/

    public ArrayList<Symptom> getSymptons(){
        return symptons;
    }

    public String getLastExam(){
        return lastExam;
    }

    /************************************ Set functions ************************************/

    public void setHealthState(){

        float s = 0, b = 0;

        for(Symptom symptom : symptons){
            s += symptom.getHealth();
            b += symptom.getDecreaseRate();
        }

        this.decreaseRate = b;
        this.initialState = s;
        long timeInHospital = System.currentTimeMillis() - enterTime;

        this.healthState =  initialState-(decreaseRate/timeInHospital);
    }

    public Boolean removeSymptom(){
        int i = 0;

        while(i < symptons.size()){
            if(lastExam.compareTo(symptons.get(i).getExam()) == 0){
                symptons.remove(i);
                return true;
            }
            else
                i++;
        }

        return false;
    }

    /************************************ OVERRIDE FUNCTIONS ************************************/

    protected void setup()
    {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Patient");
        sd.setName(getName());
        //sd.setOwnership("TILAB");
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            addBehaviour(new SendMessage());
        } catch (FIPAException e) {
            myLogger.log(Logger.SEVERE, "Agent " + getLocalName()+" - Cannot register with DF", e);
            doDelete();
        }

    }

    public class SendMessage extends SimpleBehaviour {

        private Boolean done;

        public SendMessage(){
            super();
        }

        @Override
        public void action() {

            AMSAgentDescription[] agents = null;
            try {
                SearchConstraints c = new SearchConstraints();
                c.setMaxResults (new Long(-1));
                agents = AMSService.search(myAgent, new AMSAgentDescription(), c);
            }
            catch (Exception e) {
                System.out.println( "Problem searching AMS: " + e );
                e.printStackTrace();
            }
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setContent( "Ping" );

            for (int i=0; i<agents.length;i++) {
                System.out.println("Agent: " + agents[i].getName());
                msg.addReceiver(agents[i].getName());
            }

            send(msg);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            done = true;
        }

        @Override
        public boolean done() {
            return done;
        }

        @Override
        public int onEnd() {
            takeDown();
            myAgent.doDelete();
            return super.onEnd();
        }
    }

}
