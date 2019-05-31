import { fromJS } from 'immutable';
import faceitReducer from '../reducer';

describe('faceitReducer', () => {
  it('returns the initial state', () => {
    expect(faceitReducer(undefined, {})).toEqual(fromJS({}));
  });
});
