package edwardlee.todolist;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import static edwardlee.todolist.ToDoListActivity.REQ_CODE;

public class addToList extends AppCompatActivity {
    public final static String TASK_MESSAGE = "com.edwardlee.todolist.TASK_MESSAGE";
    public final static String DATE_MESSAGE = "com.edwardlee.todolist.DATE_MESSAGE";
    @Override
    // Run when activity created
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_list);
    }

    // Adds the to-do to the to-do list on the todolist activity
    // Called when 'Add' button is clicked in activity_add_to_list.xml
    public void addTodo(View view) {
        // Get elements
        EditText taskTodo = (EditText) findViewById(R.id.task_todo);

        String sDay = ((EditText) findViewById(R.id.due_day)).getText().toString();
        String sMonth = ((EditText) findViewById(R.id.due_month)).getText().toString();
        String sYear = ((EditText) findViewById(R.id.due_year)).getText().toString();

        // Create intent
        Intent intent = new Intent();

        // Get task and put into intent
        String taskToSend = taskTodo.getText().toString();
        intent.putExtra(TASK_MESSAGE, taskToSend);
        // If date was input, put into intent
        if(!isDateEmpty(sYear, sMonth, sDay)) {
            if (!checkDate(sYear, sMonth, sDay)) {
                return;
            } else {
                intent.putExtra(DATE_MESSAGE, toDate(sYear, sMonth, sDay));
            }
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    // Displays date picker if calendar button is clicked
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");

    }

    // Makes sure month and day are standardized to 2 digits
    // e.g. 1995-7-9 --> 1995-07-09
    private String toDate(String year, String month, String day) {
        if (month.length() == 0) {
            month = "0" + month;
        }
        if (day.length() == 0) {
            day = "0" + day;
        }
        return year + "-" + month + "-" + day;
    }

    // Checks if the date is empty
    private boolean isDateEmpty(String year, String month, String day) {
        // either all empty or no empty
        boolean eday = (day.length() == 0);
        boolean emonth = (month.length() == 0);
        boolean eyear = (year.length() == 0);
        return (eday && emonth && eyear);
    }

    // Checks if the date entered into addToList is valid
    // Validity determined by date/month/year-length
    private boolean checkDate(String year, String month, String day) {

        // valid length days/months/years (as valid as I want to go...)
        boolean bday = (day.length() == 1 || day.length() == 2);
        boolean bmonth = (month.length() == 1 || month.length() == 2);
        boolean byear = (year.length() == 4);

        return (bday && bmonth && byear);
    }
}
