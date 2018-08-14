package com.witcher.refreshlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;

public class SwipeRefreshActivity extends AppCompatActivity {


    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);

        initView();
    }

    private void initView() {
        refreshLayout = findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        L.i("我是上层 刷新了数据");
                       refreshLayout.setRefreshing(false);
                    }
                },1000);
            }
        });

        findViewById(R.id.bt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.setRefreshing(false);
            }
        });

        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(new TestAdapter(this));

//        lv = findViewById(R.id.lv);
//        lv.setAdapter(new BaseAdapter() {
//            @Override
//            public int getCount() {
//                return 50;
//            }
//
//            @Override
//            public Object getItem(int position) {
//                return null;
//            }
//
//            @Override
//            public long getItemId(int position) {
//                return 0;
//            }
//
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                View view = LayoutInflater.from(SwipeRefreshActivity.this).inflate(R.layout.item,parent,false);
//                return view;
//            }
//        });
    }

}
