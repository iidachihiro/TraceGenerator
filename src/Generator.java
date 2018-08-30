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
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            String curStateName = this.states.get(0).getTransitions().get(0).getPostState();
            for (int i = 0; i <= this.traceSize; i++) {
                State curState = this.states.get(Integer.parseInt(curStateName.substring(1))); // "Q" is omitted from state name
                Transition transition = curState.getTransitionAtRandom();
                pw.println(transition.getAction());
                curStateName = transition.getPostState();
            }
            pw.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
        System.out.println("Generated "+this.traceFileName+" file.");
    }
}
