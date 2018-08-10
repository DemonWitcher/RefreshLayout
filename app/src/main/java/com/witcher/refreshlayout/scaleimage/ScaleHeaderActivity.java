package com.witcher.refreshlayout.scaleimage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.witcher.refreshlayout.R;
import com.witcher.refreshlayout.TestAdapter;

public class ScaleHeaderActivity extends AppCompatActivity {

    private ScaleHeaderView refreshLayout;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale_iv);

        initView();
    }

    private void initView() {
        refreshLayout = findViewById(R.id.refresh_layout);
        recyclerView = findViewById(R.id.rv);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(new TestAdapter(this));


        ImageView iv = (ImageView) refreshLayout.getHeaderView();
        iv.setImageResource(R.drawable.img2);
    }

}
