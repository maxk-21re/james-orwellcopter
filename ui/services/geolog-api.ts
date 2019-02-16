import {default as Axios} from "axios";
import {Geolog} from "../models/geologs";
import {Promise} from "es6-promise";
import { Observable, Observer } from "rxjs";
import { of } from "rxjs";

export function getGeologs(from: Date, to: Date): Observable<Geolog[]> {
  console.log(from.toISOString().substr(0, 10))
  return Observable.create( (observer: Observer<Geolog[]>) =>
    Axios.get(`/geologs/${from.toISOString().substr(0, 10)}/${to.toISOString().substr(0, 10)}`).then( response => {
      observer.next( response.data );
      observer.complete();
    }).catch( error => observer.error(error))
  )
}
