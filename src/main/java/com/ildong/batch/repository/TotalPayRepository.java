package com.ildong.batch.repository;

import com.ildong.batch.model.Pay;
import com.ildong.batch.model.TotalPay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface TotalPayRepository extends JpaRepository<TotalPay, Long> {

    Optional<TotalPay> findByDate(LocalDate date);
}
