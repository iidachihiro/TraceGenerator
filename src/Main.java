import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static String basePath;
    private static String resourcesPath;
    private static String configFileName = "generator.config";
    
    private static String transitionFileName;
    private static String probFileName;
    private static int traceSize; // actual size is +1
    private static boolean setting;
    
    private static String processName;
    
    static List<State> states = new ArrayList<>();
    
    private static String tab = "  ";
    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please input the mode.");
            printPartition();
            System.out.println("[prepare]"+tab+"prepare the transition probability file.");
            System.out.println("[generate]"+tab+"generate the trace.");
            printPartition();
            return;
        }
        if (args[0].equals("generate")) {
            setConfig();
            
            readTransitionFile();
            
            if (setting) {
                readProbabilityConfigurationFile();
            }

            Generator generator = new Generator(states, traceSize, basePath, processName);
            generator.generate();
        } else if (args[0].equals("prepare")) {
            setConfig();
            
            readTransitionFile();
            
            generateProbabilityCSV();
        } else if (args[0].equals("generateEX1")) {
            setConfig();
            readTransitionFile();
            ProbabilityConfigurationFile pcf = new ProbabilityConfigurationFile(probFileName);
            pcf.read();
            Generator generator = new Generator(states, traceSize, basePath, processName);
            generator.generate(pcf);
        }
    }
    
    private static void setConfig() {
        basePath = System.getProperty("user.dir")+"/";
        resourcesPath = basePath+"resources/";
        File file = new File(resourcesPath+configFileName);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("Transition File Name")) 
                    transitionFileName = resourcesPath+removeSpace(br.readLine());
                else if (line.contains("Probability File Name"))
                    probFileName = resourcesPath+removeSpace(br.readLine());
                else if (line.contains("Trace Size"))
                    traceSize = Integer.parseInt(removeSpace(br.readLine()));
                else if (line.contains("Probability Setting"))
                    setting = Boolean.parseBoolean(removeSpace(br.readLine()));
            }
            br.close();
            System.out.println("Read config file.");
            printConfig();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    private static void readProbabilityConfigurationFile() {
        File file = new File(probFileName);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                removeSpace(line);
                if (line.length() == 0) {
                    continue;
                }
                String[] strs = line.split(",", 0);
                String preStateName = strs[0];
                String action = strs[1];
                String postStateName = strs[2];
                Double probability = Double.parseDouble(strs[3]);
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
            System.out.println("Read probability file.");
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }
    
    public static void readTransitionFile() {
        File file = new File(transitionFileName);
        int stateNum = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = removeSpace(br.readLine())) != null && !line.equals("Process:")) {}
            processName = removeSpace(br.readLine());
            while ((line = removeSpace(br.readLine())) != null && !line.equals("States:")) {}
            stateNum = Integer.parseInt(removeSpace(br.readLine()));
            while ((line = removeSpace(br.readLine())) != null && !line.equals("Transitions:")) {}
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
            System.out.println("Read transition file.");
            printPartition();
            System.out.println("processName: "+processName);    
            System.out.println("stateNum: "+stateNum);
            printPartition();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
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
    
    private static void generateProbabilityCSV() {
        try {
            File file = new File(probFileName);
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            for (State state : states) {
                for (Transition transition : state.getTransitions()) {
                    pw.println(state.getName()+","+transition.getAction()+","+transition.getPostState()+","+transition.getProbability());
                }
            }
            pw.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
        System.out.println("Generate "+probFileName+".");
        System.out.println("Please set probability of each transition.");
    }
}
