import { createSelector } from 'reselect';
import { initialState } from './reducer';
import { selectGlobal } from '../App/selectors';

/**
 * Direct selector to the faceit state domain
 */

const selectFaceitDomain = state => state.get('faceit', initialState);

/**
 * Other specific selectors
 */

const makeSelectUsers = () =>
  createSelector(selectFaceitDomain, globalState =>
    globalState.getIn(['users']),
  );

/**
 * Default selector used by Faceit
 */

const makeSelectFaceitDomain = () =>
  createSelector(selectFaceitDomain, substate => substate.toJS());

export default makeSelectFaceitDomain;
export { selectFaceitDomain, makeSelectUsers };
