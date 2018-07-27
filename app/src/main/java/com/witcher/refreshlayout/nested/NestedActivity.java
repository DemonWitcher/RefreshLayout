package com.witcher.refreshlayout.nested;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.witcher.refreshlayout.R;
import com.witcher.refreshlayout.TestAdapter;

public class NestedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NestedParentView nestedParentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested);

        initView();
    }

    private void initView() {
        nestedParentView = findViewById(R.id.nested_parent_view);
        findViewById(R.id.bt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nestedParentView.test1();
            }
        });

        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(new TestAdapter(this));

    }

}
