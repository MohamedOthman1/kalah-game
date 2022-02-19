package com.kalah.backend.services.GameService;


import com.kalah.backend.exception.GameInvalidMoveException;
import com.kalah.backend.exception.GameNotFoundException;
import com.kalah.backend.models.Game;
import com.kalah.backend.models.Player;

public interface IGameService {
        public Game createNewGame(Integer seedsNumber);
        public Game getGameById(Long id) throws  GameNotFoundException;
        public Game move(Long id, Integer pitPosition) throws GameInvalidMoveException, GameNotFoundException;
    }
    
 
