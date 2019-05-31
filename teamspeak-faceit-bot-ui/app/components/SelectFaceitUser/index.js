/**
 *
 * SelectFaceitUser
 *
 */

import React from "react";
import AsyncSelect from "react-select/lib/Async";
import request from "../../utils/request";
import {call} from "redux-saga";

export function query(inputValue) {
  // Select username from store
  const requestURL = `https://api.faceit.com/search/v1?game=csgo&limit=10&query=${inputValue}`;

  try {
    return request(requestURL);
  } catch (err) {
  }
}

const loadOptions = (inputValue, callback) => {
  query(inputValue).then(response => {
    callback(response.payload.players.results.map(player => {
      return {value: player.nickname, label: player.nickname};
    }));
  });
};
// import PropTypes from 'prop-types';
// import styled from 'styled-components';

const customStyles = {
  option: (base, state) => ({
    ...base,
    color: "black"
  })
};

/* eslint-disable react/prefer-stateless-function */
class SelectFaceitUser extends React.Component {
  state = {inputValue: ""};

  constructor(props) {
    super(props);
    if (this.props.defaultValue) {
      this.defValue = {value: this.props.defaultValue, label: this.props.defaultValue};
    }
  }

  handleChange = (selected) => {
    this.setState({inputValue: selected.value});
    this.props.onSelectFaceitUser(selected.value);
    return selected;
  };

  render() {
    return (
      <div>
        <AsyncSelect
          defaultValue={this.defValue}
          className="async-select"
          styles={customStyles}
          cacheOptions
          loadOptions={loadOptions}
          defaultOptions
          onChange={this.handleChange}
        />
      </div>
    );
  }
}

SelectFaceitUser.propTypes = {};

export default SelectFaceitUser;
