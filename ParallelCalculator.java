import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;
import java.util.List;
/* ඞඞඞ <-  */

public class ParallelCalculator implements DeltaParallelCalculator {

    private interface PrioritizedRunnable extends Runnable {
        public int getPriority();
    }

    private class FutureWorker extends FutureTask<FutureWorker>
        implements Comparable<FutureWorker> {

        private PrioritizedRunnable runnable; 

        public FutureWorker(PrioritizedRunnable runnable){
            super(runnable, null);
            this.runnable = runnable;
        }

        @Override
        public int compareTo(ParallelCalculator.FutureWorker o) {
            return runnable.getPriority() - o.runnable.getPriority();
        }
    }

    private class ComparingWorker implements PrioritizedRunnable {
        private Data dataLeft;
        private Data dataRight;
        private int thread;
        private Vector<Delta> result;
        private int priority;

        public ComparingWorker(Data dataLeft, Data dataRight, int thread){
            this.dataLeft = dataLeft;
            this.dataRight = dataRight;
            this.thread = thread;
            this.result = new Vector<Delta>();
            this.priority = dataLeft.getDataId();
        }

        @Override
        public void run() {
            for ( int i = thread; i < dataSize; i+= threadCount) {
                if(dataLeft.getValue(i) != dataRight.getValue(i)){
                    result.add(new Delta(dataLeft.getDataId(), i, dataLeft.getValue(i) - dataRight.getValue(i)));
                }
            }
            threadPoolExecutor.execute(new FutureWorker(new DataDelivereryWorker(result, dataLeft.getDataId())));
        }

        @Override
        public int getPriority() {
            return this.priority;
        }    
    }

    private class DataDelivereryWorker implements PrioritizedRunnable {
        private List<Delta> deltas;
        public int dataId;
        public DataDelivereryWorker(List<Delta> deltas, int dataId){
            this.deltas = deltas;
            this.dataId = dataId;
        }

        @Override
        public void run() {
            while(dataId != currentResultCounter/threadCount){
                Thread.yield();
            }
            deltas.sort((Delta o1, Delta o2) -> o1.getIdx() - o2.getIdx());
            deltaReceiver.accept(deltas);
            mutex.lock();
            ++currentResultCounter;
            mutex.unlock();
        }

        @Override
        public int getPriority() {
            return this.dataId; 
        }
    }

    private class DataWrapper implements Data, Cloneable {
        private Data data;
        private boolean isProcessedLeft = false;
        public boolean isProcessedLeft() {
            return isProcessedLeft;
        }

        public void setProcessedLeft(boolean isProcessedLeft) {
            this.isProcessedLeft = isProcessedLeft;
        }

        private boolean isProcessedRight = false;
        public boolean isProcessedRight() {
            return isProcessedRight;
        }

        public void setProcessedRight(boolean isProcessedRight) {
            this.isProcessedRight = isProcessedRight;
        }

        public DataWrapper(Data data){
            this.data = data;
        }

        @Override
        public int getDataId() {
            return data.getDataId();
        }

        @Override
        public int getSize() {
            return data.getSize();
        }

        @Override
        public int getValue(int idx) {
            return data.getValue(idx);
        }
        public Object clone(){  
            try{  
                return super.clone();  
            }catch(Exception e){ 
                return null; 
            }
        }
    }

    private void ScheduleIfPairFound() {
        //Data is already sorted
        for(int i = 0; i < dataVector.size(); i++){
            if ( i < dataVector.size() - 1 ){
                if((dataVector.get(i).getDataId()) == (dataVector.get(i + 1).getDataId()-1)){
                    if (!dataVector.get(i).isProcessedRight && 
                    !dataVector.get(i+1).isProcessedLeft) {
                        dataVector.get(i).setProcessedRight(true);
                        dataVector.get(i+1).setProcessedLeft(true);
                        for(int thread = 0 ; thread < threadCount; thread++){
                            threadPoolExecutor.execute(
                                new FutureWorker(
                                    new ComparingWorker(
                                        (DataWrapper) dataVector.get(i).clone(), (DataWrapper) dataVector.get(i+1).clone(), thread)
                                    )
                            );
                        }
                    }
                }
            }
        }
    }

    private ReentrantLock mutex = new ReentrantLock();
    private int currentResultCounter = 0;
    private int threadCount;
    private Vector<DataWrapper> dataVector = new Vector<DataWrapper>();
    private PriorityBlockingQueue taskQueue;
    private ThreadPoolExecutor threadPoolExecutor;
    private DeltaReceiver deltaReceiver;
    private Integer dataSize;

    @Override
    public void setThreadsNumber(int threads) {
        this.threadCount = threads;
        this.taskQueue = new PriorityBlockingQueue(100);
        this.threadPoolExecutor = new ThreadPoolExecutor(threads, threads, 10L, TimeUnit.MILLISECONDS, this.taskQueue);
    }

    @Override
    public void setDeltaReceiver(DeltaReceiver receiver) {
        this.deltaReceiver = receiver;
    }

    @Override
    public synchronized void addData(Data data) {
        if(dataSize == null){
            this.dataSize = data.getSize(); //All data will have the same size
        }
        dataVector.add(new DataWrapper(data));
        Collections.sort(dataVector, (Data o1, Data o2) -> o1.getDataId() - o2.getDataId());
        ScheduleIfPairFound();
    }
}