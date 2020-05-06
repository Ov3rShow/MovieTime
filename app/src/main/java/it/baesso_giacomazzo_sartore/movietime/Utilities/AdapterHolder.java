package it.baesso_giacomazzo_sartore.movietime.Utilities;

import it.baesso_giacomazzo_sartore.movietime.Adapter.RecyclerViewFilmsAdapter;

public class AdapterHolder {

    private RecyclerViewFilmsAdapter mAdapter;
    private static AdapterHolder holder;

    private AdapterHolder()
    {}

    public static AdapterHolder getInstance()
    {
        if(holder == null)
            holder = new AdapterHolder();

        return holder;
    }

    public RecyclerViewFilmsAdapter getmAdapter() {
        return mAdapter;
    }

    public void setmAdapter(RecyclerViewFilmsAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }
}
