package hospital;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
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

    protected void setup()
    {
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                System.out.println("Agent: " + myAgent.getLocalName());
                msg = receive();
                if (msg!=null)
                    System.out.println( " - " + myAgent.getLocalName() + " <- " + msg.getContent() );
                block(99999999);
            }
        });
    }


    public class Triage extends SimpleBehaviour {

        private Boolean done;

        @Override
        public void action() {

            //block(); // block doesn't stop execution, it just schedules the next execution

            /*
            done = false;

            String lastExam  = patient.getLastExam();
            ArrayList<Symptom> symptoms = patient.getSymptons();

            if (symptoms == null) {
                // Treatment is over
            }
            else if (lastExam == null && symptoms.isEmpty()) {
                triageConsultation();
            }
            else {
                updateHealthState();
            }

            done = true;

            */
        }

        @Override
        public boolean done() {
            return done;
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
