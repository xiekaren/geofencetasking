package com.app.gfour.geofencetasker.tasks;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.app.gfour.geofencetasker.R;
import com.app.gfour.geofencetasker.data.AchievementService;
import com.app.gfour.geofencetasker.data.Task;
import com.app.gfour.geofencetasker.data.TaskHelper;
import com.app.gfour.geofencetasker.newtask.NewTaskActivity;

import java.util.ArrayList;

import static android.view.ContextMenu.ContextMenuInfo;

/**
 * Main Activity which shows list of tasks.
 * Credits to: https://guides.codepath.com/android/Basic-Todo-App-Tutorial
 */
public class TasksActivity extends AppCompatActivity {
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 9;


    private TaskHelper taskHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TasksActivity ta = new TasksActivity();
        anotherPackage.TasksActivity taa = new anotherPackage.TasksActivity();
        com.app.gfour.geofencetasker.newtask.TasksActivity tab = new com.app.gfour.geofencetasker.newtask.TasksActivity();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btnAddItem);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TasksActivity.this, NewTaskActivity.class));
            }
        });

        taskHelper = new TaskHelper(this);

        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<String>();

        for (Task task : taskHelper.getAllTasks()){
            items.add(task.getTitle() + "\n" + task.getAddress());
        }

        itemsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);
        registerForContextMenu(lvItems);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
            }
        }
    }

    @Override
    public void onCreateContextMenu(android.view.ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String taskItem = itemsAdapter.getItem(info.position);
        String[] taskFields = taskItem.split("\n");
        int id = taskHelper.getIdByFields(taskFields[0], taskFields[1]);

        switch (item.getItemId()) {

            case R.id.deleteItem:
                // Remove the task from the database.
                taskHelper.deleteTask(id);
                // Remove the task from the listView.
                itemsAdapter.remove(taskItem);
                return true;

            case R.id.completeItem:
                Intent intent = new Intent(this, AchievementService.class);
                intent.putExtra("Address", taskFields[1]);
                startService(intent);

                // Remove the task from the database.
                taskHelper.deleteTask(id);

                // Remove the task from the listView.
                itemsAdapter.remove(taskItem);
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tasks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
