package com.trinetix.app.API;

import android.os.AsyncTask;
import android.widget.Toast;

import com.trinetix.app.PasswordActivity;
import com.trinetix.app.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Обращаемся к API и получаем JSON.
 * Created by fess on 5/19/14.
 */
public class ApiHelper {
    private final String baseUrl;
    private final String paramName;
    private final String somethingWentWrong;

    private final PasswordActivity activity;

    public ApiHelper(PasswordActivity activity) {
        this.activity = activity;
        baseUrl = activity.getResources().getString(R.string.api_base_url);
        paramName = activity.getResources().getString(R.string.api_param_name);
        somethingWentWrong = activity.getResources().getString(R.string.something_went_wrong);
    }

    public void makeApiRequest(String password) {
        String url = baseUrl + "?" + paramName + "=" + password;
        new ApiRequestTask().execute(url);
    }

    private class ApiRequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                } else {
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (IOException e) {
                Toast.makeText(activity, somethingWentWrong, Toast.LENGTH_SHORT).show();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            activity.productsLoaded(result);
        }
    }
}
