import * as React from "react";
import {connect} from "react-redux";
import {GeologState} from "./reducers/root-reducer";
import {fetchGeologs} from "./actions/root-actions";

const mapStateToProps = (state: GeologState) => ({
  isLoading: state.loading,
  error: state.error
})

const dispatchProps = {
  fetchGeologs: fetchGeologs.request
}

type Props = ReturnType<typeof mapStateToProps> & typeof dispatchProps;
type State = {}
class Dummy extends React.Component<Props, State> {
  render() {
  console.log("rendered")
    return(
      <>
      <button onClick={_ => this.props.fetchGeologs()}>Klick mich</button>
      {this.props.isLoading ? "Ich lade" : ""}
      {this.props.error ? <div>{JSON.stringify(this.props.error)}</div> : null}
      </>
    )
  }
}

export default connect(mapStateToProps, dispatchProps)(Dummy)
