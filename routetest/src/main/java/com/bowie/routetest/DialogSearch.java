package com.bowie.routetest;



import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bowie.routetest.db.AssetsDatabaseManager;
import com.bowie.routetest.db.StationBean;

import java.util.List;

import views.ModelDialog;

/**
 * Created by ningbj on 2016/5/23.
 */
public class DialogSearch {

    private ModelDialog modelDialog;
    private Context context;
    private OnSearchListener onSearchListener;
    RadioGroup rg;
    RadioButton radio_CTCC, radio_CMCC, radio_CUCC;
    ImageView img_back;
    EditText et_station_name, et_station_code_lac, et_station_code_ci, et_station_adr;
    Button btn_search_type, btn_search;
    LinearLayout lt_exact, lt_fuzzy;
    private int type = 0;
    private String codeLAC = "";
    private String codeCI = "";
    private String name = "";
    private String adr = "";
    private boolean isExactSearch = true;

    public DialogSearch(Context context, OnSearchListener onSearchListener){
        this.context = context;
        this.onSearchListener = onSearchListener;
    }

    public void open(){
        modelDialog = new ModelDialog((Activity)context,R.layout.dialog_simple_search, R.style.AppTheme);
        findView(modelDialog);
        modelDialog.show();
    }

    private void findView(final ModelDialog modelDialog){
        img_back = (ImageView)modelDialog.findViewById(R.id.img_back);
        radio_CTCC = (RadioButton)modelDialog.findViewById(R.id.radio_CTCC);
        radio_CMCC = (RadioButton)modelDialog.findViewById(R.id.radio_CMCC);
        radio_CUCC = (RadioButton)modelDialog.findViewById(R.id.radio_CTCC);
        rg = (RadioGroup)modelDialog.findViewById(R.id.rg);
        et_station_name = (EditText)modelDialog.findViewById(R.id.et_station_name);
        et_station_adr = (EditText)modelDialog.findViewById(R.id.et_station_adr);
        et_station_code_lac = (EditText)modelDialog.findViewById(R.id.et_station_code_lac);
        et_station_code_ci = (EditText)modelDialog.findViewById(R.id.et_station_code_ci);
        lt_exact = (LinearLayout) modelDialog.findViewById(R.id.lt_exact);
        lt_fuzzy = (LinearLayout) modelDialog.findViewById(R.id.lt_fuzzy);
        btn_search = (Button)modelDialog.findViewById(R.id.btn_search);
        btn_search_type = (Button)modelDialog.findViewById(R.id.btn_search_type);
        btn_search_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isExactSearch){
                    btn_search_type.setText("切换精确查找");
                    isExactSearch = false;
                    lt_fuzzy.setVisibility(View.VISIBLE);
                    lt_exact.setVisibility(View.GONE);
                    btn_search.setText("模糊查询");
                }else{
                    btn_search_type.setText("切换模糊查找");
                    isExactSearch = true;
                    lt_fuzzy.setVisibility(View.GONE);
                    lt_exact.setVisibility(View.VISIBLE);
                    btn_search.setText("精确查询");
                }
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                search();
                actionSearch();
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modelDialog.dismiss();
            }
        });

        rg.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    private void search(){
        if(TextUtils.isEmpty(et_station_code_lac.getText().toString())){
            if(TextUtils.isEmpty(et_station_name.getText().toString()) && TextUtils.isEmpty(et_station_adr.getText().toString())){
                Toast.makeText(context, "请完善查询条件", Toast.LENGTH_SHORT).show();
            }else{
                name = et_station_name.getText().toString();
                adr = et_station_adr.getText().toString();
            }
        }else{
            if(et_station_code_lac.getText().toString().length() > 4){
                codeLAC = et_station_code_lac.getText().toString();
                codeCI = et_station_code_ci.getText().toString();
                List<StationBean> list = AssetsDatabaseManager.selectByCode(type, codeLAC, codeCI);
                if(list != null){
                    onSearchListener.onRest(list);
                    modelDialog.dismiss();
                }else{
                    Toast.makeText(context, "查找不到此基站信息", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(context, "基站代码不能小于5位", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void actionSearch(){
        if(checkSearchCondition()){
            List<StationBean> list;
            if(isExactSearch){
                list = AssetsDatabaseManager.selectByCode(type, codeLAC, codeCI);
            }else{
                list = AssetsDatabaseManager.selectByNameAdr(type, name, adr);
            }
            if(list != null){
                onSearchListener.onRest(list);
                modelDialog.dismiss();
            }else{
                Toast.makeText(context, "查找不到此基站信息", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 检查查询条件
     * */
    private boolean checkSearchCondition(){
        boolean isValidate = false;
        name = et_station_name.getText().toString();
        adr = et_station_adr.getText().toString();
        codeLAC = et_station_code_lac.getText().toString();
        codeCI = et_station_code_ci.getText().toString();
        if(isExactSearch){
            if(TextUtils.isEmpty(codeLAC) || TextUtils.isEmpty(codeCI)){
                isValidate = false;
                Toast.makeText(context,"请完善查询条件",Toast.LENGTH_SHORT).show();
            }else{
                if(codeLAC.length() == 5 && codeCI.length() == 5){
                    isValidate = true;
                }else{
                    Toast.makeText(context,"基站必须均为5位数",Toast.LENGTH_SHORT).show();
                    isValidate = false;
                }
            }
        }else{
            if(TextUtils.isEmpty(name) && TextUtils.isEmpty(adr)){
                isValidate = false;
                Toast.makeText(context,"请完善查询条件",Toast.LENGTH_SHORT).show();
            }else {
                isValidate = true;
            }
        }
        return  isValidate;
    }

    RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if(checkedId == radio_CMCC.getId()){
                type = 0;
            }else if(checkedId == radio_CTCC.getId()){
                type = 1;
            }else{
                type = 2;
            }
        }
    };

    public interface OnSearchListener{
        void onRest(List<StationBean> stations);
    }
}
