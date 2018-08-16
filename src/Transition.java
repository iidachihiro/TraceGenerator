public class Transition {
    private String action;
    private String postState;
    private double probability;
    
    public Transition(String _action, String _postState) {
        this.action = _action;
        this.postState = _postState;
        this.probability = 1.0;
    }
    
    public Transition(String _action, String _postState, double _probability) {
        this.action = _action;
        this.postState = _postState;
        this.probability = _probability;
    }
    
    public void setProbability(double _probability) {
        this.probability = _probability;
    }
    
    public String getAction() {
        return this.action;
    }
    
    public String getPostState() {
        return this.postState;
    }
    
    public double getProbability() {
        return this.probability;
    }
    
    public boolean isSameActionAndPostState(Transition transition) {
        if (this.action.equals(transition.getAction())
                && this.postState.equals(transition.getPostState())) {
            return true;
        }
        return false;
    }
}
