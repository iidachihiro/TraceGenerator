import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProbabilityConfigurationFile {
    private List<Integer> points;
    private List<List<Double>> probabilitiesList;
    
    private File pcFile;
    
    public ProbabilityConfigurationFile(String pcPath) {
        this.points = new ArrayList<>();
        this.probabilitiesList = new ArrayList<>();
        this.pcFile = new File(pcPath);
    }
    
    public List<Integer> getPoints() {
        return points;
    }
    
    public List<List<Double>> getProbabilitiesList() {
        return probabilitiesList;
    }
    
    public void read() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(pcFile));
            String line = br.readLine();
            String[] strs = line.split(",");
            for (int i = 3; i < strs.length; i++) {
                points.add(Integer.valueOf(strs[i]));
            }
            while ((line = br.readLine()) != null) {
                strs = line.split(",");
                List<Double> probabilities = new ArrayList<>();
                for (int i = 3; i < strs.length; i++) {
                    probabilities.add(Double.valueOf(strs[i]));
                }
                probabilitiesList.add(probabilities);
            }
            br.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }
}
