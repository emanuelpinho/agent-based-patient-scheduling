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

        String[] symptoms = new String[] {"fever","mulligrubs", "back pain", "heart palpitations",
                "muscles aches", "intestinal pain"};


        PatientAgent patient = new PatientAgent(symptoms, "teste");
        Doctor d = new Doctor("doctor");
        Treatment t = new Treatment("analysis");

        AgentContainer c = getContainerController();

        try{
            AgentController a = c.acceptNewAgent("teste", patient);
            a.start();
            AgentController b = c.acceptNewAgent("testeDoctor", d);
            b.start();
            AgentController ta = c.acceptNewAgent("testetreatment", t);
            ta.start();
        }
        catch( Exception e ){
            System.out.println(e.getCause());
        }

    }
}
