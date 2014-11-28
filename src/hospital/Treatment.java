package hospital;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Created by Emanuelpinho on 18/11/14.
 */
public class Treatment extends Agent {

    private boolean done;

    protected void setup()
    {
        System.out.println("Patient start");

        addBehaviour(new SimpleBehaviour() {
            @Override
            public void action() {

                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setContent("Ping");

                msg.addReceiver( new AID( "Common" , AID.ISLOCALNAME) );


                send(msg);

                done = true;

            }

            @Override
            public boolean done() {
                return done;
            }
        });
    }
}
