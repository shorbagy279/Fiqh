package com.fiqhmaster.repository;

import com.fiqhmaster.entity.Marja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarjaRepository extends JpaRepository<Marja, Long> {
}
