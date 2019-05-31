import { createSelector } from 'reselect';
import { initialState } from './reducer';

/**
 * Direct selector to the teamspeak state domain
 */

const selectTeamspeakDomain = state => state.get('teamspeak', initialState);

/**
 * Other specific selectors
 */

/**
 * Default selector used by Teamspeak
 */

const makeSelectTeamspeak = () =>
  createSelector(selectTeamspeakDomain, substate => substate.toJS());

export default makeSelectTeamspeak;
export { selectTeamspeakDomain };
