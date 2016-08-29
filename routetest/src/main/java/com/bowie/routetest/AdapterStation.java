package com.bowie.routetest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bowie.routetest.db.StationBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ningbj on 2016/5/26.
 */
public class AdapterStation extends BaseAdapter {

    private Context context;
    private View.OnClickListener onClick;
    private List<StationBean> mList;
    private List<Boolean> mListVisible = new ArrayList<>();


    public AdapterStation(Context context, List<StationBean> mList, View.OnClickListener onClick) {
        super();
        this.context = context;
        this.mList = mList;
        this.onClick = onClick;
        if(mList != null){
            initVisible();
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mList == null ? 0:mList.size();
    }

    private void initVisible(){
        if(mList.size() > 0){
            for (int i = 0; i < mList.size(); i++){
                mListVisible.add(true);
            }
        }
    }

    public void setVisible(int position, boolean isVisible){
        mListVisible.set(position,isVisible);
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int i) {
        // TODO Auto-generated method stub
        return i;
    }

    @Override
    public long getItemId(int i) {
        // TODO Auto-generated method stub
        return 0;
    }

    public class ViewHolder{
        TextView tv_item_station_name;
        TextView tv_item_station_show;
        LinearLayout lt_station;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder = null;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_station, null);
            viewHolder.tv_item_station_name=(TextView)convertView.findViewById(R.id.tv_item_station_name);
            viewHolder.tv_item_station_show=(TextView)convertView.findViewById(R.id.tv_item_station_show);
            viewHolder.lt_station = (LinearLayout)convertView.findViewById(R.id.lt_station);
            convertView.setTag(viewHolder);

        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.tv_item_station_name.setText(mList.get(position).name);
        viewHolder.tv_item_station_show.setTag(R.id.tag_position,position);
        viewHolder.lt_station.setTag(R.id.tag_position,position);
        viewHolder.tv_item_station_show.setOnClickListener(onClick);
        viewHolder.lt_station.setOnClickListener(onClick);
        if(mListVisible.get(position)){
            viewHolder.tv_item_station_show.setText("隐藏");
        }else{
            viewHolder.tv_item_station_show.setText("显示");
        }
        return convertView;
    }

}
