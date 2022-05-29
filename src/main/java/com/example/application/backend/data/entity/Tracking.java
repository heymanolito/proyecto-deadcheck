package com.example.application.backend.data.entity;

import com.example.application.backend.data.util.DateUtil;
import lombok.*;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "tracking")
public class Tracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tracking_id", nullable = false)
    private Long tracking_id;

    private LocalDateTime workCheckIn;

    private LocalDateTime workCheckOut;

    private boolean parteAprobado;

    private LocalDateTime parteAprobadoDate;

    @ManyToOne
    @JoinColumn(name = "user_user_id")
    private User user;

    public String getTotalWorkTime() {
        if (workCheckIn != null && workCheckOut != null) {
            Duration duration = Duration.between(workCheckIn, workCheckOut);
            return DateUtil.formatDuration(duration);
        } else {
            return "";
        }
    }


    public LocalDateTime getWorkCheckInIfNull() {
        if(workCheckIn == null) {
            return LocalDateTime.of(0, 0, 0, 0, 0);
        }
        return workCheckIn;
    }

    public LocalDateTime getWorkCheckOutIfNull() {
        if(workCheckOut == null) {
            return LocalDateTime.of(1, 1, 1, 1, 1);
        }
        return workCheckOut;
    }
//    @OneToMany(cascade = CascadeType.ALL)
//    @ToString.Exclude
//    private List<Break> descansos;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
