package com.example.duhosadmin;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {


    ImageButton idiNatrag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //micanje default action bara i postavljanje posebnog
        //micanje default action bara i postavljanje posebnog
        getSupportActionBar().setCustomView(R.layout.toolbar);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(this.getResources().getDrawable(R.color.background));
        View viewActionBar=mActionBar.getCustomView();
        idiNatrag=viewActionBar.findViewById(R.id.idiNatrag);
        idiNatrag.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,new PrijavaFragment()).commit();

    }
    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
            finish();
        } else {
           getSupportFragmentManager().popBackStack();
        }
    }
}
