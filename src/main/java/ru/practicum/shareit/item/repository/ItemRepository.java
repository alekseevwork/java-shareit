package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findAllByOwnerId(Long ownerId);

    @Query("SELECT i FROM Item i JOIN FETCH i.owner WHERE i.id = :id")
    Optional<Item> findItemWithOwnerById(Long id);

    @Query("SELECT i FROM Item i JOIN FETCH i.owner " +
            "WHERE i.name ILIKE %:text% " +
            "OR i.description ILIKE %:text%")
    Collection<Item> findAllByNamePattern(String text);
}
