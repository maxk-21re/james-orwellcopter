import * as React from "react";
import { connect } from "react-redux";
import { GeologState } from "../reducers/root-reducer";
import { fetchGeologs } from "../actions/root-actions";
import { Grid, GridItem } from "typed-components";
import ReactMapGL from "react-map-gl";

const mapStateToProps = (state: GeologState) => ({
  isLoading: state.loading,
  error: state.error
})

const dispatchProps = {
  fetchGeologs: fetchGeologs.request
}

type Props = ReturnType<typeof mapStateToProps> & typeof dispatchProps;
type State = {}

class LocationHistory extends React.Component<Props, State> {

  componentDidMount() {
    this.props.fetchGeologs();
  }

  render() {
    return(
      <Grid>
        <GridItem columnStart={1} rowStart={1}>
          <ReactMapGL
            width="100%"
            height="100%"
            mapboxApiAccessToken="pk.eyJ1IjoibWlldHpla290emUiLCJhIjoiY2l4cTgwem9kMDAxejMzcWhtbTZmc3p5biJ9.3rDragM8GFmJwZ44m5y2JA"
          />
        </GridItem>
      </Grid>
    )
  }
}

export default connect(mapStateToProps, dispatchProps)(LocationHistory)
