/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.nokia.bean;

/**
 *
 * @author kevinchang
 */
public class NokiaSubscribeBalanceBean {

    private String result;
    private String ErrorCode;
    private String ErrorMsg;
    private String ErrorCategory;

    //基本帳本虛擬
    private String APT_LC_State;
    private String APT_LC_StartDate;
    private String APT_LC_EndDate;

    //通信費基本
    private String APT5GVoice_650_State = "";
    private String APT5GVoice_650_StartDate = "";
    private String APT5GVoice_650_EndDate = "";
    private String APT5GVoice_650_DiscountID = "";
    private String APT5GVoice_650_Counter = "0.0";

    //通信費贈送(包括ZTE通信費贈送)
    private String APT5GVoice1_660_State = "";
    private String APT5GVoice1_660_StartDate = "";
    private String APT5GVoice1_660_EndDate = "";
    private String APT5GVoice1_660_DiscountID = "";
    private String APT5GVoice1_660_Counter = "0.0";

    //通信費贈送(元)-促案
    private String APT5GVoice1Pro_66P_State = "";
    private String APT5GVoice1Pro_66P_StartDate = "";
    private String APT5GVoice1Pro_66P_EndDate = "";
    private String APT5GVoice1Pro_66P_DiscountID = "";
    private String APT5GVoice1Pro_66P_Counter = "0.0";

    //國内通信費贈送
    private String APT5GVoice2_840_State = "";
    private String APT5GVoice2_840_StartDate = "";
    private String APT5GVoice2_840_EndDate = "";
    private String APT5GVoice2_840_DiscountID = "";
    private String APT5GVoice2_840_Counter = "0.0";

    //通信贈送國內使用(元)-促案
    private String APT5GVoice2Pro_84P_State = "";
    private String APT5GVoice2Pro_84P_StartDate = "";
    private String APT5GVoice2Pro_84P_EndDate = "";
    private String APT5GVoice2Pro_84P_DiscountID = "";
    private String APT5GVoice2Pro_84P_Counter = "0.0";

    //3G通信費基本
    private String APT3GVoice_652_State = "";
    private String APT3GVoice_652_StartDate = "";
    private String APT3GVoice_652_EndDate = "";
    private String APT3GVoice_652_DiscountID = "";
    private String APT3GVoice_652_Counter = "0.0";

    //3G通信費贈送
    private String APT3GVoice1_662_State = "";
    private String APT3GVoice1_662_StartDate = "";
    private String APT3GVoice1_662_EndDate = "";
    private String APT3GVoice1_662_DiscountID = "";
    private String APT3GVoice1_662_Counter = "0.0";

    //國際通信費基本
    private String APT5GIDD_670_State = "";
    private String APT5GIDD_670_StartDate = "";
    private String APT5GIDD_670_EndDate = "";
    private String APT5GIDD_670_DiscountID = "";
    private String APT5GIDD_670_Counter = "0.0";

    //國際通信費贈送
    private String APT5GIDD1_680_State = "";
    private String APT5GIDD1_680_StartDate = "";
    private String APT5GIDD1_680_EndDate = "";
    private String APT5GIDD1_680_DiscountID = "";
    private String APT5GIDD1_680_Counter = "0.0";

    //計量數據基本
    private String APT5GData_750_State = "";
    private String APT5GData_750_StartDate = "";
    private String APT5GData_750_EndDate = "";
    private String APT5GData_750_DiscountID = "";
    private String APT5GData_750_Counter = "0.0";

    //計量數據贈送
    private String APT5GData1_760_State = "";
    private String APT5GData1_760_StartDate = "";
    private String APT5GData1_760_EndDate = "";
    private String APT5GData1_760_DiscountID = "";
    private String APT5GData1_760_Counter = "0.0";

    //數據贈送(MB)促案
    private String APT5GData1Pro_76P_State = "";
    private String APT5GData1Pro_76P_StartDate = "";
    private String APT5GData1Pro_76P_EndDate = "";
    private String APT5GData1Pro_76P_DiscountID = "";
    private String APT5GData1Pro_76P_Counter = "0.0";

    //計日型數據高速上網(FUL)不限量
    private String APT5GDataFULUm_430_State = "";
    private String APT5GDataFULUm_430_StartDate = "";
    private String APT5GDataFULUm_430_EndDate = "";
    private String APT5GDataFULUm_430_DiscountID = "";
    private String APT5GDataFULUm_430_Counter = "0.0";

    //計日型數據高速上網(FUL)限量G ==>限量型流量帳本
    private String APT5GDataFULLm_431_State = "";
    private String APT5GDataFULLm_431_StartDate = "";
    private String APT5GDataFULLm_431_EndDate = "";
    private String APT5GDataFULLm_431_DiscountID = "";
    private String APT5GDataFULLm_431_Counter = "0.0";
    //計日型數據高速上網(FUL)限量G ==>不限量型流量帳本(5Mb) 計日型數據微量型上網(5Mb)不限量
    //ID2 (帳本2 ID)
    private String APT5GDataFULLm_441_DiscountID = "";
    private String APT5GDataFULLm_441_Counter = "";

    //計日型數據輕量型上網(12Mb)不限量
    private String APT5GData12Mb_433_State = "";
    private String APT5GData12Mb_433_StartDate = "";
    private String APT5GData12Mb_433_EndDate = "";
    private String APT5GData12Mb_433_DiscountID = "";
    private String APT5GData12Mb_433_Counter = "0.0";

    //計日型輕量型上網(12Mb)-促案
    private String APT5GData12MbPr_4P3_State = "";
    private String APT5GData12MbPr_4P3_StartDate = "";
    private String APT5GData12MbPr_4P3_EndDate = "";
    private String APT5GData12MbPr_4P3_DiscountID = "";
    private String APT5GData12MbPr_4P3_Counter = "0.0";

    //計日型數據輕量型上網(12Mb)限量G ==>流量型帳本
    private String APT5GData12MbLm_435_State = "";
    private String APT5GData12MbLm_435_StartDate = "";
    private String APT5GData12MbLm_435_EndDate = "";
    private String APT5GData12MbLm_435_DiscountID = "";
    private String APT5GData12MbLm_435_Counter = "0.0";

    //計日型數據輕量型上網(12Mb)限量G ==>不限量型流量帳本(5Mb) 計日型數據微量型上網(5Mb)不限量
    private String APT5GData12MbLm_445_State = "";
    private String APT5GData12MbLm_445_StartDate = "";
    private String APT5GData12MbLm_445_EndDate = "";
    private String APT5GData12MbLm_445_DiscountID = "";
    private String APT5GData12MbLm_445_Counter = "0.0";

    //計日型數據輕量型上網(12Mb)限量每月60G
    private String APT5GData12MbMt_436_State = "";
    private String APT5GData12MbMt_436_StartDate = "";
    private String APT5GData12MbMt_436_EndDate = "";
    private String APT5GData12MbMt_436_DiscountID = "";
    private String APT5GData12MbMt_436_Counter = "";
    private String APT5GData12MbMt_446_DiscountID = "";
    private String APT5GData12MbMt_446_Counter = "0.0";

    //計日型數據低量型上網(8Mb)不限量  ==>不限量型流量帳本
    private String APT5GData8Mb_437_State = "";
    private String APT5GData8Mb_437_StartDate = "";
    private String APT5GData8Mb_437_EndDate = "";
    private String APT5GData8Mb_437_DiscountID = "";
    private String APT5GData8Mb_437_Counter = "0.0";

    //網內語音通話費==>金額型帳本
    private String APT5GVoiceOnnet_830_State = "";
    private String APT5GVoiceOnnet_830_StartDate = "";
    private String APT5GVoiceOnnet_830_EndDate = "";
    private String APT5GVoiceOnnet_830_DiscountID = "";
    private String APT5GVoiceOnnet_830_Counter = "0.0";

    //網內語音通話費(元)-促案 ==>金額型帳本
    private String APT5GVoiceOnPro_83P_State = "";
    private String APT5GVoiceOnPro_83P_StartDate = "";
    private String APT5GVoiceOnPro_83P_EndDate = "";
    private String APT5GVoiceOnPro_83P_DiscountID = "";
    private String APT5GVoiceOnPro_83P_Counter = "0.0";

    //網內語音(分鐘) ==> 時間型帳本
    private String APT5GVoiceOnUlm_831_State = "";
    ;
    private String APT5GVoiceOnUlm_831_StartDate = "";
    private String APT5GVoiceOnUlm_831_EndDate = "";
    private String APT5GVoiceOnUlm_831_DiscountID = "";
    private String APT5GVoiceOnUlm_831_Counter = "0.0";

    //促案 031701 ==>儲值30日上網，贈送10日上網+贈送100元(最高500)
    private String RechargeCounter_RC1_State = "";
    private String RechargeCounter_RC1_StartDate = "";
    private String RechargeCounter_RC1_EndDate = "";
    private String RechargeCounter_RC1_DiscountID = "";
    private String RechargeCounter_RC1_Counter = "0.0";

    //共享數據限量 ==>770 流量型帳本
    private String ShareLm5G_770_State = "";
    private String ShareLm5G_770_StartDate = "";
    private String ShareLm5G_770_EndDate = "";
    private String ShareLm5G_770_DiscountID = "";
    private String ShareLm5G_770_Counter = "0.0";

    //共享數據每日限量 771
    private String ShareDt5G_771_State = "";
    private String ShareDt5G_771_StartDate = "";
    private String ShareDt5G_771_EndDate = "";
    private String ShareDt5G_771_DiscountID = "";
    private String ShareDt5G_771_Counter = "0.0";

    //計日飆速無限上網(FUL) 530 不限量型流量帳本
    private String DataFUm5G_530_State = "";
    private String DataFUm5G_530_StartDate = "";
    private String DataFUm5G_530_EndDate = "";
    private String DataFUm5G_530_DiscountID = "";
    private String DataFUm5G_530_Counter = "0.0";

