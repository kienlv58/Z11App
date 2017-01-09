package vn.com.z11.z11app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.com.z11.z11app.Adapter.RestAdapter;
import vn.com.z11.z11app.ApiResponseModel.ErroResponse;
import vn.com.z11.z11app.ApiResponseModel.LoginResponse;
import vn.com.z11.z11app.ApiResponseModel.RegisterResponse;
import vn.com.z11.z11app.Database.Query.SQLUser;
import vn.com.z11.z11app.RestAPI.ApiUser;
import vn.com.z11.z11app.RestAPI.ErrorUtils;
import vn.com.z11.z11app.Utilities.AppConstants;
import vn.com.z11.z11app.Utilities.CommonMethod;

public class LoginActivity extends AppCompatActivity {
    RelativeLayout loading;
    EditText edt_email,edt_password;
    Button btn_login, btn_facebook,btn_google;
    TextView txtv_forgotpass,txtv_register;
    SQLUser sqlUser = new SQLUser(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.WhiteColor), PorterDuff.Mode.SRC_ATOP);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Đăng Nhập");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        initView();
        loginPassword();
        register();
    }

    public void initView(){
        loading = (RelativeLayout)findViewById(R.id.rl_loading);
        loading.setVisibility(View.INVISIBLE);
        edt_email = (EditText)findViewById(R.id.edt_email);
        edt_password = (EditText)findViewById(R.id.edt_pass);
        btn_login = (Button)findViewById(R.id.btn_loginAccount);
        btn_facebook = (Button)findViewById(R.id.btn_loginFB);
        btn_google = (Button)findViewById(R.id.btn_loginGoogle);
        txtv_forgotpass = (TextView)findViewById(R.id.txtv_forgotpass);
        txtv_register = (TextView)findViewById(R.id.txtv_register);
    }

    public void loginPassword(){
        final CommonMethod commonMethod = new CommonMethod();
        final ApiUser apiUser =  RestAdapter.getClient().create(ApiUser.class);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edt_email.getText().toString();
                String pass = edt_password.getText().toString();
                Boolean checkEmail = commonMethod.validateEmail(email,LoginActivity.this);
                if(!email.isEmpty()  && checkEmail){
                    if(!pass.isEmpty() && pass.length() >= 6){
                        loading.setVisibility(View.VISIBLE);
                        Call<LoginResponse> loginResponseCall = apiUser.userLogin("password",email,pass,null);
                        loginResponseCall.enqueue(new Callback<LoginResponse>() {
                            @Override
                            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                                loading.setVisibility(View.INVISIBLE);
                                int code = response.code();
                                if(code == 200){
                                    LoginResponse result = response.body();
                                    String token = "Bearer {"+result.getMetadata().getToken()+"}";
                                    sqlUser.insertUser(result.getMetadata().getUser(),token);
                                    Intent intent = new Intent();
                                    intent.putExtra("loginResponse",result);
                                    setResult(AppConstants.LOGIN_KEY,intent);
                                    finish();
                                }
                                else if(code == 400 || code == 404){
                                    ErroResponse erroResponse = ErrorUtils.parseError(response);
                                    Toast.makeText(LoginActivity.this,erroResponse.getStatus(),Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<LoginResponse> call, Throwable t) {
                                Toast.makeText(LoginActivity.this,t.toString(),Toast.LENGTH_SHORT).show();
                                loading.setVisibility(View.INVISIBLE);

                            }
                        });

                    }
                    else {
                        Toast.makeText(LoginActivity.this,"password short",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(LoginActivity.this,"email invalid",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void register(){
        txtv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivityForResult(register,AppConstants.REGISTER_KEY);
                overridePendingTransition(R.anim.fade_in_right, R.anim.fade_out_right);
            }

            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case AppConstants.REGISTER_KEY:
                if(resultCode == 200){
                    Bundle response =  data.getBundleExtra("registerResponse");
                    String email = (String) response.get("email");
                    String password = (String) response.get("password");

                    edt_email.setText(email);
                    edt_password.setText(password);
                }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in_left, R.anim.fade_out_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
