package in.shalomworshipcentre.shalom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class Notif extends Activity {
    // Adapter for the Todos Parse Query
    private ParseQueryAdapter<Todo> todoListAdapter;
    private LayoutInflater inflater;
    // For showing empty and non-empty todo views
    private ListView todoListView;
    private LinearLayout noTodosView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // hide status and actionbar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.activity_todo_list);
       /* Intent b = getIntent();
        String pushStore = b.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView mTextView = (TextView) this.findViewById(R.id.no_todos);
        if (pushStore != null) {
            mTextView.setText(pushStore);
        }*/
        // Set up the views
        todoListView = (ListView) findViewById(R.id.todo_list_view);
        noTodosView = (LinearLayout) findViewById(R.id.no_todos_view);
        todoListView.setEmptyView(noTodosView);

        // Set up the Parse query to use in the adapter
        ParseQueryAdapter.QueryFactory<Todo> factory = new ParseQueryAdapter.QueryFactory<Todo>() {
            public ParseQuery<Todo> create() {
                ParseQuery<Todo> query = Todo.getQuery();
                query.orderByAscending("createdAt");
                query.fromLocalDatastore();
                return query;
            }
        };
        // Set up the adapter
        inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        todoListAdapter = new ToDoListAdapter(this, factory);

        // Attach the query adapter to the view
        ListView todoListView = (ListView) findViewById(R.id.todo_list_view);
        todoListView.setAdapter(todoListAdapter);

    }


    private class ToDoListAdapter extends ParseQueryAdapter<Todo> {

        public ToDoListAdapter(Context context,
                               ParseQueryAdapter.QueryFactory<Todo> queryFactory) {
            super(context, queryFactory);
        }

        @Override
        public View getItemView(Todo todo, View view, ViewGroup parent) {
            ViewHolder holder;
            if (view == null) {
                view = inflater.inflate(R.layout.list_item_todo, parent, false);
                holder = new ViewHolder();
                holder.todoTitle = (TextView) view
                        .findViewById(R.id.todo_title);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            TextView todoTitle = holder.todoTitle;
            todoTitle.setText(todo.getTitle());
            todoTitle.setTypeface(null, Typeface.NORMAL);
            return view;
        }


    }

    private static class ViewHolder {
        TextView todoTitle;
    }

    public void clear(View view) {
        // Clear the view
        todoListAdapter.clear();
        // Unpin all the current objects
        ParseObject
                .unpinAllInBackground(Application.TODO_GROUP_NAME);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}