package com.example.application.backend.data.repository;

import com.example.application.backend.data.entity.Tracking;
import com.example.application.backend.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TrackingRepository extends JpaRepository<Tracking, Long> {

    @Query(value = "select * from partes_de_trabajo pt where pt.user_id = :#{#user.user_id} order by pt.fecha_hora desc limit 1", nativeQuery = true)
    Tracking findLatestParteByUser(User user);
}
