package controllers;

import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.twirl.api.Content;

public class TimetableSearchController extends Controller {

    public Result defaultResponse(){
        Http.Session session = Http.Context.current().session();
        String faculty = session.get("faculty") == null ? "" : session.get("faculty");
        Content html = views.html.timetableSearch.render(faculty);
        return ok(html);
    }

}
