package com.example.duhosadmin;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class KreirajFragment extends Fragment {
    ImageView actionBarImage;
    ImageButton idiNatrag;
    private View kreirajFragmentView;
    Button pjesmarica,kalendar,molitva,novosti,pitanja;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActionBar mActionBar =  ((AppCompatActivity)getActivity()).getSupportActionBar();
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setCustomView(R.layout.toolbar);
        mActionBar.setBackgroundDrawable(this.getResources().getDrawable(R.color.background));
        View view=mActionBar.getCustomView();
        actionBarImage=view.findViewById(R.id.prijavaImage);
        actionBarImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_kreirajnaslov));
        idiNatrag=view.findViewById(R.id.idiNatrag);
        idiNatrag.setVisibility(View.VISIBLE);
        kreirajFragmentView = inflater.inflate(R.layout.fragment_kreiraj, container, false);

        pjesmarica=kreirajFragmentView.findViewById(R.id.pjesmaricaButton);
        kalendar=kreirajFragmentView.findViewById(R.id.kalendarButton);
        molitva=kreirajFragmentView.findViewById(R.id.molitvaButton);
        novosti=kreirajFragmentView.findViewById(R.id.novostiButton);
        pitanja=kreirajFragmentView.findViewById(R.id.pitanjaButton);

        pjesmarica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,new PjesmaricaFragment()).addToBackStack("pjesmaricaFragment").commit();

            }
        });
        kalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,new KalendarFragment()).addToBackStack("kalendarFragment").commit();

            }
        });
        molitva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,new MolitvaFragment()).addToBackStack("molitvaFragment").commit();

            }
        });
        novosti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,new NovostiFragment()).addToBackStack("novostiFragment").commit();

            }
        });
        pitanja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,new PitanjaFragment()).addToBackStack("pitanjaFragment").commit();

            }
        });
        idiNatrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return kreirajFragmentView;

    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

}










