import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { IGame } from '../models/IGame';
import { IKalahPit } from '../models/IKalahPit';
import { GameService } from '../services/game.service';
@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.scss']
})
export class BoardComponent implements OnInit {
  showBoard = false;
  game : IGame = {
    kalahPit : []
  };
  kalahPit:IKalahPit[] = [];
  constructor(private gameService: GameService, private router: Router, private toastr:ToastrService) { }

  ngOnInit(): void {
    let gameId : number = +localStorage.getItem('game_id')!;
    if(isNaN(gameId) || gameId == 0)
    {
      this.router.navigate(['/'])
    } else {
      this.gameService.GetGameById(gameId).subscribe((res:IGame) => {
        if(res)
        {
          this.game = res;
          this.kalahPit = this.game.kalahPit;
          this.showBoard = true;
        } else {
          localStorage.removeItem('game_id')
          this.router.navigate(['/'])
        }
        
      })
    }
    
  }

  move(pitPosition: number) {
    let pitSeeds = this.kalahPit.find(x => x.pitNumber == pitPosition)?.seeds
    if(pitSeeds == 0)
    {
      this.toastr.error('', 'No seeds available')
    } 
    else if(this.game.nextPlayer! == 'NORTH' && pitPosition <=7)
    {
        this.toastr.error('', 'Invalid Pit')
    } else if (this.game.nextPlayer! == 'SOUTH' && pitPosition >= 7)
    {
      this.toastr.error('', 'Invalid Pit')
    } else {
      let gameId  = this.game.id;
      this.gameService.Move(gameId!, pitPosition).subscribe((res: IGame) => {
          if(res)
          {
            this.game = res;
            this.kalahPit = this.game.kalahPit;
          }
      });
    }
   
  }

  StartAgain()
  {
    localStorage.removeItem('game_id')
    this.router.navigate(['/'])
  }

}
