package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.item.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long>, CrudRepository<Item, Long> {

    @Query("SELECT i " +
            "FROM Item i " +
            "JOIN FETCH i.owner o " +
            "WHERE i.id = ?1")
    Optional<Item> findByIdFetch(Long itemId);

    List<Item> findItemsByOwnerId(Long userId);

    void deleteByIdAndOwnerId(Long itemId, Long userId);

    Optional<Item> findByIdAndOwnerId(Long itemId, Long userId);

    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE i.available = true " +
            "AND (LOWER(i.name) LIKE CONCAT('%', ?1, '%') " +
            "OR LOWER(i.description) LIKE CONCAT('%', ?1, '%'))")

    List<Item> getListILikeByText(String text);

    List<Item> findByRequestId(Long requestId);

    List<Item> findByRequestIdIn(List<Long> requestId);
}