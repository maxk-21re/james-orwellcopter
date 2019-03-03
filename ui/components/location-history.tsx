import * as React from "react";
import { connect } from "react-redux";
import { GeologState } from "../reducers/root-reducer";
import { fetchGeologs } from "../actions/root-actions";
import { Grid, GridItem, Panel, Button } from "typed-components";
import {default as ReactMapGL, Marker} from "react-map-gl";
import { Geolog } from "../models/geologs";
import MaterialIcons from "@material/react-material-icon";
import * as moment from "moment";

const mapStateToProps = (state: GeologState) => ({
  isLoading: state.loading,
  error: state.error,
  logs: state.data
})

const dispatchProps = {
  fetchGeologs: fetchGeologs.request
}

type Props = ReturnType<typeof mapStateToProps> & typeof dispatchProps;
type State = {
  hovered: Geolog | undefined
}

class LocationHistory extends React.Component<Props, State> {
  private intervalId: number;
  constructor(props: Props) {
    super(props);

    this.state = {
      hovered: undefined
    }
  }

  componentDidMount() {
    this.props.fetchGeologs();
    this.intervalId = window.setInterval(() => this.props.fetchGeologs(), 60000);
  }

  componentWillUnmount() {
    clearInterval(this.intervalId);
  }

  renderLocationItem(log: Geolog, active: boolean) {
    return (<>
      <Grid columns={[[1, "fr"]]}>
        <GridItem alignItems="center">
          {moment.parseZone(log.timestamp).format("HH:mm")}
        </GridItem>
      </Grid>
      <div />
      <Grid columns={[[1, "fr"]]}>
        <Button color={active ? "primary" : "secondary"} type="blank" onClick={() => this.setState({hovered: log})}>
          <MaterialIcons icon={ active ? "gps_fixed" : "location_searching"}  />
        </Button>
      </Grid>
    </>)
  }

  render() {
    const [first, ...rest] = this.props.logs;
    const {hovered} = this.state;
    const center_lat = hovered != undefined ? hovered.location.latitude : first && first.location.latitude || 13;
    const center_lon = hovered != undefined ? hovered.location.longitude : first && first.location.longitude || 52;

    return(
      <Grid height={[100, "vh"]} width={[100, "vw"]}>
        <GridItem columnStart={1} rowStart={1} zIndex={1}>
          <Grid colSpec="repeat(12, 1fr)" rowSpec="repeat(12, 1fr)" height={[100, "%"]} width={[100, "%"]}>
            <GridItem columnStart={9} columnEnd={12} rowStart={4} rowEnd={12} >
              <Panel header="Logs">
                <Grid rowGap={[20, "px"]} colSpec={"fit-content(100%) 1fr fit-content(100%)"}>
                  {
                    this.props.logs
                        .map(i => this.renderLocationItem(i, hovered && hovered.id == i.id || false))}
                </Grid>
              </Panel>
            </GridItem>
          </Grid>
        </GridItem>
        <GridItem columnStart={1} rowStart={1}>
          <ReactMapGL
            width="100%"
            height="100%"
            longitude={center_lon}
            latitude={center_lat}
            zoom={18}
            mapboxApiAccessToken="pk.eyJ1IjoibWlldHpla290emUiLCJhIjoiY2l4cTgwem9kMDAxejMzcWhtbTZmc3p5biJ9.3rDragM8GFmJwZ44m5y2JA">
            { rest.map( i => <Marker className={`marker ${hovered && hovered.id == i.id ? "marker--active" : ""}`} key={i.id} longitude={i.location.longitude} latitude={i.location.latitude} />) }
            { first && <Marker className={`marker marker--current ${hovered && hovered.id == first.id ? "marker--active" : ""}`} longitude={first.location.longitude} latitude={first.location.latitude} /> }
          </ReactMapGL>
        </GridItem>
      </Grid>
    )
  }
}

export default connect(mapStateToProps, dispatchProps)(LocationHistory)
