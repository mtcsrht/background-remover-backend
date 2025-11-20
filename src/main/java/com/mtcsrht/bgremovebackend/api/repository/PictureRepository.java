package com.mtcsrht.bgremovebackend.api.repository;

import com.mtcsrht.bgremovebackend.api.model.Picture;
import com.mtcsrht.bgremovebackend.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PictureRepository extends JpaRepository<Picture, Integer> {
    List<Picture> findAllByUserId(Long user_id);

    Long user(User user);
}
