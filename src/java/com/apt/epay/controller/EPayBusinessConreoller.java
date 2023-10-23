package com.apt.epay.controller;

import com.epay.ejb.bean.EPAY_BUCKET;
import com.epay.ejb.bean.EPAY_BUCKETHISTORY;
import com.epay.ejb.bean.EPAY_CALLER;
import com.epay.ejb.bean.EPAY_COMMON_USER;
import com.epay.ejb.bean.EPAY_CP_INFO;
import com.epay.ejb.bean.EPAY_DEALERCARD;
import com.epay.ejb.bean.EPAY_DEALERMDN;
import com.epay.ejb.bean.EPAY_INVOICE;
import com.epay.ejb.bean.EPAY_INVOICE_ITEM;
import com.epay.ejb.bean.EPAY_IP_TABLES;
import com.epay.ejb.bean.EPAY_PROMOTIONCODE;
import com.epay.ejb.bean.EPAY_SCT_CARD;
import com.epay.ejb.bean.EPAY_SERVICE_INFO;
import com.epay.ejb.bean.EPAY_DTONESERVICE_INFO;
import com.epay.ejb.bean.EPAY_SYS_FUNCTIONS;
import com.epay.ejb.bean.EPAY_SYS_ROLES;
import com.epay.ejb.bean.EPAY_SYS_ROLEFUNCS;
import com.epay.ejb.bean.EPAY_TRANSACTION;
import com.epay.ejb.bean.EPAY_VCARD;
import com.epay.ejb.bean.EPAY_VCARDTYPE;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
public interface EPayBusinessConreoller {

    public EPAY_SCT_CARD getSCTCardInfoById(int id) throws Exception;

    public EPAY_SCT_CARD getSCTCardInfoByIccid(String iccid) throws Exception;

    public boolean insertSctCard(EPAY_SCT_CARD bean) throws Exception;

    public List<EPAY_SCT_CARD> listSctCardInfoByAPN(String apn) throws Exception;

    public List<EPAY_SCT_CARD> listSctCardInfo() throws Exception;

    public boolean updateSctCard(EPAY_SCT_CARD bean) throws Exception;
    //dealermdb

    public EPAY_DEALERMDN getDealerMDNByMDN(String dealerId, String mdn) throws Exception;

    //dealcard
    public EPAY_DEALERCARD getDealerCardByCardId(String dealerId, String cardId) throws Exception;

    //promotioncode
    public EPAY_PROMOTIONCODE getPomtionCode(String promotioncode) throws Exception;

    //iptables
    public boolean chkIpValidation(String target_ip, int cpid);

    public EPAY_IP_TABLES getIP_TABLES_Cache_ByCPID(int cpid);

    public EPAY_IP_TABLES getIP_TABLES_ByCPID(int cpid);

    //vcardtype
    public List<EPAY_VCARDTYPE> listCardTypeInfo() throws Exception;

    public List<EPAY_SYS_FUNCTIONS> listAllFunc() throws Exception;

    public EPAY_SYS_FUNCTIONS getFunc(String id) throws Exception;

    public boolean updateVCardType(EPAY_VCARDTYPE bean) throws Exception;

    public boolean updateRole(EPAY_SYS_ROLES bean) throws Exception;

    public boolean insertRole(EPAY_SYS_ROLES bean) throws Exception;

    public boolean insertRoleFunc(EPAY_SYS_ROLEFUNCS bean) throws Exception;

    public boolean updateRoleFunc(EPAY_SYS_ROLEFUNCS bean) throws Exception;

    public List<EPAY_SYS_ROLEFUNCS> listAllRuleFunc() throws Exception;

    public EPAY_SYS_ROLEFUNCS queryRuleFuncById(int id) throws Exception;

    public EPAY_VCARDTYPE queryCardTypeByCardType(String cardtype) throws Exception;

    public boolean updateFunc(EPAY_SYS_FUNCTIONS bean) throws Exception;

    public boolean insertVCardType(EPAY_VCARDTYPE bean) throws Exception;

    //vcard
    public boolean insertVcard(EPAY_VCARD bean) throws Exception;

    public EPAY_VCARD getVCPass(String tokenid) throws Exception;

