import { createSelector } from 'reselect';
import { initialState } from './reducer';

/**
 * Direct selector to the userStats state domain
 */

const selectUserStatsDomain = state => state.get('userStats', initialState);

/**
 * Other specific selectors
 */

/**
 * Default selector used by UserStats
 */

const makeSelectUserStats = () =>
  createSelector(selectUserStatsDomain, substate => substate.toJS());

export default makeSelectUserStats;
export { selectUserStatsDomain };
