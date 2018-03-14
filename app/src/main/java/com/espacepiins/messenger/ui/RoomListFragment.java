package com.espacepiins.messenger.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.espacepiins.messenger.model.Room;
import com.espacepiins.messsenger.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RoomListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RoomListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomListFragment extends Fragment {
    interface OnRoomInteractionListener {
        void onRoomSelected(Room room);
    }

    private final String TAG = RoomListFragment.class.getName();

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private RoomsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Room> mRoomsDataSet = new ArrayList<>();
    private OnRoomInteractionListener mListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new RoomsAdapter(mRoomsDataSet);
        mLayoutManager = new LinearLayoutManager(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.room_list_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated() - " + this.getActivity().toString());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        seedData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRoomInteractionListener) {
            mListener = (OnRoomInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRoomInteractionListener");
        }
        Log.d(TAG, "onAttach()");
    }

    public void seedData(){
        String[] usernames = new String[]{"etsraphael", "idrick_k", "gmaka"};
        String[] names = new String[]{"Raphaël Etang-Sale", "Idrick Kuisseau", "Guillaume Maka"};
        String[] lastMessages = new String[]{
                "Je suis à la cafétéria",
                "Le cours d'aujourd'hui est annulé",
                "Demain rendez-vous TP Android"
        };

        for(int i=0;i<names.length;i++){
            Room room = new Room();
            room.setFrom(usernames[i]);
            room.setFromDisplayName(names[i]);
            room.setLastMessage(lastMessages[i]);
            mRoomsDataSet.add(room);
        }
        mAdapter.setRooms(mRoomsDataSet);
    }

    /**
     *
     */
    public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.ViewHolder>{
        private List<Room> mRooms;

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.room_row)
            View row;

            @BindView(R.id.room_row_displayName)
            TextView displayName;

            @BindView(R.id.room_row_lastMessage)
            TextView lastMessage;

            @BindView(R.id.room_row_avatar)
            ImageView avatar;

            @BindView(R.id.lastMessageTime)
            TextView lastMessageTime;

            ViewHolder(View v){
                super(v);
                ButterKnife.bind(this, v);
            }
        }

        public RoomsAdapter(List<Room> rooms) {
            mRooms = rooms;
        }

        public void setRooms(List<Room> rooms){
            this.mRooms = rooms;
            this.notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewGroup view = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.room_row, parent, false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Room room = mRooms.get(position);
            holder.displayName.setText(room.getFromDisplayName());
            holder.lastMessage.setText(room.getLastMessage());
            holder.row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onRoomSelected(room);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mRooms.size();
        }
    }
}
