import java.util.ArrayList;
import java.util.List;

public class MyDeltaReceiver implements DeltaReceiver {

    private ArrayList<Delta> deltas = new ArrayList<Delta>();

    @Override
    public void accept(List<Delta> deltasAccepted) {
        this.deltas.addAll(deltasAccepted);
        for (Delta delta : deltasAccepted) {
            System.out.println("Dataset " + delta.getDataID() + " is different to " + delta.getDataID()+1 + " at index: " + delta.getIdx() + " by " +delta.getDelta()); 
        }
    }

    public void printDeltas(){
        for (Delta delta : deltas) {
            System.out.println("Dataset " + delta.getDataID() + " is different to " + delta.getDataID()+1 + " at index: " + delta.getIdx() + " by " +delta.getDelta()); 
        }
    }
}
