import org.scalatestplus.play._
import com.vividsolutions.jts.geom.{GeometryFactory, Coordinate}
import play.api.libs.json._
import models._

class ClusterTest extends PlaySpec {

  "A Shell" must {
    "be parsable to JSON" in {
      val shell = Shell(
        new GeometryFactory().createPolygon(
          (
            new Coordinate(0, 0) ::
            new Coordinate(1, 0) ::
            new Coordinate(1, 1) ::
            new Coordinate(2, 1) ::
            new Coordinate(1, 2) ::
            new Coordinate(0, 2) ::
            new Coordinate(0, 1) ::
            new Coordinate(0, 0) ::
            Nil
          ).toArray))

      Json.toJson(shell) mustBe Json.parse("""[[0,0],[1,0],[1,1],[2,1],[1,2],[0,2],[0,1],[0,0]]""")
    }
  }

}
