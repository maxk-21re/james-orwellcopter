import * as React from "react";
import { Provider } from "react-redux";
import store from "./store";
import LocationHistory from "./components/location-history";

export default class MainFrame extends React.Component {
  render() {
    return (
      <Provider store={store}>
        <LocationHistory />
      </Provider>
    )
  }
}
