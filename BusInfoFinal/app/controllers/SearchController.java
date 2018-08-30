package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Content;


public class SearchController extends Controller {

    public Result defaultResponse(){

        Content html = views.html.search.render();
        return ok(html);
    }

}
