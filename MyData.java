import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class MyData implements Data, Cloneable {
    private int dataId;
    private int[] data;
    private int maxValue;
    private Random random = new Random();
    public int numberOfReads = 0;

    public MyData(int dataId, int size, int maxValue){
        this.dataId = dataId;
        this.data = new int[size];
        this.maxValue = maxValue;
        for(int i = 0; i < size; i ++){
            this.data[i] = random.nextInt(maxValue+1);
        }
    }

    public MyData(MyData leftNeighbour){
        this.dataId = leftNeighbour.getDataId() + 1;
        this.data = new int[leftNeighbour.getSize()];
        this.maxValue = leftNeighbour.getMaxValue();
        for( int i = 0; i < leftNeighbour.getSize(); i ++){
            this.data[i] = leftNeighbour.getValue(i);
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
    public synchronized int getValue(int idx) {
        numberOfReads++;
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
