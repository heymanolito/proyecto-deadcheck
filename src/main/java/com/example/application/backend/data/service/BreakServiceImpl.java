package com.example.application.backend.data.service;

import com.example.application.backend.data.entity.Break;
import com.example.application.backend.data.repository.BreakRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
public class BreakServiceImpl implements IBreakService{

    private static final Logger log = LoggerFactory.getLogger(BreakServiceImpl.class);

    private final BreakRepository repository;

    @Autowired
    public BreakServiceImpl(BreakRepository repository) {
        this.repository = repository;
    }

    @Override
    public Break saveBreak(Break descanso) {
        return repository.save(descanso);
    }

    @Override
    public Page<Break> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Optional<Break> findById(Long id) {
        Optional<Break> opt = repository.findById(id);
        if (opt.isEmpty()) {
            throw new NoSuchElementException("No existe ese descanso");
        }
        return repository.findById(id);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void updateBreak(Break descanso) {
        repository.save(descanso);
    }
}
