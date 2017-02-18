package com.naruto.mobile.adapterdelegate.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.naruto.mobile.R;
import com.naruto.mobile.adapterdelegate.sample.adapter.MainAdapter;
import com.naruto.mobile.adapterdelegate.sample.adapter.MainListAdapter;
import com.naruto.mobile.adapterdelegate.sample.model.Advertisement;
import com.naruto.mobile.adapterdelegate.sample.model.Cat;
import com.naruto.mobile.adapterdelegate.sample.model.DisplayableItem;
import com.naruto.mobile.adapterdelegate.sample.model.Dog;
import com.naruto.mobile.adapterdelegate.sample.model.Gecko;
import com.naruto.mobile.adapterdelegate.sample.model.Snake;

public class AdapterDelegateTestActivity extends AppCompatActivity {

    ViewGroup mParentViewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adapter_delegate_test);
        mParentViewGroup = (ViewGroup) findViewById(R.id.parentView);
        setupRecyclerView();//默认是RecyclerView加载样式
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_adapter_delegate_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_list) {
            setupListView();//ListView样式
            return true;
        } else if (id == R.id.action_recycler) {
            setupRecyclerView();//RecyclerView样式
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        setTitle("RecyclerView");
        mParentViewGroup.removeAllViews();
        final RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MainAdapter adapter = new MainAdapter(getAnimals());
        recyclerView.setAdapter(adapter);
        mParentViewGroup.addView(recyclerView);
    }

    private void setupListView() {
        setTitle("ListView");
        mParentViewGroup.removeAllViews();
        final ListView listView = new ListView(this);
        MainListAdapter adapter = new MainListAdapter(getAnimals());
        listView.setAdapter(adapter);
        mParentViewGroup.addView(listView);
    }

    private List<DisplayableItem> getAnimals() {
        List<DisplayableItem> animals = new ArrayList<>();
        animals.add(new Cat("American Curl"));
        animals.add(new Cat("Baliness"));
        animals.add(new Cat("Bengal"));
        animals.add(new Cat("Corat"));
        animals.add(new Cat("Manx"));
        animals.add(new Cat("Nebelung"));
        animals.add(new Dog("Aidi"));
        animals.add(new Dog("Chinook"));
        animals.add(new Dog("Appenzeller"));
        animals.add(new Dog("Collie"));
        animals.add(new Snake("Mub Adder", "Adder"));
        animals.add(new Snake("Texas Blind Snake", "Blind snake"));
        animals.add(new Snake("Tree Boa", "Boa"));
        animals.add(new Gecko("Fat-tailed", "Hemitheconyx"));
        animals.add(new Gecko("Stenodactylus", "Dune Gecko"));
        animals.add(new Gecko("Leopard Gecko", "Eublepharis"));
        animals.add(new Gecko("Madagascar Gecko", "Phelsuma"));
        animals.add(new Advertisement());
        animals.add(new Advertisement());
        animals.add(new Advertisement());
        animals.add(new Advertisement());
        animals.add(new Advertisement());
        Collections.shuffle(animals);
        return animals;
    }
}