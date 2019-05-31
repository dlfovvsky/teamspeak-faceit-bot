import { call, put, takeLatest } from 'redux-saga/effects';
import request from '../../utils/request';
import { LOAD_USERS } from '../App/constants';
import { usersLoaded, usersLoadingError } from '../App/actions';
import environment from 'environment';

export function* getUsers() {
  // Select username from store
  const requestURL = `${environment.api}/users`;

  try {
    // Call our request helper (see 'utils/request')
    const users = yield call(request, requestURL);
    yield put(usersLoaded(users));
  } catch (err) {
    yield put(usersLoadingError(err));
  }
}

/**
 * Root saga manages watcher lifecycle
 */
export default function* usersData() {
  // Watches for LOAD_REPOS actions and calls getRepos when one comes in.
  // By using `takeLatest` only the result of the latest API call is applied.
  // It returns task descriptor (just like fork) so we can continue execution
  // It will be cancelled automatically on component unmount
  yield takeLatest(LOAD_USERS, getUsers);
}
