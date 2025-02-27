package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE b.booker.id = :bookerId AND i.id = :itemId AND b.status = 'APPROVED' AND b.end < :currentTime")
    List<Booking> getAllUserBookings(Long bookerId, Long itemId, LocalDateTime currentTime);

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

    @Query(value = "SELECT b.id AS booking_id, b.start_date, b.end_date, b.status, b.booker_id, b.item_id AS booking_item_id, i.id AS item_id, i.name, i.description, i.is_available, i.owner_id " +
            "FROM bookings b JOIN items i ON i.id = b.item_id " +
            "WHERE b.item_id = :itemId AND b.end_date < :currentTime ORDER BY b.end_date ASC LIMIT 1",
            nativeQuery = true)
    Optional<Booking> getLastBooking(Long itemId, LocalDateTime currentTime);

    @Query(value = "SELECT b.id AS booking_id, b.start_date, b.end_date, b.status, b.booker_id, b.item_id AS booking_item_id, i.id AS item_id, i.name, i.description, i.is_available, i.owner_id " +
            "FROM bookings b JOIN items i ON i.id = b.item_id " +
            "WHERE b.item_id = :itemId AND b.start_date > :currentTime AND b.status != 'REJECTED' ORDER BY b.start_date ASC LIMIT 1",
            nativeQuery = true)
    Optional<Booking> getNextBooking(Long itemId, LocalDateTime currentTime);
}
