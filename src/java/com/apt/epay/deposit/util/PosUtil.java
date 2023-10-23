/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.deposit.util;

import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.deposit.bean.PincodeOrderReqBean;
import com.apt.epay.deposit.bean.ServiceOrderReqBean;
import com.apt.epay.nokia.bean.NokiaSubscribeBalanceBean;
import com.apt.epay.nokia.main.NokiaMainBasicInfoUtil;
import com.apt.epay.share.ShareParm;
import com.apt.util.PPMdnUtil;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_PROMOTIONCODE;
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.log4j.Logger;

/**
 *
 * @author kevinchang
 */
public class PosUtil {

    private final Logger log;

    public PosUtil() {
        this.log = Logger.getLogger("EPAY");
    }

    public boolean insertPincodeOrderTransaction(PPMdnUtil ppmdn, PincodeOrderReqBean bean, String libm, String cpid, EPayBusinessConreoller epaybusinesscontroller) throws Exception {

        boolean result = false;
//        String itemCode = ShareParm.PINCODE_ITEMCODE;
        Calendar nowDateTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
        String tradedate = sdf.format(nowDateTime.getTime());
        String itemCode = ShareParm.PINCODE_ITEMCODE; //PinCode
        String itemName = ShareParm.PINCODE_ITEMNAME; //實體卡片儲值
        int tradeAmount = 0;// PinCode實體卡片儲值金額為null, 我們不會知道該實體卡片實際金額(Price)
        int tradeQuantity = 1;
        String orderTotal = String.valueOf(tradeAmount * tradeQuantity);

        EPAY_PROMOTIONCODE epaypromotioncode = new EPAY_PROMOTIONCODE();
        log.info(libm + "==>ppmdn.getPromotioncode():" + ppmdn.getPromotioncode());
        epaypromotioncode = epaybusinesscontroller.getPomtionCode(ppmdn.getPromotioncode());
        int platformtype = epaypromotioncode.getPlatformtype();

        try {
            //get serviceinfo data
            String cplibm = bean.getCplibm();
//            EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(Integer.valueOf(cpid));
//            String cpname = cpinfo.getCpName();

            EPAY_TRANSACTION trans = epaybusinesscontroller.getTransaction(libm);

            if (trans == null && !"".equalsIgnoreCase(libm) && !"null".equalsIgnoreCase(libm)) {

                trans = new EPAY_TRANSACTION();
                trans.setLibm(libm);
                trans.setItemcode(itemCode);//PinCode
                trans.setItemproductname(itemName);//實體卡片儲值
//                trans.setItemunitprice(Integer.parseInt(itemUnitPrice));//0
                trans.setItemquantity(tradeQuantity);//1
                trans.setOrdertotal(Integer.parseInt(orderTotal));//0
                trans.setFee(0);
                trans.setDiscount(0);
                trans.setTradedate(sdf.parse(tradedate));
                trans.setPaytime(sdf.parse(tradedate));
                trans.setPaymethod(ShareParm.PAYMETHOD_PINCODE); //付款方式 PinCode=4
                trans.setStatus("N"); //OCS尚未儲值完成

//                trans.setPaystatus(""); //0:失敗(default value), 但PinCode實體卡已繳費完成，所以狀態為1
                trans.setPrivatedata(bean.getPincode()); //PinCode number

                trans.setServiceId(ShareParm.PINCODE_SERVICEID);//
                trans.setCpLibm(libm);
//                trans.setCpId(Integer.parseInt(new ShareParm().PARM_EPAY_CPID));//
                trans.setCpId(Integer.valueOf(cpid));

//                String cpid = new ShareParm().PARM_EPAY_CPID;
                EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(Integer.valueOf(cpid));
                String cpname = cpinfo.getCpName();
                trans.setCpName(cpname);

                trans.setFeeType("0"); //無拆帳需求
                trans.setInvoiceContactMobilePhone(bean.getMdn());
                trans.setContractID(ppmdn.getContractid());
                trans.setPlatformtype(platformtype);
                trans.setStoreid(bean.getStoreid());
                trans.setStorename(bean.getStroename());
                log.info("bean.getSaleid()==>" + bean.getSaleid());
                if (!"".equals(bean.getSaleid())) {
                    trans.setSales_id(bean.getSaleid());
                }

                //20170713
//                trans.setApisrcid("1");
                //20171023
                if (!"".equals(bean.getApisrcid())) {
                    trans.setApisrcid(bean.getApisrcid());
                } else {
                    trans.setApisrcid("1");
                }

                trans.setPaytool(bean.getPaytool());
                trans.setPayname(bean.getPayname());

                log.info("PinCodeProce(insert Table)==>MDN:" + bean.getMdn() + ",Libm:" + libm);

                result = epaybusinesscontroller.insertTransaction(trans);

            }
        } catch (Exception ex) {
            log.info(ex);
            ex.printStackTrace();
        }
        return result;
    }

