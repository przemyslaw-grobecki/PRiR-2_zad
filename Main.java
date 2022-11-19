import java.util.Vector;

public class Main {
    /**
     * @param args
     */

    public static void main(String[] args){
        Vector<MyData> dataVector = DataFactory.CreateSmallDataVector(20);
        for(MyData data : dataVector){
            data.PrintData();
        }
        MyDeltaReceiver deltaReceiver = new MyDeltaReceiver();
        ParallelCalculator parallelCalculator = new ParallelCalculator();
        parallelCalculator.setThreadsNumber(4);
        parallelCalculator.setDeltaReceiver(deltaReceiver);
        
        for(MyData data : dataVector){
            parallelCalculator.addData(data);
        }

        deltaReceiver.printDeltas();
    }
}
