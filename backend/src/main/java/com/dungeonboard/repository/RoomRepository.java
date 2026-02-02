package com.dungeonboard.repository;

import com.dungeonboard.model.Room;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository {
    Room save(Room room);
    Optional<Room> findById(String roomId);
    List<Room> findAll();
    void deleteById(String roomId);
    List<Room> findByStatus(Room.RoomStatus status);
}
