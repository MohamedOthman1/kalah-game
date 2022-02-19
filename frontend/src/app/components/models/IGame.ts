import { IKalahPit } from "./IKalahPit";

export interface IGame {
    id?:number;
    nextPlayer? : string;
    current_index?:number;
    winner? : string;
    status?:string;
    kalahPit : IKalahPit[];
    createdAt? : Date;
    updatedAt? : Date;
}



  
