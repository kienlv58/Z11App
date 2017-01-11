package vn.com.z11.z11app;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.com.z11.z11app.Adapter.RestAdapter;
import vn.com.z11.z11app.ApiResponseModel.ChapterResponse;
import vn.com.z11.z11app.ApiResponseModel.ErroResponse;
import vn.com.z11.z11app.RestAPI.ApiChapter;
import vn.com.z11.z11app.RestAPI.ErrorUtils;
import vn.com.z11.z11app.RestAPI.onEventListenter;

public class GroupQuestionActivity extends AppCompatActivity implements onEventListenter {
    ProgressBar progressBar;
    TextView txtv_progess;
    FrameLayout fmContent;
    RelativeLayout loading;
    int chapter_id;
    int qs_pos = 0;
    int total_group_qs = 0;
    int total_wrong = 0;
    int a = 0;
    ArrayList<ChapterResponse.GroupQS> listGroupQS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_question);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.WhiteColor), PorterDuff.Mode.SRC_ATOP);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Question");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        initView();

    }

    public void initView() {
        loading = (RelativeLayout) findViewById(R.id.rl_loading);
        loading.setVisibility(View.VISIBLE);
        Intent groupdata = getIntent();
        chapter_id = groupdata.getIntExtra("chapter_id", 0);
        progressBar = (ProgressBar) findViewById(R.id.progessbar);
        txtv_progess = (TextView) findViewById(R.id.txtv_progess);
        txtv_progess.setText("0%");
        progressBar.setProgress(0);
        progressBar.setMax(100);
        fmContent = (FrameLayout) findViewById(R.id.fmcontent);
        if (chapter_id == 0) {
            //no data
            Toast.makeText(this, "no data", Toast.LENGTH_SHORT).show();
        } else {
            //get api
            getQS();

        }

    }

    public void callFragment(Fragment fragment, ChapterResponse.GroupQS groupQS) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("question", groupQS);
        fragment.setArguments(bundle);
        transaction.replace(R.id.fmcontent, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_layout, null);
        ProgressBar complete = (ProgressBar) v.findViewById(R.id.progessbar);
        TextView txtv_complete = (TextView) v.findViewById(R.id.txtv_progess);
        TextView txtv_total_qs = (TextView) v.findViewById(R.id.txtv_total_qs);
        TextView txtv_total_wrong = (TextView) v.findViewById(R.id.txtv_total_wrong);
        TextView txtv_review = (TextView) v.findViewById(R.id.txtv_review);
        complete.setMax(100);
        complete.setProgress(a);
        txtv_complete.setText(a + "%");
        txtv_total_qs.setText("Tong so cau hoi :" + total_group_qs);
        txtv_total_wrong.setText("So lan tra loi sai: " + total_wrong);

        txtv_review.setText("Danh gia :" + "Chua hoan thanh cac cau hoi");


        AlertDialog.Builder customDialog = new AlertDialog.Builder(this);
        customDialog.setView(getLayoutInflater().inflate(R.layout.dialog_layout, null));
        customDialog.setPositiveButton("Tiep tuc", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        customDialog.setNegativeButton("Thoat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                finish();
                overridePendingTransition(R.anim.fade_in_left, R.anim.fade_out_left);
            }
        });
        customDialog.setCancelable(false);
        customDialog.setView(v);
        AlertDialog alertDialog = customDialog.create();
        alertDialog.show();
        overridePendingTransition(R.anim.fade_in_left, R.anim.fade_out_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getQS() {
        ApiChapter apiChapter = RestAdapter.getClient().create(ApiChapter.class);
        Call<ChapterResponse> chapterResponseCall = apiChapter.getChapter(chapter_id);
        chapterResponseCall.enqueue(new Callback<ChapterResponse>() {
            @Override
            public void onResponse(Call<ChapterResponse> call, Response<ChapterResponse> response) {
                int code = response.code();
                loading.setVisibility(View.INVISIBLE);
                if (code == 200) {
                    ChapterResponse chapter = response.body();
                    listGroupQS = (ArrayList<ChapterResponse.GroupQS>) chapter.chapter.groupQS;
                    if (listGroupQS.size() > 0) {
                        total_group_qs = listGroupQS.size();
                        callFragment(new QuestionFragment(), listGroupQS.get(qs_pos));
                    }


                } else if (code == 400 || code == 404) {
                    ErroResponse erroResponse = ErrorUtils.parseError(response);
                    if (erroResponse.getNewToken() != null) {
                        String token = "Bearer {" + erroResponse.getNewToken().toString() + "}";
                        MainActivity.sqlUser.updateToken(token);
                        RestAdapter.MyauthHeaderContent = token;
                        getQS();
                    }

                    Toast.makeText(GroupQuestionActivity.this, erroResponse.getStatus(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChapterResponse> call, Throwable t) {
                Toast.makeText(GroupQuestionActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void eventNext(int next) {
        total_wrong += next;
        qs_pos++;
        a = (qs_pos * 100) / total_group_qs;
        if (qs_pos < listGroupQS.size()) {
            progressBar.setProgress(a);
            txtv_progess.setText(a + "%");
            callFragment(new QuestionFragment(), listGroupQS.get(qs_pos));
        } else {
            Toast.makeText(GroupQuestionActivity.this, "ban da hoan thanh tat ca cac cau hoi", Toast.LENGTH_SHORT).show();
            progressBar.setProgress(a);
            txtv_progess.setText(a + "%");

            LayoutInflater inflater = getLayoutInflater();
            View v = inflater.inflate(R.layout.dialog_layout, null);
            ProgressBar complete = (ProgressBar) v.findViewById(R.id.progessbar);
            TextView txtv_complete = (TextView) v.findViewById(R.id.txtv_progess);
            TextView txtv_total_qs = (TextView) v.findViewById(R.id.txtv_total_qs);
            TextView txtv_total_wrong = (TextView) v.findViewById(R.id.txtv_total_wrong);
            TextView txtv_review = (TextView) v.findViewById(R.id.txtv_review);
            complete.setMax(100);
            complete.setProgress(a);
            txtv_complete.setText(a + "%");
            txtv_total_qs.setText("Tong so cau hoi :" + total_group_qs);
            txtv_total_wrong.setText("So lan tra loi sai: " + total_wrong);
            if (total_wrong == 0)
                txtv_review.setText("Danh gia :" + "Rat Tot");
            else if (total_wrong <= total_group_qs)
                txtv_review.setText("Danh gia :" + "Tot");
            else if (total_wrong > total_group_qs) {
                txtv_review.setText("Danh gia :" + "Kem");
            }

            AlertDialog.Builder customDialog = new AlertDialog.Builder(this);
            customDialog.setView(getLayoutInflater().inflate(R.layout.dialog_layout, null));
            customDialog.setPositiveButton("Chapter tiep", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            customDialog.setNegativeButton("Tra loi lai", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    Intent intent = getIntent();
                    overridePendingTransition(0, 0);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                }
            });
            customDialog.setCancelable(false);
            customDialog.setView(v);
            AlertDialog alertDialog = customDialog.create();
            alertDialog.show();


        }

    }

}