    //計日型高速上網(FUL)限量GB/計日型中量無限上網(21Mb) 531 限量型流量帳本 
    private String DataF21MbLm5G_531_State = "";
    private String DataF21MbLm5G_531_StartDate = "";
    private String DataF21MbLm5G_531_EndDate = "";
    private String DataF21MbLm5G_531_DiscountID = "";
    private String DataF21MbLm5G_531_Counter = "0.0";

    //計日型高速上網(FUL)限量GB/計日型中量無限上網(21Mb) 541 計日型中量無限上網(21Mb)
    private String DataF21MbLm5G_541_State = "";
    private String DataF21MbLm5G_541_StartDate = "";
    private String DataF21MbLm5G_541_EndDate = "";
    private String DataF21MbLm5G_541_DiscountID = "";
    private String DataF21MbLm5G_541_Counter = "0.0";

    //計日型高速上網(FUL)限量GB/計日型中量無限上網(12Mb) 532 限量型流量帳本
    private String DataF12MbLm5G_532_State = "";
    private String DataF12MbLm5G_532_StartDate = "";
    private String DataF12MbLm5G_532_EndDate = "";
    private String DataF12MbLm5G_532_DiscountID = "";
    private String DataF12MbLm5G_532_Counter = "0.0";

    //計日型高速上網(FUL)限量GB/計日型中量無限上網(12Mb) 542 計日型中量無限上網(12Mb)
    private String DataF12MbLm5G_542_State = "";
    private String DataF12MbLm5G_542_StartDate = "";
    private String DataF12MbLm5G_542_EndDate = "";
    private String DataF12MbLm5G_542_DiscountID = "";
    private String DataF12MbLm5G_542_Counter = "0.0";

    //計日型高速上網(100Mb)限量GB/計日型中量無限上網(12Mb) 536 限量型流量帳本
    private String OneHundredMb12MbLm5G_536_State = "";
    private String OneHundredMb12MbLm5G_536_StartDate = "";
    private String OneHundredMb12MbLm5G_536_EndDate = "";
    private String OneHundredMb12MbLm5G_536_DiscountID = "";
    private String OneHundredMb12MbLm5G_536_Counter = "0.0";

    //計日型高速上網(100Mb)限量GB/計日型中量無限上網(12Mb) 546 計日型中量無限上網(12Mb)
    private String OneHundredMb12MbLm5G_546_State = "";
    private String OneHundredMb12MbLm5G_546_StartDate = "";
    private String OneHundredMb12MbLm5G_546_EndDate = "";
    private String OneHundredMb12MbLm5G_546_DiscountID = "";
    private String OneHundredMb12MbLm5G_546_Counter = "0.0";

    //計日型高速上網(500Mb)限量GB/計日型中量無限上網(21Mb) 537 限量型流量帳本
    private String FiveHundredMb21MbLm5G_537_State = "";
    private String FiveHundredMb21MbLm5G_537_StartDate = "";
    private String FiveHundredMb21MbLm5G_537_EndDate = "";
    private String FiveHundredMb21MbLm5G_537_DiscountID = "";
    private String FiveHundredMb21MbLm5G_537_Counter = "0.0";

    //計日型高速上網(500Mb)限量GB/計日型中量無限上網(21Mb) 547 計日型中量無限上網(21Mb)
    private String FiveHundredMb21MbLm5G_547_State = "";
    private String FiveHundredMb21MbLm5G_547_StartDate = "";
    private String FiveHundredMb21MbLm5G_547_EndDate = "";
    private String FiveHundredMb21MbLm5G_547_DiscountID = "";
    private String FiveHundredMb21MbLm5G_547_Counter = "0.0";

    //計日型高速上網(1Gb)限量GB/計日型中量無限上網(21Mb) 538 限量型流量帳本
    private String OneGb21MbLm5G_538_State = "";
    private String OneGb21MbLm5G_538_StartDate = "";
    private String OneGb21MbLm5G_538_EndDate = "";
    private String OneGb21MbLm5G_538_DiscountID = "";
    private String OneGb21MbLm5G_538_Counter = "0.0";

    //計日型高速上網(1Gb)限量GB/計日型中量無限上網(21Mb) 548 計日型中量無限上網(21Mb)
    private String OneGb21MbLm5G_548_State = "";
    private String OneGb21MbLm5G_548_StartDate = "";
    private String OneGb21MbLm5G_548_EndDate = "";
    private String OneGb21MbLm5G_548_DiscountID = "";
    private String OneGb21MbLm5G_548_Counter = "0.0";

    //WiFI
    private String APT5GDataWiFi_555_State = "";
    private String APT5GDataWiFi_555_StartDate = "";
    private String APT5GDataWiFi_555_EndDate = "";
    private String APT5GDataWiFi_555_DiscountID = "";
    private String APT5GDataWiFi_555_Counter = "0.0";


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(String ErrorCode) {
        this.ErrorCode = ErrorCode;
    }

    public String getErrorMsg() {
        return ErrorMsg;
    }

    public void setErrorMsg(String ErrorMsg) {
        this.ErrorMsg = ErrorMsg;
    }

    public String getErrorCategory() {
        return ErrorCategory;
    }

    public void setErrorCategory(String ErrorCategory) {
        this.ErrorCategory = ErrorCategory;
    }

    public String getAPT_LC_State() {
        return APT_LC_State;
    }

    public void setAPT_LC_State(String APT_LC_State) {
        this.APT_LC_State = APT_LC_State;
    }

    public String getAPT_LC_StartDate() {
        return APT_LC_StartDate;
    }

    public void setAPT_LC_StartDate(String APT_LC_StartDate) {
        this.APT_LC_StartDate = APT_LC_StartDate;
    }

    public String getAPT_LC_EndDate() {
        return APT_LC_EndDate;
    }

    public void setAPT_LC_EndDate(String APT_LC_EndDate) {
        this.APT_LC_EndDate = APT_LC_EndDate;
    }

    public String getAPT5GVoice_650_State() {
        return APT5GVoice_650_State;
    }

    public void setAPT5GVoice_650_State(String APT5GVoice_650_State) {
        this.APT5GVoice_650_State = APT5GVoice_650_State;
    }

    public String getAPT5GVoice_650_StartDate() {
        return APT5GVoice_650_StartDate;
    }

    public void setAPT5GVoice_650_StartDate(String APT5GVoice_650_StartDate) {
        this.APT5GVoice_650_StartDate = APT5GVoice_650_StartDate;
    }

    public String getAPT5GVoice_650_EndDate() {
        return APT5GVoice_650_EndDate;
    }

    public void setAPT5GVoice_650_EndDate(String APT5GVoice_650_EndDate) {
        this.APT5GVoice_650_EndDate = APT5GVoice_650_EndDate;
    }

    public String getAPT5GVoice_650_DiscountID() {
        return APT5GVoice_650_DiscountID;
    }

    public void setAPT5GVoice_650_DiscountID(String APT5GVoice_650_DiscountID) {
        this.APT5GVoice_650_DiscountID = APT5GVoice_650_DiscountID;
    }

    public String getAPT5GVoice_650_Counter() {
        return APT5GVoice_650_Counter;
    }

    public void setAPT5GVoice_650_Counter(String APT5GVoice_650_Counter) {
        this.APT5GVoice_650_Counter = APT5GVoice_650_Counter;
    }

    public String getAPT5GVoice1_660_State() {
        return APT5GVoice1_660_State;
    }

    public void setAPT5GVoice1_660_State(String APT5GVoice1_660_State) {
        this.APT5GVoice1_660_State = APT5GVoice1_660_State;
    }

    public String getAPT5GVoice1_660_StartDate() {
        return APT5GVoice1_660_StartDate;
    }

    public void setAPT5GVoice1_660_StartDate(String APT5GVoice1_660_StartDate) {
        this.APT5GVoice1_660_StartDate = APT5GVoice1_660_StartDate;
    }

    public String getAPT5GVoice1_660_EndDate() {
        return APT5GVoice1_660_EndDate;
    }

    public void setAPT5GVoice1_660_EndDate(String APT5GVoice1_660_EndDate) {
        this.APT5GVoice1_660_EndDate = APT5GVoice1_660_EndDate;
    }

    public String getAPT5GVoice1_660_DiscountID() {
        return APT5GVoice1_660_DiscountID;
    }

    public void setAPT5GVoice1_660_DiscountID(String APT5GVoice1_660_DiscountID) {
        this.APT5GVoice1_660_DiscountID = APT5GVoice1_660_DiscountID;
    }

    public String getAPT5GVoice1_660_Counter() {
        return APT5GVoice1_660_Counter;
    }

    public void setAPT5GVoice1_660_Counter(String APT5GVoice1_660_Counter) {
        this.APT5GVoice1_660_Counter = APT5GVoice1_660_Counter;
    }

    public String getAPT5GVoice1Pro_66P_State() {
        return APT5GVoice1Pro_66P_State;
    }

    public void setAPT5GVoice1Pro_66P_State(String APT5GVoice1Pro_66P_State) {
        this.APT5GVoice1Pro_66P_State = APT5GVoice1Pro_66P_State;
    }

    public String getAPT5GVoice1Pro_66P_StartDate() {
        return APT5GVoice1Pro_66P_StartDate;
    }

    public void setAPT5GVoice1Pro_66P_StartDate(String APT5GVoice1Pro_66P_StartDate) {
        this.APT5GVoice1Pro_66P_StartDate = APT5GVoice1Pro_66P_StartDate;
    }

    public String getAPT5GVoice1Pro_66P_EndDate() {
        return APT5GVoice1Pro_66P_EndDate;
    }

    public void setAPT5GVoice1Pro_66P_EndDate(String APT5GVoice1Pro_66P_EndDate) {
        this.APT5GVoice1Pro_66P_EndDate = APT5GVoice1Pro_66P_EndDate;
    }

