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
import android.widget.Toast;

import com.espacepiins.messenger.R;
import com.espacepiins.messenger.databinding.RoomRowBinding;
import com.espacepiins.messenger.db.entity.RoomEntity;
import com.espacepiins.messenger.ui.callback.GenericDiffCallback;
import com.espacepiins.messenger.ui.viewmodel.RoomListViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RoomListFragment.OnRoomInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RoomListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomListFragment extends Fragment {
    interface OnRoomInteractionListener {
        void onRoomSelected(RoomEntity room);
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLayoutManager = new LinearLayoutManager(this.getContext());
        mAdapter = new RoomsAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.room_list_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        mRecyclerView.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRoomInteractionListener) {
            mListener = (OnRoomInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRoomInteractionListener");
        }

        mRoomListViewModel = ViewModelProviders.of(this.getActivity()).get(RoomListViewModel.class);

        mRoomListViewModel.getRooms().observe(this.getActivity(), new Observer<List<RoomEntity>>() {
            @Override
            public void onChanged(@Nullable List<RoomEntity> rooms) {
                mAdapter.setRooms(rooms);
                if(rooms.size() == 0){
                    mRecyclerView.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                }else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        });

        Log.d(TAG, "onAttach()");
    }

    /**
     *
     */
    public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.ViewHolder>{
        private List<RoomEntity> mRooms;

        public RoomsAdapter() {
            this.mRooms = new ArrayList<>();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            RoomRowBinding binding;
            ViewHolder(RoomRowBinding binding){
                super(binding.getRoot());
                this.binding = binding;
            }

            public void bind(RoomEntity room){
                binding.setRoom(room);
                binding.executePendingBindings();
            }
        }

        public void setRooms(List<RoomEntity> rooms){
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new GenericDiffCallback(this.mRooms, rooms));
            this.mRooms = rooms;
            result.dispatchUpdatesTo(this);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            RoomRowBinding binding = DataBindingUtil.inflate(layoutInflater, viewType, parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final RoomEntity room = mRooms.get(position);
            holder.bind(room);
            holder.binding.roomRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onRoomSelected(holder.binding.getRoom());
                    Toast.makeText(getActivity(), "Implementer dans le jalon 3", Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
            if(payloads.isEmpty()){
                onBindViewHolder(holder, position);
            }else {
                RoomEntity roomEntity = this.mRooms.get(position);
                for(Object data : payloads){
                    switch ((GenericDiffCallback.DiffState) data){
                        case STATE_NEW:
                            holder.binding.roomRowDisplayName.setTypeface(null, Typeface.BOLD_ITALIC);
                            holder.binding.lastMessageTime.setTypeface(null, Typeface.BOLD_ITALIC);
                            holder.binding.roomRowLastMessage.setTypeface(null, Typeface.BOLD_ITALIC);
                            break;
                        default:
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
