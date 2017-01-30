package edwardlee.todolist;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by edward on 1/28/17.
 * References http://stackoverflow.com/questions/4587301/android-adding-subitem-to-a-listview
 *
 * Used for formatting the items in the to-do list
 */

public class TodoListClass extends ListActivity {

    class TodoListItem {
        private String todoListName;
        private String todoListDueDate;

        public String getTodoListName() {
            return todoListName;
        }

        public void setTodoListName(String name) {
            todoListName = name;
        }

        public String getTodoListDueDate() {
            return todoListDueDate;
        }

        public void setTodoListDueDate(String dueDate) {
            todoListDueDate = dueDate;
        }

        public TodoListItem(String name, String dueDate) {
            todoListName = name;
            todoListDueDate = dueDate;
        }

    }

    public class TodoListAdapter extends ArrayAdapter<TodoListItem> {
        private ArrayList<TodoListItem> items;
        private TodoListViewHolder todoListHolder;

        private class TodoListViewHolder {
            TextView name;
            TextView dueDate;
        }

        public TodoListAdapter(Context context, int tvResId, ArrayList<TodoListItem> items) {
            super(context, tvResId, items);
            this.items = items;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                v = li.inflate(R.layout.activity_to_do_list, null);
                todoListHolder = new TodoListViewHolder();
                todoListHolder.name = (TextView) v.findViewById(R.id.todo_name);
                todoListHolder.dueDate = (TextView) v.findViewById(R.id.todo_due_date);
                v.setTag(todoListHolder);
            } else {
                todoListHolder = (TodoListViewHolder)v.getTag();
            }

            TodoListItem todoListItem = items.get(pos);

            if (todoListItem != null) {
                todoListHolder.name.setText(todoListItem.getTodoListName());
                todoListHolder.dueDate.setText(todoListItem.getTodoListDueDate());
            }

            return v;
        }

    }
}
