/*
 *
 * UserStats actions
 *
 */

import {
  DEFAULT_ACTION,
  LOAD_USER_STATS,
  LOAD_USER_STATS_ERROR,
  LOAD_USER_STATS_SUCCESS,
} from './constants';

export function loadUserStats(user, lastGames) {
  return {
    type: LOAD_USER_STATS,
    user,
    lastGames,
  };
}

export function userStatsLoaded(userStats) {
  return {
    type: LOAD_USER_STATS_SUCCESS,
    userStats,
  };
}

export function userStatsLoadingError(error) {
  return {
    type: LOAD_USER_STATS_ERROR,
    error,
  };
}

export function defaultAction() {
  return {
    type: DEFAULT_ACTION,
  };
}
