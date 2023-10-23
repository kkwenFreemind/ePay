package com.apt.epay.share;

//import com.apt.util.DateUtil;
//import com.epay.share.EjbShareParm;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicLong;
/**
 *
 * @author Administrator
 */
public class ShareParm {

    public ShareParm() {
        new ShareBean().loadSysConfParameters();
    }

    public String PARM_DTONE_SERVICENAME = System.getProperty("PARM_DTONE_SERVICENAME");
    public String PARM_TDE_MDN = System.getProperty("PARM_TDE_MDN");
    
    public String PARM_NOKIA_OCS_SYSTEM_ID = System.getProperty("PARM_NOKIA_OCS_SYSTEM_ID");
    public String PARM_NOKIA_OCS_SYSTEM_PWD = System.getProperty("PARM_NOKIA_OCS_SYSTEM_PWD");
    public String PARM_NOKIA_OCS_URL = System.getProperty("PARM_NOKIA_OCS_URL");

    public String PARM_NOKIA_HLAPI_URL = System.getProperty("PARM_NOKIA_HLAPI_URL");
    public String PARM_NOKIA_HLAPI_SYSTEM_ID = System.getProperty("PARM_NOKIA_HLAPI_SYSTEM_ID");
    public String PARM_NOKIA_HLAPI_SYSTEM_PWD = System.getProperty("PARM_NOKIA_HLAPI_SYSTEM_PWD");
    public String PARM_SCT_PROXY_FLAG = System.getProperty("PARM_SCT_PROXY_FLAG");
    
    //kk config
//    public String PARM_NOKIA_OCS_SYSTEM_ID = "123456";
//    public String PARM_NOKIA_OCS_SYSTEM_PWD = "123456";
//    public String PARM_NOKIA_OCS_URL = "http://localhost:1234/gateway_servlet/gateway";
//
//    public String PARM_NOKIA_HLAPI_URL = "http://localhost:4321/SvcMgr";
//    public String PARM_NOKIA_HLAPI_SYSTEM_ID = "epayprov";
//    public String PARM_NOKIA_HLAPI_SYSTEM_PWD = "epay@53362";
//    public String PARM_SCT_PROXY_FLAG = "";
    
    public String PARM_PRICEPLCODE = System.getProperty("PARM_PRICEPLCODE");
    public String PARM_DTONE_URL = System.getProperty("PARM_DTONE_URL");
    public String PARM_DTONE_TRANS_URL = System.getProperty("PARM_DTONE_TRANS_URL");
    public String PARM_DTONE_TRANS_QUERY_URL = System.getProperty("PARM_DTONE_TRANS_QUERY_URL");
    public String PARM_DTONE_USERNAME = System.getProperty("PARM_DTONE_USERNAME");
    public String PARM_DTONE_PASSWORD = System.getProperty("PARM_DTONE_PASSWORD");
    public String PARM_DTONE_QUOTA = System.getProperty("PARM_DTONE_QUOTA");
    public String PARM_DTONE_LAST_NAME = System.getProperty("PARM_DTONE_LAST_NAME");
    public String PARM_DTONE_FIRST_NAME = System.getProperty("PARM_DTONE_FIRST_NAME");
    public String PARM_COUNTRYCODE_URL = System.getProperty("PARM_COUNTRYCODE_URL");


    public String PARM_APISRCID = System.getProperty("PARM_APISRCID");
    public String PARM_PAYTOOL = System.getProperty("PARM_PAYTOOL");

    public String PARM_POS_DES_KEY = System.getProperty("PARM_POS_DES_KEY");
    public static final String EPAY_CALLID = "A015";
    public static final String ZTE_ERRORDESC1 = "S-PPS-00001";
    public static final String ZTE_ERRORDESC2 = "S-PPS-00009";
    public static final String ALU_EROR_RESULTCODE = "20160";

    public String PARM_VAS_URL = System.getProperty("PARM_VAS_URL");
    //ZTE
    public String PARM_QuerySubsProfile = System.getProperty("PARM_QuerySubsProfile");
    public String PARM_ReCharge = System.getProperty("PARM_ReCharge");
    public String PARM_AddUserIndiPricePlan = System.getProperty("PARM_AddUserIndiPricePlan");
    public String PARM_DeductFee = System.getProperty("PARM_DeductFee");
    public String PARM_4GZTEOCS_SYSTEM_ID = System.getProperty("PARM_4GZTEOCS_SYSTEM_ID");
    public String PARM_4GZTEOCS_SYSTEM_PWD = System.getProperty("PARM_4GZTEOCS_SYSTEM_PWD");
    public String PARM_4GZTEPINCODE_ACC = System.getProperty("PARM_4GZTEPINCODE_ACC");

