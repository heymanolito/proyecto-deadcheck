package com.example.application.backend.data.service;

import com.example.application.backend.data.entity.Tracking;

import com.example.application.backend.data.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface ITrackingService {

    Tracking saveTracking(Tracking tracking);

    Page<Tracking> findAll(Pageable pageable);

    Optional<Tracking> findById(Long id);

    void deleteAll();

    void deleteById(Long id);

    void updateTracking(Tracking tracking);

    Tracking findLatestParteByUser(User user);

}
