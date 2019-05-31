/**
 *
 * Faceit
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { Helmet } from 'react-helmet';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import injectSaga from 'utils/injectSaga';
import injectReducer from 'utils/injectReducer';
import reducer from './reducer';
import saga from './saga';
import { makeSelectFaceit } from '../App/selectors';
import { loadUsers } from '../App/actions';
import Page from '../../components/Page/Page';
import UsersList from '../../components/UsersList';

/* eslint-disable react/prefer-stateless-function */
class Faceit extends React.PureComponent {
  componentDidMount() {
    this.props.onLoadUsers();
  }

  render() {
    console.log(this.props);
    const { loading, error, users } = this.props.faceit;
    const usersListProps = {
      loading,
      error,
      users,
    };

    console.log(usersListProps);

    return (
      <div>
        <Helmet>
          <title>Faceit</title>
          <meta name="description" content="Description of Faceit" />
        </Helmet>
        <Page>
          <div className="m-y-4 text-md-center">
            <button
              onClick={this.props.onLoadUsers}
              className="btn btn-outline"
            >
              <i className="fa fa-refresh" />
            </button>
          </div>
          <div>Liczba uzytkownikow: {users.length}</div>
          <UsersList {...usersListProps} />
        </Page>
      </div>
    );
  }
}

Faceit.propTypes = {
  faceit: PropTypes.oneOfType([PropTypes.any]),
  onLoadUsers: PropTypes.func,
};

const mapStateToProps = createStructuredSelector({
  faceit: makeSelectFaceit(),
});

export function mapDispatchToProps(dispatch) {
  return {
    onLoadUsers: evt => {
      if (evt !== undefined && evt.preventDefault) evt.preventDefault();
      dispatch(loadUsers());
    },
  };
}

const withConnect = connect(
  mapStateToProps,
  mapDispatchToProps,
);

const withReducer = injectReducer({ key: 'faceit', reducer });
const withSaga = injectSaga({ key: 'faceit', saga });

export const FaceitConnected = compose(
  withReducer,
  withSaga,
  withConnect,
)(Faceit);

export default FaceitConnected;
