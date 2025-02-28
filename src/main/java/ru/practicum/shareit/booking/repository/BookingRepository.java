package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(long booker, LocalDateTime end);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE b.booker.id = :bookerId " +
            "ORDER BY b.start DESC")
    List<Booking> getAllBookingsByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE b.booker.id = :bookerId AND :currentTime BETWEEN b.start AND b.end " +
            "ORDER BY b.start DESC")
    List<Booking> getAllCurrentBookingsByBookerId(Long bookerId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE b.booker.id = :bookerId AND b.start > :currentTime " +
            "ORDER BY b.start DESC")
    List<Booking> getAllFutureBookingsByBookerId(Long bookerId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE b.booker.id = :bookerId AND b.status = 'REJECTED' " +
            "ORDER BY b.start DESC")
    List<Booking> getAllRejectedBookingsByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE b.booker.id = :bookerId AND b.end < :currentTime " +
            "ORDER BY b.start DESC")
    List<Booking> getAllPastBookingsByBookerId(Long bookerId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE b.booker.id = :bookerId AND b.status = 'WAITING' AND b.start > :currentTime " +
            "ORDER BY b.start DESC")
    List<Booking> getAllWaitingBookingsByBookerId(Long bookerId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE i.owner.id = :ownerId " +
            "ORDER BY b.start DESC")
    List<Booking> getAllBookingsByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE i.owner.id = :ownerId AND :currentTime BETWEEN b.start AND b.end " +
            "ORDER BY b.start DESC")
    List<Booking> getAllCurrentBookingsByOwnerId(Long ownerId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE i.owner.id = :ownerId AND b.status = 'WAITING' AND b.start > :currentTime " +
            "ORDER BY b.start DESC")
    List<Booking> getAllWaitingBookingsByOwnerId(Long ownerId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE i.owner.id = :ownerId AND b.start > :currentTime " +
            "ORDER BY b.start DESC")
    List<Booking> getAllFutureBookingsByOwnerId(Long ownerId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE i.owner.id = :ownerId AND b.status = 'REJECTED' " +
            "ORDER BY b.start DESC")
    List<Booking> getAllRejectedBookingsByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE i.owner.id = :ownerId AND b.end < :currentTime " +
            "ORDER BY b.start DESC")
    List<Booking> getAllPastBookingsByOwnerId(Long ownerId, LocalDateTime currentTime);

    Booking getFirstByItemIdOrderByEndDesc(long itemId);

    Booking getFirstByItemIdOrderByStartAsc(long itemId);
}
