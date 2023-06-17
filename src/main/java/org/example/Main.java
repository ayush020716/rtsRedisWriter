package org.example;

import org.example.benchmark.Benchmark;
import org.example.benchmark.PcmToUlaw;

public class Main {
    public static void main(String[] args) throws Exception {
        Benchmark.runBenchmark(new PcmToUlaw(), 20);
    }
}