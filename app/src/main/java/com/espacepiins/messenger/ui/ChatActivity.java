package com.espacepiins.messenger.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.espacepiins.messenger.R;
import com.espacepiins.messenger.application.Constants;
import com.espacepiins.messenger.databinding.ChatActivityBinding;
import com.espacepiins.messenger.ui.viewmodel.MessagesViewModel;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ChatActivity extends FirebaseAuthAwareActivity implements MessageAdapter.OnProfileSelectionListener {
    public static final String EXTRA_ROOM_ID = "room_id";
    public static final String EXTRA_FROM_ID = "from_id";
    public static final String EXTRA_TO_ID = "to_id";

    private String mRoomId;
    private String mFromId;
    private String mToId;

    private MessageAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private MessagesViewModel mMessagesViewModel;

    @BindView(R.id.chat_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.rc_messages)
    RecyclerView mRecyclerView;

    @BindView(R.id.message_content)
    EditText mMessageEditText;
    @BindView(R.id.btn_send)
    Button mSendBtn;

    ChatActivityBinding mChatActivityBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRoomId = getIntent().getStringExtra(EXTRA_ROOM_ID);
        mFromId = getIntent().getStringExtra(EXTRA_FROM_ID);
        mToId = getIntent().getStringExtra(EXTRA_TO_ID);

        mChatActivityBinding = DataBindingUtil.setContentView(this, R.layout.chat_activity);

        ButterKnife.bind(this, mChatActivityBinding.getRoot());

        setSupportActionBar(mToolbar);

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);

        mAdapter = new MessageAdapter();
        mAdapter.setListener(this);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mSendBtn.setEnabled(false);
                } else {
                    mSendBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        initViewModels();
    }

    private void initViewModels() {
        mMessagesViewModel = ViewModelProviders.of(this).get(MessagesViewModel.class);
        mMessagesViewModel.setRoomID(mRoomId);
        mMessagesViewModel.getMessages().observe(this, messages -> {
            if (messages.size() > 0) {
                mAdapter.setMessages(messages);
                mRecyclerView.postDelayed(() -> {
                    mRecyclerView.smoothScrollToPosition(messages.size());
                }, 1000);
            }
        });

        mMessagesViewModel.getRecipientStatus().observe(this, (connected) -> {
            if (connected != null && connected.booleanValue()) {
                mChatActivityBinding.toStatus.setText("Connecté");
            }
        });

        mMessagesViewModel.getLastOnline().observe(this, (lastOnline) -> {
            if (lastOnline != null && (mMessagesViewModel.getRecipientStatus().getValue() != null && !mMessagesViewModel.getRecipientStatus().getValue().booleanValue())) {
                String relativeDate = DateUtils
                        .getRelativeTimeSpanString(
                                lastOnline,
                                new Date().getTime(),
                                Constants.DEFAULT_DATE_MIN_RESOLUTION).toString();
                mChatActivityBinding.setStatus(relativeDate);
            }
        });

        mMessagesViewModel.getToProfile().observe(this, profile -> mChatActivityBinding.setToProfile(profile));
    }

    @OnClick(R.id.btn_send)
    public void onBtnSendClicked(View view) {
        if (!mMessageEditText.getText().toString().isEmpty()) {
            mMessagesViewModel.postMessage(mMessageEditText.getText().toString());
            mMessageEditText.setText("");
            hideSoftKeyboard();
        }
    }

    @Override
    public void showProfile(String profileId) {
        Intent profileIntent = new Intent(this, ProfileActivity.class);

        profileIntent.putExtra(ProfileActivity.EXTRA_USER_PROFILE_ID, profileId);
        profileIntent.putExtra(ProfileActivity.EXTRA_DISABLE_SIGNOUT, true);
        profileIntent.putExtra(ProfileActivity.EXTRA_READ_ONLY, true);
        profileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(profileIntent);
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromInputMethod(getWindow().getCurrentFocus().getWindowToken(), 0);
        }
    }
}
