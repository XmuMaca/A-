package wxm.com.androiddesign.ui;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

import wxm.com.androiddesign.R;
import wxm.com.androiddesign.adapter.MultipleItemAdapter;
import wxm.com.androiddesign.module.ActivityItemData;
import wxm.com.androiddesign.module.CommentData;

public class DetailActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    static ActivityItemData activityItemData;
    static ArrayList<CommentData>commentDatas=new ArrayList<CommentData>();
    static {
        activityItemData = new ActivityItemData(R.drawable.miao);
        for (int i = 0; i < 5; i++) {
            commentDatas.add(new CommentData(R.drawable.miao,5,"I'm comment"));
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail2);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        setupRecyclerView(recyclerView);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        final ActionBar actionBar=getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        addComment();
    }

    public void addComment()
    {
        ImageView cmt_comment=(ImageView)findViewById(R.id.cmt_comment);
        EditText cmt_text = (EditText) findViewById(R.id.add_comment);
        cmt_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setupRecyclerView(RecyclerView recyclerView){
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        recyclerView.setAdapter(new MultipleItemAdapter(activityItemData,commentDatas));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
