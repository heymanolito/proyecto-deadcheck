package com.example.application.backend.data.service;

import com.example.application.backend.data.entity.Tracking;
import com.example.application.backend.data.entity.User;
import com.example.application.backend.data.repository.TrackingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TrackingServiceImpl implements ITrackingService {

    private final TrackingRepository repository;

    private static final Logger log = LoggerFactory.getLogger(BreakServiceImpl.class);

    @Autowired
    public TrackingServiceImpl(TrackingRepository repository) {
        this.repository = repository;
    }

    @Override
    public Tracking saveTracking(Tracking tracking) {
        return repository.save(tracking);
    }

    @Override
    public Page<Tracking> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Optional<Tracking> findById(Long id) {
        Optional<Tracking> opt = repository.findById(id);
        if (opt.isEmpty()) {
            throw new NoSuchElementException("No existe ese parte");
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
    public void updateTracking(Tracking tracking) {
        repository.save(tracking);
    }

    @Override
    public Tracking findLatestParteByUser(User user) {
        return repository.findLatestParteByUser(user);
    }


}
