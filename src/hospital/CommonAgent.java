package hospital;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import patient.PatientAgent;
import symptons.Symptom;

import java.util.ArrayList;

/**
 * This agent is responsible to do first consultation and update health state after each exam
 */
public class CommonAgent extends Agent {

    /**
     * Patient to do triage consultation
     */
    private PatientAgent patient;

    /**
     * Define if Agent is available
     */
    private Boolean free;


    public class Triage extends SimpleBehaviour {

        private Boolean done;

        @Override
        public void action() {
            done = false;

            String lastExam  = patient.getLastExam();
            ArrayList<Symptom> symptons = patient.getSymptons();

            if (symptons == null) {
                // Treatment is over
            }
            else if (lastExam == null && symptons.isEmpty()) {
                triageConsultation();
            }
            else {
                updateHealthState();
            }

            done = true;
        }

        @Override
        public boolean done() {
            return done;
        }

        /**
         * Initial healt state "s" is equals to 1 - symptons ( fever 0.25, mulligrubs 0.1, back pain 0.15,
         * heart palpitations 0.35, muscle aches 0.05, instestinal pain 0.2 )
         * Decrease Rate "b" is equals to 1 - symptons (  fever 0.6, mulligrubs 0.3, back pain 0.3,
         * heart palpitations 0.6, muscle aches 0.2, instestinal pain 0.4 )
         * Exams that patient has to do are equals to the symptons list in same order to the symptons list
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