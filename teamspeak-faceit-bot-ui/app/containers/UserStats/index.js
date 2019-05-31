/**
 *
 * UserStats
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import {Helmet} from 'react-helmet';
import {createStructuredSelector} from 'reselect';
import {compose} from 'redux';

import injectSaga from 'utils/injectSaga';
import injectReducer from 'utils/injectReducer';
import makeSelectUserStats from './selectors';
import reducer from './reducer';
import saga from './saga';
import {loadUserStats} from './actions';
import Page from '../../components/Page/Page';
import LoadingIndicator from '../../components/LoadingIndicator';
import ListItem from '../../components/ListItem';
import List from '../../components/List';
import {lastResults, nextLvl} from '../../components/UsersList';
import moment from 'moment';
import SelectFaceitUser from '../../components/SelectFaceitUser';
import FaceitIcon from '../../images/FACEIT.png';

/* eslint-disable react/prefer-stateless-function */
class UserStats extends React.Component {
  options = [
    {value: 'chocolate', label: 'Chocolate'},
    {value: 'strawberry', label: 'Strawberry'},
    {value: 'vanilla', label: 'Vanilla'},
  ];
  lastGames = 20;

  constructor(props) {
    super(props);
    console.log(props);
    this.onSelectFaceitUser = this.onSelectFaceitUser.bind(this);
  }

  componentDidMount() {
    if (this.props.match.params.lastGames) {
      this.lastGames = this.props.match.params.lastGames;
    } else {
      this.lastGames = 20;
    }
    if (this.props.match.params.nickname) {
      this.props.onLoadUserStats(this.props.match.params.nickname, this.lastGames);
    }
    // console.log(this.props);
  }

  onSelectFaceitUser(event) {
    console.log('onSelectFaceitUser: ' + event);
    if (event) {
      this.props.onLoadUserStats(event, this.lastGames);
      this.props.history.push('/user-stats/' + event + '/' + this.lastGames);
    }
  }

  render() {


    const {loading, error} = this.props.userstats;
    if (loading) {
      return (
        <div>
          <Helmet>
            <title>UserStats</title>
            <meta name="description" content="Description of UserStats"/>
          </Helmet>
          <Page>
            <SelectFaceitUser defaultValue={this.props.match.params.nickname}
                              onSelectFaceitUser={this.onSelectFaceitUser}/>
            <List component={LoadingIndicator}/>
          </Page>
        </div>
      );
    }

    if (error !== false) {
      const ErrorComponent = () => (
        <ListItem item="Something went wrong, please try again!"/>
      );
      return <List component={ErrorComponent}/>;
    }

    const StatsComponent = () => {
      if (this.props.userstats.userStats) {
        const {playerInfo, playerStats, playerTimeStats, recent} = this.props.userstats.userStats;
        const nextLevel = nextLvl(playerInfo.games.csgo.skill_level, playerInfo.games.csgo.faceit_elo);
        const p1 = panel([
          {label: 'NICKNAME', value: playerInfo.nickname},
          {label: 'LEVEL', value: playerInfo.games.csgo.skill_level},
          {label: 'ELO', value: playerInfo.games.csgo.faceit_elo},
          {label: 'NEXT', value: nextLevel ? nextLevel : 'max level reached'},
        ]);

        const p2 = panel([
          {label: 'K/D', value: playerStats.lifetime.k5},
          {label: 'HEADSHOTS %', value: playerStats.lifetime.k8},
          {label: 'WIN RATE %', value: playerStats.lifetime.k6},
          {label: 'MATCHES', value: playerStats.lifetime.m1},
        ]);
        const lastkad = `${recent.avg_i6}-${recent.avg_i7}-${recent.avg_i8} [${recent.avg_c2}]`
        const p3 = panel([
          {label: `LAST ${this.lastGames} K-A-D`, value: lastkad},
          {label: `LAST ${this.lastGames} HEADSHOTS %`, value: recent.avg_c4},
          {label: `LAST ${this.lastGames} WIN RATE %`, value: recent.avg_i10},
          {label: `RECENT`, value: lastResults(playerStats.lifetime.s0)},
        ]);
        return (
          <div style={{paddingTop: 10}}>
            {p1}
            {p2}
            {p3}
            {tableHistory(playerTimeStats)}
          </div>
        );
      }
      return null;
    };

    return (
      <div>
        <Helmet>
          <title>UserStats</title>
          <meta name="description" content="Description of UserStats"/>
        </Helmet>
        <Page>
          <SelectFaceitUser defaultValue={this.props.match.params.nickname}
                            onSelectFaceitUser={this.onSelectFaceitUser}/>
          <StatsComponent/>
        </Page>
      </div>
    );
  }
}

function tableHistory(list) {
  const tbody = list.map((element, idx) => {
    return (
      <tr key={idx}>
        <td>{moment(element.date).format('DD/MM/YYYY HH:mm:ss')}</td>
        <td style={{fontWeight: 800}}>{
          element.i10 === '1' ?
            <span style={{color: '#3c763d'}}>W [+{element.i12}]</span> :
            <span style={{color: '#c15856'}}>L [-{element.i12}]</span>}
        </td>
        <td>{element.i18}</td>
        <td>{element.i1}</td>
        <td>{element.elo}</td>
        <td style={{fontWeight: 800}}>{
          element.c2 >= '1' ?
            <span style={{color: '#3c763d'}}>{element.i6}-{element.i7}-{element.i8} [{element.c2}]</span> :
            <span style={{color: '#c15856'}}>{element.i6}-{element.i7}-{element.i8} [{element.c2}]</span>}
        </td>
        <td>{element.c4}%</td>
        <td>
          <a target="_blank" href={`https://www.faceit.com/en/csgo/room/${element.matchId}`} role="button">
            <img alt="faceit" src={FaceitIcon}/>
          </a>
        </td>
      </tr>
    );
  });
  return (
    <table className="table">
      <thead>
      <tr>
        <th rowSpan="2">Date</th>
        <th rowSpan="2">Result</th>
        <th rowSpan="2">Score</th>
        <th rowSpan="2">Map</th>
        <th rowSpan="2">Elo</th>
        <th>K-A-D</th>
        <th>HS%</th>
        <th rowSpan="2">Link</th>
      </tr>
      </thead>
      <tbody>{tbody}</tbody>
    </table>
  );
}

function panel(list) {
  return (
    <div className="panel box">
      <div className="box-row">
        <div className="box-container valign-middle text-xs-center">
          {list.map((element, idx) => {
            return (
              <div key={idx} className="box-cell p-y-1 b-r-1">
                <div className="font-size-17"><strong>{element.value}</strong></div>
                <div className="font-size-11">{element.label}</div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}

UserStats.propTypes = {
  dispatch: PropTypes.func.isRequired,
  onLoadUserStats: PropTypes.func,
};

const mapStateToProps = createStructuredSelector({
  userstats: makeSelectUserStats(),
});

function mapDispatchToProps(dispatch) {
  return {
    dispatch,
    onLoadUserStats: (evt, lastGames) => {
      if (evt !== undefined && evt.preventDefault) evt.preventDefault();
      dispatch(loadUserStats(evt, lastGames));
    },
  };
}

const withConnect = connect(
  mapStateToProps,
  mapDispatchToProps,
);

const withReducer = injectReducer({key: 'userStats', reducer});
const withSaga = injectSaga({key: 'userStats', saga});

const UserStatsConnected = compose(
  withReducer,
  withSaga,
  withConnect,
)(UserStats);
export default UserStatsConnected;
