package com.example.alexander.groupup.group;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.alexander.groupup.R;
import com.example.alexander.groupup.models.FriendsModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddGroupMembersActivity extends AppCompatActivity {

    //XML
    private RecyclerView MemberPreview, MemberSelect;
    private EditText addedMembersText;
    private Button Save, Cancel;

    //Firebase
    private DatabaseReference FriendsDatabase;
    private DatabaseReference RequestDatabase;

    //Variables
    private ArrayList<FriendModelSelectedMap> friendsToSelectAsMembers;
    private ArrayList<FriendsModel> selectedPreviewMembers;

    //Adpaters
    private SelectMembersAdapter membersAdapter;
    private SelectMembersPreviewAdapter previewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_members);

        //Add the Toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar_add_member);
        setSupportActionBar(myToolbar);

        RequestDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");

        //Find Views
        MemberPreview = findViewById(R.id.AddGroupMembersPrieview);
        MemberSelect = findViewById(R.id.AddGroupSelectMembers);
        //invitedFriendsTV = findViewById(R.id.invited_friends_tv);
        addedMembersText = findViewById(R.id.textAddedFriends);
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGroupMembersActivity.super.onBackPressed();
            }
        });
        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGroupMembers();
                AddGroupMembersActivity.super.onBackPressed();
            }
        });

        //Setting the Adapter
        MemberPreview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        friendsToSelectAsMembers = new ArrayList<>();
        selectedPreviewMembers = new ArrayList<>();

        membersAdapter = new SelectMembersAdapter(this, friendsToSelectAsMembers);
        previewAdapter = new SelectMembersPreviewAdapter(this, selectedPreviewMembers);

        //Initializing FireBase
        FriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends");

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

                    FirebaseDatabase.getInstance().getReference().child("Groups").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    for (FriendModelSelectedMap fmap : friendsToSelectAsMembers)
                                        if (fmap.friendsModel.getUid().equals(snapshot.getKey())) {
                                            fmap.setSelected(true);
                                            selectedPreviewMembers.add(fmap.friendsModel);
                                            if(selectedPreviewMembers.size() == 1)
                                            {
                                                addedMembersText.setVisibility(View.VISIBLE);
                                                MemberPreview.setVisibility(View.VISIBLE);
                                            }
                                            break;
                                        }
                                }
                                previewAdapter.notifyDataSetChanged();
                                membersAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                String str = e.getMessage();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } catch (Exception e) {
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

    private void saveGroupMembers() {
        Map groupRequestMap = new HashMap();
        groupRequestMap.put("type", "group_invite");
        groupRequestMap.put("from", FirebaseAuth.getInstance().getCurrentUser().getUid());
        groupRequestMap.put("time", ServerValue.TIMESTAMP);

        for(FriendsModel m : previewAdapter.getSelectedFriends())
            RequestDatabase.child(m.getUid()).child(FirebaseAuth.getInstance().getUid()).updateChildren(groupRequestMap);
    }

    public class SelectMembersAdapter extends RecyclerView.Adapter<SelectMembersViewHolder> {

        ArrayList<FriendModelSelectedMap> SelectableFriends = new ArrayList<>();
        //ArrayList<FriendsModel> SelectableFriendsIndexes = new ArrayList<>();
        Context c;

        public ArrayList<FriendModelSelectedMap> getSelectableFriends() {
            return SelectableFriends;
        }

        public SelectMembersAdapter(Context c, ArrayList<FriendModelSelectedMap> SelectableFriends) {
            this.SelectableFriends = SelectableFriends;
            //for(Map.Entry<FriendsModel, Boolean> entry : SelectableFriends.entrySet())
            //SelectableFriendsIndexes.add(entry.getKey());
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
                    if (fmMap.selected) {
                        selectedPreviewMembers.add(fmMap.getFriendsModel());
                        if (selectedPreviewMembers.size() == 1) {
                            addedMembersText.setVisibility(View.VISIBLE);
                            MemberPreview.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        int index = selectedPreviewMembers.indexOf(fmMap.getFriendsModel());
                        selectedPreviewMembers.remove(index);
                        if(selectedPreviewMembers.size() == 0)
                        {
                            addedMembersText.setVisibility(View.GONE);
                            MemberPreview.setVisibility(View.GONE);
                        }
                        previewAdapter.notifyItemRemoved(index);
                    }
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

    public class SelectMembersPreviewAdapter extends RecyclerView.Adapter<SelectedMembersPreviewViewHolder> {

        ArrayList<FriendsModel> SelectableFriends = new ArrayList<>();
        //ArrayList<FriendsModel> SelectableFriendsIndexes = new ArrayList<>();
        Context c;
        private int lastPosition = -1;

        public ArrayList<FriendsModel> getSelectedFriends() {
            return SelectableFriends;
        }

        public SelectMembersPreviewAdapter(Context c, ArrayList<FriendsModel> SelectableFriends) {
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

                    for (FriendModelSelectedMap fmap : friendsToSelectAsMembers) {
                        if (fmap.friendsModel == model) {
                            fmap.setSelected(false);
                            break;
                        }
                    }
                    int index = selectedPreviewMembers.indexOf(model);
                    selectedPreviewMembers.remove(model);
                    if (selectedPreviewMembers.size() == 0) {
                        addedMembersText.setVisibility(View.GONE);
                        MemberPreview.setVisibility(View.GONE);
                    }
                    membersAdapter.notifyItemRemoved(index);
                    membersAdapter.notifyDataSetChanged();
                    notifyDataSetChanged();
                }
            });
            return new SelectedMembersPreviewViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull SelectedMembersPreviewViewHolder holder, final int position) {
            holder.setValues(SelectableFriends.get(position), c);
            setAnimation(holder.itemView, position);
        }

        private void setAnimation(View itemView, int position) {
            if (position > lastPosition)
            {
                Animation animation = AnimationUtils.loadAnimation(c, android.R.anim.fade_in);
                itemView.startAnimation(animation);
                lastPosition = position;
            }
        }

        private void setFadeOutAnimationprivate(View itemView, int position) {
            if (position > lastPosition)
            {
                Animation animation = AnimationUtils.loadAnimation(c, android.R.anim.fade_out);
                itemView.startAnimation(animation);
                lastPosition = -1;
            }
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

        public void setValues(FriendsModel model, Context context) {
            ((TextView) mView.findViewById(R.id.add_member_single_name)).setText(model.getName());
            CircleImageView v = mView.findViewById(R.id.add_member_single_thumb);
            Picasso.with(context).load(model.getThumb_image()).placeholder(R.drawable.default_user_black).into(v);
        }

        public void setCheckbox(Boolean ischecked) {
            CircleImageView v = mView.findViewById(R.id.add_member_single_checked);
            if (ischecked)
                v.setVisibility(View.VISIBLE);
            else
                v.setVisibility(View.GONE);
        }
    }

    class SelectedMembersPreviewViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public SelectedMembersPreviewViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setValues(FriendsModel model, Context context) {
            CircleImageView v = mView.findViewById(R.id.add_group_member_preview_thumb);
            Picasso.with(context).load(model.getThumb_image()).placeholder(R.drawable.default_user_black).into(v);
        }
    }

    class FriendModelSelectedMap {
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