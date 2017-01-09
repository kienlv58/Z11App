package vn.com.z11.z11app.Database.Query;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import vn.com.z11.z11app.ApiResponseModel.LoginResponse;
import vn.com.z11.z11app.Database.DataBaseHelper;

/**
 * Created by kienlv58 on 12/6/16.
 */
public class SQLUser {
    private DataBaseHelper dataBaseHelper;
    private static final String TABLE_USER = "user";

    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_GRANT_TYPE = "grant_type";
    private static final String COLUMN_USER_NAME = "name";
    private static final String COLUMN_USER_IMAGE = "image";
    private static final String COLUMN_USER_GENDER = "gender";
    private static final String COLUMN_USER_COIN = "coin";
    private static final String COLUMN_USER_TOKEN = "token";



    private static String sqlUser = "SELECT * "
            + "FROM " + TABLE_USER;

    private static String sqlUserDetail = "SELECT * "
            + "FROM " + TABLE_USER + " WHERE tournament_id = '%s' ";
    public SQLUser(Context context) {
        super();
        // TODO Auto-generated constructor stub
        dataBaseHelper = new DataBaseHelper(context);
    }
    public long insertUser(LoginResponse.Metadata.User user,String token){
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID,user.getId());
        values.put(COLUMN_USER_EMAIL,user.getEmail());
        values.put(COLUMN_USER_GRANT_TYPE,user.getGrant_type());
        values.put(COLUMN_USER_NAME,user.getProfile().getName());
        values.put(COLUMN_USER_IMAGE,user.getProfile().getImage());
        values.put(COLUMN_USER_GENDER,user.getProfile().getGender());
        values.put(COLUMN_USER_COIN,user.getProfile().getCoin());
        values.put(COLUMN_USER_TOKEN,token);
        long result = db.insert(TABLE_USER,null,values);
        return result;
    }

    public LoginResponse.Metadata.User getUser(){
        LoginResponse.Metadata.User user = null;
        Cursor cursor = dataBaseHelper.getReadableDatabase().rawQuery(String.format(sqlUser, "0"),null);
        if(cursor == null)
            return null;
        cursor.moveToFirst();
        int index_id = cursor.getColumnIndex(COLUMN_USER_ID);
        int index_email = cursor.getColumnIndex(COLUMN_USER_EMAIL);
        int index_grant_type = cursor.getColumnIndex(COLUMN_USER_GRANT_TYPE);
        int index_name = cursor.getColumnIndex(COLUMN_USER_NAME);
        int index_image = cursor.getColumnIndex(COLUMN_USER_IMAGE);
        int index_gender = cursor.getColumnIndex(COLUMN_USER_GENDER);
        int index_coin = cursor.getColumnIndex(COLUMN_USER_COIN);

        while (!cursor.isAfterLast()){
            user = new LoginResponse.Metadata.User(cursor.getInt(index_id),cursor.getString(index_email),1,cursor.getString(index_grant_type),new LoginResponse.Metadata.User.Profile(cursor.getString(index_image),
                    cursor.getString(index_name),cursor.getString(index_gender),cursor.getInt(index_coin)));
            cursor.moveToNext();
        }
        cursor.close();
        return user;

    }
    public long updateProfile(LoginResponse.Metadata.User.Profile profile){
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_USER_NAME, profile.getName());
        contentValues.put(COLUMN_USER_IMAGE, profile.getImage());
        contentValues.put(COLUMN_USER_GENDER, profile.getGender());
        contentValues.put(COLUMN_USER_COIN, profile.getCoin());
        return dataBaseHelper.getWritableDatabase().update(TABLE_USER,contentValues,null,null);
    }

    public String getToken(){
        Cursor cursor = dataBaseHelper.getReadableDatabase().rawQuery(sqlUser,null);
        if(cursor == null)
            return null;
        if (cursor.moveToFirst()) {
            int index_token = cursor.getColumnIndex(COLUMN_USER_TOKEN);
            String token = cursor.getString(index_token);
            cursor.close();
            return token;
        }
        return  null;
    }

    public long updateToken(String newToken){
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_USER_TOKEN, newToken);
        return dataBaseHelper.getWritableDatabase().update(TABLE_USER,contentValues,null,null);
    }
    public long updateCoin(int newCoin){
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_USER_COIN, newCoin);
        return dataBaseHelper.getWritableDatabase().update(TABLE_USER,contentValues,null,null);
    }
    public void deleteUser() {
        long result = dataBaseHelper.getWritableDatabase().delete(TABLE_USER, null, null);
    }


}
