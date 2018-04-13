package com.espacepiins.messenger.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.espacepiins.messenger.model.Room;
import com.espacepiins.messenger.ui.callback.GenericDiffCallback;
import com.espacepiins.messenger.ui.viewmodel.RoomListViewModel;
import com.espacepiins.messsenger.R;
import com.espacepiins.messsenger.databinding.RoomRowBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RoomListFragment.OnRoomInteractionListener} interface
 * to handle interaction events.
 */
public class RoomListFragment extends Fragment {
    interface OnRoomInteractionListener {
        void onRoomSelected(Room room);
    }

    private final String TAG = RoomListFragment.class.getName();

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.emptyView)
    TextView mEmptyView;

    private RoomsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private OnRoomInteractionListener mListener;

    private RoomListViewModel mRoomListViewModel;
    private FirebaseUser mCurrentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.room_list_fragment_layout, container, false);
        ButterKnife.bind(this, view);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mLayoutManager = new LinearLayoutManager(this.getContext());
        mAdapter = new RoomsAdapter();

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mRoomListViewModel = ViewModelProviders.of(this.getActivity()).get(RoomListViewModel.class);

        mRoomListViewModel.getRooms(mCurrentUser.getUid()).observe(this.getActivity(), new Observer<List<Room>>() {
            @Override
            public void onChanged(@Nullable List<Room> rooms) {
                mAdapter.setRooms(rooms);
                if (rooms.size() == 0) {
                    mRecyclerView.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach()");
        if (context instanceof OnRoomInteractionListener) {
            mListener = (OnRoomInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRoomInteractionListener");
        }
    }

    /**
     *
     */
    public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.ViewHolder>{
        private List<Room> mRooms;

        public RoomsAdapter() {
            this.mRooms = new ArrayList<>();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            RoomRowBinding binding;
            ViewHolder(RoomRowBinding binding){
                super(binding.getRoot());
                this.binding = binding;
            }

            public void bind(Room room) {
                binding.setRoom(room);
                binding.executePendingBindings();
            }
        }

        public void setRooms(List<Room> rooms) {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new GenericDiffCallback(this.mRooms, rooms));
            this.mRooms = rooms;
            result.dispatchUpdatesTo(this);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            RoomRowBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.room_row, parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Room room = mRooms.get(position);
            holder.bind(room);
            holder.binding.roomRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onRoomSelected(holder.binding.getRoom());
                }
            });
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
            if(payloads.isEmpty()){
                onBindViewHolder(holder, position);
            }else {
                for(Object data : payloads){
                    switch ((GenericDiffCallback.DiffState) data){
                        case STATE_NEW:
                            holder.binding.roomRowDisplayName.setTypeface(null, Typeface.BOLD_ITALIC);
                            holder.binding.lastMessageTime.setTypeface(null, Typeface.BOLD_ITALIC);
                            holder.binding.roomRowLastMessage.setTypeface(null, Typeface.BOLD_ITALIC);
                            break;
                        default:
                            holder.binding.roomRowDisplayName.setTypeface(null, Typeface.NORMAL);
                            holder.binding.lastMessageTime.setTypeface(null, Typeface.NORMAL);
                            holder.binding.roomRowLastMessage.setTypeface(null, Typeface.NORMAL);
                            break;
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return mRooms == null ? 0 : mRooms.size();
        }
    }
}
