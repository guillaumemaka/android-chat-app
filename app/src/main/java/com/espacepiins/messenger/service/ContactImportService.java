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

import com.espacepiins.messenger.BuildConfig;
import com.espacepiins.messenger.db.AppDatabase;
import com.espacepiins.messenger.db.entity.ContactEntity;
import com.espacepiins.messenger.db.entity.EmailEntity;
import com.espacepiins.messenger.db.entity.PhoneEntity;

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
            ContactsContract.CommonDataKinds.Phone.TYPE,
            ContactsContract.CommonDataKinds.Email.LABEL,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.TYPE,
            ContactsContract.CommonDataKinds.Phone.LABEL,
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
     * Starts this service to perform action ACTION_CONTACT_IMPORT with the given parameters. If
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
//                brooadcast();
                handleActionContactImport();
            }
        }
    }

    private void brooadcast(){
        Intent intent = new Intent();
        intent.setAction(ContactImportReceiver.CONTACT_IMPORTED_ACTION);
        sendBroadcast(intent);
    }

    /**
     * Handle action ACTION_CONTACT_IMPORT in the provided background thread with the provided
     * parameters.
     */
    private void handleActionContactImport() {
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());

//        if(BuildConfig.DEBUG){
//            db.contactDao().deleteAll();
//        }

        Log.i(TAG, "handleActionContactImport");
        ContentResolver contentResolver = getContentResolver();
        Cursor data = contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                PROJECTION,
                SELECTION,
                null,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " COLLATE LOCALIZED ASC");
        extractContactsFromCursor(db, data);
    }

    /**
     * Helper method for extracting contact from a cursor
     * @param data a cursor containing contact information
     * @return a list of contacts
     */
    @NonNull
    private void extractContactsFromCursor(AppDatabase db, Cursor data) {
        List<ContactEntity> contacts = new ArrayList<>();
        List<EmailEntity> emails = new ArrayList<>();
        List<PhoneEntity> phones = new ArrayList<>();

        data.moveToFirst();

        while (data.moveToNext()){
            String _id = data.getString(data.getColumnIndex(ContactsContract.Data._ID));
            String lookupKey = data.getString(data.getColumnIndex(ContactsContract.Data.LOOKUP_KEY));
            String displayName = data.getString(data.getColumnIndex(ContactsContract.Data.DISPLAY_NAME_PRIMARY));
            String emailAddress = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
            String emailAddressType = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
            String emailAddressLabel = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Email.LABEL));
            String phoneNumber = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String phoneNumberType = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            String phoneNumberLabel = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
            String photoThumbnailUri = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_THUMBNAIL_URI));

            if(phoneNumber != null || emailAddress != null)
            {
                if(BuildConfig.DEBUG){
                    Log.i(TAG, "id: " + _id);
                    Log.i(TAG, "displayName: " + displayName);
                    Log.i(TAG, "emailAddress: " + emailAddress);
                    Log.i(TAG, "emailAddressType: " + emailAddressType);
                    Log.i(TAG, "emailAddressLabel: " + emailAddressLabel);
                    Log.i(TAG, "phoneNumber: " + phoneNumber);
                    Log.i(TAG, "phoneNumberType: " + phoneNumberType);
                    Log.i(TAG, "phoneNumberLabel: " + phoneNumberLabel);
                    Log.i(TAG, "lookupKey: " + lookupKey);
                    Log.i(TAG, "photoThumbnailUri: " + photoThumbnailUri);
                }

                if(Patterns.PHONE.matcher(phoneNumber).matches()){
                    final PhoneEntity phoneEntity = new PhoneEntity();
                    phoneEntity.setId(String.format("%s_%s", _id, phoneNumberType));
                    phoneEntity.setPhoneNumber(phoneNumber);
                    phoneEntity.setPhoneType(phoneNumberType);
                    phoneEntity.setContactLookupKey(lookupKey);
                    phones.add(phoneEntity);
                }


                if(Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()){
                    final EmailEntity emailEntity = new EmailEntity();
                    emailEntity.setId(String.format("%s_%s", _id, emailAddressType));
                    emailEntity.setEmailAddress(emailAddress);
                    emailEntity.setEmailType(emailAddressType);
                    emailEntity.setContactLookupKey(lookupKey);

                    emails.add(emailEntity);
                }

                final ContactEntity contactEntity = new ContactEntity();

                contactEntity.setDisplayName(displayName);
                contactEntity.setId(_id);
                contactEntity.setLookupKey(lookupKey);
                contactEntity.setPhotoThumbnailUri(photoThumbnailUri);

                contacts.add(contactEntity);
            }
        }

        db.contactDao().insertContacts(contacts);
        db.contactDao().insertEmails(emails);
        db.contactDao().insertPhones(phones);

        Log.d(TAG, "Search empty size: " + db.contactDao().search("").size());
        Log.d(TAG, "Search 'adam' size: " + db.contactDao().search("adam").size());
    }
}
