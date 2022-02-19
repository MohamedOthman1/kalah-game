package com.kalah.backend.services.GameService;
import com.kalah.backend.dal.IGameDAO;
import com.kalah.backend.exception.GameInvalidMoveException;
import com.kalah.backend.exception.GameNotFoundException;
import com.kalah.backend.helpers.Constant;
import com.kalah.backend.models.Game;
import com.kalah.backend.models.KalahPit;
import com.kalah.backend.models.Player;

import com.kalah.backend.models.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Optional;
@Slf4j
@Service
public class GameService implements IGameService {

    @Value("6")
    private int playerPits;

    @Value("14")
    private int pitsTotal;


    @Autowired
    private IGameDAO _gameDao;

    @Override
    public Game createNewGame(Integer numberOfSeeds) {
        Game game = new Game();

        ArrayList<KalahPit> pits = new ArrayList<KalahPit>();

        for (int i = 0; i < pitsTotal; i++) {
            KalahPit pit = new KalahPit();
            pit.setPitNumber(i + 1);
            if ((i != playerPits) && (i != pitsTotal - 1)) {
                pit.setSeeds(numberOfSeeds);
            } else {
                pit.setSeeds(0);
            }
            pit.setGame(game);
            pits.add(pit);
        }
        game.setKalahPit(pits);
        _gameDao.saveAndFlush(game);

        log.info("createNewGame(): Game created successfully");
        return game;
    }
    @Override
    public Game getGameById(Long id) throws GameNotFoundException {
        Optional<Game> game = _gameDao.findById(id);
        if (!game.isPresent()) {
            throw new GameNotFoundException("Game not found for id " + id);
        }
        log.info("getGameById(): return game by Id " + id);
        return game.get();
    }

    public boolean isValidIndex(Player player, Integer pitPosition)
    {
        return ((player.equals(Player.SOUTH) && pitPosition <= playerPits)
                || (player.equals(Player.NORTH) && pitPosition > playerPits + 1));
    }

    public void validateMove(Game game, Integer pitPosition) throws GameInvalidMoveException
    {
        if(Constant.southBoardIndex == pitPosition || Constant.northBoardIndex == pitPosition)
        {
            throw new GameInvalidMoveException("Invalid pit index.");
        }
        if(pitPosition > Constant.northBoardIndex || pitPosition < 1)
        {
            throw new GameInvalidMoveException("Pit index out of bounds");
        }
        Player nextPlayer = game.getNextPlayer();
        KalahPit kalahPit = game.getKalahPit().stream().filter(p -> p.getPitNumber().equals(pitPosition)).findFirst().get();
        Integer seeds = kalahPit.getSeeds();

        if(seeds.equals(0))
        {
            throw new GameInvalidMoveException("No seeds available");
        }
        if((nextPlayer.equals(Player.SOUTH) && pitPosition > Constant.southBoardIndex) ||
                (nextPlayer.equals(Player.NORTH) && pitPosition < Constant.southBoardIndex ))
        {
            throw new GameInvalidMoveException("Not your pit");
        }
    }

    private Integer getPlayerBoardIndex(Player player) {
        if (player.equals(Player.NORTH)) {
            return Constant.northBoardIndex;
        } else {
            return Constant.southBoardIndex;
        }
    }

    public Player getPreviousTurn(Player player)
    {
            if(player.equals(Player.NORTH))
                return Player.SOUTH;
            return Player.NORTH;
    }

