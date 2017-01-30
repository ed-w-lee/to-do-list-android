package edwardlee.todolist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * Edward Lee <edlee1@stanford.edu>
 * CS 193A, Winter 2017 (instructor: Marty Stepp)
 * Homework Assignment 2
 * ToDoList - This app creates a to-do list with tasks and due dates.
 * It also sorts based on due dates.
 */
public class ToDoListActivity extends AppCompatActivity {

    /**
     * Referenced http://stackoverflow.com/questions/4587301/android-adding-subitem-to-a-listview
     * for how to change format of ListView
     */
    // Item in the list that contains name of the to-do task and the due date
    class TodoListItem implements Comparable<TodoListItem> {
        private String todoListName;
        private String todoListDueDate;

        // Getters and Setters
        String getTodoListName() {
            return todoListName;
        }

        public void setTodoListName(String name) {
            todoListName = name;
        }

        String getTodoListDueDate() {
            return todoListDueDate;
        }

        public void setTodoListDueDate(String dueDate) {
            todoListDueDate = dueDate;
        }

        // Constructor
        TodoListItem(String name, String dueDate) {
            todoListName = name;
            todoListDueDate = dueDate;
        }

        // So that we can sort based on due date
        public int compareTo(TodoListItem other) {
            String date1 = this.getTodoListDueDate();
            String date2 = other.getTodoListDueDate();
            boolean notDate1 = (date1.equals("No date"));
            boolean notDate2 = (date2.equals("No date"));
            if (notDate1 && notDate2) {
                return 0;
            } else if (notDate1) {
                return 1;
            } else if (notDate2) {
                return -1;
            } else {
                for (int i = 0; i < date1.length(); i++) {
                    if (date1.charAt(i) == date2.charAt(i)) continue;
                    return (date1.charAt(i) - date2.charAt(i));
                }
                return 0;
            }
        }
    }

    /**
     * Controller between todo_list in xml and toDoList arraylist
     * Makes sure data in arraylist gets manifested in the view
     */
    public class TodoListAdapter extends ArrayAdapter<TodoListItem> {
        private ArrayList<TodoListItem> items;
        private TodoListViewHolder todoListHolder;

        private class TodoListViewHolder {
            TextView name;
            TextView dueDate;
        }

        TodoListAdapter(Context context, int tvResId, ArrayList<TodoListItem> items) {
            super(context, tvResId, items);
            this.items = items;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                v = li.inflate(R.layout.to_do_list_item, null);
                todoListHolder = new TodoListViewHolder();
                todoListHolder.name = (TextView) v.findViewById(R.id.todo_name);
                todoListHolder.dueDate = (TextView) v.findViewById(R.id.todo_due_date);
                v.setTag(todoListHolder);
            } else {
                todoListHolder = (TodoListViewHolder)v.getTag();
            }

            TodoListItem todoListItem = items.get(pos);

            if (todoListItem != null) {
                if (todoListHolder.name != null) todoListHolder.name.setText(todoListItem.getTodoListName());
                if (todoListHolder.dueDate != null) todoListHolder.dueDate.setText(todoListItem.getTodoListDueDate());
            }

            return v;
        }

    }

    private static final String todoListFile = "com.edwardlee.todolist.todoList.txt";
    public static final int REQ_CODE = 1453;

    private ArrayList<TodoListItem> todoList = new ArrayList<>();

    @Override
    // Called when activity is created
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        // Get saved todoList
        addFileToTodoList();
        updateList();

        // Update list view
        ListView list = (ListView) findViewById(R.id.todo_list);

        // Set up delete on long-click
        list.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent,
                                                   View view, int position, long id) {
                        todoList.remove(position);
                        whenTodoListEmpty();
                        updateList();
                        return true;
                    }
                }
        );
    }

    @Override
    // Not really sure how helpful it is, but I don't want to go through the trouble of
    // re-testing the activity lifecycle stuff
    protected void onStart() {
        super.onStart();

        updateList();
        whenTodoListEmpty();
    }

    @Override
    // when activity is paused, save current to-do list to todoListFile
    protected void onStop() {
        super.onStop();

        saveTodoList(todoListFile);
    }

    @Override
    // Called when result received from secondary activities
    // Adds the data gotten from the addToList activity to the todoList
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE) {
            if (resultCode == RESULT_OK) {
                // From addToList activity
                if (data.hasExtra(addToList.TASK_MESSAGE)) {
                    Toast.makeText(this, "Task added to list.", Toast.LENGTH_SHORT).show();
                    String todoName = data.getStringExtra(addToList.TASK_MESSAGE);
                    String todoDue = data.getStringExtra(addToList.DATE_MESSAGE);
                    if (todoDue == null) todoDue = "No date";
                    TodoListItem todoElem = new TodoListItem(todoName, todoDue);
                    todoList.add(todoElem);
                    data.removeExtra(addToList.TASK_MESSAGE);
                    data.removeExtra(addToList.DATE_MESSAGE);
                    updateList();
                }
            }
        }
    }

    // Called when user clicks the FloatingActionButton
    public void enterTodoInfo(View view) {
        // Calls addToList activity
        Intent intent = new Intent(this, addToList.class);
        startActivityForResult(intent, REQ_CODE);
    }

    // Save todoList to specified file
    private void saveTodoList(String file) {
        try {
            PrintStream out = new PrintStream(openFileOutput(file, MODE_PRIVATE));

            System.out.println("Saving...");
            for (TodoListItem item : todoList) {
                System.out.println(item.getTodoListName() + " " + item.getTodoListDueDate());
                out.println(item.getTodoListName());
                out.println(item.getTodoListDueDate());
            }

            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Add saved elements in todoListFile to todoList
    private void addFileToTodoList() {
        try {
            Scanner scan = new Scanner(openFileInput(todoListFile));

            System.out.println("Reading...");
            boolean itemFinished = false;
            String todoName = "";
            while (scan.hasNextLine()) {
                String todoElem = scan.nextLine();
                System.out.println(todoElem);
                // If name, continue reading in
                if (!itemFinished) todoName = todoElem;
                // If date, add to todoList
                else {
                    todoList.add(new TodoListItem(todoName, todoElem));
                }
                itemFinished = !itemFinished;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // If todoList is empty, make 'No tasks' visible
    private void whenTodoListEmpty() {
        TextView noTasks = (TextView) findViewById(R.id.no_tasks);
        if (todoList.isEmpty()) noTasks.setVisibility(View.VISIBLE);
        else noTasks.setVisibility(View.GONE);
    }

    // General update list function
    private void updateList() {
        // Shouldn't ever have too many elements for time complexity to matter
        Collections.sort(todoList);

        ListView list = (ListView) findViewById(R.id.todo_list);
        TodoListAdapter adapter;
        if (list.getAdapter() == null) {
             adapter = new TodoListAdapter(this,
                    R.layout.to_do_list_item, todoList);
            list.setAdapter(adapter);
        } else {
            adapter = (TodoListAdapter) list.getAdapter();
        }

        adapter.notifyDataSetChanged();
    }

}
