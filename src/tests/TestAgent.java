package tests;

import hospital.Doctor;
import hospital.Treatment;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import patient.PatientAgent;


public class TestAgent extends Agent {

    public static String TYPE = "Test";

    protected void setup()
    {

        System.out.println("Test Agent " + getLocalName() + " started.");
        ServiceDescription sd  = new ServiceDescription();
        sd.setType(TestAgent.TYPE);
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
            addElements();
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
            doDelete();
        }
    }

    public void addElements(){

        /*String[] symptoms = new String[] {"fever","mulligrubs", "back pain", "heart palpitations",
                "muscles aches", "intestinal pain"};
*/


        AgentContainer c = getContainerController();
        AgentController p, d, t;

        try{
            d = c.acceptNewAgent("doctor", new Doctor("doctor"));
            d.start();
            d = c.acceptNewAgent("doctor2", new Doctor("doctor2"));
            d.start();
            d = c.acceptNewAgent("doctor3", new Doctor("doctor3"));
            d.start();

            t = c.acceptNewAgent("analysis", new Treatment("analysis"));
            t.start();
            t = c.acceptNewAgent("endoscopy", new Treatment("endoscopy"));
            t.start();
            t = c.acceptNewAgent("resonance", new Treatment("resonance"));
            t.start();
            t = c.acceptNewAgent("electrocardiogram", new Treatment("electrocardiogram"));
            t.start();
            t = c.acceptNewAgent("sonography", new Treatment("sonography"));
            t.start();
            t = c.acceptNewAgent("colonoscopy", new Treatment("colonoscopy"));
            t.start();

            p = c.acceptNewAgent("paciente", new PatientAgent(new String[] {"fever"}, "paciente"));
            p.start();
            p = c.acceptNewAgent("paciente2", new PatientAgent(new String[] {"mulligrubs"}, "paciente2"));
            p.start();
            p = c.acceptNewAgent("paciente3", new PatientAgent(new String[] {"back pain"}, "paciente3"));
            p.start();
            p = c.acceptNewAgent("paciente4", new PatientAgent(new String[] {"muscles aches"}, "paciente4"));
            p.start();
            p = c.acceptNewAgent("paciente6", new PatientAgent(new String[] {"heart palpitations"}, "paciente6"));
            p.start();
            p = c.acceptNewAgent("paciente9", new PatientAgent(new String[] {"intestinal pain"}, "paciente9"));
            p.start();
            p = c.acceptNewAgent("paciente8", new PatientAgent(new String[] {"fever"}, "paciente8"));
            p.start();

        }
        catch( Exception e ){
            System.out.println(e.getCause());
        }

    }
}
