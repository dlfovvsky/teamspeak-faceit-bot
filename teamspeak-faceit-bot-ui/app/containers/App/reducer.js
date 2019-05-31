/*
 * AppReducer
 *
 * The reducer takes care of our data. Using actions, we can change our
 * application state.
 * To add a new action, add it to the switch statement in the reducer function
 *
 * Example:
 * case YOUR_ACTION_CONSTANT:
 *   return state.set('yourStateVariable', true);
 */

import { fromJS } from 'immutable';

import {
  LOAD_REPOS_SUCCESS,
  LOAD_REPOS,
  LOAD_REPOS_ERROR,
  LOAD_USERS,
  LOAD_USERS_ERROR,
  LOAD_USERS_SUCCESS,
} from './constants';

// The initial state of the App
const initialState = fromJS({
  loading: false,
  error: false,
  currentUser: false,
  userData: {
    repositories: false,
  },
  faceit: {
    loading: false,
    error: false,
    users: false,
  },
});

function appReducer(state = initialState, action) {
  switch (action.type) {
    case LOAD_REPOS:
      return state
        .set('loading', true)
        .set('error', false)
        .setIn(['userData', 'repositories'], false);
    case LOAD_REPOS_SUCCESS:
      return state
        .setIn(['userData', 'repositories'], action.repos)
        .set('loading', false)
        .set('currentUser', action.username);
    case LOAD_REPOS_ERROR:
      return state.set('error', action.error).set('loading', false);
    case LOAD_USERS:
      return state
        .setIn(['faceit', 'loading'], true)
        .setIn(['faceit', 'error'], false)
        .setIn(['faceit', 'users'], false);
    case LOAD_USERS_SUCCESS:
      return state
        .setIn(['faceit', 'users'], action.users)
        .setIn(['faceit', 'loading'], false);
    case LOAD_USERS_ERROR:
      return state
        .setIn(['faceit', 'error'], false)
        .setIn(['faceit', 'loading'], false);
    default:
      return state;
  }
}

export default appReducer;
