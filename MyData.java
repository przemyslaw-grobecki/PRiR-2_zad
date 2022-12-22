import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class MyData implements Data, Cloneable {
    private int dataId;
    private int[] data;
    private int maxValue;
    private Random random = new Random();
    private int[] counter;

    public MyData(int dataId, int size, int maxValue){
        this.dataId = dataId;
        this.data = new int[size];
        this.maxValue = maxValue;
        for(int i = 0; i < size; i ++){
            this.data[i] = random.nextInt(maxValue+1);
        }
        this.counter = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            this.counter[i] = 0;
        }
    }

    public MyData(MyData leftNeighbour){
        this.dataId = leftNeighbour.getDataId() + 1;
        this.data = new int[leftNeighbour.getSize()];
        this.maxValue = leftNeighbour.getMaxValue();
        for( int i = 0; i < leftNeighbour.getSize(); i ++){
            this.data[i] = leftNeighbour.getValue(i);
        }
        this.counter = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            this.counter[i] = 0;
        }
    }

    public int[] ChangeData(int numberOfChanges) {
        int[] indicesChanged = new int[numberOfChanges];
        for (int i = 0; i < numberOfChanges; i++){
            int indexChanged = getRandomWithExclusion(0, this.data.length - 1, indicesChanged);
            int indexChangedValue = getRandomWithExclusion(0, this.maxValue, this.data[i]);
            this.data[indexChanged] = indexChangedValue;
            indicesChanged[i] = indexChanged;
        }
        return indicesChanged;
    }

    public void PrintData(){
        for (int i : data) {
            System.out.print("[" + i + "]\t");
        }
        System.out.print("\n");
    }

    @Override
    public int getDataId() {
        return this.dataId;
    }

    @Override
    public int getSize() {
        return data.length;
    }

    @Override
    public int getValue(int idx) {
        counter[idx]++;
        if (counter[idx] > 3) {
            System.out.println("More than two usages for dataId=" + dataId + " idx: " + (counter[idx]-1));
        }
        return data[idx];
    }

    public int getMaxValue() {
        return this.maxValue;
    }
    public Object clone(){  
        try{  
            return super.clone();  
        }catch(Exception e){ 
            return null; 
        }
    }

    private int getRandomWithExclusion(int start, int end, int... exclude) {
        int randomNumber = start + random.nextInt(end - start + 1 - exclude.length);
        for (int ex : exclude) {
            if (randomNumber < ex) {
                break;
            }
            randomNumber++;
        }
        return randomNumber;
    }
}
