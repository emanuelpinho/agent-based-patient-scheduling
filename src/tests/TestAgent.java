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

import java.util.Scanner;


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

        Scanner scan = new Scanner(System.in);

        System.out.println("Qual a opção de testes que pretende correr? (1, 2 ou 3)");
        String option = scan.nextLine();

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



            if(option.compareTo("1")==0) {

                AgentContainer cp1 = getContainerController();
                AgentController p1;
                p1 = cp1.acceptNewAgent("paciente", new PatientAgent(new String[]{"fever", "mulligrubs"}, "paciente"));
                p1.start();


                AgentContainer cp2 = getContainerController();
                AgentController p2;
                p2 = cp2.acceptNewAgent("paciente2", new PatientAgent(new String[]{"mulligrubs"}, "paciente2"));
                p2.start();

                AgentContainer cp3 = getContainerController();
                AgentController p3;
                p3 = cp3.acceptNewAgent("paciente3", new PatientAgent(new String[]{"back pain"}, "paciente3"));
                p3.start();

                AgentContainer cp4 = getContainerController();
                AgentController p4;
                p4 = cp4.acceptNewAgent("paciente4", new PatientAgent(new String[]{"muscles aches"}, "paciente4"));
                p4.start();

                AgentContainer cp5 = getContainerController();
                AgentController p5;
                p5 = cp5.acceptNewAgent("paciente6", new PatientAgent(new String[]{"heart palpitations"}, "paciente6"));
                p5.start();

                AgentContainer cp6 = getContainerController();
                AgentController p6;
                p6 = cp6.acceptNewAgent("paciente9", new PatientAgent(new String[]{"intestinal pain"}, "paciente9"));
                p6.start();
            }
            else if(option.compareTo("2")==0) {

                AgentContainer cp1 = getContainerController();
                AgentController p1;
                p1 = cp1.acceptNewAgent("paciente", new PatientAgent(new String[] {"fever", "mulligrubs", "back pain"}, "paciente"));
                p1.start();


                AgentContainer cp2 = getContainerController();
                AgentController p2;
                p2 = cp2.acceptNewAgent("paciente2", new PatientAgent(new String[] {"mulligrubs", "heart palpitations", "intestinal pain"}, "paciente2"));
                p2.start();

                AgentContainer cp3 = getContainerController();
                AgentController p3;
                p3 = cp3.acceptNewAgent("paciente3", new PatientAgent(new String[] {"back pain", "fever", "intestinal pain"}, "paciente3"));
                p3.start();

                AgentContainer cp4 = getContainerController();
                AgentController p4;
                p4 = cp4.acceptNewAgent("paciente4", new PatientAgent(new String[] {"muscles aches", "back pain"}, "paciente4"));
                p4.start();

                AgentContainer cp5 = getContainerController();
                AgentController p5;
                p5 = cp5.acceptNewAgent("paciente6", new PatientAgent(new String[] {"heart palpitations", "fever"}, "paciente6"));
                p5.start();

                AgentContainer cp6 = getContainerController();
                AgentController p6;
                p6 = cp6.acceptNewAgent("paciente9", new PatientAgent(new String[] {"intestinal pain", "mulligrubs"}, "paciente9"));
                p6.start();

            }
            else{

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
                p3 = cp3.acceptNewAgent("paciente3", new PatientAgent(new String[] {"back pain", "fever"}, "paciente3"));
                p3.start();

                AgentContainer cp4 = getContainerController();
                AgentController p4;
                p4 = cp4.acceptNewAgent("paciente4", new PatientAgent(new String[] {"back pain"}, "paciente4"));
                p4.start();

                AgentContainer cp5 = getContainerController();
                AgentController p5;
                p5 = cp5.acceptNewAgent("paciente6", new PatientAgent(new String[] {"heart palpitations"}, "paciente6"));
                p5.start();

                AgentContainer cp6 = getContainerController();
                AgentController p6;
                p6 = cp6.acceptNewAgent("paciente9", new PatientAgent(new String[] {"fever"}, "paciente9"));
                p6.start();

                AgentContainer cp8 = getContainerController();
                AgentController p8;
                p8 = cp8.acceptNewAgent("paciente11", new PatientAgent(new String[] {"intestinal pain", "heart palpitations"}, "paciente11"));
                p8.start();

                AgentContainer cp9 = getContainerController();
                AgentController p9;
                p9 = cp9.acceptNewAgent("paciente12", new PatientAgent(new String[] {"mulligrubs"}, "paciente12"));
                p9.start();

                AgentContainer cp10 = getContainerController();
                AgentController p10;
                p10 = cp10.acceptNewAgent("paciente13", new PatientAgent(new String[] {"mulligrubs", "back pain"}, "paciente13"));
                p10.start();

                AgentContainer cp11 = getContainerController();
                AgentController p11;
                p11 = cp11.acceptNewAgent("paciente14", new PatientAgent(new String[] {"fever", "back pain"}, "paciente14"));
                p11.start();

                AgentContainer cp12 = getContainerController();
                AgentController p12;
                p12 = cp12.acceptNewAgent("paciente15", new PatientAgent(new String[] {"fever"}, "paciente15"));
                p12.start();

                AgentContainer cp13 = getContainerController();
                AgentController p13;
                p13 = cp13.acceptNewAgent("paciente16", new PatientAgent(new String[] {"heart palpitations", "back pain"}, "paciente16"));
                p13.start();

                AgentContainer cp14 = getContainerController();
                AgentController p14;
                p14 = cp14.acceptNewAgent("paciente17", new PatientAgent(new String[] {"intestinal pain", "fever"}, "paciente17"));
                p14.start();

                AgentContainer cp15 = getContainerController();
                AgentController p15;
                p15 = cp15.acceptNewAgent("paciente18", new PatientAgent(new String[] {"intestinal pain", "mulligrubs"}, "paciente18"));
                p15.start();

                AgentContainer cp16 = getContainerController();
                AgentController p16;
                p16 = cp16.acceptNewAgent("paciente19", new PatientAgent(new String[] {"back pain"}, "paciente19"));
                p16.start();
            }


        }
        catch( Exception e ){
            System.out.println(e.getCause());
        }

    }
}
