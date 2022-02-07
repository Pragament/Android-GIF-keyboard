package com.optimum.coolkeybord;

import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.optimum.coolkeybord.adapter.SearchAdapter;
import com.optimum.coolkeybord.settingssession.SettingSesson;

import java.util.ArrayList;

public class DictionaryActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView backimgtogif;
    private androidx.core.widget.NestedScrollView settingspage;
    private LinearLayout gifviewpagli;
    private ImageView increase_char;
    private ImageView decrease_char;
    private TextView minout_txt;
    private SettingSesson settingSesson;
    private CardView langcard;
    private CardView startendcard;
    private CheckBox showench_ch,showtel_ch,showtamil_ch;
    private RadioGroup starconrd;
    private RadioButton searchbystart;
    private RadioButton searchbycontains;
    private CheckBox youtubehcekc;
    private CheckBox appendtxttogif;
//    EditText searchEditText;
//    Spinner languageSpinner;
//    ArrayList<String> languages, words;
//
//    private RecyclerView mRecyclerView;
//    private RecyclerView.Adapter mAdapter;
//    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);
        settingSesson = new SettingSesson(this);
//        settings = findViewById(R.id.settings);
        searchbystart = findViewById(R.id.searchbystart);
        searchbycontains = findViewById(R.id.searchbycontains);
        langcard = findViewById(R.id.langcard);
        starconrd = findViewById(R.id.starconrd);

        showench_ch = findViewById(R.id.showench_ch);
        showtel_ch = findViewById(R.id.showtel_ch);
        showtamil_ch = findViewById(R.id.showtamil_ch);
        youtubehcekc = findViewById(R.id.youtubehcekc);
        appendtxttogif = findViewById(R.id.appendtxttogif);

        increase_char = findViewById(R.id.increase_char);
        minout_txt = findViewById(R.id.minout_txt);
        decrease_char = findViewById(R.id.decrease_char);
        backimgtogif = findViewById(R.id.backimgtogif);
        settingspage = findViewById(R.id.settingspage);
        gifviewpagli = findViewById(R.id.gifviewpagli);
        startendcard = findViewById(R.id.startendcard);
        if(settingSesson.getSearchbyStartsorEnd().equals("S"))
        {
            searchbystart.setChecked(true);
        }else {
            searchbycontains.setChecked(true);
        }
        youtubehcekc.setChecked(settingSesson.getAppendlink());
        appendtxttogif.setChecked(settingSesson.getgiflink());
        minout_txt.setText(String.valueOf(Integer.parseInt(settingSesson.getMinimumcharacters())));
        settingSesson.setMinimumcharacters(minout_txt.getText().toString());
        starconrd.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                Toast.makeText(radioGroup.getContext(),"Starts with selected "+starconrd.getCheckedRadioButtonId(),Toast.LENGTH_SHORT).show();
                if(starconrd.getCheckedRadioButtonId() ==radioGroup.getChildAt(0).getId())
                {
                    Toast.makeText(radioGroup.getContext(),"Starts with selected ",Toast.LENGTH_SHORT).show();
                    settingSesson.setSearchbystarts("S");
                }else {
                    Toast.makeText(radioGroup.getContext(),"Ends with selected ",Toast.LENGTH_SHORT).show();
                    settingSesson.setSearchbystarts("E");
                }
//                int selectedId=   starconrd.getCheckedRadioButtonId();
//                RadioButton radioSexButton = (RadioButton) findViewById(selectedId);
//                Toast.makeText(view.getContext(),radioSexButton.getText(),Toast.LENGTH_SHORT).show();
            }
        });
        showench_ch.setChecked(settingSesson.getShowenglish());
        showench_ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settingSesson.setShowenglish(b);
            }
        });
        showtel_ch.setChecked(settingSesson.getShowtelugu());
        showtel_ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settingSesson.setShowtelugu(b);
            }
        });
        showtamil_ch.setChecked(settingSesson.getShowtamil());
        showtamil_ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settingSesson.setShowtamil(b);
            }
        });
        increase_char.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Increased ", Toast.LENGTH_SHORT).show();
                minout_txt.setText(String.valueOf(Integer.parseInt(minout_txt.getText().toString()) +1));
                settingSesson.setMinimumcharacters(minout_txt.getText().toString());


            }
        });
        decrease_char.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Decrease ", Toast.LENGTH_SHORT).show();
                if(Integer.parseInt(minout_txt.getText().toString()) <=1 )
                {
                    return;
                }
                minout_txt.setText( String.valueOf(Integer.parseInt(minout_txt.getText().toString()) -1));
                settingSesson.setMinimumcharacters(minout_txt.getText().toString());
            }
        });
        youtubehcekc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settingSesson.appendlinkyou(b);
            }
        });
//        languageSpinner = findViewById(R.id.spinner);
//        searchEditText = findViewById(R.id.searchText);
//        Button searchButton = findViewById(R.id.searchButton);
//        mRecyclerView = findViewById(R.id.recyclerView);
//
//        // use a linear layout manager
//        mLayoutManager = new LinearLayoutManager(this);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//
//        // Set event listener on search button
//        searchButton.setOnClickListener(this);
//
//        // Array for spinner
//        languages = new ArrayList<>();
//        languages.add("English");
//        languages.add("Farsi");
//        languages.add("Pashto");
//
//        // Add items to the spinner
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, languages);
//        languageSpinner.setAdapter(adapter);

        backimgtogif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.youtubehcekc) {//
//            Toast.makeText(v.getContext() ,"Enabled" ,Toast.LENGTH_LONG).show();
//            //          search();
//            settingSesson.appendlinkyou(true);
//        }
    }

//    private void search() {
//        String searchWord = searchEditText.getText().toString();
//
//        if(!searchWord.equals("")) {
////            DatabaseManager db = new DatabaseManager(this);
////
////            String language = languages.get(languageSpinner.getSelectedItemPosition());
////
////            words = new ArrayList<>();
////            words = db.getAllRow(searchWord, language.toLowerCase());
//
////            if(words.size() > 0) {
////                // specify an adapter
////                mAdapter = new SearchAdapter(words, this, language.toLowerCase());
////                mRecyclerView.setAdapter(mAdapter);
////                mAdapter.notifyDataSetChanged();
////
////                // Hide Keyboard
////                hideKeyboard();
////            } else {
////                Toast.makeText(this, getString(R.string.no_result), Toast.LENGTH_LONG).show();
////            }
//        } else {
//            Toast.makeText(this, getString(R.string.search_hint_toast), Toast.LENGTH_LONG).show();
//        }
//    }

//    private void hideKeyboard() {
//        // Check if no view has focus:
//        View view = this.getCurrentFocus();
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//            assert imm != null;
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
//    }
}
