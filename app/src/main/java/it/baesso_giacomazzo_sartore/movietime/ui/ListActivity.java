package it.baesso_giacomazzo_sartore.movietime.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import it.baesso_giacomazzo_sartore.movietime.API.WebService;
import it.baesso_giacomazzo_sartore.movietime.ListActivityInterface;
import it.baesso_giacomazzo_sartore.movietime.R;
import it.baesso_giacomazzo_sartore.movietime.objects.PopularResult;

public class ListActivity extends AppCompatActivity implements ListActivityInterface {

    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);




        WebService webService = WebService.getInstance();
        webService.getAllPopular(ListActivity.this, getString(R.string.api_key), "it-IT", 1);





        recyclerView = findViewById(R.id.listActivity_recyclerView);
        mLayoutManager = new GridLayoutManager(ListActivity.this, 2);

        String[] strings = new String[] {"tavolo", "albero", "zaino", "cane", "cocorita"};

        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RecyclerViewFilmsAdapter(strings);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showApiCallResult(PopularResult result) {
        Toast.makeText(ListActivity.this, String.valueOf(result.getResults().size()), Toast.LENGTH_SHORT).show();
    }
}
