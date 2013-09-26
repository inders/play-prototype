package controllers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.codehaus.jackson.node.ObjectNode;
import play.data.*;
import play.libs.Json;
import play.mvc.*;

import models.Task;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Application extends Controller {

    static play.data.Form<Task> taskForm = Form.form(Task.class);
    static Multimap<String, String> fsaMap = HashMultimap.create();
    //Contains previous state to current state mapping for META states
    //eg:   >, < -> $num
    static Map<String, String> metaSateMap = new HashMap<>();


    public static Result index() {
        return TODO;
    }

    static  {
        fsaMap.put("user", "with");
        fsaMap.put("with", "age");
        fsaMap.put("with", "gender");
        fsaMap.put("gender", "male");
        fsaMap.put("gender", "female");
        fsaMap.put("age", ">");
        fsaMap.put("age", "<");

        fsaMap.put("$num", "and");
        fsaMap.put("$num", "or");
        fsaMap.put("male", "and");
        fsaMap.put("female", "and");
        fsaMap.put("male", "or");
        fsaMap.put("female", "or");
        fsaMap.put("and", "age");
        fsaMap.put("and", "gender");
        fsaMap.put("or", "age");
        fsaMap.put("or", "gender");
        fsaMap.put("female", "who");
        fsaMap.put("male", "who");
        fsaMap.put("$num", "who");

        //start edge Type here
        fsaMap.put("who", "visit");
        fsaMap.put("who", "own");
        fsaMap.put("who", "livein");

        //fsaMap.put("visit", "$site");
        //fsaMap.put("own", "$device");
        //fsaMap.put("livein", "$location");

        fsaMap.put("$site", "AND");
        fsaMap.put("$site", "having");

        fsaMap.put("$device", "AND");
        fsaMap.put("$device", "having");

        fsaMap.put("$location", "AND");
        fsaMap.put("$location", "having");


        fsaMap.put("$site", "OR");
        fsaMap.put("$device", "OR");
        fsaMap.put("$location", "OR");

        fsaMap.put("AND", "visit");
        fsaMap.put("AND", "own");
        fsaMap.put("AND", "livein");

        fsaMap.put("OR", "visit");
        fsaMap.put("OR", "own");
        fsaMap.put("OR", "livein");

        fsaMap.put("having", "request");
        fsaMap.put("having", "impressions");
        fsaMap.put("having", "click");

        fsaMap.put("request", "greater");
        fsaMap.put("request", "less");

        fsaMap.put("impressions", "greater");
        fsaMap.put("impressions", "less");

        fsaMap.put("click", "greater");
        fsaMap.put("click", "less");

        fsaMap.put("$ATTRNUM", "(and)");
        fsaMap.put("$ATTRNUM", "(or)");
        fsaMap.put("$ATTRNUM", "IN");


        fsaMap.put("(and)", "request");
        fsaMap.put("(and)", "click");
        fsaMap.put("(and)", "impressions");

        fsaMap.put("(or)", "request");
        fsaMap.put("(or)", "click");
        fsaMap.put("(or)", "impressions");

        fsaMap.put("IN", "last 1 day");
        fsaMap.put("IN", "last 2 day");
        fsaMap.put("IN", "last 3 day");



        metaSateMap.put(">", "$num");
        metaSateMap.put("<", "$num");

        metaSateMap.put("greater", "$ATTRNUM") ;
        metaSateMap.put("less", "$ATTRNUM") ;


        metaSateMap.put("visit", "$site");
        metaSateMap.put("own", "$device");
        metaSateMap.put("livein", "$location");




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
        ObjectNode result = Json.newObject();

        //Split the input query each word is a state
        String[] states = query.split(" ");

        //check the last state in machine
        String lastState = states[states.length - 1];
        System.out.println("Finding last state " +  lastState + " in map ");
        Collection<String> nextStates = fsaMap.get(lastState);
        if (nextStates == null || nextStates.size() == 0) {
            //we have reached a META state which can't be found so we have lookup previous state and jump
            // to meta state
            String previousState = states[states.length - 2];
            String currentState = metaSateMap.get(previousState.trim());
            System.out.println("Reached meta-state for last state " + lastState);
            System.out.println(" found meta state using previous State [" + previousState + "] meta state [" + currentState + "]");
            nextStates = fsaMap.get(currentState);
        }
        System.out.println("for user " + fsaMap.get("user"));
        //make alternate responses for all possible nextStates

        Integer i=0;
        for (String state : nextStates) {
            result.put("Response" + i.toString(), new String(query + " " + state) );
            i++;
        }

        /*if (query.trim().equalsIgnoreCase("inmobi")) {
            result.put("Response", "inmobi solutions");
        } else  if (query.trim().trim().equalsIgnoreCase("Google")) {
            result.put("Response", "Google Inc");
        } else if (query.trim().equalsIgnoreCase("Google Inc")) {
            result.put("Response", "Google Inc formed in year");
            result.put("Response1", "Google Inc great company");

        } else if (query.trim().equalsIgnoreCase("inmobi solutions")) {
            result.put("Response", "founded by naveen");

        }
        */


        return Results.ok(Json.toJson(result));
    }

    private static void init() {

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
