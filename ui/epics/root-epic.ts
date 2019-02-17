// todo
import { Epic, ofType, combineEpics } from "redux-observable";
import { filter, switchMap, map, catchError, mapTo, ignoreElements, tap, mergeMap} from "rxjs/operators";
import { isActionOf, ActionType, isOfType, getType} from "typesafe-actions";
import { GeologState,GeologAction } from "../reducers/root-reducer";
import {fetchGeologs } from "../actions/root-actions";
import * as Actions from "../actions/root-actions";
import {getGeologs} from "../services/geolog-api";
import { from, of, pipe} from "rxjs";
import { ajax } from "rxjs/ajax";
import { Geolog } from "../models/geologs";

const geologsFetchEpic: Epic<GeologAction, GeologAction, GeologState> = (action$, _) =>
  action$
    .pipe(
      ofType(getType(fetchGeologs.request)),
      switchMap(_ => getGeologs(new Date(), new Date()).pipe(
        map(val => Actions.fetchGeologs.success(val)),
        catchError( val => of(Actions.fetchGeologs.failure(val)))
      ))
    )

export default combineEpics(geologsFetchEpic);
