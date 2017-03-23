package com.rubenpla.develop.safetynetsample;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.gson.Gson;
import com.rubenpla.develop.safetynetsample.model.JWS;
import com.rubenpla.develop.safetynetsample.model.JWSRequest;
import com.rubenpla.develop.safetynetsample.model.JWSResponse;
import com.rubenpla.develop.safetynetsample.retrofit.RetrofitInterface;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {

    public static final String GOOGLE_API_VERIFY_URL = "https://www.googleapis.com/androidcheck/v1/attestations/";

    public static final String TAG = MainActivity.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;

    private boolean isConnected = false;

    @BindView(R.id.btn_proceed)
     FloatingActionButton mButton;

    @BindView(R.id.card_integrity)
     CardView mCvIntegrity;

    @BindView(R.id.card_cts)
     CardView mCvCTS;

    @BindView(R.id.img_integrity)
     ImageView mIvIntegrity;

    @BindView(R.id.img_cts)
     ImageView mIvCTS;

    @BindView(R.id.progress)
     ProgressBar mProgressBar;

    @BindView(R.id.dialogBtn)
     Button dialogBtn;

/*    @BindView(R.id.snackbar)
    Snackbar snackbar;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViews();
        initClient();
    }

    private void initClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(SafetyNet.API)
                .addConnectionCallbacks(this)
                .build();

        mGoogleApiClient.connect();
    }

    private void initViews() {
        dialogBtn.setText("DialogTile, press me!!");
    }

    @OnClick(R.id.btn_proceed)
    void OnProceedClick(View btnProceed) {
        Log.i(TAG, "OnProceedClick clicked!!");

        if (isConnected) {
            mCvIntegrity.setVisibility(View.INVISIBLE);
            mCvCTS.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            startVerification();
        } else {
            //Snackbar.make(snackbar, "Snackbar!", Snackbar.LENGTH_LONG);
            Toast.makeText(this, "service not Connected!!", Toast.LENGTH_LONG).show();
        }

    }

    @OnClick(R.id.dialogBtn)
    void OnClickDailogTile(View dialogView) {

    }

    private void startVerification() {
        final byte[] nonce = getRequestNonce();

        SafetyNet.SafetyNetApi.attest(mGoogleApiClient, nonce)
                .setResultCallback(new ResultCallback<SafetyNetApi.AttestationResult>() {
                    @Override
                    public void onResult(@NonNull SafetyNetApi.AttestationResult attestationResult) {
                        Status status = attestationResult.getStatus();

                        if (status.isSuccess()) {
                            try {
                                String jwsResult = attestationResult.getJwsResult();
                                verifyOnline(jwsResult);
                            } catch (Exception ex) {
                                Log.e(TAG, "JWSException : " + ex.toString());
                            }
                        } else {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void verifyOnline(final String jwsResult) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(GOOGLE_API_VERIFY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

        JWSRequest request = new JWSRequest();
        request.setSignedAttestation(jwsResult);
        Call<JWSResponse> responseCall = retrofitInterface.getResult(request,
                getString(R.string.safetynet_api_key));

        responseCall.enqueue(new Callback<JWSResponse>() {
            @Override
            public void onResponse(Call<JWSResponse> call, Response<JWSResponse> response) {
                boolean result = response.body().isValidSignature();

                if (result) {
                    decodeJWS(jwsResult);
                } else {
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Verification Error !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JWSResponse> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                Log.d(TAG, "onFailure: "+t.getLocalizedMessage());
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void decodeJWS(String jwsString) {
        byte[] json = Base64.decode(jwsString.split("[.]")[1], Base64.DEFAULT);
        String text = new String(json, StandardCharsets.UTF_8);

        Gson gson = new Gson();
        JWS jws = gson.fromJson(text, JWS.class);

        displayResults(jws.isBasicIntegrity(), jws.isCtsProfileMatch());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        isConnected = true;
    }

    @Override
    public void onConnectionSuspended(int status) {
        switch (status) {
            case CAUSE_NETWORK_LOST:
                Log.e(TAG, "Network connection lost!");
                break;
            case CAUSE_SERVICE_DISCONNECTED:
                Log.e(TAG, "Service is disconnected");
                break;
            default:
                Log.e(TAG, "Generic error, something is wrong!");
                break;
        }

        isConnected = false;
    }

    private byte[] getRequestNonce() {

        String data = String.valueOf(System.currentTimeMillis());

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[24];
        Random random = new Random();
        random.nextBytes(bytes);
        try {
            byteStream.write(bytes);
            byteStream.write(data.getBytes());
        } catch (IOException e) {
            return null;
        }

        return byteStream.toByteArray();
    }

    private void displayResults(boolean integrity, boolean cts) {

        mProgressBar.setVisibility(View.GONE);

        if (integrity) {

            mIvIntegrity.setImageResource(R.drawable.ic_result_ok);
            mCvIntegrity.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorSuccess));

        } else {

            mIvIntegrity.setImageResource(R.drawable.ic_result_error);
            mCvIntegrity.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorFailure));
        }

        mCvIntegrity.setVisibility(View.VISIBLE);

        if (cts) {

            mIvCTS.setImageResource(R.drawable.ic_result_ok);
            mCvCTS.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorSuccess));

        } else {

            mIvCTS.setImageResource(R.drawable.ic_result_error);
            mCvCTS.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorFailure));
        }

        mCvCTS.setVisibility(View.VISIBLE);
    }
}
