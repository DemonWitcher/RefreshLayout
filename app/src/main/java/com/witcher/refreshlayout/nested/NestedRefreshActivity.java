package com.witcher.refreshlayout.nested;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.witcher.refreshlayout.L;
import com.witcher.refreshlayout.R;
import com.witcher.refreshlayout.TestAdapter;

public class NestedRefreshActivity extends AppCompatActivity {

    private NestedRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested_refresh);

        initView();
    }

    private void initView() {
        refreshLayout = findViewById(R.id.refresh_layout);
        recyclerView = findViewById(R.id.rv);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(new TestAdapter(this));

        refreshLayout.setRefreshListener(new NestedRefreshLayout.RefreshListener() {
            @Override
            public void onRefresh() {
                L.i("开始刷新");
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        L.i("刷新完成");
                        refreshLayout.stopRefresh();
                    }
                },1000);
            }

            @Override
            public void onFinish() {

            }
        });

        findViewById(R.id.bt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.stopRefresh();
            }
        });
    }

}
