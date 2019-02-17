import { getType, ActionType} from "typesafe-actions";
import {Geolog} from "../models/geologs";
import * as geologs from "../actions/root-actions";
import { combineReducers } from "redux";
import { AxiosError } from "axios";

export const GeologDefaultState: GeologState = {
  data: [],
  loading: false,
  error: null
}

export type GeologState = Readonly<{
  data: Geolog[],
  loading: boolean,
  error: AxiosError | null
}>;
export type GeologAction = ActionType<typeof geologs>;
const reducer = combineReducers<GeologState, GeologAction>({
  data: (state = GeologDefaultState.data, action: GeologAction) => {
    switch(action.type) {
      case getType(geologs.fetchGeologs.success):
        return action.payload;
      case getType(geologs.addGeologs):
        return [...state, ...action.payload]
      default:
        return state;
    }
  },
  error: (state = GeologDefaultState.error, action: GeologAction) => {
    switch(action.type) {
      case getType(geologs.fetchGeologs.failure):
        return action.payload;
      case getType(geologs.fetchGeologs.request):
        return null;
      case getType(geologs.fetchGeologs.success):
        return null;
      default: return state;
    }
  },
  loading: (state = GeologDefaultState.loading, action: GeologAction) => {
    switch(action.type) {
      case getType(geologs.fetchGeologs.failure): return false;
      case getType(geologs.fetchGeologs.request): return true;
      case getType(geologs.fetchGeologs.success): return false;
      default: return state;
    }
  }
})

export default reducer;
