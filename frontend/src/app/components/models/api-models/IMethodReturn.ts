import { FlagEnum } from "./FlagEnum";

export interface IMethodReturn {
    flag: FlagEnum;
    message: string;
    result: any;
    errorMessage:string;
    code:number;
  }
  