import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.ConcurrentSkipListMap;

public class Main {
    /**
     * @param args
     */

    public static void main(String[] args){
        //UnorderedSmallDataTest();
        //OrderedSmallDataTest();
        ConcurrentUnorderedSmallDataTest();
    }

    private static void OrderedSmallDataTest(){
        Vector<MyData> dataVector = DataFactory.CreateOrderedSmallDataVector(10000);
        for(MyData data : dataVector){
            data.PrintData();
        }
        MyDeltaReceiver deltaReceiver = new MyDeltaReceiver();
        ParallelCalculator parallelCalculator = new ParallelCalculator();
        parallelCalculator.setThreadsNumber(8);
        parallelCalculator.setDeltaReceiver(deltaReceiver);
        
        for(MyData data : dataVector){
            parallelCalculator.addData(data);
        }
    }

    private static void UnorderedSmallDataTest(){
        Vector<MyData> dataVector = DataFactory.CreateOrderedSmallDataVector(100);
        for(MyData data : dataVector){
            data.PrintData();
        }
        Collections.shuffle(dataVector);
        MyDeltaReceiver deltaReceiver = new MyDeltaReceiver();
        ParallelCalculator parallelCalculator = new ParallelCalculator();
        parallelCalculator.setThreadsNumber(8);
        parallelCalculator.setDeltaReceiver(deltaReceiver);
        
        for(MyData data : dataVector){
            parallelCalculator.addData(data);
        }
    }

    private static void ConcurrentUnorderedSmallDataTest(){
        Vector<MyData> dataVector = DataFactory.CreateOrderedSmallDataVector(100);
        for(MyData data : dataVector){
            data.PrintData();
        }
        Collections.shuffle(dataVector);
        MyDeltaReceiver deltaReceiver = new MyDeltaReceiver();
        ParallelCalculator parallelCalculator = new ParallelCalculator();
        parallelCalculator.setThreadsNumber(4);
        parallelCalculator.setDeltaReceiver(deltaReceiver);
        
        dataVector.parallelStream().forEach((data) -> {
            parallelCalculator.addData(data);
        });
        try{
            Thread.sleep(3000);
        }
        catch(Exception e)
        {

        }

        /*
        for(MyData data : dataVector){
            System.out.println(data.numberOfReads);
        }
         */
    }
}
