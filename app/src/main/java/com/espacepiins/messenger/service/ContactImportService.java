package com.espacepiins.messenger.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Patterns;

import com.espacepiins.messenger.db.AppDatabase;
import com.espacepiins.messenger.db.entity.ContactEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 *
 * helper methods.
 */
public class ContactImportService extends IntentService {
    private final String TAG = ContactImportService.class.getName();
    private static final String ACTION_CONTACT_IMPORT = "com.espacepiins.messenger.service.action.ACTION_CONTACT_IMPORT";

    private static final String[] PROJECTION = new String[]{
            ContactsContract.Data.LOOKUP_KEY,
            ContactsContract.Data._ID,
            ContactsContract.Data.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Photo.PHOTO_THUMBNAIL_URI
    };

    private static final String SELECTION ="((" +
            ContactsContract.Data.DISPLAY_NAME_PRIMARY + " NOTNULL) AND (" +
            ContactsContract.Data.DISPLAY_NAME_PRIMARY + " != '' ) AND ("+
            ContactsContract.CommonDataKinds.Phone.NUMBER + " NOTNULL) AND (" +
            ContactsContract.CommonDataKinds.Phone.NUMBER + " != '') AND (" +
            ContactsContract.CommonDataKinds.Email.ADDRESS+ " NOTNULL) AND (" +
            ContactsContract.CommonDataKinds.Email.ADDRESS+ " != ''))";
//            ContactsContract.CommonDataKinds.Phone.TYPE + " = '" + ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE + "') AND (" +
//            ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "') AND (" +
//            ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'))";

    public ContactImportService() {
        super("ContactImportService");
    }

    /**
     * Starts this service to perform action ContactImport with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionContactImport(Context context) {
        Intent intent = new Intent(context, ContactImportService.class);
        intent.setAction(ACTION_CONTACT_IMPORT);
        context.startService(intent);
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CONTACT_IMPORT.equals(action)) {
                handleActionContactImport();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionContactImport() {
        Log.i(TAG, "handleActionContactImport");
        ContentResolver contentResolver = getContentResolver();
        Cursor data = contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                PROJECTION,
                SELECTION,
                null,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " COLLATE LOCALIZED ASC");
        List<ContactEntity> extractedContacts = extractContactsFromCursor(data);
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());

        db.contactDao().insertAll(extractedContacts);
    }

    /**
     * Helper method for extracting contact from a cursor
     * @param data a cursor containing contact information
     * @return a list of contacts
     */
    @NonNull
    private List<ContactEntity> extractContactsFromCursor(Cursor data) {
        List<ContactEntity> contacts = new ArrayList<>();

        data.moveToFirst();

        while (data.moveToNext()){
            String _id = data.getString(data.getColumnIndex(ContactsContract.Data._ID));
            String lookupKey = data.getString(data.getColumnIndex(ContactsContract.Data.LOOKUP_KEY));
            String displayName = data.getString(data.getColumnIndex(ContactsContract.Data.DISPLAY_NAME_PRIMARY));
            String emailAddress = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
            String phoneNumber = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String photoThumbnailUri = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_THUMBNAIL_URI));
            Log.i(TAG, "id: " + _id);
            Log.i(TAG, "displayName: " + displayName);
            Log.i(TAG, "emailAddress: " + emailAddress);
            Log.i(TAG, "phoneNumber: " + phoneNumber);
            Log.i(TAG, "lookupKey: " + lookupKey);
            Log.i(TAG, "photoThumbnailUri: " + photoThumbnailUri);

            if(Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches() || Patterns.PHONE.matcher(phoneNumber).matches()){
                contacts.add(new ContactEntity(_id, lookupKey, displayName, null, emailAddress, phoneNumber, photoThumbnailUri));
            }
        }
        return contacts;
    }
}
