import java.util.ArrayList;
import java.util.List;

public class State {
    private String name;
    private List<Transition> transitions;
    
    public State(String _name) {
        this.name = _name;
        this.transitions = new ArrayList<>();
    }
    
    public void addTransition(String action, String postState) {
        if (action.contains("[") && action.contains("..") && action.contains("]")) {
            List<String> actions = getActionsFromSequence(action);
            for (String _action : actions) {
                transitions.add(new Transition(_action, postState));
            }
        } else {
            transitions.add(new Transition(action, postState));
        }
        if (transitions.size() >= 2) {
            setProbability();
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public List<Transition> getTransitions() {
        return this.transitions;
    }
    
    public Transition getTransitionAtRandom() {
        double rd = Math.random();
        for (Transition transition : this.transitions) {
            if (rd <= transition.getProbability()) {
                return transition;
            }
            rd -= transition.getProbability();
        }
        return this.transitions.get(transitions.size()-1);
    }
    
    public void normalizeProbabilities() {
        double sum = 0;
        for (Transition transition : this.transitions) {
            sum += transition.getProbability();
        }
        for (Transition transition : this.transitions) {
            double cur = transition.getProbability();
            transition.setProbability(cur/sum);
        }
    }
    
    private void setProbability() {
        int num = transitions.size();
        double newProb = 1.0 / num;
        for (Transition transition : this.transitions) {
            transition.setProbability(newProb);
        }
    }
    
    private List<String> getActionsFromSequence(String action) {
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < action.length(); i++) {
            char c = action.charAt(i);
            if (c == '[') {
                positions.add(i);
            }
        }
        positions.add(action.length());
        String baseAction = action.substring(0, positions.get(0));
        List<String> subs = new ArrayList<>();
        for (int i = 0; i < positions.size()-1; i++) {
            subs.add(action.substring(positions.get(i), positions.get(i+1)));
        }
        List<String> result = new ArrayList<>();
        result = recursive("", result, subs, 0);
        for (int i = 0; i < result.size(); i++) {
            result.set(i, baseAction+result.get(i));
        }
        return result;
    }
    
    private List<String> recursive(String tmp, List<String> result, List<String> subs, int i) {
        if (i == subs.size()) {
            result.add(tmp);
            return result;
        }
        String contents = subs.get(i);
        contents = contents.substring(1, contents.length()-1);
        if (contents.contains("..")) {
            int sequencePosition = contents.indexOf("..");
            int left = Integer.valueOf(contents.substring(0, sequencePosition));
            int right = Integer.valueOf(contents.substring(sequencePosition+2));
            for (int j = left; j < right; j++) {
                result = recursive(tmp+"["+j+"]", result, subs, i+1);
            }
        } else {
            result = recursive(tmp+"["+contents+"]", result, subs, i+1);
        }
        return result;
    }
}
