package listeners;

import com.google.common.math.Quantiles;
import com.google.common.math.Stats;

import java.util.*;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public class TimeSync {
    private Map<Long, Double> latencyMap;

    private int MAX_COLLECT = 10;

    // TODO: Algorithm described in https://gamedev.stackexchange.com/a/93662
    public TimeSync() {
        latencyMap = new LinkedHashMap<>(MAX_COLLECT);
    }

    public synchronized void addLatencyValue(long correctedTime, double latency) {
        if (latencyMap.size() > MAX_COLLECT) {
            latencyMap.remove(latencyMap.entrySet().iterator().next().getKey());
        }

        latencyMap.put(correctedTime, latency);
    }

    // TODO: This is wildly inefficient, but this is a temp fix
    public double calcApproxLatency() {
        /**
        long median = (long)Quantiles.median().compute(latencyMap.values());

        // key/value entries by the latency
        Stream<Map.Entry<Long, Double>> sorted =
                latencyMap.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue());

        DoubleStream sortedLatencies = DoubleStream.of(latencyMap.values().<Double>toArray());

        long[] mad = new long[latencyLst.size()];
        for (int i = 0; i < latencyLst.size(); i++) {
            mad[i] = Math.abs(latencyLst.get(i) - median);
        }



        // calculate Median absolute deviation
        long MAD_value = (long)Quantiles.median().compute(mad);

        return Stats.meanOf(Arrays.stream(mad)
                .filter(n -> Math.abs(n - median) <= MAD_value).toArray());
         **/

        return 0;
    }

    public static long calcCorrectTime() {
        // TODO: Stub
        return 0;
    }

}
