import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static String basePath;
    private static String configFileName = "generation.config";
    
    private static String transitionFileName;
    private static String probFileName;
    private static int traceSize; // actual size is +1
    private static boolean setting;
    
    static List<State> states = new ArrayList<>();
    
    public static void main(String[] args) {
        setConfig();
        
        readTransitionFile();
        
        if (setting) {
            readProbabilityConfigurationFile();
        }

        Generator generator = new Generator(states, traceSize, basePath);
        generator.generate();
    }
    
    private static void setConfig() {
        basePath = System.getProperty("user.dir");
        basePath = basePath.substring(0, basePath.length()-3); // "bin" is omitted.
        File file = new File(basePath+"resources/"+configFileName);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("Transition File Name")) 
                    transitionFileName = basePath+"resources/"+removeSpace(br.readLine());
                else if (line.contains("Probability File Name"))
                    probFileName = basePath+"resources/"+removeSpace(br.readLine());
                else if (line.contains("Trace Size"))
                    traceSize = Integer.parseInt(removeSpace(br.readLine()));
                else if (line.contains("Probability Setting"))
                    setting = Boolean.parseBoolean(removeSpace(br.readLine()));
            }
            br.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
        System.out.println("Read config file.");
        printConfig();
    }

    public static void readProbabilityConfigurationFile() {
        File file = new File(probFileName);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                removeSpace(line);
                if (line.length() == 0) {
                    continue;
                }
                String[] str = line.split(",", 0);
                String preStateName = str[0];
                String action = str[1];
                String postStateName = str[2];
                Double probability = Double.parseDouble(str[3]);
                State state = states.get(Integer.parseInt(preStateName.substring(1)));
                for (Transition transition : state.getTransitions()) {
                    if (transition.isSameActionAndPostState(new Transition(action, postStateName))) {
                        transition.setProbability(probability);
                    }
                }
            }
            br.close();
            for (State state : states) {
                state.normalizeProbabilities();
            }
        } catch (IOException e) {
            System.err.println(e.toString());
        }
        System.out.println("Read probability file.");
    }
    
    public static void readTransitionFile() {
        File file = new File(transitionFileName);
        String processName = null;
        int stateNum = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null && !line.equals("Process:")) {}
            processName = br.readLine();
            while ((line = br.readLine()) != null && !line.equals("States:")) {}
            stateNum = Integer.parseInt(br.readLine());
            while ((line = br.readLine()) != null && !line.equals("Transitions:")) {}
            br.readLine(); // [processName = Q0,] is omitted.
            
            while ((line = br.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }
                line = removeSpace(line);
                String stateName = getStateName(line);
                State state = new State(stateName);
                String action = getActionName(line);
                String postState = getPostStateName(line);
                state.addTransition(action, postState);
                while (!line.endsWith(".") && !line.endsWith(",")) {
                    line = removeSpace(br.readLine());
                    action = getActionName(line);
                    postState = getPostStateName(line);
                    state.addTransition(action, postState);
                }
                states.add(state);
            }
            br.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
        System.out.println("Read transition file.");
        printPartition();
        System.out.println("processName: "+processName);
        System.out.println("stateNum: "+stateNum);
        printPartition();
    }
    
    private static String removeSpace(String line) {
        line = line.replaceAll(" ", "");
        line = line.replaceAll("\t", "");
        return line;
    }
    
    private static String getStateName(String line) {
        String state = "";
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '=') {
                break;
            }
            state += c;
        }
        return state;
    }
    
    private static String getActionName(String line) {
        String action = "";
        boolean flag = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '-' && line.charAt(i+1) == '>') {
                break;
            }
            if (flag) {
                action += c;
            }
            if ((c == '(' && line.charAt(i-1) == '=') || c == '|') {
                flag = true;
            }
        }
        return action;
    }
    
    private static String getPostStateName(String line) {
        String action = "";
        boolean flag = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == ')' || i == line.length()) {
                break;
            }
            if (flag) {
                action += c;
            }
            if (c == '>' && line.charAt(i-1) == '-') {
                flag = true;
            }
        }
        return action;
    }
    
    private static void printPartition() {
        for (int i = 0; i < 80; i++)
            System.out.print("*");
        System.out.println("");
    }
    
    private static void printConfig() {
        printPartition();
        System.out.println("Transition File Name = "+transitionFileName);
        System.out.println("Probability File Name = "+probFileName);
        System.out.println("Trace Size = "+traceSize);
        System.out.println("Probability Setting = "+setting);
        printPartition();
    }
}
