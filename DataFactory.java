import java.util.Vector;

public class DataFactory {
    public static Vector<MyData> CreateSmallDataVector(int size){
        Vector<MyData> dataVector = new Vector<MyData>();
        MyData tempData = new MyData(0, 10, 250); 
        dataVector.add((MyData) tempData.clone());
        for (int i = 0; i < size - 1; i++) {
            tempData = new MyData(tempData);
            if(i%3 == 0){
                tempData.ChangeData(5);
            }
            dataVector.add((MyData) tempData.clone());
        }
        return dataVector;
    }
}
