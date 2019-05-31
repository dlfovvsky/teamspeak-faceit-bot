import React, { Fragment } from 'react';
import PropTypes from 'prop-types';

import List from 'components/List';
import ListItem from 'components/ListItem';
import LoadingIndicator from 'components/LoadingIndicator';
import moment from 'moment';
import FaceitIcon from '../../images/FACEIT.png';
import SteamIcon from '../../images/steam.png';
import {Link} from "react-router-dom";

/**
 * @return {null}
 */
function UsersList({ loading, error, users }) {
  if (loading) {
    return <List component={LoadingIndicator} />;
  }

  if (error !== false) {
    const ErrorComponent = () => (
      <ListItem item="Something went wrong, please try again!" />
    );
    return <List component={ErrorComponent} />;
  }

  if (users !== false) {
    const list = users.map((user, idx) => (
      <tr key={`user-${user.id}`}>
        <td>{idx + 1}</td>
        <td>{user.faceitClient.nickname}</td>
        <td>
          {user.teamspeakClient.online ? (
            <i style={{ color: 'green' }} className="fa fa-check" />
          ) : (
            <i className="fa fa-times" />
          )}
        </td>
        <td>
          {user.faceitClient.playing ? (
            <Fragment>
              <i style={{ color: 'green' }} className="fa fa-check" />
              <a
                target="_blank"
                className="btn btn-success"
                style={{ marginLeft: 15 }}
                href={`https://www.faceit.com/en/csgo/room/${
                  user.faceitClient.match_id
                }`}
                role="button"
              >
                mecz
              </a>
            </Fragment>
          ) : (
            <Fragment>
              <i className="fa fa-times" />
            </Fragment>
          )}
        </td>
        <td>{user.faceitClient.lvl}</td>
        <td>{user.faceitClient.elo}</td>
        <td>{nextLvl(user.faceitClient.lvl, user.faceitClient.elo)}</td>
        <td>{fromNow(user.faceitClient.lastGame)}</td>
        <td style={{ fontWeight: 800, letterSpacing: 2 }}>
          <span>
            {lastResults(user.faceitClient.lastGames)}
          </span>
        </td>
        <td>
          {synced(user.faceitClient.lastRefreshDate) ? (
            <i style={{ color: 'green' }} className="fa fa-check" />
          ) : (
            <i className="fa fa-times" />
          )}
        </td>
        <td>
          <a href={`http://steamcommunity.com/profiles/${user.steam64Id}`}>
            <img alt="steam" src={SteamIcon} />
          </a>
          <a
            style={{ marginLeft: 10 }}
            href={`https://www.faceit.com/en/players/${
              user.faceitClient.nickname
            }`}
          >
            <img alt="faceit" src={FaceitIcon} />
          </a>
          <Link  style={{ marginLeft: 10 }} to={`/user-stats/${user.faceitClient.nickname}`}>Faceit Stats</Link>

        </td>
      </tr>
    ));
    return (
      <div>
        <table className="table">
          <thead>
            <tr>
              <th>Rank</th>
              <th>faceit nickname</th>
              <th>jest ts?</th>
              <th>rozgrywa gre?</th>
              <th>faceit lvl</th>
              <th>faceit elo</th>
              <th>faceit next</th>
              <th>last game</th>
              <th>Recent results</th>
              <th>Sync</th>
              <th>linki</th>
            </tr>
          </thead>
          <tbody>{list}</tbody>
        </table>
      </div>
    );
  }

  return null;
}

export function lastResults(list) {
  return list.map(
    (lastGame, i) =>
      lastGame === '1' ? (
        <span key={`user-${i}`} style={{ color: '#3c763d' }}>W</span>
      ) : (
        <span key={`user-${i}`} style={{ color: '#c15856' }}>L</span>
      ),
  )
}
export function nextLvl(lvl, elo) {
  if (lvl.toString() === '10') return null;
  const lvls = {
    10: 2001,
    9: 1851,
    8: 1701,
    7: 1551,
    6: 1401,
    5: 1251,
    4: 1101,
    3: 951,
    2: 801,
  };
  const nxtLVL = Number(lvl) + 1;
  const eloToNext = lvls[nxtLVL] - elo;
  return `${eloToNext} elo to lvl:${nxtLVL}`;
}

function synced(value) {
  const diff = -moment(value).diff(moment.now());
  const seconds = 120 * 1000;
  return diff < seconds;
}

function fromNow(value) {
  if (value) {
    return moment(value).fromNow();
  }
  return null;
}

UsersList.propTypes = {
  loading: PropTypes.bool,
  error: PropTypes.any,
  users: PropTypes.any,
};

export default UsersList;
