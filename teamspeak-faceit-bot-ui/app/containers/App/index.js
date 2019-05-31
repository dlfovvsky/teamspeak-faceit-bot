/**
 *
 * App
 *
 * This component is the skeleton around the actual pages, and should only
 * contain code that should be seen on all pages. (e.g. navigation bar)
 */

import React from 'react';

import { Helmet } from 'react-helmet';
import styled from 'styled-components';
import { Route, Switch } from 'react-router-dom';
import FeaturePage from '../FeaturePage';
import FaceitConnected from '..//Faceit/Loadable';
import NotFoundPage from 'containers/NotFoundPage/Loadable';
import Footer from '../../components/Footer';
import Header from '../../components/Header';
import Teamspeak from '../Teamspeak/Loadable';
import { HomePageConnected } from '../HomePage';
import LoadingIndicator from '../../components/LoadingIndicator';
import UserStatsConnected from "../UserStats";

const AppWrapper = styled.div``;

export default function App() {
  return (
    <AppWrapper>
      <Helmet titleTemplate="%s - Teamspeak Faceit Bot" defaultTitle="Teamspeak Faceit Bot">
        <meta name="description" content="Teamspeak Faceit Bot" />
      </Helmet>

      <Header />
      <div className="px-content">
        <div>
          <Switch>
            <Route exact path="/" component={FaceitConnected} />
            <Route path="/features" component={FeaturePage} />
            <Route path="/faceit" component={FaceitConnected} />
            <Route path="/teamspeak" component={Teamspeak} />
            <Route path="/user-stats/:nickname?/:lastGames?" component={UserStatsConnected} />
            <Route path="/loader" component={LoadingIndicator} />
            <Route path="" component={NotFoundPage} />
          </Switch>
        </div>
      </div>
      <br />
      <Footer />
    </AppWrapper>
  );
}
