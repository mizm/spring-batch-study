package com.ildong.batch.totalJob.partition;

import com.ildong.batch.model.Pay;
import com.ildong.batch.repository.PayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class PayIdRangePartitioner implements Partitioner {

    private final PayRepository payRepository;
    private final String requestDate;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Long max = payRepository.findMaxId(requestDate);
        Long min = payRepository.findMinId(requestDate);

        Long targetSize = (max - min) / gridSize + 1;
        log.info("max,min = {} {}", max,min);
        Map<String, ExecutionContext> result = new HashMap<>();

        long number = 0;
        long start = min;
        long end = start + targetSize - 1;

        while (start <= max) {
            ExecutionContext value = new ExecutionContext();
            result.put("partition" + number, value);

            if (end >= max) {
                end = max;
            }

            value.putLong("minId", start);
            value.putLong("maxId", end);
            log.info("partion min max = {} {}",start,end);
            start += targetSize;
            end += targetSize;
            number++;
        }

        return result;
    }
}
