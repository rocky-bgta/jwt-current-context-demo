package com.example.contextdemo.repository;

import com.example.contextdemo.model.RoomContext;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface RoomContextRepository extends MongoRepository<RoomContext, String> {}
