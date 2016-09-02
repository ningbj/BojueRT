package com.bowie.routetest;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.model.Marker;

import views.ModelDialog;

/**
 * Created by ningbj on 2016/9/2.
 */
public class DialogMaker {

    private ModelDialog modelDialog;
    private Context context;
    private Marker marker;
    private OnMakerSetListener onMakerSetListener;

    public DialogMaker(Context context, Marker marker, OnMakerSetListener onMakerSetListener){
        this.context = context;
        this.marker = marker;
        this.onMakerSetListener = onMakerSetListener;
    }

    public void open(){
        modelDialog = new ModelDialog((Activity) context,R.layout.dialog_marker,R.style.Theme_dialog,true);
        findDialog(modelDialog);
        modelDialog.show();
    }

    private void findDialog(ModelDialog modelDialog){
        TextView btn_start_point = (TextView)modelDialog.findViewById(R.id.btn_start_point);
        TextView btn_end_point = (TextView)modelDialog.findViewById(R.id.btn_end_point);
        btn_start_point.setOnClickListener(onClickListener);
        btn_end_point.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            modelDialog.dismiss();
            switch (v.getId()){
                case R.id.btn_start_point:
                    onMakerSetListener.onStartPoint(marker);
                    break;
                case R.id.btn_end_point:
                    onMakerSetListener.onEndPoint(marker);
                    break;
            }
        }
    };

    public interface OnMakerSetListener{
        public void onStartPoint(Marker marker);
        public void onEndPoint(Marker marker);

    }
}
