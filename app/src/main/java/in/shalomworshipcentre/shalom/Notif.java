package in.shalomworshipcentre.shalom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ConfigCallback;
import com.parse.GetDataCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.SaveCallback;

public class Notif extends Activity {
    // Adapter for the Todos Parse Query
    private ParseQueryAdapter<Todo> todoListAdapter;
    private LayoutInflater inflater;
    // For showing empty and non-empty todo views
    ListView todoListView;
    static TextView server;
    public Todo todo;
    static ImageView pic;
    static ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            todo = new Todo();
            todo.setUuidString();
            todo.setTitle(getIntent().getStringExtra("txt"));
            todo.setDraft(true);
            todo.pinInBackground(Application.TODO_GROUP_NAME,
                    new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (isFinishing()) {
                                return;
                            }
                            if (e == null) {
                                setResult(Activity.RESULT_OK);
                            }
                        }
                    });
        } catch (Exception e) {
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);
        // Set up the views
        todoListView = (ListView) findViewById(R.id.todo_list_view);
        server = (TextView) findViewById(R.id.config);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        pic = (ImageView) findViewById(R.id.pic);
        todo();
        Helper.refreshConfig();
    }

    private void todo() {

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
        // Unpin all the current objects
        ParseObject.unpinAllInBackground(Application.TODO_GROUP_NAME);
        // Clear the view
        todoListAdapter.clear();
        server.setVisibility(View.GONE);
        pic.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}