package com.espacepiins.messenger.ui;

import android.support.annotation.NonNull;

import com.espacepiins.messenger.model.Contact;

import java.util.List;

/**
 * Created by guillaume on 18-03-07.
 */

public interface ContactAdapterContract {
    void setContacts(@NonNull List<? extends Contact> contacts);
}
