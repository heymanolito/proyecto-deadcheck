package com.example.application.backend.data.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "descansos")
public class Break {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "break_id", nullable = false)
    private Long break_id;

    @Column
    private Double tiempoDescanso;

    @Column
    private BreakEnum tipoDescanso;

    @ManyToOne
    @JoinColumn(name = "tracking_id")
    private Tracking tracking;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Break aBreak = (Break) o;
        return break_id != null && Objects.equals(break_id, aBreak.break_id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
