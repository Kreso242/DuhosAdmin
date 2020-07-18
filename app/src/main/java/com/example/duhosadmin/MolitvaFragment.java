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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MolitvaFragment extends Fragment {
    ImageView actionBarImage;
    ImageButton idiNatrag,osvjeziButton,objaviButton;
    private View connectionFragmentView,molitvaFragmentView;
    private boolean connectionFlag=false;
    ImageView opceMolitvaIcon,marijanskaMolitvaIcon,nadahnucaIcon,poboznostiIcon;
    private boolean opceMolitvaFlag=false;
    private boolean marijanskaMolitvaFlag=false;
    private boolean nadahnucaFlag=false;
    private boolean poboznostiFlag=false;
    EditText editTextNazivMolitve;
    EditText editTextTekstMolitve;
    DatabaseReference marijanskeReference,opceReference,nadahnucaReference,poboznostiReference;
    private int idNumberPoboznosti,idNumberOpce,idNumberMarijanske,idNumberNadahnuca;
    String date;
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
        actionBarImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_molitvanaslov));
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
            molitvaFragmentView = inflater.inflate(R.layout.fragment_molitva, container, false);
            setUpIcons();
            getReference();
            editTextNazivMolitve = molitvaFragmentView.findViewById(R.id.editTextNazivMolitve);
            editTextTekstMolitve = molitvaFragmentView.findViewById(R.id.editTextTekstMolitve);

            date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());


            objaviButton = molitvaFragmentView.findViewById(R.id.objaviButton);
            onTextChange();
            objaviButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editTextNazivMolitve.length() == 0 || editTextTekstMolitve.length() == 0) {
                        Toast.makeText(getContext(), "Unesi podatke u sva ponuđena polja!", Toast.LENGTH_SHORT).show();
                    }
                    else if(!opceMolitvaFlag && !marijanskaMolitvaFlag && !nadahnucaFlag && !poboznostiFlag)
                        Toast.makeText(getContext(), "Odaberi kategoriju molitve!", Toast.LENGTH_SHORT).show();
                    else {
                        if(opceMolitvaFlag) {
                            opceReference.child(String.valueOf(idNumberOpce + 1)).child("Naziv").setValue(editTextNazivMolitve.getText().toString());
                            opceReference.child(String.valueOf(idNumberOpce + 1)).child("Datum").setValue(date.toString());
                            opceReference.child(String.valueOf(idNumberOpce + 1)).child("Tekst").setValue(editTextTekstMolitve.getText().toString());
                        }
                        else if(marijanskaMolitvaFlag) {
                            marijanskeReference.child(String.valueOf(idNumberMarijanske + 1)).child("Naziv").setValue(editTextNazivMolitve.getText().toString());
                            marijanskeReference.child(String.valueOf(idNumberMarijanske + 1)).child("Datum").setValue(date.toString());
                            marijanskeReference.child(String.valueOf(idNumberMarijanske + 1)).child("Tekst").setValue(editTextTekstMolitve.getText().toString());
                        }
                        else if(nadahnucaFlag) {
                            nadahnucaReference.child(String.valueOf(idNumberNadahnuca + 1)).child("Naziv").setValue(editTextNazivMolitve.getText().toString());
                            nadahnucaReference.child(String.valueOf(idNumberNadahnuca + 1)).child("Datum").setValue(date.toString());
                            nadahnucaReference.child(String.valueOf(idNumberNadahnuca + 1)).child("Tekst").setValue(editTextTekstMolitve.getText().toString());
                        }
                        else if(poboznostiFlag) {
                            poboznostiReference.child(String.valueOf(idNumberPoboznosti + 1)).child("Naziv").setValue(editTextNazivMolitve.getText().toString());
                            poboznostiReference.child(String.valueOf(idNumberPoboznosti + 1)).child("Datum").setValue(date.toString());
                            poboznostiReference.child(String.valueOf(idNumberPoboznosti + 1)).child("Tekst").setValue(editTextTekstMolitve.getText().toString());
                        }
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter, new VratiSeFragment()).commit();
                    }
                }
            });
            return molitvaFragmentView;
        }
        else {
            connectionFragmentView = inflater.inflate(R.layout.no_internet_connection_fragment, container, false);
            osvjeziButton=connectionFragmentView.findViewById(R.id.osvjeziButton);
            osvjeziButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,new MolitvaFragment()).addToBackStack("molitvaFragment").commit();
                }
            });

            return connectionFragmentView;
        }
    }

    private void onTextChange() {
        editTextNazivMolitve.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    editTextNazivMolitve.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.active_shape));
                } else {
                    editTextNazivMolitve.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.no_active_shape));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editTextTekstMolitve.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    editTextTekstMolitve.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.active_shape));
                } else {
                    editTextTekstMolitve.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.no_active_shape));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void getReference() {
        opceReference= FirebaseDatabase.getInstance().getReference("Molitve/Opće molitve");
        opceReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if(snapshot.exists()) {
                        idNumberOpce= (int) dataSnapshot.getChildrenCount();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Greška u čitanju iz baze podataka", databaseError.toException());
            }
        });

        marijanskeReference= FirebaseDatabase.getInstance().getReference("Molitve/Marijanske molitve");
        marijanskeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if(snapshot.exists()) {
                        idNumberMarijanske= (int) dataSnapshot.getChildrenCount();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Greška u čitanju iz baze podataka", databaseError.toException());
            }
        });

        nadahnucaReference= FirebaseDatabase.getInstance().getReference("Molitve/Nadahnuća");
        nadahnucaReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if(snapshot.exists()) {
                        idNumberNadahnuca= (int) dataSnapshot.getChildrenCount();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Greška u čitanju iz baze podataka", databaseError.toException());
            }
        });

        poboznostiReference= FirebaseDatabase.getInstance().getReference("Molitve/Pobožnosti");
        poboznostiReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if(snapshot.exists()) {
                        idNumberPoboznosti= (int) dataSnapshot.getChildrenCount();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Greška u čitanju iz baze podataka", databaseError.toException());
            }
        });
    }

    private void setUpIcons() {
        opceMolitvaIcon = molitvaFragmentView.findViewById(R.id.opceMolitveIcon);
        marijanskaMolitvaIcon = molitvaFragmentView.findViewById(R.id.marijanskeMolitveIcon);
        nadahnucaIcon = molitvaFragmentView.findViewById(R.id.nadahnucaIcon);
        poboznostiIcon = molitvaFragmentView.findViewById(R.id.poboznostiIcon);

        opceMolitvaIcon.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                opceMolitvaFlag = true;
                marijanskaMolitvaFlag = false;
                nadahnucaFlag = false;
                poboznostiFlag = false;
                opceMolitvaIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_opcemolitveactive));
                marijanskaMolitvaIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_blazenadjevicamarijano));
                nadahnucaIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_nadahnucano));
                poboznostiIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_poboznostino));
            }
        });
        marijanskaMolitvaIcon.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                opceMolitvaFlag = false;
                marijanskaMolitvaFlag = true;
                nadahnucaFlag = false;
                poboznostiFlag = false;
                opceMolitvaIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_opcemolitveno));
                marijanskaMolitvaIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_blazenadjevicamarijaactive));
                nadahnucaIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_nadahnucano));
                poboznostiIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_poboznostino));
            }
        });
        nadahnucaIcon.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                opceMolitvaFlag = false;
                marijanskaMolitvaFlag = false;
                nadahnucaFlag = true;
                poboznostiFlag = false;
                opceMolitvaIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_opcemolitveno));
                marijanskaMolitvaIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_blazenadjevicamarijano));
                nadahnucaIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_nadahnucaactive));
                poboznostiIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_poboznostino));
            }
        });
        poboznostiIcon.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                opceMolitvaFlag = false;
                marijanskaMolitvaFlag = false;
                nadahnucaFlag = false;
                poboznostiFlag = true;
                opceMolitvaIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_opcemolitveno));
                marijanskaMolitvaIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_blazenadjevicamarijano));
                nadahnucaIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_nadahnucano));
                poboznostiIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_poboznostiactive));
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








