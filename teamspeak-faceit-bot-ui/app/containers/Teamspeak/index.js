/**
 *
 * Teamspeak
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
import makeSelectTeamspeak from './selectors';
import reducer from './reducer';
import saga from './saga';

function new_script(src) {
  return new Promise((resolve, reject) => {
    const script = document.createElement('script');
    script.src = src;
    script.addEventListener('load', () => {
      resolve();
    });
    script.addEventListener('error', e => {
      reject(e);
    });
    document.body.appendChild(script);
  });
}

// Promise Interface can ensure load the script only once.
const tsViewerScript = new_script(
  'https://static.tsviewer.com/short_expire/js/ts3viewer_loader.js',
);

/* eslint-disable react/prefer-stateless-function */
export class Teamspeak extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      status: 'start',
    };
  }
  doLoad = () => {
    const self = this;
    tsViewerScript
      .then(() => {
        self.setState({ status: 'done' });
      })
      .catch(() => {
        self.setState({ status: 'error' });
      });
  };
  render() {
    if (this.state.status === 'start') {
      this.state.status = 'loading';
      setTimeout(() => {
        this.doLoad();
      }, 0);
    }

    if (this.state.status === 'done') {
      const url =
        'https://www.tsviewer.com/ts3viewer.php?ID=1096083&text=d6d6d6&text_size=12&text_family=1&text_s_color=ffffff&text_s_weight=normal&text_s_style=normal&text_s_variant=normal&text_s_decoration=none&text_i_color=&text_i_weight=normal&text_i_style=normal&text_i_variant=normal&text_i_decoration=none&text_c_color=&text_c_weight=normal&text_c_style=normal&text_c_variant=normal&text_c_decoration=none&text_u_color=ffffff&text_u_weight=normal&text_u_style=normal&text_u_variant=normal&text_u_decoration=none&text_s_color_h=&text_s_weight_h=bold&text_s_style_h=normal&text_s_variant_h=normal&text_s_decoration_h=none&text_i_color_h=ffffff&text_i_weight_h=bold&text_i_style_h=normal&text_i_variant_h=normal&text_i_decoration_h=none&text_c_color_h=&text_c_weight_h=normal&text_c_style_h=normal&text_c_variant_h=normal&text_c_decoration_h=none&text_u_color_h=&text_u_weight_h=bold&text_u_style_h=normal&text_u_variant_h=normal&text_u_decoration_h=none&iconset=default_colored_2014';
      window.ts3v_display.init(url, 1096083, 100);
    }

    return (
      <div>
        <Helmet>
          <title>Teamspeak</title>
          <meta name="description" content="Description of Teamspeak" />
        </Helmet>
        <div>
          <div className="header">
            <h1>Check current teamspeak status:</h1>
          </div>
          <div className="wrapper">
            <div id="ts3viewer_1096083" style={{ padding: 25 }} />
          </div>
        </div>
      </div>
    );
  }
}

Teamspeak.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  teamspeak: makeSelectTeamspeak(),
});

function mapDispatchToProps(dispatch) {
  return {
    dispatch,
  };
}

const withConnect = connect(
  mapStateToProps,
  mapDispatchToProps,
);

const withReducer = injectReducer({ key: 'teamspeak', reducer });
const withSaga = injectSaga({ key: 'teamspeak', saga });

export default compose(
  withReducer,
  withSaga,
  withConnect,
)(Teamspeak);
