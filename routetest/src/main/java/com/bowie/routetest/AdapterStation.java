package com.bowie.routetest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bowie.routetest.db.StationBean;

import java.util.List;

/**
 * Created by ningbj on 2016/5/26.
 */
public class AdapterStation extends BaseAdapter {

    private Context context;
    List<StationBean> mList;


    public AdapterStation(Context context, List<StationBean> mList) {
        super();
        this.context = context;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mList == null ? 0:mList.size();
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
        public TextView tv_item_station_name;
        public TextView tv_item_station_show;
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
            convertView.setTag(viewHolder);

        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.tv_item_station_name.setText(mList.get(position).name);
        return convertView;
    }

}
