package com.example.duhosadmin;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class PjesmaricaFragment extends Fragment {
    ImageView actionBarImage;
    ImageButton idiNatrag,osvjeziButton,objaviButton;
    private View connectionFragmentView,pjesmaricaFragmentView;
    private boolean connectionFlag=false;
    EditText editTextNazivPjesme;
    EditText editTextIzvodjac;
    EditText editTextLinkZaAkorde;
    EditText editTextTesktPjesme;
    DatabaseReference databaseReference;
    private String idNumberString;
    private int idNumberInt;
    private List<String> listaNaslova=new ArrayList<>();
    private boolean vecPostojiPjesmaFlag=false;
    private int brojNaslova;

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
        actionBarImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_pjesmaricanaslov));
        idiNatrag=view.findViewById(R.id.idiNatrag);
        idiNatrag.setVisibility(View.VISIBLE);

        idiNatrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        checkInternetConnection();

        if(connectionFlag==true) {
            pjesmaricaFragmentView = inflater.inflate(R.layout.fragment_pjesmarica, container, false);

            databaseReference= FirebaseDatabase.getInstance().getReference("Pjesmarica");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        if(snapshot.exists()) {
                            idNumberString= snapshot.getKey();
                            idNumberInt=Integer.parseInt(idNumberString);
                            idNumberInt++;

                            if(snapshot.child("Naslov").getValue()==null ){
                            }
                            else{
                                final String naslov = snapshot.child("Naslov").getValue().toString().toLowerCase();
                                listaNaslova.add(naslov);
                            }

                        }

                    }
                    brojNaslova=listaNaslova.size();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "Greška u čitanju iz baze podataka", databaseError.toException());
                }
            });


            editTextNazivPjesme = pjesmaricaFragmentView.findViewById(R.id.editTextNazivPjesme);
            editTextIzvodjac = pjesmaricaFragmentView.findViewById(R.id.editTextIzvodjac);
            editTextLinkZaAkorde = pjesmaricaFragmentView.findViewById(R.id.editTextLinkZaAkorde);
            editTextTesktPjesme = pjesmaricaFragmentView.findViewById(R.id.editTextTesktPjesme);

            objaviButton = pjesmaricaFragmentView.findViewById(R.id.objaviButton);
            onTextChange();

            objaviButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(editTextIzvodjac.length()==0)
                        editTextIzvodjac.setText("Nepoznati izvođač");

                    String naslov=editTextNazivPjesme.getText().toString().toLowerCase();
                    String izvodjac=editTextIzvodjac.getText().toString().toLowerCase();
                    for(int i=0; i<brojNaslova;i++){
                        if(listaNaslova.get(i).equals(naslov) ){
                            vecPostojiPjesmaFlag=true;
                        }
                    }

                    if (editTextNazivPjesme.length() == 0  || editTextLinkZaAkorde.length() == 0 || editTextTesktPjesme.length() == 0) {
                        Toast.makeText(getContext(), "Unesi podatke u sva ponuđena polja! Jedino izvođač može ostati nepoznat! ", Toast.LENGTH_SHORT).show();
                    }

                    else if(vecPostojiPjesmaFlag){
                        Toast.makeText(getContext(), "Navedena pjesma već postoji u bazi!", Toast.LENGTH_SHORT).show();
                        vecPostojiPjesmaFlag=false;

                    }
                    else {
                        databaseReference.child(String.valueOf(idNumberInt)).child("Naslov").setValue(editTextNazivPjesme.getText().toString());
                        databaseReference.child(String.valueOf(idNumberInt)).child("Izvođač").setValue(editTextIzvodjac.getText().toString());
                        databaseReference.child(String.valueOf(idNumberInt)).child("Link").setValue(editTextLinkZaAkorde.getText().toString());
                        databaseReference.child(String.valueOf(idNumberInt)).child("Tekst").setValue(editTextTesktPjesme.getText().toString());

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter, new VratiSeFragment()).commit();
                    }

                }
            });
            return pjesmaricaFragmentView;
        }
        else {
            connectionFragmentView = inflater.inflate(R.layout.no_internet_connection_fragment, container, false);
            osvjeziButton=connectionFragmentView.findViewById(R.id.osvjeziButton);
            osvjeziButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,new PjesmaricaFragment()).addToBackStack("pjesmaricaFragment").commit();
                }
            });
            return connectionFragmentView;
        }
    }

    private void checkInternetConnection() {
        ConnectivityManager connectivityManager=(ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork=connectivityManager.getActiveNetworkInfo();
        if(null!=activeNetwork){
            if(activeNetwork.getType()==ConnectivityManager.TYPE_WIFI){
                connectionFlag=true;
            }
            else if(activeNetwork.getType()==ConnectivityManager.TYPE_MOBILE){
                connectionFlag=true;
            }

        }
        else
        {
            connectionFlag=false;
        }
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    private void onTextChange() {
        editTextNazivPjesme.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    editTextNazivPjesme.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.active_shape));
                } else {
                    editTextNazivPjesme.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.no_active_shape));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        editTextIzvodjac.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    editTextIzvodjac.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.active_shape));
                } else {
                    editTextIzvodjac.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.no_active_shape));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        editTextLinkZaAkorde.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    editTextLinkZaAkorde.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.active_shape));
                } else {
                    editTextLinkZaAkorde.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.no_active_shape));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        editTextTesktPjesme.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    editTextTesktPjesme.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.active_shape));
                } else {
                    editTextTesktPjesme.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.no_active_shape));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}











