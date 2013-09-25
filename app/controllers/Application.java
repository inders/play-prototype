package controllers;

import org.codehaus.jackson.node.ObjectNode;
import play.data.*;
import play.libs.Json;
import play.mvc.*;

import models.Task;

public class Application extends Controller {

    static play.data.Form<Task> taskForm = Form.form(Task.class);

    public static Result index() {
      return TODO;
    }


    public static Result tasks() {
        return Results.ok(
                views.html.index.render(Task.all(), taskForm)
        );
    }

    private class QueryResponse {
        String key = "result";
        String response = "inder";

        private QueryResponse(String key, String response) {
            this.key = key;
            this.response = response;
        }

        private String getKey() {
            return key;
        }

        private String getResponse() {
            return response;
        }
    }

    public static Result query(String query){

        System.out.println("Query " + query);
        System.out.println("Response " + Json.toJson("inder"));
        ObjectNode result = Json.newObject();
        if (query.contains("inmobi")) {
          result.put("Response", "inmobi solutions private limited");
        } else  if (query.contains("google")) {
            result.put("Response", "Google Inc");

        }


        return Results.ok(Json.toJson(result));
    }

    public static Result newTask() {
        Form<Task> filledForm = taskForm.bindFromRequest();
        if(filledForm.hasErrors()) {
            return badRequest(
                    views.html.index.render(Task.all(), filledForm)
            );
        } else {
         //   Task.create(filledForm.get());
            return redirect(routes.Application.tasks());
        }    }

    public static Result deleteTask(Long id) {
        Task.delete(id);
        return Results.ok();

    }
}
