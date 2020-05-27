package ru.netology.lists;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListViewActivity extends AppCompatActivity {
    private static final String KEY_TITLE = "key_title";
    private static final String KEY_COUNT = "key_count";
    private static List<Map<String, String>> simpleAdapterContent = new ArrayList<>();
    private FloatingActionButton btnAddContent;
    private int btnClickCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btnAddContent = findViewById(R.id.btn_fab_add);

        final ListView list = findViewById(R.id.list);
        final SwipeRefreshLayout swipeRefresh = findViewById(R.id.swipe_refresh);
        final BaseAdapter listContentAdapter = createAdapter(simpleAdapterContent);

        String string = getString(R.string.large_text);
        saveToFile(string);
        final String[] titles = readFromFile().split("\n\n");

        list.setAdapter(listContentAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                simpleAdapterContent.remove(position);
                listContentAdapter.notifyDataSetChanged();
                Toast.makeText(ListViewActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
            }
        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                simpleAdapterContent.clear();
                listContentAdapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
                Toast.makeText(ListViewActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareContent(titles);
                listContentAdapter.notifyDataSetChanged();

            }
        });
    }

    @NonNull
    private BaseAdapter createAdapter(List<Map<String, String>> values) {
        return new SimpleAdapter(this, values, R.layout.item, new String[]{KEY_TITLE, KEY_COUNT}, new int[]{R.id.text_Tv, R.id.symbol_cnt_Tv});
    }

    @NonNull
    private void prepareContent(String[] titles) {
        Map<String, String> map = new HashMap<>();
        map.put(KEY_TITLE, titles[btnClickCounter]);
        map.put(KEY_COUNT, String.valueOf(titles[btnClickCounter].length()));
        simpleAdapterContent.add(map);
        if (btnClickCounter == titles.length) {
            return;
        }
        btnClickCounter++;
    }

    public void saveToFile(String string) {
        if (isExternalStorageWritable() == true) {
            File saveData = new File(getApplicationContext()
                    .getExternalFilesDir(null), "save.txt");

            FileWriter writer = null;
            try {
                writer = new FileWriter(saveData, true);
                writer.append(string);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(this, "No access", Toast.LENGTH_SHORT).show();
        }
    }

    public String readFromFile() {
        if (isExternalStorageWritable() == true) {
            File saveData = new File(getApplicationContext()
                    .getExternalFilesDir(null), "save.txt");

            FileReader reader = null;
            try {
                String str = "";
                int current = 0;
                reader = new FileReader(saveData);
                while ((current = reader.read()) != -1) {
                    str += (char) (current);
                }

                return str;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(this, "No access", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
