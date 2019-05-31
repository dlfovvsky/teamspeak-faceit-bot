import { fromJS } from 'immutable';
import teamspeakReducer from '../reducer';

describe('teamspeakReducer', () => {
  it('returns the initial state', () => {
    expect(teamspeakReducer(undefined, {})).toEqual(fromJS({}));
  });
});