    public String getAPT5GVoice1Pro_66P_DiscountID() {
        return APT5GVoice1Pro_66P_DiscountID;
    }

    public void setAPT5GVoice1Pro_66P_DiscountID(String APT5GVoice1Pro_66P_DiscountID) {
        this.APT5GVoice1Pro_66P_DiscountID = APT5GVoice1Pro_66P_DiscountID;
    }

    public String getAPT5GVoice1Pro_66P_Counter() {
        return APT5GVoice1Pro_66P_Counter;
    }

    public void setAPT5GVoice1Pro_66P_Counter(String APT5GVoice1Pro_66P_Counter) {
        this.APT5GVoice1Pro_66P_Counter = APT5GVoice1Pro_66P_Counter;
    }

    public String getAPT5GVoice2_840_State() {
        return APT5GVoice2_840_State;
    }

    public void setAPT5GVoice2_840_State(String APT5GVoice2_840_State) {
        this.APT5GVoice2_840_State = APT5GVoice2_840_State;
    }

    public String getAPT5GVoice2_840_StartDate() {
        return APT5GVoice2_840_StartDate;
    }

    public void setAPT5GVoice2_840_StartDate(String APT5GVoice2_840_StartDate) {
        this.APT5GVoice2_840_StartDate = APT5GVoice2_840_StartDate;
    }

    public String getAPT5GVoice2_840_EndDate() {
        return APT5GVoice2_840_EndDate;
    }

    public void setAPT5GVoice2_840_EndDate(String APT5GVoice2_840_EndDate) {
        this.APT5GVoice2_840_EndDate = APT5GVoice2_840_EndDate;
    }

    public String getAPT5GVoice2_840_DiscountID() {
        return APT5GVoice2_840_DiscountID;
    }

    public void setAPT5GVoice2_840_DiscountID(String APT5GVoice2_840_DiscountID) {
        this.APT5GVoice2_840_DiscountID = APT5GVoice2_840_DiscountID;
    }

    public String getAPT5GVoice2_840_Counter() {
        return APT5GVoice2_840_Counter;
    }

    public void setAPT5GVoice2_840_Counter(String APT5GVoice2_840_Counter) {
        this.APT5GVoice2_840_Counter = APT5GVoice2_840_Counter;
    }

    public String getAPT5GVoice2Pro_84P_State() {
        return APT5GVoice2Pro_84P_State;
    }

    public void setAPT5GVoice2Pro_84P_State(String APT5GVoice2Pro_84P_State) {
        this.APT5GVoice2Pro_84P_State = APT5GVoice2Pro_84P_State;
    }

    public String getAPT5GVoice2Pro_84P_StartDate() {
        return APT5GVoice2Pro_84P_StartDate;
    }

    public void setAPT5GVoice2Pro_84P_StartDate(String APT5GVoice2Pro_84P_StartDate) {
        this.APT5GVoice2Pro_84P_StartDate = APT5GVoice2Pro_84P_StartDate;
    }

    public String getAPT5GVoice2Pro_84P_EndDate() {
        return APT5GVoice2Pro_84P_EndDate;
    }

    public void setAPT5GVoice2Pro_84P_EndDate(String APT5GVoice2Pro_84P_EndDate) {
        this.APT5GVoice2Pro_84P_EndDate = APT5GVoice2Pro_84P_EndDate;
    }

    public String getAPT5GVoice2Pro_84P_DiscountID() {
        return APT5GVoice2Pro_84P_DiscountID;
    }

    public void setAPT5GVoice2Pro_84P_DiscountID(String APT5GVoice2Pro_84P_DiscountID) {
        this.APT5GVoice2Pro_84P_DiscountID = APT5GVoice2Pro_84P_DiscountID;
    }

    public String getAPT5GVoice2Pro_84P_Counter() {
        return APT5GVoice2Pro_84P_Counter;
    }

    public void setAPT5GVoice2Pro_84P_Counter(String APT5GVoice2Pro_84P_Counter) {
        this.APT5GVoice2Pro_84P_Counter = APT5GVoice2Pro_84P_Counter;
    }

    public String getAPT3GVoice_652_State() {
        return APT3GVoice_652_State;
    }

    public void setAPT3GVoice_652_State(String APT3GVoice_652_State) {
        this.APT3GVoice_652_State = APT3GVoice_652_State;
    }

    public String getAPT3GVoice_652_StartDate() {
        return APT3GVoice_652_StartDate;
    }

    public void setAPT3GVoice_652_StartDate(String APT3GVoice_652_StartDate) {
        this.APT3GVoice_652_StartDate = APT3GVoice_652_StartDate;
    }

    public String getAPT3GVoice_652_EndDate() {
        return APT3GVoice_652_EndDate;
    }

    public void setAPT3GVoice_652_EndDate(String APT3GVoice_652_EndDate) {
        this.APT3GVoice_652_EndDate = APT3GVoice_652_EndDate;
    }

    public String getAPT3GVoice_652_DiscountID() {
        return APT3GVoice_652_DiscountID;
    }

    public void setAPT3GVoice_652_DiscountID(String APT3GVoice_652_DiscountID) {
        this.APT3GVoice_652_DiscountID = APT3GVoice_652_DiscountID;
    }

    public String getAPT3GVoice_652_Counter() {
        return APT3GVoice_652_Counter;
    }

    public void setAPT3GVoice_652_Counter(String APT3GVoice_652_Counter) {
        this.APT3GVoice_652_Counter = APT3GVoice_652_Counter;
    }

    public String getAPT3GVoice1_662_State() {
        return APT3GVoice1_662_State;
    }

    public void setAPT3GVoice1_662_State(String APT3GVoice1_662_State) {
        this.APT3GVoice1_662_State = APT3GVoice1_662_State;
    }

    public String getAPT3GVoice1_662_StartDate() {
        return APT3GVoice1_662_StartDate;
    }

    public void setAPT3GVoice1_662_StartDate(String APT3GVoice1_662_StartDate) {
        this.APT3GVoice1_662_StartDate = APT3GVoice1_662_StartDate;
    }

    public String getAPT3GVoice1_662_EndDate() {
        return APT3GVoice1_662_EndDate;
    }

    public void setAPT3GVoice1_662_EndDate(String APT3GVoice1_662_EndDate) {
        this.APT3GVoice1_662_EndDate = APT3GVoice1_662_EndDate;
    }

    public String getAPT3GVoice1_662_DiscountID() {
        return APT3GVoice1_662_DiscountID;
    }

    public void setAPT3GVoice1_662_DiscountID(String APT3GVoice1_662_DiscountID) {
        this.APT3GVoice1_662_DiscountID = APT3GVoice1_662_DiscountID;
    }

    public String getAPT3GVoice1_662_Counter() {
        return APT3GVoice1_662_Counter;
    }

    public void setAPT3GVoice1_662_Counter(String APT3GVoice1_662_Counter) {
        this.APT3GVoice1_662_Counter = APT3GVoice1_662_Counter;
    }

    public String getAPT5GIDD_670_State() {
        return APT5GIDD_670_State;
    }

    public void setAPT5GIDD_670_State(String APT5GIDD_670_State) {
        this.APT5GIDD_670_State = APT5GIDD_670_State;
    }

    public String getAPT5GIDD_670_StartDate() {
        return APT5GIDD_670_StartDate;
    }

    public void setAPT5GIDD_670_StartDate(String APT5GIDD_670_StartDate) {
        this.APT5GIDD_670_StartDate = APT5GIDD_670_StartDate;
    }

    public String getAPT5GIDD_670_EndDate() {
        return APT5GIDD_670_EndDate;
    }

    public void setAPT5GIDD_670_EndDate(String APT5GIDD_670_EndDate) {
        this.APT5GIDD_670_EndDate = APT5GIDD_670_EndDate;
    }

    public String getAPT5GIDD_670_DiscountID() {
        return APT5GIDD_670_DiscountID;
    }

    public void setAPT5GIDD_670_DiscountID(String APT5GIDD_670_DiscountID) {
        this.APT5GIDD_670_DiscountID = APT5GIDD_670_DiscountID;
    }

    public String getAPT5GIDD_670_Counter() {
        return APT5GIDD_670_Counter;
    }

    public void setAPT5GIDD_670_Counter(String APT5GIDD_670_Counter) {
        this.APT5GIDD_670_Counter = APT5GIDD_670_Counter;
    }

    public String getAPT5GIDD1_680_State() {
        return APT5GIDD1_680_State;
    }

    public void setAPT5GIDD1_680_State(String APT5GIDD1_680_State) {
        this.APT5GIDD1_680_State = APT5GIDD1_680_State;
    }

    public String getAPT5GIDD1_680_StartDate() {
        return APT5GIDD1_680_StartDate;
    }

    public void setAPT5GIDD1_680_StartDate(String APT5GIDD1_680_StartDate) {
        this.APT5GIDD1_680_StartDate = APT5GIDD1_680_StartDate;
    }

    public String getAPT5GIDD1_680_EndDate() {
        return APT5GIDD1_680_EndDate;
    }

    public void setAPT5GIDD1_680_EndDate(String APT5GIDD1_680_EndDate) {
        this.APT5GIDD1_680_EndDate = APT5GIDD1_680_EndDate;
    }

    public String getAPT5GIDD1_680_DiscountID() {
        return APT5GIDD1_680_DiscountID;
    }

    public void setAPT5GIDD1_680_DiscountID(String APT5GIDD1_680_DiscountID) {
        this.APT5GIDD1_680_DiscountID = APT5GIDD1_680_DiscountID;
    }

    public String getAPT5GIDD1_680_Counter() {
        return APT5GIDD1_680_Counter;
    }

    public void setAPT5GIDD1_680_Counter(String APT5GIDD1_680_Counter) {
        this.APT5GIDD1_680_Counter = APT5GIDD1_680_Counter;
    }

