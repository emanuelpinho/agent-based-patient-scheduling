package symptons;

/**
 * Created by Emanuelpinho on 19/11/14.
 */
public class Symptom {

    private String name;

    private double decreseRate;

    private double health;

    private String exam;

    public Symptom(String name){
        this.name = name;
        if (name.compareTo("fever") == 0) {
            this.decreseRate = 0.6;
            this.health =  0.25;
            exam = "analysis";
        }
        else if (name.compareTo("mulligrubs") == 0){
            this.decreseRate = 0.3;
            this.health =  0.1;
            exam = "endoscopy";
        }
        else if (name.compareTo("back") == 0){
            this.decreseRate = 0.3;
            this.health =  0.15;
            exam = "resonance";
        }
        else if (name.compareTo("palpitations") == 0){
            this.decreseRate = 0.6;
            this.health =  0.35;
            exam = "electrocardiogram";
        }
        else if (name.compareTo("muscle") == 0){
            this.decreseRate = 0.2;
            this.health =  0.05;
            exam = "sonography";
        }
        else if (name.compareTo("intestinal") == 0){
            this.decreseRate = 0.4;
            this.health =  0.2;
            exam = "colonoscopy";
        }

    }

    public String getExam() {
        return exam;
    }

    public double getDecreseRate() {
        return decreseRate;
    }

    public double getHealth() {
        return health;
    }
}
