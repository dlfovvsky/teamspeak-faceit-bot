/*
 *
 * UserStats reducer
 *
 */

import { fromJS } from 'immutable';
import {DEFAULT_ACTION, LOAD_USER_STATS, LOAD_USER_STATS_ERROR, LOAD_USER_STATS_SUCCESS} from "./constants";

export const initialState = fromJS({
  loading: false,
  error: false,
  userStats: false,
});

function userStatsReducer(state = initialState, action) {
  switch (action.type) {
    case DEFAULT_ACTION:
      return state;
    case LOAD_USER_STATS:
      return state
        .setIn(['loading'], true)
        .setIn(['error'], false)
        .setIn(['userStats'], false);
    case LOAD_USER_STATS_SUCCESS:
      return state
        .setIn(['userStats'], action.userStats)
        .setIn(['loading'], false);
    case LOAD_USER_STATS_ERROR:
      return state
        .setIn(['error'], false)
        .setIn(['loading'], false);
    default:
      return state;
  }
}

export default userStatsReducer;
