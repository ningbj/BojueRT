package com.bowie.routetest.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;

/**
 * Created by ningbj on 2016/9/3.
 */
public class CellUtil {

    private Context context;
    public CellUtil(Context context){
        this.context = context;
    }
    /**
     * 获取基站信息
     */
    public SCell getCellInfo() throws Exception {

        SCell cell = new SCell();
        /** 调用API获取基站信息 */
        TelephonyManager mTelNet = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        CdmaCellLocation location = (CdmaCellLocation) mTelNet.getCellLocation();

        if (location == null)

            throw new Exception("获取基站信息失败");

        String operator = mTelNet.getNetworkOperator();
        int mcc = Integer.parseInt(operator.substring(0, 3));
        int mnc = Integer.parseInt(operator.substring(3));
        int cid = location.getBaseStationId();
        int lac = location.getNetworkId();

        /** 将获得的数据放到结构体中 */
        cell.MCC = mcc;
        cell.MNC = mnc;
        cell.LAC = lac;
        cell.CID = cid;
        return cell;

    }


    /** 基站信息结构体 */
    public class SCell{
        public int MCC;
        public int MNC;
        public int LAC;
        public int CID;
    }


}
