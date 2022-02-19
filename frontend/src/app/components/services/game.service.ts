import { Injectable } from "@angular/core";
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { apiConfig } from '../../../assets/config/api.config';
import { catchError, map, Observable } from "rxjs";
import { IGame } from "../models/IGame";
import { IMethodReturn } from "../models/api-models/IMethodReturn";
import { FlagEnum } from "../models/api-models/FlagEnum";
import { ToastrService } from "ngx-toastr";

@Injectable()
export class GameService {
    constructor(
        private http: HttpClient,
        private toastr : ToastrService
      ) { }
    GetGameById(id: number): Observable<any> {
    let self = this;
    const url = apiConfig.baseUrl + '/' + apiConfig.endpoints.Game.GetGameById + '/' + id;
    return this.http.get<IMethodReturn>(url).pipe(
        map(function (res: IMethodReturn) {
          
            if (res.flag == FlagEnum.Pass) {
              return res.result;
            } else {
              self.toastr.error('', res.errorMessage)
            }
            return res.result
          }),
        catchError(error => {
           return error
        }));
  }

  Move(gameId: number, pitPosition:number): Observable<any> {
    let self = this;
    const url = apiConfig.baseUrl + '/' + apiConfig.endpoints.Game.Move + '/' + gameId + '/' + pitPosition;
    return this.http.post<IMethodReturn>(url, {}).pipe(
        map(function (res: IMethodReturn) {
            if (res.flag == FlagEnum.Pass) {
              return res.result;
            }
            else {
              self.toastr.error('', res.errorMessage)
            }
            return res.result
          }),
        catchError(error => {
           return error
        }));
  }

  CreateGame(seedNumber:number): Observable<any> {
    let self = this;
    const url = apiConfig.baseUrl + '/' + apiConfig.endpoints.Game.Post + '?seedsNumber=' + seedNumber;
    return this.http.post<IMethodReturn>(url, {}).pipe(
        map(function (res: IMethodReturn) {
            if (res.flag == FlagEnum.Pass) {
              return res.result;
            }
            else {
              self.toastr.error('', res.errorMessage)
            }
            return res.result
          }),
        catchError(error => {
           return error
        }));
  }

}