package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchByRecipeAttribute extends BaseActivity {

    private DatabaseReference mRoot;
    private String[] recipeAttributeList;
    private ArrayList<String> recipe_list = new ArrayList<>();
    private String attribute, attributeSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Get the string that determines if search is by type or cuisine
        attribute = (String) getIntent().getSerializableExtra("RECIPE_ATTRIBUTE");

        // Prepares for retrieving an array from strings.xml
        int arrayId;
        Resources res = getResources();

        // Set arrayId based on which search, either recipe type or cuisine
        if(attribute.equals("Type_Recipes"))
            arrayId = R.array.recipe_types;
        else
            arrayId = R.array.recipe_cuisines;

        // Get respective array from strings.xml
        recipeAttributeList = res.getStringArray(arrayId);

        setContentView(R.layout.activity_recipe_types);
        super.onCreate(savedInstanceState);

        // Set ListView items by using an adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                SearchByRecipeAttribute.this, arrayId, android.R.layout.simple_list_item_1);
        ListView lv_recipe_types = findViewById(R.id.lv_recipe_types);
        lv_recipe_types.setAdapter(adapter);
        lv_recipe_types.setOnItemClickListener(itemClickListener);
    }

    // Create a message handling object as an anonymous class.
    private AdapterView.OnItemClickListener itemClickListener =
            new AdapterView.OnItemClickListener() {
                // When the user clicks on a list item, it is removed from the list.
                @Override
                public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                    search(recipeAttributeList[position]);
                }
            };

    // Returns all recipes under the specified type or cuisine, determined by parameter
    private void search(String selection) {

        mRoot = FirebaseDatabase.getInstance().getReference();
        this.attributeSelection = selection;

        mRoot.child(attribute).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipe_list = queryData(dataSnapshot);
                Intent intent = new Intent(SearchByRecipeAttribute.this, SearchResults.class);
                intent.putExtra("RECIPE_RESULTS", recipe_list);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // Returns the key Set of the DataSnapshot as an ArrayList
    public ArrayList<String> queryData(DataSnapshot dataSnapshot) {

        HashMap<String, Object> result =
                    (HashMap) dataSnapshot.child(attributeSelection).getValue();

        return new ArrayList<>(result.keySet());
    }

}
