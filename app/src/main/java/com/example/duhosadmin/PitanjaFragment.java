package com.example.duhosadmin;

import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class PitanjaFragment extends Fragment {
    ImageView actionBarImage;
    ImageButton idiNatrag,osvjeziButton,objaviButton;
    private View connectionFragmentView,pitanjaFragmentView;
    private boolean connectionFlag=false;
    EditText editTextPitanje;
    EditText editTextOdgovor;

    private List<String> listaPitanja=new ArrayList<>();
    private List<Integer> idLista=new ArrayList<>();
    private int idPostojecegPitanja=0;
    private boolean vecPostojiPitanjeFlag=false;
    private int brojPitanja;

    DatabaseReference databaseReference;
    private String idNumberString;
    private int idNumberInt;    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActionBar mActionBar =  ((AppCompatActivity)getActivity()).getSupportActionBar();
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setCustomView(R.layout.toolbar);
        mActionBar.setBackgroundDrawable(this.getResources().getDrawable(R.color.background));
        View view=mActionBar.getCustomView();
        actionBarImage=view.findViewById(R.id.prijavaImage);
        actionBarImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_pitanjanaslov));
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
            pitanjaFragmentView = inflater.inflate(R.layout.fragment_pitanja, container, false);

            databaseReference= FirebaseDatabase.getInstance().getReference("Pitanja");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        if(snapshot.exists()) {
                            idNumberString= snapshot.getKey();
                            idNumberInt=Integer.parseInt(idNumberString);
                            idNumberInt++;
                            if (snapshot.child("Pitanje").getValue() == null) {
                            } else {
                                final String pitanje = snapshot.child("Pitanje").getValue().toString().toLowerCase().trim();
                                listaPitanja.add(pitanje);
                                idLista.add(idNumberInt - 1);
                            }
                        }
                    }
                    brojPitanja=listaPitanja.size();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "Greška u čitanju iz baze podataka", databaseError.toException());
                }
            });
            editTextPitanje = pitanjaFragmentView.findViewById(R.id.editTextPitanje);
            editTextOdgovor = pitanjaFragmentView.findViewById(R.id.editTextOdgovor);


            objaviButton = pitanjaFragmentView.findViewById(R.id.objaviButton);
            onTextChange();

            objaviButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String pitanje = editTextPitanje.getText().toString().toLowerCase().trim();
                    String odgovor = editTextOdgovor.getText().toString().toLowerCase().trim();


                    if (editTextPitanje.length() == 0 || editTextOdgovor.length() == 0) {
                        Toast.makeText(getContext(), "Unesi podatke u sva ponuđena polja!", Toast.LENGTH_SHORT).show();
                    } else {
                        idPostojecegPitanja = 0;
                        for (int i = 0; i < brojPitanja; i++) {
                            if (listaPitanja.get(i).equals(pitanje)) {
                                vecPostojiPitanjeFlag = true;
                                idPostojecegPitanja = idLista.get(i);
                            }
                        }

                        if (vecPostojiPitanjeFlag) {
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Upozorenje")
                                    .setMessage("Unešeno pitanje već postoji u bazi! Ukoliko želite izbrisati ovo pitanje te dodati navedeno odaberite \"Uredu\", ukoliko to ne želite odaberite \"Natrag\"!")
                                    .setPositiveButton("Uredu", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            databaseReference.child(String.valueOf(idPostojecegPitanja)).removeValue();
                                            databaseReference.child(String.valueOf(idNumberInt)).setValue(new Pitanja(editTextPitanje.getText().toString(), editTextOdgovor.getText().toString()));
                                            vecPostojiPitanjeFlag = false;
                                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter, new VratiSeFragment()).commit();
                                        }
                                    })
                                    .setNegativeButton("Natrag", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            vecPostojiPitanjeFlag = false;
                                        }
                                    })
                                    .setIcon(R.drawable.duhos_logo)
                                    .show();
                        } else {
                            databaseReference.child(String.valueOf(idNumberInt)).setValue(new Pitanja(editTextPitanje.getText().toString(), editTextOdgovor.getText().toString()));


                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter, new VratiSeFragment()).commit();
                        }
                    }
                }
            });
            return pitanjaFragmentView;
        }
        else {
            connectionFragmentView = inflater.inflate(R.layout.no_internet_connection_fragment, container, false);
            osvjeziButton=connectionFragmentView.findViewById(R.id.osvjeziButton);
            osvjeziButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,new PitanjaFragment()).addToBackStack("pitanjaFragment").commit();
                }
            });
            return connectionFragmentView;
        }
    }

    private void onTextChange() {
        editTextPitanje.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    editTextPitanje.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.active_shape));
                } else {
                    editTextPitanje.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.no_active_shape));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        editTextOdgovor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    editTextOdgovor.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.active_shape));
                } else {
                    editTextOdgovor.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.no_active_shape));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
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

}








