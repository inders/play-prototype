package models;

import play.data.validation.Constraints;

import java.util.ArrayList;
import java.util.List;
import play.db.ebean.*;

import javax.persistence.Entity;
import javax.persistence.Id;


/**
 * Created with IntelliJ IDEA.
 * User: inderbir.singh
 * Date: 15/09/13
 * Time: 2:44 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Task extends Model{

    @Id
    public Long id;
    @Constraints.Required
    public String label;

    public static Model.Finder<Long,Task> find = new Model.Finder<Long, Task>(
            Long.class, Task.class
    );

    public static List<Task> all() {
        return find.all();
    }

    public static void create(Task task) {
        task.save();
    }

    public static void delete(Long id) {
        find.ref(id).delete();
   }



}