    public String PARM_SYS_UPLOAD_TMP_PATH = "/tmp/";
    // Epay Bucket List
    public static String[][] OCS_bucketIDArray = {{"通信基本費", "610"}, {"通信贈送費", "620"}, {"數據基本", "720"}, {"數據贈送", "730"}, {"網內贈送語音(計量)", "810"}};
    public static String[][] STATUS_ServiceID = {{"啟用", "1"}, {"停用", "0"}};
    public static String[][] TYPE_DepositType = {{"網路儲值", "1"}, {"餘額抵扣", "11"}};
    public static long HTTP_REQ_TIMEOUT = 200000;

    public String PARM_ECF_URL = System.getProperty("PARM_ECF_URL");
    public String PARM_VCARD_EXPIRE = System.getProperty("PARM_VCARD_EXPIRE");
    public String OCS_SERVICE_DAY = System.getProperty("PARM_4GOCS_SERICE_DAY");
    public String MERCHANT_CALLER_ID = System.getProperty("PARM_MERCHANT_CALLER_ID");

    public String PARM_MAIL_RELAY_HOST = System.getProperty("PARM_MAIL_RELAY_HOST");
    public String PARM_MAIL_FROM = System.getProperty("PARM_MAIL_FROM");
    public String PARM_MAIL_TO = System.getProperty("PARM_MAIL_TO");
    public String PARM_MAIL_TO_OC = System.getProperty("PARM_MAIL_TO_OC");
    public String PARM_MAIL_TO_4GOCS = System.getProperty("PARM_MAIL_TO_4GOCS");

    public static String PINCODE_SERVICEID = "0000000000";
    public static String PINCODE_ITEMCODE = "儲值卡";
    public static String PINCODE_ITEMNAME = "儲值卡";

    public static String VAS_ITEMNAME = "虛擬卡片儲值";

    public static String CHIL_ID_VOLVB = "345000";
    public static String CHIL_ID_DATADB = "346100";
    public static String CHIL_ID_VOCVB = "310113";
    public static String ACC_CHIL_ID_VOLVB = "304610";
    public static String TRANS_ID = "466001";
    public static int TTYPE_ADJUSTACCOUNT = 1;
    public static int TTYPE_CCACCOUNT = 2;

    public String PARM_XSMS_URL = System.getProperty("PARM_XSMS_URL");
    public String PARM_XSMS_UID = System.getProperty("PARM_XSMS_UID");
    public String PARM_XSMS_PWD = System.getProperty("PARM_XSMS_PWD");
    public String PARM_XSMS_MDN = System.getProperty("PARM_XSMS_MDN");
    public String PARM_XSMS_CALLBACK = System.getProperty("PARM_XSMS_CALLBACK");

    public String PARM_XSMS_OTP_URL = System.getProperty("PARM_XSMS_OTP_URL");
    public String PARM_XSMS_OTP_UID = System.getProperty("PARM_XSMS_OTP_UID");
    public String PARM_XSMS_OTP_PWD = System.getProperty("PARM_XSMS_OTP_PWD");
    public String PARM_XSMS_OTP_MDN = System.getProperty("PARM_XSMS_OTP_MDN");
    public String PARM_XSMS_OTP_CALLBACK = System.getProperty("PARM_XSMS_OTP_CALLBACK");

    public String PGENKEY = "";
    public String OCS_SYSTEM_ID = System.getProperty("PARM_4GOCS_SYSTEM_ID");
    public String OCS_SYSTEM_PWD = System.getProperty("PARM_4GOCS_SYSTEM_PWD");

    //Nokia 



    public String PARM_APT_URL = System.getProperty("PARM_APT_URL");
    public String PARM_ECARE_URL = System.getProperty("PARM_ECARE_URL");

    public String SOA_SYSTEM_ID = System.getProperty("PARM_SOA_SYSTEM_ID");
    public String SOA_SYSTEM_PWD = System.getProperty("PARM_SOA_SYSTEM_PWD");

    public String PARM_SOA_URL = System.getProperty("PARM_SOA_URL");
    public String PARM_CWS_URL = System.getProperty("PARM_CWS_URL");

    public String PARM_3GOCS_URL = System.getProperty("PARM_3GOCS_URL");
    public String PARM_CPECF_URL = System.getProperty("PARM_CPECF_URL");
    public String PARM_EPAY_CPID = System.getProperty("PARM_EPAY_CPID");
    public String PARM_PG_KEY = System.getProperty("PARM_PG_KEY");
    public String PARM_PG_IDENT = System.getProperty("PARM_PG_IDENT");

    public String PARM_4GOCS_SYSTEM_ID = System.getProperty("PARM_4GOCS_SYSTEM_ID");
    public String PARM_4GOCS_SYSTEM_PWD = System.getProperty("PARM_4GOCS_SYSTEM_PWD");
    public String PARM_4GOCS_URL = System.getProperty("PARM_4GOCS_URL");
    public String PARM_OC_MDN = System.getProperty("PARM_OC_MDN");
    public String PARM_CMS_KEY = System.getProperty("PARM_CMS_KEY");