    public String getAPT5GData_750_State() {
        return APT5GData_750_State;
    }

    public void setAPT5GData_750_State(String APT5GData_750_State) {
        this.APT5GData_750_State = APT5GData_750_State;
    }

    public String getAPT5GData_750_StartDate() {
        return APT5GData_750_StartDate;
    }

    public void setAPT5GData_750_StartDate(String APT5GData_750_StartDate) {
        this.APT5GData_750_StartDate = APT5GData_750_StartDate;
    }

    public String getAPT5GData_750_EndDate() {
        return APT5GData_750_EndDate;
    }

    public void setAPT5GData_750_EndDate(String APT5GData_750_EndDate) {
        this.APT5GData_750_EndDate = APT5GData_750_EndDate;
    }

    public String getAPT5GData_750_DiscountID() {
        return APT5GData_750_DiscountID;
    }

    public void setAPT5GData_750_DiscountID(String APT5GData_750_DiscountID) {
        this.APT5GData_750_DiscountID = APT5GData_750_DiscountID;
    }

    public String getAPT5GData_750_Counter() {
        return APT5GData_750_Counter;
    }

    public void setAPT5GData_750_Counter(String APT5GData_750_Counter) {
        this.APT5GData_750_Counter = APT5GData_750_Counter;
    }

    public String getAPT5GData1_760_State() {
        return APT5GData1_760_State;
    }

    public void setAPT5GData1_760_State(String APT5GData1_760_State) {
        this.APT5GData1_760_State = APT5GData1_760_State;
    }

    public String getAPT5GData1_760_StartDate() {
        return APT5GData1_760_StartDate;
    }

    public void setAPT5GData1_760_StartDate(String APT5GData1_760_StartDate) {
        this.APT5GData1_760_StartDate = APT5GData1_760_StartDate;
    }

    public String getAPT5GData1_760_EndDate() {
        return APT5GData1_760_EndDate;
    }

    public void setAPT5GData1_760_EndDate(String APT5GData1_760_EndDate) {
        this.APT5GData1_760_EndDate = APT5GData1_760_EndDate;
    }

    public String getAPT5GData1_760_DiscountID() {
        return APT5GData1_760_DiscountID;
    }

    public void setAPT5GData1_760_DiscountID(String APT5GData1_760_DiscountID) {
        this.APT5GData1_760_DiscountID = APT5GData1_760_DiscountID;
    }

    public String getAPT5GData1_760_Counter() {
        return APT5GData1_760_Counter;
    }

    public void setAPT5GData1_760_Counter(String APT5GData1_760_Counter) {
        this.APT5GData1_760_Counter = APT5GData1_760_Counter;
    }

    public String getAPT5GData1Pro_76P_State() {
        return APT5GData1Pro_76P_State;
    }

    public void setAPT5GData1Pro_76P_State(String APT5GData1Pro_76P_State) {
        this.APT5GData1Pro_76P_State = APT5GData1Pro_76P_State;
    }

    public String getAPT5GData1Pro_76P_StartDate() {
        return APT5GData1Pro_76P_StartDate;
    }

    public void setAPT5GData1Pro_76P_StartDate(String APT5GData1Pro_76P_StartDate) {
        this.APT5GData1Pro_76P_StartDate = APT5GData1Pro_76P_StartDate;
    }

    public String getAPT5GData1Pro_76P_EndDate() {
        return APT5GData1Pro_76P_EndDate;
    }

    public void setAPT5GData1Pro_76P_EndDate(String APT5GData1Pro_76P_EndDate) {
        this.APT5GData1Pro_76P_EndDate = APT5GData1Pro_76P_EndDate;
    }

    public String getAPT5GData1Pro_76P_DiscountID() {
        return APT5GData1Pro_76P_DiscountID;
    }

    public void setAPT5GData1Pro_76P_DiscountID(String APT5GData1Pro_76P_DiscountID) {
        this.APT5GData1Pro_76P_DiscountID = APT5GData1Pro_76P_DiscountID;
    }

    public String getAPT5GData1Pro_76P_Counter() {
        return APT5GData1Pro_76P_Counter;
    }

    public void setAPT5GData1Pro_76P_Counter(String APT5GData1Pro_76P_Counter) {
        this.APT5GData1Pro_76P_Counter = APT5GData1Pro_76P_Counter;
    }

    public String getAPT5GDataFULUm_430_State() {
        return APT5GDataFULUm_430_State;
    }

    public void setAPT5GDataFULUm_430_State(String APT5GDataFULUm_430_State) {
        this.APT5GDataFULUm_430_State = APT5GDataFULUm_430_State;
    }

    public String getAPT5GDataFULUm_430_StartDate() {
        return APT5GDataFULUm_430_StartDate;
    }

    public void setAPT5GDataFULUm_430_StartDate(String APT5GDataFULUm_430_StartDate) {
        this.APT5GDataFULUm_430_StartDate = APT5GDataFULUm_430_StartDate;
    }

    public String getAPT5GDataFULUm_430_EndDate() {
        return APT5GDataFULUm_430_EndDate;
    }

    public void setAPT5GDataFULUm_430_EndDate(String APT5GDataFULUm_430_EndDate) {
        this.APT5GDataFULUm_430_EndDate = APT5GDataFULUm_430_EndDate;
    }

    public String getAPT5GDataFULUm_430_DiscountID() {
        return APT5GDataFULUm_430_DiscountID;
    }

    public void setAPT5GDataFULUm_430_DiscountID(String APT5GDataFULUm_430_DiscountID) {
        this.APT5GDataFULUm_430_DiscountID = APT5GDataFULUm_430_DiscountID;
    }

    public String getAPT5GDataFULUm_430_Counter() {
        return APT5GDataFULUm_430_Counter;
    }

    public void setAPT5GDataFULUm_430_Counter(String APT5GDataFULUm_430_Counter) {
        this.APT5GDataFULUm_430_Counter = APT5GDataFULUm_430_Counter;
    }

    public String getAPT5GDataFULLm_431_State() {
        return APT5GDataFULLm_431_State;
    }

    public void setAPT5GDataFULLm_431_State(String APT5GDataFULLm_431_State) {
        this.APT5GDataFULLm_431_State = APT5GDataFULLm_431_State;
    }

    public String getAPT5GDataFULLm_431_StartDate() {
        return APT5GDataFULLm_431_StartDate;
    }

    public void setAPT5GDataFULLm_431_StartDate(String APT5GDataFULLm_431_StartDate) {
        this.APT5GDataFULLm_431_StartDate = APT5GDataFULLm_431_StartDate;
    }

    public String getAPT5GDataFULLm_431_EndDate() {
        return APT5GDataFULLm_431_EndDate;
    }

    public void setAPT5GDataFULLm_431_EndDate(String APT5GDataFULLm_431_EndDate) {
        this.APT5GDataFULLm_431_EndDate = APT5GDataFULLm_431_EndDate;
    }

    public String getAPT5GDataFULLm_431_DiscountID() {
        return APT5GDataFULLm_431_DiscountID;
    }

    public void setAPT5GDataFULLm_431_DiscountID(String APT5GDataFULLm_431_DiscountID) {
        this.APT5GDataFULLm_431_DiscountID = APT5GDataFULLm_431_DiscountID;
    }

    public String getAPT5GDataFULLm_431_Counter() {
        return APT5GDataFULLm_431_Counter;
    }

    public void setAPT5GDataFULLm_431_Counter(String APT5GDataFULLm_431_Counter) {
        this.APT5GDataFULLm_431_Counter = APT5GDataFULLm_431_Counter;
    }

    public String getAPT5GDataFULLm_441_DiscountID() {
        return APT5GDataFULLm_441_DiscountID;
    }

    public void setAPT5GDataFULLm_441_DiscountID(String APT5GDataFULLm_441_DiscountID) {
        this.APT5GDataFULLm_441_DiscountID = APT5GDataFULLm_441_DiscountID;
    }

    public String getAPT5GDataFULLm_441_Counter() {
        return APT5GDataFULLm_441_Counter;
    }

    public void setAPT5GDataFULLm_441_Counter(String APT5GDataFULLm_441_Counter) {
        this.APT5GDataFULLm_441_Counter = APT5GDataFULLm_441_Counter;
    }

    public String getAPT5GData12Mb_433_State() {
        return APT5GData12Mb_433_State;
    }

    public void setAPT5GData12Mb_433_State(String APT5GData12Mb_433_State) {
        this.APT5GData12Mb_433_State = APT5GData12Mb_433_State;
    }

    public String getAPT5GData12Mb_433_StartDate() {
        return APT5GData12Mb_433_StartDate;
    }

    public void setAPT5GData12Mb_433_StartDate(String APT5GData12Mb_433_StartDate) {
        this.APT5GData12Mb_433_StartDate = APT5GData12Mb_433_StartDate;
    }

    public String getAPT5GData12Mb_433_EndDate() {
        return APT5GData12Mb_433_EndDate;
    }

    public void setAPT5GData12Mb_433_EndDate(String APT5GData12Mb_433_EndDate) {
        this.APT5GData12Mb_433_EndDate = APT5GData12Mb_433_EndDate;
    }

    public String getAPT5GData12Mb_433_DiscountID() {
        return APT5GData12Mb_433_DiscountID;
    }

    public void setAPT5GData12Mb_433_DiscountID(String APT5GData12Mb_433_DiscountID) {
        this.APT5GData12Mb_433_DiscountID = APT5GData12Mb_433_DiscountID;
    }

    public String getAPT5GData12Mb_433_Counter() {
        return APT5GData12Mb_433_Counter;
    }

    public void setAPT5GData12Mb_433_Counter(String APT5GData12Mb_433_Counter) {
        this.APT5GData12Mb_433_Counter = APT5GData12Mb_433_Counter;
    }

