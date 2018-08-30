package controllers;

import play.api.Application;
import play.mvc.*;

import play.twirl.api.Content;
import views.html.*;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    @Inject
    Provider<Application> app;

    public Result index() {
        Properties prop;
        try {
            prop = new Properties();
            InputStream is = app.get().classloader().getResourceAsStream("SavedData.properties");
            prop.load(is);
            is.close();

            System.out.println("### Saved file load check ###");
            System.out.println("Check file: " + prop.toString());

            String bNumber = prop.getProperty("bus_number");
            String bBstation = prop.getProperty("station_name");

            if (!bNumber.equals("") && !bBstation.equals("")){
                // TODO: find some way via Play API to redirect 'right' way
                return redirect("http://localhost:9000/test?bNumber="+bNumber+"&bBstation="+bBstation);
            } else {
                System.out.println("No saved properties detected!");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Content html = views.html.search.render();
        return ok(html);
    }

}
