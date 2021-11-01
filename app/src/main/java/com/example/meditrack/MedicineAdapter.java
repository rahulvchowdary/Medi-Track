package com.example.meditrack;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class MedicineAdapter extends FirebaseRecyclerAdapter<MedHelperClass, MedicineAdapter.MedicineViewHolder> {

    public MedicineAdapter(@NonNull FirebaseRecyclerOptions<MedHelperClass> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MedicineViewHolder holder, int position, @NonNull MedHelperClass model) {

    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //View view = ;
        return null;
    }

    public class MedicineViewHolder extends RecyclerView.ViewHolder {
        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
