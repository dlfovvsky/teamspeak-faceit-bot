import React from 'react';

// const Wrapper = styled.div`
//   margin: 2em auto;
//   width: 40px;
//   height: 40px;
//   position: relative;
// `;

export default function Page(props) {
  return (
    <div>
      <div className="header">
        {props.header ? <h1>{props.header}</h1> : <h1/> }
      </div>
      <div className="wrapper">{props.children}</div>
    </div>
  );
}
