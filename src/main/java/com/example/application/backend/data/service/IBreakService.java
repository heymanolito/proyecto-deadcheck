package com.example.application.backend.data.service;

import com.example.application.backend.data.entity.Break;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface IBreakService {

    Break saveBreak(Break descanso);

    Page<Break> findAll(Pageable pageable);

    Optional<Break> findById(Long id);

    void deleteAll();

    void deleteById(Long id);

    void updateBreak(Break descanso);
}
