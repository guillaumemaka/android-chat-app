package com.espacepiins.messenger.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;

import com.espacepiins.messenger.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wilfrield on 2018-03-06.
 */

public class ProfileActivity extends AppCompatActivity {
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        RecyclerView rv = findViewById(R.id.my_list);
        adapter = new MyAdapter(this, getItems());
        rv.setAdapter(adapter);
        GridLayoutManager lm =
                new GridLayoutManager(this, 1);
        rv.setLayoutManager(lm);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                lm.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);

        rv.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // inflater le menu ici
        return true;
    }

    private List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        items.add(new Item("Georges Simmons", R.drawable.ic_face, R.color.colorContact));
        items.add(new Item("@georges_simmons", R.drawable.ic_at, R.color.colorAro));
        items.add(new Item("+7 (974) 849-0782", R.drawable.ic_phone_buttons, R.color.colorPhone));
        items.add(new Item("Preferences", R.drawable.ic_settings, R.color.colorSetting));
        return items;

    }
}
