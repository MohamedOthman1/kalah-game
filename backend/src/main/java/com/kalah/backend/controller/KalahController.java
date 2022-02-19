package com.kalah.backend.controller;

import com.kalah.backend.exception.GameNotFoundException;
import com.kalah.backend.helpers.ApiResponse;
import com.kalah.backend.models.Game;
import com.kalah.backend.services.GameService.GameService;
import lombok.RequiredArgsConstructor;

import com.kalah.backend.exception.GameInvalidMoveException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/kalaha")
public class KalahController {

    @Autowired
    private GameService _gameService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getGameById(@PathVariable Long id)  {
        ApiResponse response = new ApiResponse();
        try {
            response.setResult(_gameService.getGameById(id));
            response.setCode(HttpStatus.OK.value());
            response.setFlag(ApiResponse.Flag.Pass);
            return ResponseEntity.ok(response);
        } catch (GameNotFoundException ex)
        {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            response = response.ReturnErrorResponse(response, HttpStatus.NOT_FOUND.value(),
                    stackTrace,ex.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e)
        {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            String errorMessage = "Something went wrong. Please try again later !";
            response = response.ReturnErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    stackTrace,errorMessage);
            return ResponseEntity.ok(response);
        }
    }


    @PostMapping
    public ResponseEntity<ApiResponse> createGame(@RequestParam(name = "seedsNumber", required = false, defaultValue = "6") Integer seedsNumber){
        ApiResponse response = new ApiResponse();
        try {
            Game game = _gameService.createNewGame(seedsNumber);
            response.setResult(game);
            response.setCode(HttpStatus.CREATED.value());
            response.setFlag(ApiResponse.Flag.Pass);
            return ResponseEntity.ok(response);
        }catch (Exception e)
        {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            String errorMessage = "Something went wrong. Please try again later !";
            response = response.ReturnErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    stackTrace,errorMessage);
            return ResponseEntity.ok(response);
        }

    }

    @PostMapping("/move/{gameId}/{pitPosition}")
    public ResponseEntity<ApiResponse> move(@PathVariable Long gameId,@PathVariable Integer pitPosition) {
        ApiResponse response = new ApiResponse();
        try {
            Game game = _gameService.move(gameId, pitPosition);
            response.setResult(game);
            response.setCode(HttpStatus.OK.value());
            response.setFlag(ApiResponse.Flag.Pass);
            return ResponseEntity.ok(response);
        } catch (GameInvalidMoveException ex)
        {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            response = response.ReturnErrorResponse(response, HttpStatus.BAD_REQUEST.value(),
                    stackTrace,ex.getMessage());
            return ResponseEntity.ok(response);
        } catch (GameNotFoundException ex)
        {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            response = response.ReturnErrorResponse(response, HttpStatus.NOT_FOUND.value(),
                    stackTrace,ex.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e)
        {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            String errorMessage = "Something went wrong. Please try again later !";
            response = response.ReturnErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    stackTrace,errorMessage);
            return ResponseEntity.ok(response);
        }

    }

    

}
