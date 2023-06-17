package org.example.benchmark;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public abstract class Benchmark {
    /*
        Interface for benchmarking classes.
        exemptFromBenchmark() is used to execute processes from time/size benchmarking
        execute() is used to execute processes and record time/size benchmarking
    */
    abstract public void exemptFromBenchmark() throws UnsupportedAudioFileException, IOException;
    abstract public void execute() throws Exception;
    abstract public void collect() throws Exception;
    public static void runBenchmark(Benchmark benchmark, long iterations) throws Exception {
        benchmark.exemptFromBenchmark();
        System.out.println("---------------------");
        System.out.println("Benchmarking for "+benchmark.getClass().getName());
        System.out.println("Start Benchmark  "+ "  End Benchmark Time  " + "  Total Time (nanoSeconds)");
        for(int i=0;i<iterations;i++) {
            benchmark.execute();
        }
        benchmark.collect();
        System.out.println("---------------------");
    }
}
