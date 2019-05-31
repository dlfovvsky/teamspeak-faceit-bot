/*
 *
 * Faceit reducer
 *
 */

import { fromJS } from 'immutable';

export const initialState = fromJS({});

function faceitReducer(state = initialState, action) {
  switch (action.type) {
    default:
      return state;
  }
}

export default faceitReducer;
