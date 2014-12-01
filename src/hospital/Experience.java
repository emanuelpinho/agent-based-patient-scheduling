package hospital;

/**
 * Created by Emanuelpinho on 01/12/14.
 */
public class Experience {

    private int i;

    private double exp;

    public Experience(){
        i=1;
        exp=0;
    }

    public void incrementExp(){
        i++;
        exp = (0.1*i + 1)/0.1*i;
    }
}
