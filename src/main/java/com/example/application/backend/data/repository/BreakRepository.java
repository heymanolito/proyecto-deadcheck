package com.example.application.backend.data.repository;

import com.example.application.backend.data.entity.Break;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BreakRepository extends JpaRepository<Break, Long> {
}
