package com.witcher.refreshlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class MainActivity2 extends AppCompatActivity {

    private RefreshLayout2 refreshLayout;
    private RecyclerView recyclerView;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initView();
    }

    private void initView() {
        refreshLayout = findViewById(R.id.refresh_layout);
        refreshLayout.setRefreshListener(new RefreshLayout2.RefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        L.i("我是上层 刷新了数据");
//                        refreshLayout.stopRefresh();
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

//        recyclerView = findViewById(R.id.rv);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
//        recyclerView.setAdapter(new TestAdapter(this));

        lv = findViewById(R.id.lv);
        lv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 50;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = LayoutInflater.from(MainActivity2.this).inflate(R.layout.item,parent,false);
                return view;
            }
        });
    }

}
