package org.example.benchmark;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

import static java.lang.System.*;

public abstract class Benchmark {
    /*
        Interface for benchmarking classes.
        exemptFromBenchmark() is used to execute processes from time/size benchmarking
        execute() is used to execute processes and record time/size benchmarking
    */
    public abstract void exemptFromBenchmark() throws UnsupportedAudioFileException, IOException;

    public abstract void execute() throws UnsupportedAudioFileException, IOException;

    public abstract void collect() throws Exception;
    public static void runBenchmark(Benchmark benchmark, long iterations) throws Exception {
        benchmark.exemptFromBenchmark();
        out.println("---------------------");
        out.println("Benchmarking for "+benchmark.getClass().getName());
        out.println("Start Benchmark  "+ "  End Benchmark Time  " + "  Total Time (nanoSeconds)");
        for(int i=0;i<iterations;i++) {
            benchmark.execute();
        }
        benchmark.collect();
        out.println("---------------------");
    }
}
