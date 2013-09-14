package controllers;

import play.data.*;
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

    public static Result newTask() {
        Form<Task> filledForm = taskForm.bindFromRequest();
        if(filledForm.hasErrors()) {
            return badRequest(
                    views.html.index.render(Task.all(), filledForm)
            );
        } else {
            Task.create(filledForm.get());
            return redirect(routes.Application.tasks());
        }    }

    public static Result deleteTask(Long id) {
        Task.delete(id);
        return Results.ok();

    }
}
