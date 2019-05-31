import { fromJS } from 'immutable';
import userStatsReducer from '../reducer';

describe('userStatsReducer', () => {
  it('returns the initial state', () => {
    expect(userStatsReducer(undefined, {})).toEqual(fromJS({}));
  });
});
