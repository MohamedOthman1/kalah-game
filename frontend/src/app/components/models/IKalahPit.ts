import { IGame } from "./IGame";

export interface IKalahPit {
    id:number;
    pitNumber:number;
    seeds:number;
    game: IGame;
    createdAt:Date;
    updatedAt:Date
}