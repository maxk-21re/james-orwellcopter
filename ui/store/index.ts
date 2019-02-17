import { GeologAction, GeologState, GeologDefaultState, default as GeologReducer} from "../reducers/root-reducer"
import {default as RootEpic} from "../epics/root-epic"
import { createEpicMiddleware } from "redux-observable";
import { createStore, applyMiddleware } from 'redux';
import * as services from "../services/geolog-api";
import { compose } from 'redux';

export const epicMiddleware = createEpicMiddleware<GeologAction, GeologAction, GeologState, typeof services.getGeologs>({dependencies: services.getGeologs})

const middlewares = [epicMiddleware];
const composeEnhancers = (window as any).__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

const store = createStore(GeologReducer, GeologDefaultState, composeEnhancers(applyMiddleware(...middlewares)));

epicMiddleware.run(RootEpic);

export default store;
