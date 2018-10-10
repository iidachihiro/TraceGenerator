import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class Generator {
    private List<State> states;
    private String traceFilePath;
    private String traceFileName;
    private int traceSize;
    
    public Generator(List<State> _states, int _traceSize, String basePath, String processName) {
        this.states = _states;
        this.traceFileName = processName+"_Traces.txt";
        this.traceFilePath = basePath+"output/"+this.traceFileName;
        this.traceSize = _traceSize;
    }
    
    public void generate() {
        File file = new File(this.traceFilePath);
        int i = 0;
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            Transition firstTransition;
            String curStateName;
            while (true) {
                firstTransition = this.states.get(0).getTransitionAtRandom();
                curStateName = firstTransition.getPostState();
                if (!curStateName.equals("ERROR")) {
                    pw.println(firstTransition.getAction());
                    break;
                }
            }
            for (i = 0; i < this.traceSize; i++) {
                State curState = this.states.get(Integer.parseInt(curStateName.substring(1))); // "Q" is omitted from state name
                Transition transition = curState.getTransitionAtRandom();
                pw.println(transition.getAction());
                if ((curStateName = transition.getPostState()).equals("ERROR")) {
                    pw.println("ERROR");
                    break;
                }
            }
            pw.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
        if (i == this.traceSize) {
            System.out.println("Generated "+this.traceFileName+" file.");
        } else {
            System.out.println("Generated "+this.traceFileName+" file, but ERROR is caused in Traces.");
            System.out.println("Trace size is "+(i+2)+".");
        }
    }
    
    public void generate(ProbabilityConfigurationFile pcf) {
        List<Integer> points = pcf.getPoints();
        List<List<Double>> probabilitiesList = pcf.getProbabilitiesList();
        File file = new File(this.traceFilePath);
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            setProbabilities(probabilitiesList, 0);
            Transition firstTransition = this.states.get(0).getTransitionAtRandom();
            String curStateName = firstTransition.getPostState();
            pw.println(firstTransition.getAction());
            int index = 1;
            for (int i = 0; i < this.traceSize; i++) {
                if (index < points.size() && i == points.get(index)) {
                    setProbabilities(probabilitiesList, index++);
                }
                State curState = this.states.get(Integer.parseInt(curStateName.substring(1))); // "Q" is omitted from state name
                Transition transition = curState.getTransitionAtRandom();
                pw.println(transition.getAction());
                curStateName = transition.getPostState();
            }
            pw.close();
            System.out.println("Generated "+this.traceFileName+" file.");
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }
    
    private void setProbabilities(List<List<Double>> probabilitiesList, int i) {
        int j = 0;
        for (State state : this.states) {
            for (Transition transition : state.getTransitions()) {
                transition.setProbability(probabilitiesList.get(j++).get(i));
            }
        }
    }
}