    public void SeedsMove(Game game, Integer pitPosition)
    {
        KalahPit kalahPit = game.getKalahPit().stream().filter(p -> p.getPitNumber().equals(pitPosition)).findFirst().get();
        Integer seeds = kalahPit.getSeeds();
        kalahPit.setSeeds(0);
        Player nextPlayer = game.getNextPlayer();
        Player previousPlayer = getPreviousTurn(nextPlayer);
        Integer opponentBoardIndex = getPlayerBoardIndex(previousPlayer);
        Integer nextMove = pitPosition;
        KalahPit selectedKalahPit = null;
        while (seeds > 0) {
            nextMove++;
            if (nextMove == opponentBoardIndex) {
                nextMove++; // skip the opponent's kalah
            }
            if (nextMove > Constant.northBoardIndex) {
                nextMove = 1;
            }
            Integer finalNextMove = nextMove;
            selectedKalahPit = game.getKalahPit().stream().filter(p -> p.getPitNumber()
                                .equals(finalNextMove)).findFirst().get();
            Integer oldSeeds = selectedKalahPit.getSeeds();
            selectedKalahPit.setSeeds(oldSeeds+1);
            game.setCurrent_index(nextMove);
            seeds--;
        }
        if(isValidIndex(nextPlayer,nextMove) && selectedKalahPit.getSeeds() == 1)
        {
                Integer oppositeIndex = 14 - nextMove;
                KalahPit oppositeKalah = game.getKalahPit().stream().filter(p -> p.getPitNumber()
                            .equals(oppositeIndex)).findFirst().get();
                Integer finalNextMove1 = nextMove;
                KalahPit nextMoveKalah = game.getKalahPit().stream().filter(p -> p.getPitNumber()
                    .equals(finalNextMove1)).findFirst().get();
                oppositeKalah.setSeeds(0);
                nextMoveKalah.setSeeds(0);

            KalahPit boardKahalPit;
            if (game.getNextPlayer() == Player.SOUTH) {
                boardKahalPit = game.getKalahPit().stream().filter(p -> p.getPitNumber()
                        .equals(Constant.southBoardIndex)).findFirst().get();
            } else {
                boardKahalPit = game.getKalahPit().stream().filter(p -> p.getPitNumber()
                        .equals(Constant.northBoardIndex)).findFirst().get();
            }
            Integer score = oppositeKalah.getSeeds() + 1 + boardKahalPit.getSeeds();
            boardKahalPit.setSeeds(score);
        }

        if(game.getNextPlayer() == Player.SOUTH && game.getCurrent_index() != Constant.southBoardIndex)
        {
            game.setNextPlayer(Player.NORTH);
        } else if (game.getNextPlayer() == Player.NORTH && game.getCurrent_index() != Constant.northBoardIndex)
        {
            game.setNextPlayer(Player.SOUTH);
        }
    }

    public void checkWinner(Game game)
    {
        Integer southSum = game.getKalahPit().stream().filter(p -> p.getPitNumber() >= 1 && p.getPitNumber() < 7).mapToInt(KalahPit::getSeeds).sum();
        Integer northSum = game.getKalahPit().stream().filter(p -> p.getPitNumber() > 7 && p.getPitNumber() < 14).mapToInt(KalahPit::getSeeds).sum();

        if(southSum == 0 || northSum == 0)
        {
            KalahPit southBoard = game.getKalahPit().stream().filter(p -> p.getPitNumber().equals(Constant.southBoardIndex)).findFirst().get();
            KalahPit northBoard = game.getKalahPit().stream().filter(p -> p.getPitNumber().equals(Constant.northBoardIndex)).findFirst().get();

            if(southBoard.getSeeds() > northBoard.getSeeds())
            {
                game.setStatus(Status.FINISHED);
                game.setWinner(Player.SOUTH);
            } else if (southBoard.getSeeds() < northBoard.getSeeds())
            {
                game.setStatus(Status.FINISHED);
                game.setWinner(Player.NORTH);
            }

            game.getKalahPit().forEach(pit -> {
                if(pit.getSeeds() != Constant.southBoardIndex && pit.getSeeds() != Constant.northBoardIndex){
                    pit.setSeeds(0);
                }
            });
            southBoard.setSeeds(southBoard.getSeeds() + southSum);
            northBoard.setSeeds(northBoard.getSeeds() + northSum);
        }
    }

    @Override
    public Game move(Long id, Integer pitPosition) throws GameInvalidMoveException, GameNotFoundException {

        log.info("move(): start moving seeds gameId = " + id + " pit position = " + pitPosition);
        Game game = getGameById(id);
        validateMove(game,pitPosition);
        SeedsMove(game, pitPosition);
        checkWinner(game);
        log.info("move(): end moving seeds gameId = " + id + " pit position = " + pitPosition);
        _gameDao.saveAndFlush(game);
        return game;
    }
    
     

}