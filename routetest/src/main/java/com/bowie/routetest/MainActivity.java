package com.bowie.routetest;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.bowie.routetest.db.StationBean;
import com.bowie.routetest.utils.CellUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements LocationSource,
        AMapLocationListener, CompoundButton.OnCheckedChangeListener {

    MapView mMapView = null;
    private AMap aMap;
    public AMapLocationClientOption mLocationOption = null;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    private OnLocationChangedListener mListener;
    private ListView lv;
    private TextView tv_distance;
    private AdapterStation adapter;
    private ArrayList<MarkerOptions> listMarkers;
    private ArrayList<Marker> listMarker;
    private LatLng startPoint, endPoint;
    private String startPointName, endPointName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mMapView.onCreate(savedInstanceState);
        initActivity();
    }

    private void initActivity(){
        findView();
        getLocation();
        location();
    }

    private void findView(){
        lv = (ListView)findViewById(R.id.lv_station);
        tv_distance = (TextView)findViewById(R.id.tv_distance);
    }

    private void getCell(){
        CellUtil cellUtil = new CellUtil(this);
        try {
            CellUtil.SCell sCell = cellUtil.getCellInfo();
            Log.e("SCELL", "LAC : " + sCell.LAC + "   CID : " + sCell.CID);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", e.toString());
        }

    }


    private void getLocation() {
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(60000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    private void location() {
        aMap = mMapView.getMap();
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式，参见类AMap。
        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
        aMap.moveCamera(CameraUpdateFactory.zoomBy(12));
    }

    /**
     * 绘制当前所属基站
     * */
    private void markerCurrentStation(LatLng myposition){
        CellUtil cellUtil = new CellUtil(this);
        StationBean stationBean = cellUtil.getCurrwntStation();
        View view = View.inflate(this,R.layout.item_marker, null);
        TextView tv_marker = (TextView)view.findViewById(R.id.tv_marker);
        ImageView img_sign = (ImageView)view.findViewById(R.id.img_sign);
        img_sign.setImageDrawable(getResources().getDrawable(R.mipmap.ic_training_32dp));
        tv_marker.setText("当前基站");
        Bitmap bitmap = convertViewToBitmap(view);
        LatLng latlng = LatLngUtil.transformFromWGSToGCJ(new LatLng(stationBean.lat, stationBean.lng));

        MarkerOptions makeerOption = new MarkerOptions();
        makeerOption.position(latlng);
        makeerOption.title("当前基站：" + stationBean.name);
        makeerOption.snippet(stationBean.adr);
        makeerOption.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        aMap.addMarker(makeerOption);
        aMap.setOnMarkerClickListener(onMarkerClickListener);
        LatLng latLng = new LatLng(stationBean.lat, stationBean.lng);
        drawCirle(myposition, latLng);
    }

    private void drawCirle(LatLng myposition, LatLng currentStation){
        float distance = calculateDistance(myposition, currentStation);
        CircleOptions circleOption = new CircleOptions();
        circleOption.center(currentStation);
        circleOption.fillColor(R.color.colorBlack50);
        circleOption.radius(distance);
        aMap.addCircle(circleOption);
    }

    private LatLng latLngConvert(LatLng latLng){
        CoordinateConverter converter  = new CoordinateConverter(this);
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(latLng);
        return converter.convert();
    }

    private void mapMaker(List<StationBean> list) {
        aMap.clear(true);
        listMarkers = new ArrayList<>();
        for (int i = 0; i < list.size();i++){
            View view = View.inflate(this,R.layout.item_marker, null);
            TextView tv_marker = (TextView)view.findViewById(R.id.tv_marker);
//            tv_marker.setText(list.get(i).name);
            Bitmap bitmap = convertViewToBitmap(view);
            LatLng latlng = LatLngUtil.transformFromWGSToGCJ(new LatLng(list.get(i).lat, list.get(i).lng));
            MarkerOptions makeerOption = new MarkerOptions();
            makeerOption.position(latlng);
            makeerOption.title(list.get(i).name);
            makeerOption.snippet(list.get(i).adr);
            makeerOption.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
            listMarkers.add(makeerOption);
        }
        listMarker = aMap.addMarkers(listMarkers, true);
        aMap.setOnMarkerClickListener(onMarkerClickListener);
    }

    //view 转bitmap
    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();


        return bitmap;
    }

    private void newc(){
        aMap.clear(true);
    }



    protected Bitmap getMyBitmap(String pm_val) {
        Bitmap bitmap = BitmapDescriptorFactory.fromResource(
                R.mipmap.redpoint).getBitmap();
        bitmap = Bitmap.createBitmap(bitmap, 0 ,0, bitmap.getWidth(),
                bitmap.getHeight());
        Canvas canvas = new Canvas(bitmap);
        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(15f);
        textPaint.setColor(getResources().getColor(R.color.colorPrimary));
        canvas.drawText(pm_val, 1, 35 ,textPaint);// 设置bitmap上面的文字位置


        return bitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getCity();//城市信息
                amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码
                Log.e("location", amapLocation.toStr());
                mListener.onLocationChanged(amapLocation);
                aMap.moveCamera(CameraUpdateFactory.zoomBy(12));
                LatLng myposition = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                markerCurrentStation(myposition);
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    public void actionSearch(View view){
        DialogSearch dialogSearch = new DialogSearch(this, onSearchListener);
        dialogSearch.open();
    }

    public void actionClear(View view){
        aMap.clear(true);
        if(adapter != null){
            adapter.clear();
        }
    }

    DialogSearch.OnSearchListener onSearchListener = new DialogSearch.OnSearchListener() {
        @Override
        public void onRest(List<StationBean> stations) {
            setListView(stations);
            mapMaker(stations);
        }
    };

    private void setListView(List<StationBean> stations){
        adapter = new AdapterStation(this, stations, onClickListener);
        lv.setAdapter(adapter);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag(R.id.tag_position);
            switch (v.getId()){
                case R.id.tv_item_station_show:
                    if(listMarker.get(position).isVisible()){
                        listMarker.get(position).setVisible(false);
                        adapter.setVisible(position,false);
                    }else{
                        listMarker.get(position).setVisible(true);
                        adapter.setVisible(position,true);
                    }
                    break;
                case R.id.lt_station:
                    LatLng latLng = listMarker.get(position).getPosition();
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
                    break;
                default:
                    break;
            }
        }
    };

    AMap.OnMarkerClickListener onMarkerClickListener = new AMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            DialogMaker dialogMaker = new DialogMaker(MainActivity.this, marker, onMakerSetListener);
            dialogMaker.open();
            return false;

        }
    };

    DialogMaker.OnMakerSetListener onMakerSetListener = new DialogMaker.OnMakerSetListener() {
        @Override
        public void onStartPoint(Marker marker) {
            Log.e("OnMakerSetListener", "起点位置 : " + marker.getPosition().latitude + " : " + marker.getPosition().longitude);
            startPoint = marker.getPosition();
            startPointName = marker.getTitle();
            if (startPointName == null){
                startPointName = "当前位置";
            }
            showDistance();
        }

        @Override
        public void onEndPoint(Marker marker) {
            Log.e("OnMakerSetListener", "终点位置 : " + marker.getPosition().latitude + " : " + marker.getPosition().longitude);
            endPoint = marker.getPosition();
            endPointName = marker.getTitle();
            if(endPointName == null){
                endPointName = "当前位置";
            }
            showDistance();
        }
    };

    private void showDistance(){
        if(startPoint != null && endPoint != null){
            float distance = calculateDistance(startPoint,endPoint);
            Log.e("OnMakerSetListener", "两地距离 : " + distance + " 米");
            tv_distance.setText(startPointName + " 距离 " + endPointName + " " + distance + " 米");
        }
    }

    private float calculateDistance(LatLng startPoint, LatLng endPoint){
        AMapUtils aMapUtils = new AMapUtils();
        float distance = aMapUtils.calculateLineDistance(startPoint,endPoint);
        return distance;
    }



}
