package computerdatabase

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration._

class DropwizardSimulation extends Simulation {

  val httpConf = http
    .baseURL("http://localhost:8080") // Here is the root for all relative URLs
    .acceptHeader("application/json,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    //.doNotTrackHeader("1")
    //.acceptLanguageHeader("en-US,en;q=0.5")
    //.acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val headers_10 = Map("Content-Type" -> """application/json""")

  private val scn: ScenarioBuilder = scenario("Projects")
    .exec(http("Create project first project")
      .post("/api/projects")
      .body(StringBody("""{ "title": "The project", "description": "This is the project description" }""")
    ).check(
      jsonPath("$.id")
        .saveAs("project_1_id")
    )
    .asJSON)
    .exec(http("Get all project")
      .get("/api/projects"))
    .pause(100 millisecond)
    .exec(http("Create project second project")
      .post("/api/projects")
      .body(StringBody("""{ "title": "The project", "description": "This is the project description" }""")
    ).check(
      jsonPath("$.id")
      .saveAs("project_2_id")
    ).asJSON)
    .pause(100 millisecond)
    .exec(http("Get project 1")
      .get("/api/projects/${project_1_id}"))
    .pause(100 millisecond)
    .exec(http("Create story 1 project 1")
      .post("/api/stories")
      .body(StringBody("""{ "title": "The project", "description": "This is the project description", "project": { "id": ${project_1_id} }}""")
    ).check(
      jsonPath("$.id")
      .saveAs("story_1_id")
    ).asJSON)
    .exec(http("Get project 2")
      .get("/api/projects/${project_2_id}"))
    .exec(http("Create story 1 project 2")
    .post("/api/stories")
    .body(StringBody("""{ "title": "The project", "description": "This is the project description", "project": { "id": ${project_2_id}}}""")
    ).check(
      jsonPath("$.id")
        .saveAs("story_1_id")
    ).asJSON)
    .pause(100 millisecond)
    .exec(http("Get unknown project")
      .get("/api/projects/9999").check(status.is(404)))


  setUp(scn.inject(rampUsers(100) over (30 seconds)).protocols(httpConf))
}
