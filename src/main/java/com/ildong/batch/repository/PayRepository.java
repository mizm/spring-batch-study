package com.ildong.batch.repository;

import com.ildong.batch.model.Pay;
import com.ildong.batch.model.Pay2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PayRepository extends JpaRepository<Pay, Long> {

    @Query("select sum(p.amount) from Pay p where to_char(tx_date_time,'yyyy-mm-dd') = :requestDate")
    Long findForTest(@Param(value = "requestDate") String requestDate);

    @Query("select MAX(p.id) from Pay p where to_char(tx_date_time,'yyyy-mm-dd') = :requestDate")
    Long findMaxId(@Param(value = "requestDate") String requestDate);

    @Query("select MIN(p.id) from Pay p where to_char(tx_date_time,'yyyy-mm-dd') = :requestDate")
    Long findMinId(@Param(value = "requestDate") String requestDate);
}