    public String getAPT5GData12MbPr_4P3_State() {
        return APT5GData12MbPr_4P3_State;
    }

    public void setAPT5GData12MbPr_4P3_State(String APT5GData12MbPr_4P3_State) {
        this.APT5GData12MbPr_4P3_State = APT5GData12MbPr_4P3_State;
    }

    public String getAPT5GData12MbPr_4P3_StartDate() {
        return APT5GData12MbPr_4P3_StartDate;
    }

    public void setAPT5GData12MbPr_4P3_StartDate(String APT5GData12MbPr_4P3_StartDate) {
        this.APT5GData12MbPr_4P3_StartDate = APT5GData12MbPr_4P3_StartDate;
    }

    public String getAPT5GData12MbPr_4P3_EndDate() {
        return APT5GData12MbPr_4P3_EndDate;
    }

    public void setAPT5GData12MbPr_4P3_EndDate(String APT5GData12MbPr_4P3_EndDate) {
        this.APT5GData12MbPr_4P3_EndDate = APT5GData12MbPr_4P3_EndDate;
    }

    public String getAPT5GData12MbPr_4P3_DiscountID() {
        return APT5GData12MbPr_4P3_DiscountID;
    }

    public void setAPT5GData12MbPr_4P3_DiscountID(String APT5GData12MbPr_4P3_DiscountID) {
        this.APT5GData12MbPr_4P3_DiscountID = APT5GData12MbPr_4P3_DiscountID;
    }

    public String getAPT5GData12MbPr_4P3_Counter() {
        return APT5GData12MbPr_4P3_Counter;
    }

    public void setAPT5GData12MbPr_4P3_Counter(String APT5GData12MbPr_4P3_Counter) {
        this.APT5GData12MbPr_4P3_Counter = APT5GData12MbPr_4P3_Counter;
    }

    public String getAPT5GData12MbLm_435_State() {
        return APT5GData12MbLm_435_State;
    }

    public void setAPT5GData12MbLm_435_State(String APT5GData12MbLm_435_State) {
        this.APT5GData12MbLm_435_State = APT5GData12MbLm_435_State;
    }

    public String getAPT5GData12MbLm_435_StartDate() {
        return APT5GData12MbLm_435_StartDate;
    }

    public void setAPT5GData12MbLm_435_StartDate(String APT5GData12MbLm_435_StartDate) {
        this.APT5GData12MbLm_435_StartDate = APT5GData12MbLm_435_StartDate;
    }

    public String getAPT5GData12MbLm_435_EndDate() {
        return APT5GData12MbLm_435_EndDate;
    }

    public void setAPT5GData12MbLm_435_EndDate(String APT5GData12MbLm_435_EndDate) {
        this.APT5GData12MbLm_435_EndDate = APT5GData12MbLm_435_EndDate;
    }

    public String getAPT5GData12MbLm_435_DiscountID() {
        return APT5GData12MbLm_435_DiscountID;
    }

    public void setAPT5GData12MbLm_435_DiscountID(String APT5GData12MbLm_435_DiscountID) {
        this.APT5GData12MbLm_435_DiscountID = APT5GData12MbLm_435_DiscountID;
    }

    public String getAPT5GData12MbLm_435_Counter() {
        return APT5GData12MbLm_435_Counter;
    }

    public void setAPT5GData12MbLm_435_Counter(String APT5GData12MbLm_435_Counter) {
        this.APT5GData12MbLm_435_Counter = APT5GData12MbLm_435_Counter;
    }

    public String getAPT5GData12MbLm_445_State() {
        return APT5GData12MbLm_445_State;
    }

    public void setAPT5GData12MbLm_445_State(String APT5GData12MbLm_445_State) {
        this.APT5GData12MbLm_445_State = APT5GData12MbLm_445_State;
    }

    public String getAPT5GData12MbLm_445_StartDate() {
        return APT5GData12MbLm_445_StartDate;
    }

    public void setAPT5GData12MbLm_445_StartDate(String APT5GData12MbLm_445_StartDate) {
        this.APT5GData12MbLm_445_StartDate = APT5GData12MbLm_445_StartDate;
    }

    public String getAPT5GData12MbLm_445_EndDate() {
        return APT5GData12MbLm_445_EndDate;
    }

    public void setAPT5GData12MbLm_445_EndDate(String APT5GData12MbLm_445_EndDate) {
        this.APT5GData12MbLm_445_EndDate = APT5GData12MbLm_445_EndDate;
    }

    public String getAPT5GData12MbLm_445_DiscountID() {
        return APT5GData12MbLm_445_DiscountID;
    }

    public void setAPT5GData12MbLm_445_DiscountID(String APT5GData12MbLm_445_DiscountID) {
        this.APT5GData12MbLm_445_DiscountID = APT5GData12MbLm_445_DiscountID;
    }

    public String getAPT5GData12MbLm_445_Counter() {
        return APT5GData12MbLm_445_Counter;
    }

    public void setAPT5GData12MbLm_445_Counter(String APT5GData12MbLm_445_Counter) {
        this.APT5GData12MbLm_445_Counter = APT5GData12MbLm_445_Counter;
    }

    public String getAPT5GData8Mb_437_State() {
        return APT5GData8Mb_437_State;
    }

    public void setAPT5GData8Mb_437_State(String APT5GData8Mb_437_State) {
        this.APT5GData8Mb_437_State = APT5GData8Mb_437_State;
    }

    public String getAPT5GData8Mb_437_StartDate() {
        return APT5GData8Mb_437_StartDate;
    }

    public void setAPT5GData8Mb_437_StartDate(String APT5GData8Mb_437_StartDate) {
        this.APT5GData8Mb_437_StartDate = APT5GData8Mb_437_StartDate;
    }

    public String getAPT5GData8Mb_437_EndDate() {
        return APT5GData8Mb_437_EndDate;
    }

    public void setAPT5GData8Mb_437_EndDate(String APT5GData8Mb_437_EndDate) {
        this.APT5GData8Mb_437_EndDate = APT5GData8Mb_437_EndDate;
    }

    public String getAPT5GData8Mb_437_DiscountID() {
        return APT5GData8Mb_437_DiscountID;
    }

    public void setAPT5GData8Mb_437_DiscountID(String APT5GData8Mb_437_DiscountID) {
        this.APT5GData8Mb_437_DiscountID = APT5GData8Mb_437_DiscountID;
    }

    public String getAPT5GData8Mb_437_Counter() {
        return APT5GData8Mb_437_Counter;
    }

    public void setAPT5GData8Mb_437_Counter(String APT5GData8Mb_437_Counter) {
        this.APT5GData8Mb_437_Counter = APT5GData8Mb_437_Counter;
    }

    public String getAPT5GVoiceOnnet_830_State() {
        return APT5GVoiceOnnet_830_State;
    }

    public void setAPT5GVoiceOnnet_830_State(String APT5GVoiceOnnet_830_State) {
        this.APT5GVoiceOnnet_830_State = APT5GVoiceOnnet_830_State;
    }

    public String getAPT5GVoiceOnnet_830_StartDate() {
        return APT5GVoiceOnnet_830_StartDate;
    }

    public void setAPT5GVoiceOnnet_830_StartDate(String APT5GVoiceOnnet_830_StartDate) {
        this.APT5GVoiceOnnet_830_StartDate = APT5GVoiceOnnet_830_StartDate;
    }

    public String getAPT5GVoiceOnnet_830_EndDate() {
        return APT5GVoiceOnnet_830_EndDate;
    }

    public void setAPT5GVoiceOnnet_830_EndDate(String APT5GVoiceOnnet_830_EndDate) {
        this.APT5GVoiceOnnet_830_EndDate = APT5GVoiceOnnet_830_EndDate;
    }

    public String getAPT5GVoiceOnnet_830_DiscountID() {
        return APT5GVoiceOnnet_830_DiscountID;
    }

    public void setAPT5GVoiceOnnet_830_DiscountID(String APT5GVoiceOnnet_830_DiscountID) {
        this.APT5GVoiceOnnet_830_DiscountID = APT5GVoiceOnnet_830_DiscountID;
    }

    public String getAPT5GVoiceOnnet_830_Counter() {
        return APT5GVoiceOnnet_830_Counter;
    }

    public void setAPT5GVoiceOnnet_830_Counter(String APT5GVoiceOnnet_830_Counter) {
        this.APT5GVoiceOnnet_830_Counter = APT5GVoiceOnnet_830_Counter;
    }

    public String getAPT5GVoiceOnPro_83P_State() {
        return APT5GVoiceOnPro_83P_State;
    }

    public void setAPT5GVoiceOnPro_83P_State(String APT5GVoiceOnPro_83P_State) {
        this.APT5GVoiceOnPro_83P_State = APT5GVoiceOnPro_83P_State;
    }

    public String getAPT5GVoiceOnPro_83P_StartDate() {
        return APT5GVoiceOnPro_83P_StartDate;
    }

    public void setAPT5GVoiceOnPro_83P_StartDate(String APT5GVoiceOnPro_83P_StartDate) {
        this.APT5GVoiceOnPro_83P_StartDate = APT5GVoiceOnPro_83P_StartDate;
    }

    public String getAPT5GVoiceOnPro_83P_EndDate() {
        return APT5GVoiceOnPro_83P_EndDate;
    }

    public void setAPT5GVoiceOnPro_83P_EndDate(String APT5GVoiceOnPro_83P_EndDate) {
        this.APT5GVoiceOnPro_83P_EndDate = APT5GVoiceOnPro_83P_EndDate;
    }

    public String getAPT5GVoiceOnPro_83P_DiscountID() {
        return APT5GVoiceOnPro_83P_DiscountID;
    }

    public void setAPT5GVoiceOnPro_83P_DiscountID(String APT5GVoiceOnPro_83P_DiscountID) {
        this.APT5GVoiceOnPro_83P_DiscountID = APT5GVoiceOnPro_83P_DiscountID;
    }

