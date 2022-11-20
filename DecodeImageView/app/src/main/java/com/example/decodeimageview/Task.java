package com.example.decodeimageview;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Task extends AsyncTask<String, String, Object> {

    private AsyncResponse asyncResponse;
    private String method;
    private Connection connection;
    private PreparedStatement statement;
    private ResultSet resultSet;
    private String TAG = "TASK_DEBUG";
    public  static String GET_IMG_BLOB = "getImgBlob";

    public Task(String method, AsyncResponse asyncResponse) {
        this.method = method;
        this.asyncResponse = asyncResponse;
    }

    private void setConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://192.168.1.6:3306/menubytes", "admin", "admin");
        } catch (Exception e) {
            Log.i("DATABASE CONNECTION:", e.toString());
        }
    }

    @Override
    protected Object doInBackground(String... strings) {
        setConnection();
        if(connection!=null){
            try{
                if (method.equals(GET_IMG_BLOB)) {
                    byte[] bytes = null;
                    statement = connection.prepareStatement("SELECT payment_qr FROM payment_method;");
                    resultSet = statement.executeQuery();
                    if (!resultSet.isBeforeFirst()) {
                        Log.d(TAG, "NO IMG DATA FOUND");
                    } else {
                        Log.d(TAG, "IMG DATA FOUND");
                            while(resultSet.next()){
                                bytes = resultSet.getBytes(1);
                            }
                        return bytes;
                    }

                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        try {
            super.onPostExecute(o);
            if(o!=null){
                asyncResponse.onFinish(o);
            }else{asyncResponse.onFinish(null);}
        }
        catch (Exception e){
            Log.i("onPostExecute", e.toString());
        }
    }

}
