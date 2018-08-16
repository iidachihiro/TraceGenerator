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
        transitions.add(new Transition(action, postState));
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
}