    public String getAPT5GVoiceOnPro_83P_Counter() {
        return APT5GVoiceOnPro_83P_Counter;
    }

    public void setAPT5GVoiceOnPro_83P_Counter(String APT5GVoiceOnPro_83P_Counter) {
        this.APT5GVoiceOnPro_83P_Counter = APT5GVoiceOnPro_83P_Counter;
    }

    public String getAPT5GVoiceOnUlm_831_State() {
        return APT5GVoiceOnUlm_831_State;
    }

    public void setAPT5GVoiceOnUlm_831_State(String APT5GVoiceOnUlm_831_State) {
        this.APT5GVoiceOnUlm_831_State = APT5GVoiceOnUlm_831_State;
    }

    public String getAPT5GVoiceOnUlm_831_StartDate() {
        return APT5GVoiceOnUlm_831_StartDate;
    }

    public void setAPT5GVoiceOnUlm_831_StartDate(String APT5GVoiceOnUlm_831_StartDate) {
        this.APT5GVoiceOnUlm_831_StartDate = APT5GVoiceOnUlm_831_StartDate;
    }

    public String getAPT5GVoiceOnUlm_831_EndDate() {
        return APT5GVoiceOnUlm_831_EndDate;
    }

    public void setAPT5GVoiceOnUlm_831_EndDate(String APT5GVoiceOnUlm_831_EndDate) {
        this.APT5GVoiceOnUlm_831_EndDate = APT5GVoiceOnUlm_831_EndDate;
    }

    public String getAPT5GVoiceOnUlm_831_DiscountID() {
        return APT5GVoiceOnUlm_831_DiscountID;
    }

    public void setAPT5GVoiceOnUlm_831_DiscountID(String APT5GVoiceOnUlm_831_DiscountID) {
        this.APT5GVoiceOnUlm_831_DiscountID = APT5GVoiceOnUlm_831_DiscountID;
    }

    public String getAPT5GVoiceOnUlm_831_Counter() {
        return APT5GVoiceOnUlm_831_Counter;
    }

    public void setAPT5GVoiceOnUlm_831_Counter(String APT5GVoiceOnUlm_831_Counter) {
        this.APT5GVoiceOnUlm_831_Counter = APT5GVoiceOnUlm_831_Counter;
    }

    public String getRechargeCounter_RC1_State() {
        return RechargeCounter_RC1_State;
    }

    public void setRechargeCounter_RC1_State(String RechargeCounter_RC1_State) {
        this.RechargeCounter_RC1_State = RechargeCounter_RC1_State;
    }

    public String getRechargeCounter_RC1_StartDate() {
        return RechargeCounter_RC1_StartDate;
    }

    public void setRechargeCounter_RC1_StartDate(String RechargeCounter_RC1_StartDate) {
        this.RechargeCounter_RC1_StartDate = RechargeCounter_RC1_StartDate;
    }

    public String getRechargeCounter_RC1_EndDate() {
        return RechargeCounter_RC1_EndDate;
    }

    public void setRechargeCounter_RC1_EndDate(String RechargeCounter_RC1_EndDate) {
        this.RechargeCounter_RC1_EndDate = RechargeCounter_RC1_EndDate;
    }

    public String getRechargeCounter_RC1_DiscountID() {
        return RechargeCounter_RC1_DiscountID;
    }

    public void setRechargeCounter_RC1_DiscountID(String RechargeCounter_RC1_DiscountID) {
        this.RechargeCounter_RC1_DiscountID = RechargeCounter_RC1_DiscountID;
    }

    public String getRechargeCounter_RC1_Counter() {
        return RechargeCounter_RC1_Counter;
    }

    public void setRechargeCounter_RC1_Counter(String RechargeCounter_RC1_Counter) {
        this.RechargeCounter_RC1_Counter = RechargeCounter_RC1_Counter;
    }

    public String getAPT5GData12MbMt_436_State() {
        return APT5GData12MbMt_436_State;
    }

    public void setAPT5GData12MbMt_436_State(String APT5GData12MbMt_436_State) {
        this.APT5GData12MbMt_436_State = APT5GData12MbMt_436_State;
    }

    public String getAPT5GData12MbMt_436_StartDate() {
        return APT5GData12MbMt_436_StartDate;
    }

    public void setAPT5GData12MbMt_436_StartDate(String APT5GData12MbMt_436_StartDate) {
        this.APT5GData12MbMt_436_StartDate = APT5GData12MbMt_436_StartDate;
    }

    public String getAPT5GData12MbMt_436_EndDate() {
        return APT5GData12MbMt_436_EndDate;
    }

    public void setAPT5GData12MbMt_436_EndDate(String APT5GData12MbMt_436_EndDate) {
        this.APT5GData12MbMt_436_EndDate = APT5GData12MbMt_436_EndDate;
    }

    public String getAPT5GData12MbMt_436_DiscountID() {
        return APT5GData12MbMt_436_DiscountID;
    }

    public void setAPT5GData12MbMt_436_DiscountID(String APT5GData12MbMt_436_DiscountID) {
        this.APT5GData12MbMt_436_DiscountID = APT5GData12MbMt_436_DiscountID;
    }

    public String getAPT5GData12MbMt_436_Counter() {
        return APT5GData12MbMt_436_Counter;
    }

    public void setAPT5GData12MbMt_436_Counter(String APT5GData12MbMt_436_Counter) {
        this.APT5GData12MbMt_436_Counter = APT5GData12MbMt_436_Counter;
    }

    public String getAPT5GData12MbMt_446_DiscountID() {
        return APT5GData12MbMt_446_DiscountID;
    }

    public void setAPT5GData12MbMt_446_DiscountID(String APT5GData12MbMt_446_DiscountID) {
        this.APT5GData12MbMt_446_DiscountID = APT5GData12MbMt_446_DiscountID;
    }

    public String getAPT5GData12MbMt_446_Counter() {
        return APT5GData12MbMt_446_Counter;
    }

    public void setAPT5GData12MbMt_446_Counter(String APT5GData12MbMt_446_Counter) {
        this.APT5GData12MbMt_446_Counter = APT5GData12MbMt_446_Counter;
    }

    public String getShareLm5G_770_State() {
        return ShareLm5G_770_State;
    }

    public void setShareLm5G_770_State(String ShareLm5G_770_State) {
        this.ShareLm5G_770_State = ShareLm5G_770_State;
    }

    public String getShareLm5G_770_StartDate() {
        return ShareLm5G_770_StartDate;
    }

    public void setShareLm5G_770_StartDate(String ShareLm5G_770_StartDate) {
        this.ShareLm5G_770_StartDate = ShareLm5G_770_StartDate;
    }

    public String getShareLm5G_770_EndDate() {
        return ShareLm5G_770_EndDate;
    }

    public void setShareLm5G_770_EndDate(String ShareLm5G_770_EndDate) {
        this.ShareLm5G_770_EndDate = ShareLm5G_770_EndDate;
    }

    public String getShareLm5G_770_DiscountID() {
        return ShareLm5G_770_DiscountID;
    }

    public void setShareLm5G_770_DiscountID(String ShareLm5G_770_DiscountID) {
        this.ShareLm5G_770_DiscountID = ShareLm5G_770_DiscountID;
    }

    public String getShareLm5G_770_Counter() {
        return ShareLm5G_770_Counter;
    }

    public void setShareLm5G_770_Counter(String ShareLm5G_770_Counter) {
        this.ShareLm5G_770_Counter = ShareLm5G_770_Counter;
    }

    public String getShareDt5G_771_State() {
        return ShareDt5G_771_State;
    }

    public void setShareDt5G_771_State(String ShareDt5G_771_State) {
        this.ShareDt5G_771_State = ShareDt5G_771_State;
    }

    public String getShareDt5G_771_StartDate() {
        return ShareDt5G_771_StartDate;
    }

    public void setShareDt5G_771_StartDate(String ShareDt5G_771_StartDate) {
        this.ShareDt5G_771_StartDate = ShareDt5G_771_StartDate;
    }

    public String getShareDt5G_771_EndDate() {
        return ShareDt5G_771_EndDate;
    }

    public void setShareDt5G_771_EndDate(String ShareDt5G_771_EndDate) {
        this.ShareDt5G_771_EndDate = ShareDt5G_771_EndDate;
    }

    public String getShareDt5G_771_DiscountID() {
        return ShareDt5G_771_DiscountID;
    }

    public void setShareDt5G_771_DiscountID(String ShareDt5G_771_DiscountID) {
        this.ShareDt5G_771_DiscountID = ShareDt5G_771_DiscountID;
    }

    public String getShareDt5G_771_Counter() {
        return ShareDt5G_771_Counter;
    }

    public void setShareDt5G_771_Counter(String ShareDt5G_771_Counter) {
        this.ShareDt5G_771_Counter = ShareDt5G_771_Counter;
    }

    public String getDataFUm5G_530_State() {
        return DataFUm5G_530_State;
    }

    public void setDataFUm5G_530_State(String DataFUm5G_530_State) {
        this.DataFUm5G_530_State = DataFUm5G_530_State;
    }

    public String getDataFUm5G_530_StartDate() {
        return DataFUm5G_530_StartDate;
    }

    public void setDataFUm5G_530_StartDate(String DataFUm5G_530_StartDate) {
        this.DataFUm5G_530_StartDate = DataFUm5G_530_StartDate;
    }

    public String getDataFUm5G_530_EndDate() {
        return DataFUm5G_530_EndDate;
    }

    public void setDataFUm5G_530_EndDate(String DataFUm5G_530_EndDate) {
        this.DataFUm5G_530_EndDate = DataFUm5G_530_EndDate;
    }

    public String getDataFUm5G_530_DiscountID() {
        return DataFUm5G_530_DiscountID;
    }

