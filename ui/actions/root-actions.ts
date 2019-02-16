import {Geolog} from "../models/geologs";
import { createAsyncAction, createAction, ActionType } from "typesafe-actions";
import {AxiosError} from "axios";

export const fetchGeologs = createAsyncAction(
  "FETCH_GEOLOGS_REQUEST",
  "FETCH_GEOLOGS_SUCCESS",
  "FETCH_GEOLOGS_FAILURE",
)<void, Geolog[], AxiosError>()

export type FetchGeologsActions = ActionType<typeof fetchGeologs>;

export const addGeologs = createAction("GEOLOGS_ADD", resolve => (logs: Geolog[]) => resolve(logs) );
