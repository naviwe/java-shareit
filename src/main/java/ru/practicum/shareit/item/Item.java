package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.user.User;

import jakarta.persistence.*;

@Entity
@Table(name = "items")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "is_available")
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}