    public void setDataFUm5G_530_DiscountID(String DataFUm5G_530_DiscountID) {
        this.DataFUm5G_530_DiscountID = DataFUm5G_530_DiscountID;
    }

    public String getDataFUm5G_530_Counter() {
        return DataFUm5G_530_Counter;
    }

    public void setDataFUm5G_530_Counter(String DataFUm5G_530_Counter) {
        this.DataFUm5G_530_Counter = DataFUm5G_530_Counter;
    }

    public String getDataF21MbLm5G_531_State() {
        return DataF21MbLm5G_531_State;
    }

    public void setDataF21MbLm5G_531_State(String DataF21MbLm5G_531_State) {
        this.DataF21MbLm5G_531_State = DataF21MbLm5G_531_State;
    }

    public String getDataF21MbLm5G_531_StartDate() {
        return DataF21MbLm5G_531_StartDate;
    }

    public void setDataF21MbLm5G_531_StartDate(String DataF21MbLm5G_531_StartDate) {
        this.DataF21MbLm5G_531_StartDate = DataF21MbLm5G_531_StartDate;
    }

    public String getDataF21MbLm5G_531_EndDate() {
        return DataF21MbLm5G_531_EndDate;
    }

    public void setDataF21MbLm5G_531_EndDate(String DataF21MbLm5G_531_EndDate) {
        this.DataF21MbLm5G_531_EndDate = DataF21MbLm5G_531_EndDate;
    }

    public String getDataF21MbLm5G_531_DiscountID() {
        return DataF21MbLm5G_531_DiscountID;
    }

    public void setDataF21MbLm5G_531_DiscountID(String DataF21MbLm5G_531_DiscountID) {
        this.DataF21MbLm5G_531_DiscountID = DataF21MbLm5G_531_DiscountID;
    }

    public String getDataF21MbLm5G_531_Counter() {
        return DataF21MbLm5G_531_Counter;
    }

    public void setDataF21MbLm5G_531_Counter(String DataF21MbLm5G_531_Counter) {
        this.DataF21MbLm5G_531_Counter = DataF21MbLm5G_531_Counter;
    }

    public String getDataF21MbLm5G_541_State() {
        return DataF21MbLm5G_541_State;
    }

    public void setDataF21MbLm5G_541_State(String DataF21MbLm5G_541_State) {
        this.DataF21MbLm5G_541_State = DataF21MbLm5G_541_State;
    }

    public String getDataF21MbLm5G_541_StartDate() {
        return DataF21MbLm5G_541_StartDate;
    }

    public void setDataF21MbLm5G_541_StartDate(String DataF21MbLm5G_541_StartDate) {
        this.DataF21MbLm5G_541_StartDate = DataF21MbLm5G_541_StartDate;
    }

    public String getDataF21MbLm5G_541_EndDate() {
        return DataF21MbLm5G_541_EndDate;
    }

    public void setDataF21MbLm5G_541_EndDate(String DataF21MbLm5G_541_EndDate) {
        this.DataF21MbLm5G_541_EndDate = DataF21MbLm5G_541_EndDate;
    }

    public String getDataF21MbLm5G_541_DiscountID() {
        return DataF21MbLm5G_541_DiscountID;
    }

    public void setDataF21MbLm5G_541_DiscountID(String DataF21MbLm5G_541_DiscountID) {
        this.DataF21MbLm5G_541_DiscountID = DataF21MbLm5G_541_DiscountID;
    }

    public String getDataF21MbLm5G_541_Counter() {
        return DataF21MbLm5G_541_Counter;
    }

    public void setDataF21MbLm5G_541_Counter(String DataF21MbLm5G_541_Counter) {
        this.DataF21MbLm5G_541_Counter = DataF21MbLm5G_541_Counter;
    }

    public String getDataF12MbLm5G_532_State() {
        return DataF12MbLm5G_532_State;
    }

    public void setDataF12MbLm5G_532_State(String DataF12MbLm5G_532_State) {
        this.DataF12MbLm5G_532_State = DataF12MbLm5G_532_State;
    }

    public String getDataF12MbLm5G_532_StartDate() {
        return DataF12MbLm5G_532_StartDate;
    }

    public void setDataF12MbLm5G_532_StartDate(String DataF12MbLm5G_532_StartDate) {
        this.DataF12MbLm5G_532_StartDate = DataF12MbLm5G_532_StartDate;
    }

    public String getDataF12MbLm5G_532_EndDate() {
        return DataF12MbLm5G_532_EndDate;
    }

    public void setDataF12MbLm5G_532_EndDate(String DataF12MbLm5G_532_EndDate) {
        this.DataF12MbLm5G_532_EndDate = DataF12MbLm5G_532_EndDate;
    }

    public String getDataF12MbLm5G_532_DiscountID() {
        return DataF12MbLm5G_532_DiscountID;
    }

    public void setDataF12MbLm5G_532_DiscountID(String DataF12MbLm5G_532_DiscountID) {
        this.DataF12MbLm5G_532_DiscountID = DataF12MbLm5G_532_DiscountID;
    }

    public String getDataF12MbLm5G_532_Counter() {
        return DataF12MbLm5G_532_Counter;
    }

    public void setDataF12MbLm5G_532_Counter(String DataF12MbLm5G_532_Counter) {
        this.DataF12MbLm5G_532_Counter = DataF12MbLm5G_532_Counter;
    }

    public String getDataF12MbLm5G_542_State() {
        return DataF12MbLm5G_542_State;
    }

    public void setDataF12MbLm5G_542_State(String DataF12MbLm5G_542_State) {
        this.DataF12MbLm5G_542_State = DataF12MbLm5G_542_State;
    }

    public String getDataF12MbLm5G_542_StartDate() {
        return DataF12MbLm5G_542_StartDate;
    }

    public void setDataF12MbLm5G_542_StartDate(String DataF12MbLm5G_542_StartDate) {
        this.DataF12MbLm5G_542_StartDate = DataF12MbLm5G_542_StartDate;
    }

    public String getDataF12MbLm5G_542_EndDate() {
        return DataF12MbLm5G_542_EndDate;
    }

    public void setDataF12MbLm5G_542_EndDate(String DataF12MbLm5G_542_EndDate) {
        this.DataF12MbLm5G_542_EndDate = DataF12MbLm5G_542_EndDate;
    }

    public String getDataF12MbLm5G_542_DiscountID() {
        return DataF12MbLm5G_542_DiscountID;
    }

    public void setDataF12MbLm5G_542_DiscountID(String DataF12MbLm5G_542_DiscountID) {
        this.DataF12MbLm5G_542_DiscountID = DataF12MbLm5G_542_DiscountID;
    }

    public String getDataF12MbLm5G_542_Counter() {
        return DataF12MbLm5G_542_Counter;
    }

    public void setDataF12MbLm5G_542_Counter(String DataF12MbLm5G_542_Counter) {
        this.DataF12MbLm5G_542_Counter = DataF12MbLm5G_542_Counter;
    }

    public String getOneHundredMb12MbLm5G_536_State() {
        return OneHundredMb12MbLm5G_536_State;
    }

    public void setOneHundredMb12MbLm5G_536_State(String OneHundredMb12MbLm5G_536_State) {
        this.OneHundredMb12MbLm5G_536_State = OneHundredMb12MbLm5G_536_State;
    }

    public String getOneHundredMb12MbLm5G_536_StartDate() {
        return OneHundredMb12MbLm5G_536_StartDate;
    }

    public void setOneHundredMb12MbLm5G_536_StartDate(String OneHundredMb12MbLm5G_536_StartDate) {
        this.OneHundredMb12MbLm5G_536_StartDate = OneHundredMb12MbLm5G_536_StartDate;
    }

    public String getOneHundredMb12MbLm5G_536_EndDate() {
        return OneHundredMb12MbLm5G_536_EndDate;
    }

    public void setOneHundredMb12MbLm5G_536_EndDate(String OneHundredMb12MbLm5G_536_EndDate) {
        this.OneHundredMb12MbLm5G_536_EndDate = OneHundredMb12MbLm5G_536_EndDate;
    }

    public String getOneHundredMb12MbLm5G_536_DiscountID() {
        return OneHundredMb12MbLm5G_536_DiscountID;
    }

    public void setOneHundredMb12MbLm5G_536_DiscountID(String OneHundredMb12MbLm5G_536_DiscountID) {
        this.OneHundredMb12MbLm5G_536_DiscountID = OneHundredMb12MbLm5G_536_DiscountID;
    }

    public String getOneHundredMb12MbLm5G_536_Counter() {
        return OneHundredMb12MbLm5G_536_Counter;
    }

    public void setOneHundredMb12MbLm5G_536_Counter(String OneHundredMb12MbLm5G_536_Counter) {
        this.OneHundredMb12MbLm5G_536_Counter = OneHundredMb12MbLm5G_536_Counter;
    }

    public String getOneHundredMb12MbLm5G_546_State() {
        return OneHundredMb12MbLm5G_546_State;
    }

    public void setOneHundredMb12MbLm5G_546_State(String OneHundredMb12MbLm5G_546_State) {
        this.OneHundredMb12MbLm5G_546_State = OneHundredMb12MbLm5G_546_State;
    }

    public String getOneHundredMb12MbLm5G_546_StartDate() {
        return OneHundredMb12MbLm5G_546_StartDate;
    }

    public void setOneHundredMb12MbLm5G_546_StartDate(String OneHundredMb12MbLm5G_546_StartDate) {
        this.OneHundredMb12MbLm5G_546_StartDate = OneHundredMb12MbLm5G_546_StartDate;
    }

    public String getOneHundredMb12MbLm5G_546_EndDate() {
        return OneHundredMb12MbLm5G_546_EndDate;
    }

    public void setOneHundredMb12MbLm5G_546_EndDate(String OneHundredMb12MbLm5G_546_EndDate) {
        this.OneHundredMb12MbLm5G_546_EndDate = OneHundredMb12MbLm5G_546_EndDate;
    }

