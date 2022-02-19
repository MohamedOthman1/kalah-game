import { Component, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { GameService } from '../services/game.service';
import { apiConfig } from 'src/assets/config/api.config';
import { IGame } from '../models/IGame';
import { ActivatedRoute, Router } from '@angular/router';
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  seedNumber : number = 0;
  constructor(private gameService:GameService, private toastr:ToastrService, private router: Router) { }

  ngOnInit(): void {
    let gameId = +localStorage.getItem('game_id')!;
    if(gameId && !isNaN(gameId) && gameId !== 0)
    {
      this.router.navigate(['/board'])
    }
  }

  ChangeSeed(seed:number)
  {
    
  }
  CreateGame()
  {
    if(this.seedNumber ==4 || this.seedNumber == 6)
    {
      this.gameService.CreateGame(this.seedNumber).subscribe((res : IGame) => {
        if(res)
        {
          let id = res.id ? res.id.toString() : '';
          localStorage.setItem('game_id', id);
          this.router.navigate(['/board']);
        }
        
      })
    }
    else {
      this.toastr.error('', 'Select a valid seed number')
    }
    
  }
}