    public boolean updateVCard(EPAY_VCARD bean) throws Exception;

    public EPAY_VCARD queryVCard(int id) throws Exception;

    public List<EPAY_VCARD> queryCardByCardtype(String cardtype) throws Exception;

    public boolean findVCardByFilename(String filesname) throws Exception;

    public EPAY_VCARD queryVCardByToken(String token, String mdn, String serviceid) throws Exception;

    //bucket history
    public boolean insertbuckethistory(EPAY_BUCKETHISTORY bean) throws Exception;

    //adjust bucket
    public List getBucketListBySid(String sid);

    public EPAY_BUCKET getBucketListBySidAndBid(String sid, String bid);

    // 與功能帳號授權模組有關
    public EPAY_COMMON_USER getCommonUser_ByCode(String user_code);

    public List getParentFunctionName_byID(String user_id);

    public List getFunction_byID(String user_id);

    public boolean insert_Sys_Func(EPAY_SYS_FUNCTIONS func);

    //add account ui
    public List getAllRoles();

    public EPAY_SYS_ROLES getRoles_ByRolesCode(String roles_code);

    public boolean addAccount(EPAY_COMMON_USER user);

    public List getUserList(String user_code, String user_name, String user_mobile, String user_role);
    
//    public List getUserByUserCode(String user_code);

    public List getAllUserList();

    public boolean deleteUser_ByID(ArrayList<String> id_list);

    public boolean updateUser(EPAY_COMMON_USER user);

    public List getTxLogList(String startdatetime, String enddatetime);

    public List getTxLogListByMDNAndDate(String mdn, String startdatetime, String enddatetime);
    public List getTxIbonListByDate( String startdatetime, String enddatetime);
    public List getTxIbonListByDateByMdn( String startdatetime, String enddatetime,String mdn);
    public List getTxIbonListByDateByStatus( String startdatetime, String enddatetime,String status);
    public List getTxIbonListByDateByMdnByStatus( String startdatetime, String enddatetime,String mdn,String status);
    
    public List getTxCountryCodeListByDate( String startdatetime, String enddatetime);
    public List getTxCountryCodeListByDateBystatus( String startdatetime, String enddatetime, String status);
    public List getTxCountryCodeListByDateByMdn( String startdatetime, String enddatetime,String mdn);
     public List getTxCountryCodeListByDateByMdnByStatus( String startdatetime, String enddatetime,String mdn,String status);

    public List getTxLogListByMDNAndDateAndCPID(String mdn, String startdatetime, String enddatetime);

    public List getTxLogListByMDN(String mdn);

    public List getTxLogListByMDNAndCDate(String mdn, String cdate);

    public List getTxLogListByPOSCode(String poscode);

    public boolean addServiceInfo(EPAY_SERVICE_INFO serviceinfo);
    
    public boolean addDTOneServiceInfo(EPAY_DTONESERVICE_INFO serviceinfo);

    public boolean addBucket(EPAY_BUCKET bucket);

    public boolean updateServiceInfo(EPAY_SERVICE_INFO serviceinfo);
    public boolean updateDTOneServiceInfo(EPAY_DTONESERVICE_INFO serviceinfo);

    public EPAY_BUCKET getBucketByRID(int rid);

    public boolean updateBucketInfo(EPAY_BUCKET bucketinfo);

    public List ListAllBucketInfo();

//
//    public List getTxLogList(String startdatetime, String enddatetime);
    public boolean insertTransaction(EPAY_TRANSACTION bean) throws Exception;

    public boolean updateTransaction(EPAY_TRANSACTION bean) throws Exception;

    public EPAY_TRANSACTION getTransaction(String libm) throws Exception;

    public EPAY_TRANSACTION getTransactionByCPLibm(String libm) throws Exception;

    public EPAY_TRANSACTION getTransactionlibmandAuthdateNotNull(String libm) throws Exception;

    public void removeTransaction(String libm) throws Exception;

//    public List<EPAY_TRANSACTION> listTransactionByLibm(String libm) throws Exception;
//    public List<EPAY_TRANSACTION> listTransactionByItemcode(String itemcode) throws Exception;
    public List<EPAY_TRANSACTION> listTransactionByPayStatus(String payStatus) throws Exception;