    public String getOneHundredMb12MbLm5G_546_DiscountID() {
        return OneHundredMb12MbLm5G_546_DiscountID;
    }

    public void setOneHundredMb12MbLm5G_546_DiscountID(String OneHundredMb12MbLm5G_546_DiscountID) {
        this.OneHundredMb12MbLm5G_546_DiscountID = OneHundredMb12MbLm5G_546_DiscountID;
    }

    public String getOneHundredMb12MbLm5G_546_Counter() {
        return OneHundredMb12MbLm5G_546_Counter;
    }

    public void setOneHundredMb12MbLm5G_546_Counter(String OneHundredMb12MbLm5G_546_Counter) {
        this.OneHundredMb12MbLm5G_546_Counter = OneHundredMb12MbLm5G_546_Counter;
    }

    public String getFiveHundredMb21MbLm5G_537_State() {
        return FiveHundredMb21MbLm5G_537_State;
    }

    public void setFiveHundredMb21MbLm5G_537_State(String FiveHundredMb21MbLm5G_537_State) {
        this.FiveHundredMb21MbLm5G_537_State = FiveHundredMb21MbLm5G_537_State;
    }

    public String getFiveHundredMb21MbLm5G_537_StartDate() {
        return FiveHundredMb21MbLm5G_537_StartDate;
    }

    public void setFiveHundredMb21MbLm5G_537_StartDate(String FiveHundredMb21MbLm5G_537_StartDate) {
        this.FiveHundredMb21MbLm5G_537_StartDate = FiveHundredMb21MbLm5G_537_StartDate;
    }

    public String getFiveHundredMb21MbLm5G_537_EndDate() {
        return FiveHundredMb21MbLm5G_537_EndDate;
    }

    public void setFiveHundredMb21MbLm5G_537_EndDate(String FiveHundredMb21MbLm5G_537_EndDate) {
        this.FiveHundredMb21MbLm5G_537_EndDate = FiveHundredMb21MbLm5G_537_EndDate;
    }

    public String getFiveHundredMb21MbLm5G_537_DiscountID() {
        return FiveHundredMb21MbLm5G_537_DiscountID;
    }

    public void setFiveHundredMb21MbLm5G_537_DiscountID(String FiveHundredMb21MbLm5G_537_DiscountID) {
        this.FiveHundredMb21MbLm5G_537_DiscountID = FiveHundredMb21MbLm5G_537_DiscountID;
    }

    public String getFiveHundredMb21MbLm5G_537_Counter() {
        return FiveHundredMb21MbLm5G_537_Counter;
    }

    public void setFiveHundredMb21MbLm5G_537_Counter(String FiveHundredMb21MbLm5G_537_Counter) {
        this.FiveHundredMb21MbLm5G_537_Counter = FiveHundredMb21MbLm5G_537_Counter;
    }

    public String getFiveHundredMb21MbLm5G_547_State() {
        return FiveHundredMb21MbLm5G_547_State;
    }

    public void setFiveHundredMb21MbLm5G_547_State(String FiveHundredMb21MbLm5G_547_State) {
        this.FiveHundredMb21MbLm5G_547_State = FiveHundredMb21MbLm5G_547_State;
    }

    public String getFiveHundredMb21MbLm5G_547_StartDate() {
        return FiveHundredMb21MbLm5G_547_StartDate;
    }

    public void setFiveHundredMb21MbLm5G_547_StartDate(String FiveHundredMb21MbLm5G_547_StartDate) {
        this.FiveHundredMb21MbLm5G_547_StartDate = FiveHundredMb21MbLm5G_547_StartDate;
    }

    public String getFiveHundredMb21MbLm5G_547_EndDate() {
        return FiveHundredMb21MbLm5G_547_EndDate;
    }

    public void setFiveHundredMb21MbLm5G_547_EndDate(String FiveHundredMb21MbLm5G_547_EndDate) {
        this.FiveHundredMb21MbLm5G_547_EndDate = FiveHundredMb21MbLm5G_547_EndDate;
    }

    public String getFiveHundredMb21MbLm5G_547_DiscountID() {
        return FiveHundredMb21MbLm5G_547_DiscountID;
    }

    public void setFiveHundredMb21MbLm5G_547_DiscountID(String FiveHundredMb21MbLm5G_547_DiscountID) {
        this.FiveHundredMb21MbLm5G_547_DiscountID = FiveHundredMb21MbLm5G_547_DiscountID;
    }

    public String getFiveHundredMb21MbLm5G_547_Counter() {
        return FiveHundredMb21MbLm5G_547_Counter;
    }

    public void setFiveHundredMb21MbLm5G_547_Counter(String FiveHundredMb21MbLm5G_547_Counter) {
        this.FiveHundredMb21MbLm5G_547_Counter = FiveHundredMb21MbLm5G_547_Counter;
    }

    public String getOneGb21MbLm5G_538_State() {
        return OneGb21MbLm5G_538_State;
    }

    public void setOneGb21MbLm5G_538_State(String OneGb21MbLm5G_538_State) {
        this.OneGb21MbLm5G_538_State = OneGb21MbLm5G_538_State;
    }

    public String getOneGb21MbLm5G_538_StartDate() {
        return OneGb21MbLm5G_538_StartDate;
    }

    public void setOneGb21MbLm5G_538_StartDate(String OneGb21MbLm5G_538_StartDate) {
        this.OneGb21MbLm5G_538_StartDate = OneGb21MbLm5G_538_StartDate;
    }

    public String getOneGb21MbLm5G_538_EndDate() {
        return OneGb21MbLm5G_538_EndDate;
    }

    public void setOneGb21MbLm5G_538_EndDate(String OneGb21MbLm5G_538_EndDate) {
        this.OneGb21MbLm5G_538_EndDate = OneGb21MbLm5G_538_EndDate;
    }

    public String getOneGb21MbLm5G_538_DiscountID() {
        return OneGb21MbLm5G_538_DiscountID;
    }

    public void setOneGb21MbLm5G_538_DiscountID(String OneGb21MbLm5G_538_DiscountID) {
        this.OneGb21MbLm5G_538_DiscountID = OneGb21MbLm5G_538_DiscountID;
    }

    public String getOneGb21MbLm5G_538_Counter() {
        return OneGb21MbLm5G_538_Counter;
    }

    public void setOneGb21MbLm5G_538_Counter(String OneGb21MbLm5G_538_Counter) {
        this.OneGb21MbLm5G_538_Counter = OneGb21MbLm5G_538_Counter;
    }

    public String getOneGb21MbLm5G_548_State() {
        return OneGb21MbLm5G_548_State;
    }

    public void setOneGb21MbLm5G_548_State(String OneGb21MbLm5G_548_State) {
        this.OneGb21MbLm5G_548_State = OneGb21MbLm5G_548_State;
    }

    public String getOneGb21MbLm5G_548_StartDate() {
        return OneGb21MbLm5G_548_StartDate;
    }

    public void setOneGb21MbLm5G_548_StartDate(String OneGb21MbLm5G_548_StartDate) {
        this.OneGb21MbLm5G_548_StartDate = OneGb21MbLm5G_548_StartDate;
    }

    public String getOneGb21MbLm5G_548_EndDate() {
        return OneGb21MbLm5G_548_EndDate;
    }

    public void setOneGb21MbLm5G_548_EndDate(String OneGb21MbLm5G_548_EndDate) {
        this.OneGb21MbLm5G_548_EndDate = OneGb21MbLm5G_548_EndDate;
    }

    public String getOneGb21MbLm5G_548_DiscountID() {
        return OneGb21MbLm5G_548_DiscountID;
    }

    public void setOneGb21MbLm5G_548_DiscountID(String OneGb21MbLm5G_548_DiscountID) {
        this.OneGb21MbLm5G_548_DiscountID = OneGb21MbLm5G_548_DiscountID;
    }

    public String getOneGb21MbLm5G_548_Counter() {
        return OneGb21MbLm5G_548_Counter;
    }

    public void setOneGb21MbLm5G_548_Counter(String OneGb21MbLm5G_548_Counter) {
        this.OneGb21MbLm5G_548_Counter = OneGb21MbLm5G_548_Counter;
    }

    public String getAPT5GDataWiFi_555_State() {
        return APT5GDataWiFi_555_State;
    }

    public void setAPT5GDataWiFi_555_State(String APT5GDataWiFi_555_State) {
        this.APT5GDataWiFi_555_State = APT5GDataWiFi_555_State;
    }

    public String getAPT5GDataWiFi_555_StartDate() {
        return APT5GDataWiFi_555_StartDate;
    }

    public void setAPT5GDataWiFi_555_StartDate(String APT5GDataWiFi_555_StartDate) {
        this.APT5GDataWiFi_555_StartDate = APT5GDataWiFi_555_StartDate;
    }

    public String getAPT5GDataWiFi_555_EndDate() {
        return APT5GDataWiFi_555_EndDate;
    }

    public void setAPT5GDataWiFi_555_EndDate(String APT5GDataWiFi_555_EndDate) {
        this.APT5GDataWiFi_555_EndDate = APT5GDataWiFi_555_EndDate;
    }

    public String getAPT5GDataWiFi_555_DiscountID() {
        return APT5GDataWiFi_555_DiscountID;
    }

    public void setAPT5GDataWiFi_555_DiscountID(String APT5GDataWiFi_555_DiscountID) {
        this.APT5GDataWiFi_555_DiscountID = APT5GDataWiFi_555_DiscountID;
    }

    public String getAPT5GDataWiFi_555_Counter() {
        return APT5GDataWiFi_555_Counter;
    }

    public void setAPT5GDataWiFi_555_Counter(String APT5GDataWiFi_555_Counter) {
        this.APT5GDataWiFi_555_Counter = APT5GDataWiFi_555_Counter;
    }

    
}
