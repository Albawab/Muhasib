package com.example.muhasib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muhasib.models.UserKind;

import java.util.List;

public class UserAdapterRecyclerView extends RecyclerView.Adapter<UserAdapterRecyclerView.UserViewHolder> {
    private LiveData<List<UserKind>> userList;
    private Context context;

    public UserAdapterRecyclerView(LiveData<List<UserKind>> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        List<UserKind> users = userList.getValue();
        if (users != null && position < users.size()) {
            UserKind user = users.get(position);
            holder.userIdTextView.setText(String.valueOf(user.getId()));
            holder.userNameTextView.setText(user.getName());

            holder.deleteUserButton.setOnClickListener(v -> {
                // Verwijder de gebruiker uit de database
                AppDatabase.getDatabase(context).userKindDao().delete(user);
            });
        }
    }

    @Override
    public int getItemCount() {
        List<UserKind> users = userList.getValue();
        return users != null ? users.size() : 0;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userIdTextView;
        TextView userNameTextView;
        Button deleteUserButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userIdTextView = itemView.findViewById(R.id.user_id);
            userNameTextView = itemView.findViewById(R.id.user_name);
            deleteUserButton = itemView.findViewById(R.id.delete_user_button);
        }
    }
}
