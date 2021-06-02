package com.ildong.batch.totalJob.partition;

import com.ildong.batch.repository.PayRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.ExecutionContext;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class PayIdRangePartitionerTest {

    private static PayIdRangePartitioner partitioner;

    @Mock
    private PayRepository payRepository;

    @Test
    void gridSize확인() {
        //given
        Mockito.lenient()
                .when(payRepository.findMinId(any()))
                .thenReturn(1L);
        Mockito.lenient()
                .when(payRepository.findMaxId(any()))
                .thenReturn(10L);

        partitioner = new PayIdRangePartitioner(payRepository, "2021-05-29");

        //when
        Map<String, ExecutionContext> executionContextMap = partitioner.partition(5);

        //then
        ExecutionContext partition1 = executionContextMap.get("partition0");
        assertThat(partition1.getLong("minId")).isEqualTo(1L);
        assertThat(partition1.getLong("maxId")).isEqualTo(2L);

        ExecutionContext partition5 = executionContextMap.get("partition4");
        assertThat(partition5.getLong("minId")).isEqualTo(9L);
        assertThat(partition5.getLong("maxId")).isEqualTo(10L);
    }
}