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

        try{
            AgentContainer c1 = getContainerController();
            AgentController d1;
            d1 = c1.acceptNewAgent("doctor 1", new Doctor("doctor"));
            d1.start();


            AgentContainer c2 = getContainerController();
            AgentController d2;
            d2 = c2.acceptNewAgent("doctor2", new Doctor("doctor2"));
            d2.start();

            AgentContainer c3 = getContainerController();
            AgentController d3;
            d3 = c3.acceptNewAgent("doctor3", new Doctor("doctor3"));
            d3.start();



            AgentContainer ct1 = getContainerController();
            AgentController t1;
            t1 = ct1.acceptNewAgent("analysis", new Treatment("analysis"));
            t1.start();


            AgentContainer ct2 = getContainerController();
            AgentController t2;
            t2 = ct2.acceptNewAgent("endoscopy", new Treatment("endoscopy"));
            t2.start();

            AgentContainer ct3 = getContainerController();
            AgentController t3;
            t3 = ct3.acceptNewAgent("resonance", new Treatment("resonance"));
            t3.start();

            AgentContainer ct4 = getContainerController();
            AgentController t4;
            t4 = ct4.acceptNewAgent("electrocardiogram", new Treatment("electrocardiogram"));
            t4.start();

            AgentContainer ct5 = getContainerController();
            AgentController t5;
            t5 = ct5.acceptNewAgent("sonography", new Treatment("sonography"));
            t5.start();

            AgentContainer ct6 = getContainerController();
            AgentController t6;
            t6 = ct6.acceptNewAgent("colonoscopy", new Treatment("colonoscopy"));
            t6.start();



            AgentContainer cp1 = getContainerController();
            AgentController p1;
            p1 = cp1.acceptNewAgent("paciente", new PatientAgent(new String[] {"fever"}, "paciente"));
            p1.start();


            AgentContainer cp2 = getContainerController();
            AgentController p2;
            p2 = cp2.acceptNewAgent("paciente2", new PatientAgent(new String[] {"mulligrubs"}, "paciente2"));
            p2.start();

            AgentContainer cp3 = getContainerController();
            AgentController p3;
            p3 = cp3.acceptNewAgent("paciente3", new PatientAgent(new String[] {"back pain"}, "paciente3"));
            p3.start();

            AgentContainer cp4 = getContainerController();
            AgentController p4;
            p4 = cp4.acceptNewAgent("paciente4", new PatientAgent(new String[] {"muscles aches"}, "paciente4"));
            p4.start();

            AgentContainer cp5 = getContainerController();
            AgentController p5;
            p5 = cp5.acceptNewAgent("paciente6", new PatientAgent(new String[] {"heart palpitations"}, "paciente6"));
            p5.start();

            AgentContainer cp6 = getContainerController();
            AgentController p6;
            p6 = cp6.acceptNewAgent("paciente9", new PatientAgent(new String[] {"intestinal pain"}, "paciente9"));
            p6.start();

        }
        catch( Exception e ){
            System.out.println(e.getCause());
        }

    }
}