    public boolean insertInvoice(EPAY_INVOICE bean) throws Exception;

    public boolean updateInvoice(EPAY_INVOICE bean) throws Exception;

    public EPAY_INVOICE getInvoice(Integer invoiceid) throws Exception;

    public void removeInvoice(Integer invoiceid) throws Exception;

//    public INVOICE listInvoiceByInvoicenoLibm(String inviceno, String libm) throws Exception;
    public EPAY_INVOICE listInvoiceByInvoicenoLibm(String libm) throws Exception;

    public List<EPAY_INVOICE> listInvoiceByInvoiceno(String inviceno) throws Exception;

    public boolean insertInvoice_Item(EPAY_INVOICE_ITEM bean) throws Exception;

    public boolean updateInvoice_Item(EPAY_INVOICE_ITEM bean) throws Exception;

    public EPAY_INVOICE_ITEM getInvoice_Item(Integer invoiceid) throws Exception;

    public void removeInvoice_Item(Integer invoiceid) throws Exception;

    public List<EPAY_INVOICE_ITEM> listInvoiceItemByInvoiceidNo(Integer invoiceid, String no) throws Exception;

    public EPAY_CP_INFO getCpInfoTxType(Integer cpid) throws Exception;

    public EPAY_CP_INFO getCpInfo(Integer cpid) throws Exception;

    public List<EPAY_CP_INFO> listAllCpInfo() throws Exception;

    public List<EPAY_SERVICE_INFO> listAllServiceInfo(Integer cpid, String promotioncode, int platformtype) throws Exception;
    
    public List<EPAY_SERVICE_INFO> listNokialServiceInfo(Integer cpid, String promotioncode, int platformtype) throws Exception;
    
    public List<EPAY_DTONESERVICE_INFO> listDTOneServiceInfo(Integer cpid, String promotioncode, int platformtype,String operatorname) throws Exception;

    public List<EPAY_SERVICE_INFO> listAllServiceInfoByPromotion(Integer cpid, String promotioncode, String contractstatus, String lifecyclestatus) throws Exception;

    public List<EPAY_SERVICE_INFO> listServiceInfo() throws Exception;
    public List<EPAY_DTONESERVICE_INFO> listDTOneServiceInfo() throws Exception;

    public List<EPAY_SERVICE_INFO> listServiceInfoBySid(Long sid) throws Exception;
    public List<EPAY_DTONESERVICE_INFO> listDTOneServiceInfoBySid(Long sid) throws Exception;
    public List<EPAY_DTONESERVICE_INFO> listDTOneServiceInfoBySidOnly(Long sid) throws Exception;

    public List<EPAY_SERVICE_INFO> listAdjustAccServiceInfo(Integer cpid, String atype, String xcode, int platformtype) throws Exception;
    public List<EPAY_SERVICE_INFO> listAdjustAccNokiaServiceInfo(Integer cpid, String atype, String xcode, int platformtype) throws Exception;

    public EPAY_SERVICE_INFO getServiceInfoById(Long serviceid, Integer cid) throws Exception;
    
    public EPAY_DTONESERVICE_INFO getDtoneServiceInfoById(Long serviceid, Integer cid) throws Exception;
    public EPAY_DTONESERVICE_INFO getDtoneServiceInfoByIdOnly(Long serviceid, Integer cid) throws Exception;
    
    public EPAY_SERVICE_INFO getServiceInfoById_NoFlag(Long serviceid, Integer cid) throws Exception;

    public EPAY_CALLER getCallerById(String callerId) throws Exception;

    public Integer getTotalOfMonthByMobilePhone(String InvoiceContactMobilePhone) throws Exception;

    public EPAY_SERVICE_INFO getServiceInfoByCpidAndServiceId(Long serviceid, Integer cid) throws Exception;

    public EPAY_SERVICE_INFO getServiceInfoByPricePlanCode(String priceplancode) throws Exception;
    
    public List getTxLogListByBatchFie(String batchfile);
    
    public List getDTOneMdnTransAmount(String mdn,String begindate,String enddate);

}
