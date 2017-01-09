package vn.com.z11.z11app.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import vn.com.z11.z11app.ApiResponseModel.CategoryResponse;
import vn.com.z11.z11app.ApiResponseModel.ListPackageResponse;
import vn.com.z11.z11app.ChapterActibity;
import vn.com.z11.z11app.R;

/**
 * Created by kienlv58 on 12/6/16.
 */
public class PackageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<CategoryResponse.Packages> listpackage;
    ArrayList<ListPackageResponse.Packages> listLession;
    Context context;
    LayoutInflater inflater;

    public PackageAdapter(ArrayList<CategoryResponse.Packages> listpackage, ArrayList<ListPackageResponse.Packages> listLession, Context context) {
        this.listpackage = listpackage;
        this.listLession = listLession;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.package_item, parent, false);
        return new PackageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (listpackage != null) {
            final CategoryResponse.Packages itemPackage = listpackage.get(position);
            PackageViewHolder myHolder = (PackageViewHolder) holder;
            myHolder.txtv_title.setText(itemPackage.translate_name_text.get(0).text_value + "");
            myHolder.txtv_description.setText(itemPackage.translate_describe_text.get(0).text_value);
            myHolder.txtv_price.setText(itemPackage.package_cost + "");
            myHolder.rate.setRating(itemPackage.balance_rate);
            myHolder.btn_unlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Ban muon mua package nay", Toast.LENGTH_SHORT).show();
                }
            });
            myHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent toChapter = new Intent(context, ChapterActibity.class);
                    toChapter.putExtra("package_id", itemPackage.package_id);
                    context.startActivity(toChapter);

                }
            });
        } else if (listLession != null) {
            final ListPackageResponse.Packages itemLession = listLession.get(position);
            PackageViewHolder myHolder = (PackageViewHolder) holder;
            String a = itemLession.translate_name_text.get(0).text_value;
            myHolder.txtv_title.setText(a + "");
            myHolder.txtv_description.setText(itemLession.translate_describe_text.get(0).text_value);
            myHolder.txtv_price.setText(itemLession.package_cost + "");
            myHolder.rate.setRating(itemLession.balance_rate);
            myHolder.btn_unlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Ban muon mua package nay", Toast.LENGTH_SHORT).show();
                }
            });
            myHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent toChapter = new Intent(context, ChapterActibity.class);
                    toChapter.putExtra("package_id", itemLession.package_id);
                    context.startActivity(toChapter);

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if(listLession == null){
            return listpackage.size();
        }else {
            return  listLession.size();
        }
    }

    public class PackageViewHolder extends RecyclerView.ViewHolder {
        TextView txtv_title;
        TextView txtv_description;
        TextView txtv_price;
        Button btn_unlock;
        RatingBar rate;

        public PackageViewHolder(View itemView) {
            super(itemView);
            txtv_title = (TextView) itemView.findViewById(R.id.txtv_title);
            txtv_description = (TextView) itemView.findViewById(R.id.txtv_description);
            txtv_price = (TextView) itemView.findViewById(R.id.txtv_price);
            btn_unlock = (Button) itemView.findViewById(R.id.btn_unlock);
            rate = (RatingBar) itemView.findViewById(R.id.rating_bar);

        }
    }
    public void show(){
        View v = inflater.inflate(R.layout.layout_rate, null);
        RecyclerView avatar = (RecyclerView)v.findViewById(R.id.img_avt);
        TextView txtv_name = (TextView)v.findViewById(R.id.txtv_name);
        TextView txtv_email = (TextView)v.findViewById(R.id.txtv_email);
        RatingBar ratebar = (RatingBar)v.findViewById(R.id.rating_bar);
        TextView txtv_title = (TextView)v.findViewById(R.id.txtv_title);
        TextView txtv_description = (TextView)v.findViewById(R.id.txtv_description);
        RatingBar my_rate = (RatingBar) v.findViewById(R.id.my_rate);
        Button btn_rate = (Button)v.findViewById(R.id.btn_rate);
        Button btn_unlock = (Button)v.findViewById(R.id.btn_unlock);





        AlertDialog.Builder customDialog = new AlertDialog.Builder(context);
        customDialog.setView(v);
        customDialog.setCancelable(true);
        customDialog.setView(v);
        AlertDialog alertDialog = customDialog.create();
        alertDialog.show();
    }
}
