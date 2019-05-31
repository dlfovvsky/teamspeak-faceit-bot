import React from 'react';
import { FormattedMessage } from 'react-intl';

import A from './A';
import Img from './Img';
import NavBar from './NavBar';
import HeaderLink from './HeaderLink';
import Banner from './banner.jpg';
import messages from './messages';
import { Link } from 'react-router-dom';
import environment from 'environment';

/* eslint-disable react/prefer-stateless-function */
class Header extends React.Component {
  render() {
    return (
      <div>
        <nav className="navbar px-navbar">
          <button
            type="button"
            className="navbar-toggle collapsed"
            data-toggle="collapse"
            data-target="#px-navbar-using-with-nav-collapse"
            aria-expanded="false"
          >
            <i className="navbar-toggle-icon" />
          </button>
          <div
            className="navbar-collapse collapse"
            id="px-navbar-using-with-nav-collapse"
            aria-expanded="false"
          >
            <ul className="nav navbar-nav">
              <li>
                <Link className="navbar-brand" to="">TS BOT</Link>
              </li>
              <li>
                <Link to="/faceit">Faceit</Link>
              </li>
              <li>
                <Link to="/teamspeak">Teamspeak</Link>
              </li>
              <li>
                <Link to="/user-stats">Faceit Stats</Link>
              </li>
            </ul>
            <ul className="nav navbar-nav navbar-right">
              <li className="dropdown">
                <a
                  href={environment.api + "/steam/steam-auth"}
                  className="dropdown-toggle"
                  data-toggle="dropdown"
                  role="button"
                  aria-haspopup="true"
                  aria-expanded="false"
                >
                  Zaloguj się
                </a>
              </li>
            </ul>
          </div>
        </nav>
        {/* <NavBar> */}
        {/* <HeaderLink to="/"> */}
        {/* <FormattedMessage {...messages.home} /> */}
        {/* </HeaderLink> */}
        {/* <HeaderLink to="/features"> */}
        {/* <FormattedMessage {...messages.features} /> */}
        {/* </HeaderLink> */}
        {/* </NavBar> */}
      </div>
    );
  }
}

export default Header;
