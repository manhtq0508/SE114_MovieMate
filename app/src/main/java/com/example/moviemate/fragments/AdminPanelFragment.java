package com.example.moviemate.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moviemate.R;
import com.example.moviemate.activities.ChangePasswordActivity;
import com.example.moviemate.activities.LoginActivity;
import com.example.moviemate.activities.UpdateProfileActivity;
import com.example.moviemate.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminPanelFragment extends Fragment {
    private TextView nameTextView, phoneTextView, emailTextView;
    private CircleImageView avatarImageView;
    private DatabaseReference database;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Ánh xạ các TextView và ImageView
        nameTextView = view.findViewById(R.id.textViewName);
        phoneTextView = view.findViewById(R.id.textViewPhone);
        emailTextView = view.findViewById(R.id.textViewEmail);
        avatarImageView = view.findViewById(R.id.imageViewAvatar);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            loadUserInfo(user.getUid());
        }


        TextView editProfileTv = view.findViewById(R.id.editProfileTextView);
        editProfileTv.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UpdateProfileActivity.class);
            startActivity(intent);
        });

        ConstraintLayout changePasswordLayout = view.findViewById(R.id.changePasswordLayout);
        changePasswordLayout.setOnClickListener(v -> {
            changePassword();
        });

        ConstraintLayout logoutLayout = view.findViewById(R.id.logoutLayout);
        logoutLayout.setOnClickListener(v -> {
            logout();
        });
        return view;
    }

    private void changePassword() {
        Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
        startActivity(intent);
    }

    private void logout() {
        auth.signOut();

        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void loadUserInfo(String uid) {
        database.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userInfo = snapshot.getValue(User.class);
                if (userInfo != null) {
                    if (userInfo.name == null )  nameTextView.setText(R.string.not_set_yet);
                    else nameTextView.setText(userInfo.name);
                    if (userInfo.phone == null) phoneTextView.setText(R.string.not_set_yet);
                    else phoneTextView.setText(userInfo.phone);
                    emailTextView.setText(userInfo.email);

                    // Load avatar nếu có, sử dụng Picasso
                    if (userInfo.avatarUrl != null) {
                        Picasso.get().load(userInfo.avatarUrl).into(avatarImageView);
                    }
                } else {
                    Toast.makeText(getActivity(), "Failed to load user information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to load information", Toast.LENGTH_SHORT).show();
            }
        });
    }
}