    public boolean insertPosTransaction(PPMdnUtil ppmdn, ServiceOrderReqBean bean, String libm, String cpid, EPayBusinessConreoller epaybusinesscontroller) {
        boolean result = false;
//        String itemCode = ShareParm.PINCODE_ITEMCODE;
        Calendar nowDateTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
        String tradedate = sdf.format(nowDateTime.getTime());
        try {
            //get serviceinfo data
            String serviceid = bean.getServiceID();
            String cplibm = bean.getCplibm();
            EPAY_SERVICE_INFO serviceinfo = epaybusinesscontroller.getServiceInfoByCpidAndServiceId(Long.valueOf(serviceid), Integer.valueOf(cpid));
            EPAY_CP_INFO cpinfo = epaybusinesscontroller.getCpInfo(Integer.valueOf(cpid));
            String cpname = cpinfo.getCpName();

            if (serviceinfo != null) {
                String itemCode = serviceinfo.getGlcode();
                EPAY_TRANSACTION trans = epaybusinesscontroller.getTransaction(libm);

                if (trans == null && !"".equalsIgnoreCase(libm) && !"null".equalsIgnoreCase(libm)) {
                    trans = new EPAY_TRANSACTION();
                    trans.setLibm(libm);
                    trans.setItemcode(itemCode);//PinCode
                    trans.setItemproductname(serviceinfo.getServiceName());
                    trans.setItemunitprice(serviceinfo.getPrice());
                    trans.setItemquantity(1);//1
                    trans.setOrdertotal(serviceinfo.getPrice());
                    trans.setFee(0);
                    trans.setDiscount(0);
                    trans.setTradedate(sdf.parse(tradedate));

                    trans.setPaytime(sdf.parse(tradedate));
                    trans.setPaymethod(ShareParm.PAYMETHOD_POS); //付款方式 PinCode=4
                    trans.setStatus("N"); //OCS尚未儲值完成

                    trans.setPrivatedata(serviceinfo.getPriceplancode());
                    trans.setServiceId(String.valueOf(serviceinfo.getServiceId()));//
                    trans.setCpLibm(cplibm);
                    trans.setCpId(Integer.valueOf(cpid));//
                    trans.setFeeType("0"); //無拆帳需求
                    trans.setInvoiceContactMobilePhone(bean.getMdn());
                    trans.setPoscode(bean.getStoreid());

                    trans.setContractID(ppmdn.getContractid());
                    trans.setPayamount(serviceinfo.getPrice());
                    trans.setPaystatus(0);
                    trans.setCpName(cpname);
                    trans.setPlatformtype(ppmdn.getPlatformtype());
                    trans.setStoreid(bean.getStoreid());
                    trans.setStorename(bean.getStroename());
                    trans.setApisrcid("5");
                    trans.setPaytool(bean.getPaytool());
                    trans.setPayname(bean.getPayname());

                    trans.setPossaleid(bean.getSaleid());
                    if (!"".equals(bean.getSaleid())) {
                        trans.setSales_id(bean.getSaleid());
                    }

                    result = epaybusinesscontroller.insertTransaction(trans);

                }
            }
        } catch (Exception ex) {
            log.info(ex);
            ex.printStackTrace();
        }
        return result;
    }

    public String nokiaMdnBuckInfo(String mdn) {

        String result = "";
        NokiaSubscribeBalanceBean bean = new NokiaSubscribeBalanceBean();
        NokiaMainBasicInfoUtil mutil = new NokiaMainBasicInfoUtil();

        SimpleDateFormat sdf15 = new java.text.SimpleDateFormat("yyMMddHHmmssSSS");
        Calendar nowDateTime = Calendar.getInstance();
        String libm = sdf15.format(nowDateTime.getTime());
        bean = mutil.GetMDNBalanceInfo(libm, mdn);

        //基本通信費(元) 650 
        double tmp_d_value_650 = 0.0;
        tmp_d_value_650 = Double.valueOf(bean.getAPT5GVoice_650_Counter()) / 10000;
        double d_value_650 = Math.round(tmp_d_value_650 * 100.0) / 100.0;

        //贈送通信費(元) 660
        double tmp_d_value_660 = 0.0;
        tmp_d_value_660 = Double.valueOf(bean.getAPT5GVoice1_660_Counter()) / 10000;
        double d_value_660 = Math.round(tmp_d_value_660 * 100.0) / 100.0;

        //數據上網基本量(MB) 750
        double tmp_d_value_750 = 0.0;
        tmp_d_value_750 = Double.valueOf(bean.getAPT5GData_750_Counter()) / 1048576;
        double d_value_750 = Math.round(tmp_d_value_750 * 100.0) / 100.0;

        // 數據上網贈送量(MB) 760 + 76P
        double d_value_760 = 0.0, d_value_76P = 0.0;
        d_value_760 = Double.valueOf(bean.getAPT5GData1_760_Counter()) / 1048576;
        d_value_76P = Double.valueOf(bean.getAPT5GData1Pro_76P_Counter()) / 1048576;
        double tmp_d_value_76S = d_value_760 + d_value_76P;
        double d_value_76S = Math.round(tmp_d_value_76S * 100.0) / 100.0;

        //網內免費通話金額(元) 830
        double tmp_d_value_830 = 0.0;
        tmp_d_value_830 = Double.valueOf(bean.getAPT5GVoiceOnnet_830_Counter()) / 100000;
        double d_value_830 = Math.round(tmp_d_value_830 * 100.0) / 100.0;

        double intVoice = d_value_650 + d_value_660;
        double intData = d_value_750 + d_value_76S;
        double intOnnet = d_value_830;

        DecimalFormat decimalFormat = new DecimalFormat("####0.00");//小數點第二位下四捨五入
        String strVoice = decimalFormat.format(intVoice);
        String strData = decimalFormat.format(intData);
        String strOnnet = decimalFormat.format(intOnnet);
        result = "<ACCOUNT_BALANCE>" + strVoice + "</ACCOUNT_BALANCE>\n"
                        + "<SUMUPINFO>\n"
                        + "1.通信費(含贈送帳本)(元)：" + strVoice + "\n"
                        + "2.數據上網(含贈送帳本)(MB)：" + strData + "\n"
                        + "3.網內免費通話(贈送帳本)(元)：" + strOnnet + "\n";
        result = result + "★僅為系統即時資訊，將因您使用行為變動，歡迎您使用Gt網站/987 IVR進行查詢。";
        result = result + "</SUMUPINFO>";
        log.info(mdn + " nokiaMdnBuckInfo response==>" + result.replaceAll("[ \t\r\n]+", " ").trim());
        return result;
    }
}
