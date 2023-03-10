package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User owner;
    @OneToOne
    @JoinColumn(name = "last_booking_id")
    private Booking lastBooking;
    @OneToOne
    @JoinColumn(name = "next_booking_id")
    private Booking nextBooking;
    @JsonIgnore
    @OneToMany(mappedBy = "item")
    private Set<Booking> bookings = new HashSet<>();
    @JsonIgnore
    @OneToMany(mappedBy = "item")
    private Set<Comment> comments = new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;
}
