import * as React from "react";
import { Provider } from "react-redux";
import store from "./store";
import LocationHistory from "./components/location-history";
import Dummy from "./dummy";

export default class MainFrame extends React.Component {
  render() {
    return (
      <Provider store={store}>
        <Dummy />
      </Provider>
    )
  }
}
