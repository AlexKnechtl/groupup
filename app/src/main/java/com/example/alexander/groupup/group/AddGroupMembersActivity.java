package com.example.alexander.groupup.group;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alexander.groupup.R;
import com.example.alexander.groupup.models.FriendsModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddGroupMembersActivity extends AppCompatActivity {

    private RecyclerView MemberPreview, MemberSelect;

    private DatabaseReference FriendsDatabase;

    private ArrayList<FriendModelSelectedMap> friendsToSelectAsMembers;
    private ArrayList<FriendsModel> selectedPreviewMembers;

    private SelectMembersAdapter membersAdapter;
    private SelectMembersPreviewAdapter previewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_members);
        MemberPreview = findViewById(R.id.AddGroupMembersPrieview);
        MemberSelect = findViewById(R.id.AddGroupSelectMembers);
        MemberPreview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        FriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends");

        friendsToSelectAsMembers = new ArrayList<>();
        selectedPreviewMembers =new ArrayList<>();

        membersAdapter = new SelectMembersAdapter(this, friendsToSelectAsMembers);
        previewAdapter = new SelectMembersPreviewAdapter(this, selectedPreviewMembers);

        FriendsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        FriendsModel model = snapshot.getValue(FriendsModel.class);
                        model.setUid(snapshot.getKey());
                        friendsToSelectAsMembers.add(new FriendModelSelectedMap(model, false));
                    }
                    membersAdapter.notifyDataSetChanged();

                    FirebaseDatabase.getInstance().getReference().child("Groups/Steiermark").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    for(FriendModelSelectedMap fmap : friendsToSelectAsMembers)
                                        if(fmap.friendsModel.getUid().equals(snapshot.getKey()))
                                        {
                                            fmap.setSelected(true);
                                            selectedPreviewMembers.add(fmap.friendsModel);
                                            break;
                                        }
                                }
                                previewAdapter.notifyDataSetChanged();
                                membersAdapter.notifyDataSetChanged();
                            }
                            catch (Exception e){
                                String str = e.getMessage();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                catch (Exception e){
                        String str = e.getMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        MemberSelect.setAdapter(membersAdapter);
        MemberPreview.setAdapter(previewAdapter);
    }

    @Override
    public void onBackPressed() {
        // TODO implement sending to firebase
        FirebaseDatabase.getInstance().getReference().child("Groups/Steiermark").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("members").removeValue();
        FirebaseDatabase.getInstance().getReference().child("Groups/Steiermark").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("members").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("rank").setValue("creator");
        for(FriendsModel m : previewAdapter.getSelectedFriends()) // Hier sind alle ausgewählten Freunde drinnen
            FirebaseDatabase.getInstance().getReference().child("Groups/Steiermark").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("members").child(m.getUid()).child("rank").setValue("member");
        super.onBackPressed();
    }

    public class SelectMembersAdapter extends RecyclerView.Adapter<SelectMembersViewHolder>{

        ArrayList<FriendModelSelectedMap> SelectableFriends = new ArrayList<>();
        //ArrayList<FriendsModel> SelectableFriendsIndexes = new ArrayList<>();
        Context c;

        public ArrayList<FriendModelSelectedMap> getSelectableFriends() {
            return SelectableFriends;
        }

        public SelectMembersAdapter(Context c, ArrayList<FriendModelSelectedMap> SelectableFriends)
        {
            this.SelectableFriends = SelectableFriends;
//            for(Map.Entry<FriendsModel, Boolean> entry : SelectableFriends.entrySet())
//                SelectableFriendsIndexes.add(entry.getKey());
            this.c = c;
        }

        @NonNull
        @Override
        public SelectMembersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View mView;

            View v = LayoutInflater.from(c).inflate(R.layout.single_layout_add_group_member, parent, false);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FriendModelSelectedMap fmMap = SelectableFriends.get(MemberSelect.getChildLayoutPosition(v));
                    fmMap.selected = !fmMap.selected;  //TODO //////////////////////////////////////////////////
                    if (fmMap.selected)
                        selectedPreviewMembers.add(fmMap.getFriendsModel());
                    else
                        selectedPreviewMembers.remove(fmMap.getFriendsModel());
                    previewAdapter.notifyDataSetChanged();
                    notifyDataSetChanged();
                }
            });
            return new SelectMembersViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull SelectMembersViewHolder holder, final int position) {
            holder.setValues(SelectableFriends.get(position).getFriendsModel(), c);
            holder.setCheckbox(SelectableFriends.get(position).getSelected());
        }

        @Override
        public int getItemCount() {
            return SelectableFriends.size();
        }
    }

    public class SelectMembersPreviewAdapter extends RecyclerView.Adapter<SelectedMembersPreviewViewHolder>{

        ArrayList<FriendsModel> SelectableFriends = new ArrayList<>();
        //ArrayList<FriendsModel> SelectableFriendsIndexes = new ArrayList<>();
        Context c;

        public ArrayList<FriendsModel> getSelectedFriends() {
            return SelectableFriends;
        }

        public SelectMembersPreviewAdapter(Context c, ArrayList<FriendsModel> SelectableFriends)
        {
            this.SelectableFriends = SelectableFriends;
//            for(Map.Entry<FriendsModel, Boolean> entry : SelectableFriends.entrySet())
//                SelectableFriendsIndexes.add(entry.getKey());
            this.c = c;
        }

        @NonNull
        @Override
        public SelectedMembersPreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View mView;

            View v = LayoutInflater.from(c).inflate(R.layout.single_layout_add_group_member_preview, parent, false);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FriendsModel model = SelectableFriends.get(MemberSelect.getChildLayoutPosition(v));  //TODO //////////////////////////////////////////////////

                    for(FriendModelSelectedMap fmap : friendsToSelectAsMembers)
                        if(fmap.friendsModel == model)
                        {
                            fmap.setSelected(false);
                            break;
                        }
                    selectedPreviewMembers.remove(model);
                    membersAdapter.notifyDataSetChanged();
                    notifyDataSetChanged();
                }
            });
            return new SelectedMembersPreviewViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull SelectedMembersPreviewViewHolder holder, final int position) {
            holder.setValues(SelectableFriends.get(position), c);
        }

        @Override
        public int getItemCount() {
            return SelectableFriends.size();
        }
    }

    class SelectMembersViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public SelectMembersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setValues(FriendsModel model, Context context)
        {
            ((TextView)mView.findViewById(R.id.add_member_single_name)).setText(model.getName());
            CircleImageView v = ((CircleImageView)mView.findViewById(R.id.add_member_single_thumb));
            Picasso.with(context).load(model.getThumb_image()).placeholder(R.drawable.default_user_black).into(v);
        }

        public void setCheckbox(Boolean ischecked)
        {
            CircleImageView v = ((CircleImageView)mView.findViewById(R.id.add_member_single_checked));
            if (ischecked)
                v.setVisibility(View.VISIBLE);
            else
                v.setVisibility(View.GONE);
        }
    }

    class SelectedMembersPreviewViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public SelectedMembersPreviewViewHolder (View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setValues(FriendsModel model, Context context)
        {
            CircleImageView v = ((CircleImageView)mView.findViewById(R.id.add_group_member_preview_thumb));
            Picasso.with(context).load(model.getThumb_image()).placeholder(R.drawable.default_user_black).into(v);
        }
    }

    class FriendModelSelectedMap
    {
        public FriendsModel getFriendsModel() {
            return friendsModel;
        }

        public void setFriendsModel(FriendsModel friendsModel) {
            this.friendsModel = friendsModel;
        }

        public Boolean getSelected() {
            return selected;
        }

        public void setSelected(Boolean selected) {
            this.selected = selected;
        }

        public FriendModelSelectedMap(FriendsModel friendsModel, Boolean selected) {
            this.friendsModel = friendsModel;
            this.selected = selected;
        }

        private FriendsModel friendsModel;
        private Boolean selected;
    }
}
