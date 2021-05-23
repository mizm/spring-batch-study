package com.ildong.batch.repository;

import com.ildong.batch.model.Pay2;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayRepository extends JpaRepository<Pay2, Long> {
}
