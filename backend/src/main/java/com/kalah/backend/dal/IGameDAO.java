package com.kalah.backend.dal;

import com.kalah.backend.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IGameDAO  extends JpaRepository<Game, Long> {

}
