// import { take, call, put, select } from 'redux-saga/effects';

// Individual exports for testing
import { call, put, takeLatest } from 'redux-saga/effects';
import request from '../../utils/request';
import { userStatsLoaded, userStatsLoadingError } from './actions';
import { LOAD_USER_STATS } from './constants';

export function* getUserStats(action) {
  // Select username from store
  const requestURL = `https://api.faceit.com/core/v1/nicknames/${action.user}`;

  try {
    // Call our request helper (see 'utils/request')
    const playerInfo = yield call(request, requestURL);
    const playerStats = yield call(
      request,
      `https://api.faceit.com/stats/v1/stats/users/${
        playerInfo.payload.guid
      }/games/csgo`,
    );
    const playerTimeStats = yield call(
      request,
      `https://api.faceit.com/stats/v1/stats/time/users/${
        playerInfo.payload.guid
      }/games/csgo?size=${action.lastGames}`,
    );
    const last = action.lastGames;
    const recent = { sum_c2: 0, sum_c4: 0, sum_i10: 0, sum_i6: 0, sum_i7: 0, sum_i8: 0 };
    for (let i = 0; i < last; i += 1) {
      recent.sum_c2 += parseFloat(playerTimeStats[i].c2);
      recent.sum_c4 += parseFloat(playerTimeStats[i].c4);
      recent.sum_i10 += parseFloat(playerTimeStats[i].i10);
      recent.sum_i6 += parseFloat(playerTimeStats[i].i6);
      recent.sum_i7 += parseFloat(playerTimeStats[i].i7);
      recent.sum_i8 += parseFloat(playerTimeStats[i].i8);
    }
    recent.avg_c2 = Math.round((recent.sum_c2 / last) * 100) / 100;
    recent.avg_c4 = Math.round(recent.sum_c4 / last);
    recent.avg_i10 = Math.round((recent.sum_i10 / last) * 100);
    recent.avg_i6 = Math.round(recent.sum_i6 / last);
    recent.avg_i7 = Math.round(recent.sum_i7 / last);
    recent.avg_i8 = Math.round(recent.sum_i8 / last);
    console.log(recent);
    const userStats = {
      playerInfo: playerInfo.payload,
      playerStats,
      playerTimeStats,
      recent,
    };
    yield put(userStatsLoaded(userStats));
  } catch (err) {
    yield put(userStatsLoadingError(err));
  }
}

/**
 * Root saga manages watcher lifecycle
 */
export default function* userStatsData() {
  yield takeLatest(LOAD_USER_STATS, getUserStats);
}
