package edwardlee.todolist;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class ToDoListActivity extends ListActivity {
    private static final String todoListFile = "com.edwardlee.todolist.todoList.txt";
    public static final int REQ_CODE = 1453;

    private ArrayList<String> todoList = new ArrayList<>();
    private ArrayList<String> dueDates = new ArrayList<>();

    @Override
    // Called when activity is created
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("Beginning onCreate...");
        super.onCreate(savedInstanceState);
        System.out.println("Finished super onCreate...");
        setContentView(R.layout.activity_to_do_list);
        System.out.println("Content view set...");

        // Get saved todoList
        addFileToTodoList();
        updateList();

        // Update list thing
        ListView list = (ListView) findViewById(R.id.list);
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
    protected void onStart() {
        System.out.println("Beginning onStart...");
        super.onStart();

        updateList();
        whenTodoListEmpty();
        System.out.println("Ending onStart...");
    }

    @Override
    // when activity is paused, save current to-do list to todoListFile
    protected void onStop() {
        super.onStop();

        saveTodoList(todoListFile);
    }

    @Override
    // Called when result received from secondary activities
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Beginning onActivityResult...");
        if (requestCode == REQ_CODE) {
            if (resultCode == RESULT_OK) {
                // From addToList activity
                if (data.hasExtra(addToList.TASK_MESSAGE)) {
                    Toast.makeText(this, "Task added to list.", Toast.LENGTH_SHORT).show();
                    String todoName = data.getStringExtra(addToList.TASK_MESSAGE);
                    String todoDue = data.getStringExtra(addToList.DATE_MESSAGE);
                    TodoListClass.TodoListItem todoElem = new TodoListClass().new TodoListItem(todoName, todoDue);
                    todoList.add(todoElem);
                    data.removeExtra(addToList.TASK_MESSAGE);
                    updateList();
                }
            }
        }
        System.out.println("Ending onActivityResult...");
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
            for (TodoListClass.TodoListItem item : todoList) {
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
                else todoList.add(new TodoListClass().new TodoListItem(todoName, todoElem));
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
        System.out.println("List update...");
        ListView list = (ListView) findViewById(R.id.list);
        ListAdapter adapter = new SimpleAdapter()
        if (list.getAdapter() == null) {
             adapter = new TodoListClass().new TodoListAdapter(this,
                    R.layout.to_do_list_item, todoList);
            list.setAdapter(adapterTodoList);
        } else {
            adapterTodoList = (TodoListClass.TodoListAdapter) list.getAdapter();
        }

        adapterTodoList.notifyDataSetChanged();
    }

}
