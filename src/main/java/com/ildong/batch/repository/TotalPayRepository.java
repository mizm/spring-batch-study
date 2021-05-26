package com.ildong.batch.repository;

import com.ildong.batch.model.Pay;
import com.ildong.batch.model.TotalPay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TotalPayRepository extends JpaRepository<TotalPay, Long> {

    @Query("select t from TotalPay t where to_char(date,'yyyy-mm-dd') = :requestDate")
    Optional<TotalPay> findForTest(@Param(value = "requestDate") String requestDate);
}