    public static int PAYMETHOD_CREDITCARD = 1;
    public static int PAYMETHOD_ATM = 2;
    public static int PAYMETHOD_IBON = 3;
    public static int PAYMETHOD_PINCODE = 4;
    public static int PAYMETHOD_POS = 5;
    public static int PAYMETHOD_ADJUSTACC = 6;
    public static int PAYMETHOD_VoiceBucketCleanForiCallACC = 7;

    public static final String PARM_MOBILE_USER_DEFAULT_PASSWORD = "12345";
    public static final String PARM_MOBILE_USER_DEFAULT_USER_STATUS = "S01";//用戶狀態正常
    public static final String PARM_UI_PATH = "";

    public static String PARM_ServletContext_PROP_FILE_PATH = "";
//    public static String PARM_DEFAULT_PROP_FILE_PATH = "/jboss6.1.0/epay.properties";
    public static String PARM_DEFAULT_PROP_FILE_PATH = "/opt/jboss/epay.properties";
    public static long PARM_CACHE_IP_TABLES_PERIOD_TIME = 1000 * 60 * 10;
    //***********************************DateFormat****************************
    public static final String PARM_DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String PARM_DATEFORMAT3 = "yyyyMMdd";
    public static final String PARM_DATEFORMAT5 = "yyyy-MM-dd 00:00:00";
    public static final String PARM_DATEFORMAT7 = "yyyy-MM-dd";
    public static final String PARM_DATEFORMAT8 = "yyyyMMddHHmmss";
    public static final String PARM_DATEFORMAT22 = "yyyy";
    public static final String PARM_PINCODE_DATEFORMAT = "MM/dd/yyyy.HH:mm:ss";
    public static final String PARM_DATEFORMAT_LASTTIME = "yyyy-MM-dd 23:59:59";
    //**************************************************************
    public static final String PARM_CHARSETNAME_UTF8 = "UTF-8";
    //**********************REGULAR EXPRESSION**************************************
    public static final String PARM_REGULAR_MDN = "09[0-9]{2}[0-9]{6}";
    public static final String PARM_REGULAR_MDN_2 = "9[0-9]{2}[0-9]{6}";
    public static final String PARM_REGULAR_TXTFILE = ".*.(txt|TXT|csv|CSV)$";
    public static final String PARM_REGULAR_CSVFILE = ".*.(csv|CSV)$";
    public static final String PARM_REGULAR_MDN_3 = "8869[0-9]{2}[0-9]{6}";
    public static final String PARM_REGULAR_MDN_4 = "002[0-9]{10,}";
    public static final String PARM_REGULAR_MDN_5 = "005[0-9]{10,}";
    public static final String PARM_REGULAR_CHINESE = "^[\\u4e00-\\u9fa5]+$";
    public static final String PARM_REGULAR_IP = "\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b";

    //********************I18N Property*************************************************
    public final String PARM_I18N_SYS_NAME = org.zkoss.util.resource.Labels.getLabel("Sys.Name");
    //**********************System Property********************************************
    public final String PARM_AP_SERVER_CODE = System.getProperty("PARM_AP_SERVER_CODE");
    public final String PARM_DEBUGGING_MODE = System.getProperty("PARM_DEBUGGING_MODE");
    public final String PARM_CACHEPOOL_MODE = System.getProperty("PARM_CACHEPOOL_MODE");
//    public final int PARM_PAY_LIMIT = Integer.parseInt(System.getProperty("PARM_PAY_LIMIT"));
    public final String PARM_CC_URL = System.getProperty("PARM_CC_URL");
    public final String PARM_RETURN_URL = System.getProperty("PARM_RETURN_URL");
    public final String PARM_ATM_URL = System.getProperty("PARM_ATM_RUL");
    public final String PARM_CAPTURE_URL = System.getProperty("PARM_CAPTURE_URL");
    public final String PARM_IBON_URL = System.getProperty("PARM_IBON_URL");
    public final String PARM_IBON_RETURN_URL = System.getProperty("PARM_IBON_RETURN_URL");
    public final String XML_URL = "http://xsms.aptg.com.tw/XSMSAP/api/${APIName}";

    final public static int PARM_IP_TABLES_BLACK = 0;
    final public static int PARM_IP_TABLES_WHITE = 1;
    public static int PARM_TRANS_PAYTYPE_CREDITCARD = 1;
    public static int PARM_TRANS_PAYTYPE_ATM = 2;
    public static int PARM_TRANS_PAYTYPE_IBON = 3;
    public static int PARM_TRANS_PAYTYPE_PINCARD = 4;
    public static int PARM_TRANS_PAYTYPE_POS = 5;
    public static int PARM_TRANS_PAYTYPE_ACCOUNTADJ = 6;
}
