package com.example.application.backend.data.repository;

import com.example.application.backend.data.entity.Status;
import com.example.application.backend.data.entity.Tracking;
import com.example.application.backend.data.entity.User;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    @Query(value="select * from user u join partes_de_trabajo pd on pd.user_id = u.user_id",
    nativeQuery = true)
    List<Tracking> findTrackingByUser(User user);


    @Query(value="select * from user u order by u.id",
            nativeQuery = true)
    List<User> orderByUserId();

    List<User> findAllByName(String firstName);

    List<User> findAllByNameAndSurname(String firstName, String surname);

    List<User> findAllByEmail(String email);

    List<User> findAllByStatus(Status status);

    List<User> findByNameContainingIgnoreCase(String name);
}