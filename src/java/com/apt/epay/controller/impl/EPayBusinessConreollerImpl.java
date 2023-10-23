/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.apt.epay.controller.impl;

import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.injector.InjectorEJBDAO;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import com.apt.util.DateUtil;
import com.apt.util.toolsUtil;
import com.epay.ejb.EPAY_BUCKETDAO;
import com.epay.ejb.EPAY_BUCKETHISTORYDAO;
import com.epay.ejb.EPAY_CALLERDAO;
import com.epay.ejb.EPAY_COMMON_USERDAO;
import com.epay.ejb.EPAY_CP_INFODAO;
import com.epay.ejb.EPAY_DEALERCARDDAO;
import com.epay.ejb.EPAY_DEALERMDNDAO;
import com.epay.ejb.EPAY_INVOICEDAO;
import com.epay.ejb.EPAY_INVOICE_ITEMDAO;
import com.epay.ejb.EPAY_IP_TABLESDAO;
import com.epay.ejb.EPAY_PROMOTIONCODEDAO;
import com.epay.ejb.EPAY_SCTCARDDAO;
import com.epay.ejb.EPAY_SERVICE_INFODAO;
import com.epay.ejb.EPAY_DTONESERVICE_INFODAO;
import com.epay.ejb.EPAY_SYS_FUNCTIONSDAO;
import com.epay.ejb.EPAY_SYS_ROLEFUNCSDAO;
import com.epay.ejb.EPAY_SYS_ROLESDAO;
import com.epay.ejb.EPAY_TRANSACTIONDAO;
import com.epay.ejb.EPAY_VCARDDAO;
import com.epay.ejb.EPAY_VCARDTYPEDAO;
import com.epay.ejb.bean.*;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.naming.NamingException;

/**
 * @author Administrator
 */
public class EPayBusinessConreollerImpl implements EPayBusinessConreoller {

    private static Logger log = Logger.getLogger("EPAY");

    private InjectorEJBDAO epay_transactionDAO;
    private InjectorEJBDAO epay_invoiceDAO;
    private InjectorEJBDAO epay_invoice_itemDAO;
    private InjectorEJBDAO epay_cpInfoDAO;
    private InjectorEJBDAO epay_serviceInfoDAO;
    private InjectorEJBDAO epay_dtoneserviceInfoDAO;
    private InjectorEJBDAO epay_callerDAO;
    private InjectorEJBDAO epay_common_userDAO;
    private InjectorEJBDAO epay_sys_functionDAO;
    private InjectorEJBDAO epay_sys_rolefuncsDAO;
    private InjectorEJBDAO epay_sys_rolesDAO;
    private InjectorEJBDAO epay_bucketDAO;
    private InjectorEJBDAO epay_buckethistoryDAO;
    private InjectorEJBDAO epay_vcardDAO;
    private InjectorEJBDAO epay_vcardtypeDAO;
    private InjectorEJBDAO epay_iptablesDAO;
    private InjectorEJBDAO epay_promotioncodeDAO;
    private InjectorEJBDAO epay_dealercardDAO;
    private InjectorEJBDAO epay_dealermdnDAO;
    private InjectorEJBDAO epay_sctcardDAO;

    public InjectorEJBDAO getEpay_dtoneserviceInfoDAO() {
        return epay_dtoneserviceInfoDAO;
    }

    public void setEpay_dtoneserviceInfoDAO(InjectorEJBDAO epay_dtoneserviceInfoDAO) {
        this.epay_dtoneserviceInfoDAO = epay_dtoneserviceInfoDAO;
    }

    public InjectorEJBDAO getEpay_sctcardDAO() {
        return epay_sctcardDAO;
    }

    public void setEpay_sctcardDAO(InjectorEJBDAO epay_sctcardDAO) {
        this.epay_sctcardDAO = epay_sctcardDAO;
    }

    private static HashMap Ip_Tables_Cache = new HashMap();

    public InjectorEJBDAO getEpay_dealercardDAO() {
        return epay_dealercardDAO;
    }

    public void setEpay_dealercardDAO(InjectorEJBDAO epay_dealercardDAO) {
        this.epay_dealercardDAO = epay_dealercardDAO;
    }

    public InjectorEJBDAO getEpay_dealermdnDAO() {
        return epay_dealermdnDAO;
    }

    public void setEpay_dealermdnDAO(InjectorEJBDAO epay_dealermdnDAO) {
        this.epay_dealermdnDAO = epay_dealermdnDAO;
    }

    public InjectorEJBDAO getEpay_promotioncodeDAO() {
        return epay_promotioncodeDAO;
    }

    public void setEpay_promotioncodeDAO(InjectorEJBDAO epay_promotioncodeDAO) {
        this.epay_promotioncodeDAO = epay_promotioncodeDAO;
    }

    public static HashMap getIp_Tables_Cache() {
        return Ip_Tables_Cache;
    }

    public static void setIp_Tables_Cache(HashMap Ip_Tables_Cache) {
        EPayBusinessConreollerImpl.Ip_Tables_Cache = Ip_Tables_Cache;
    }

    public InjectorEJBDAO getEpay_iptablesDAO() {
        return epay_iptablesDAO;
    }

    public void setEpay_iptablesDAO(InjectorEJBDAO epay_iptablesDAO) {
        this.epay_iptablesDAO = epay_iptablesDAO;
    }

    public InjectorEJBDAO getEpay_vcardtypeDAO() {
        return epay_vcardtypeDAO;
    }

    public void setEpay_vcardtypeDAO(InjectorEJBDAO epay_vcardtypeDAO) {
        this.epay_vcardtypeDAO = epay_vcardtypeDAO;
    }

    public InjectorEJBDAO getEpay_vcardDAO() {
        return epay_vcardDAO;
    }

    public void setEpay_vcardDAO(InjectorEJBDAO epay_vcardDAO) {
        this.epay_vcardDAO = epay_vcardDAO;
    }

    public InjectorEJBDAO getEpay_buckethistoryDAO() {
        return epay_buckethistoryDAO;
    }

    public void setEpay_buckethistoryDAO(InjectorEJBDAO epay_buckethistoryDAO) {
        this.epay_buckethistoryDAO = epay_buckethistoryDAO;
    }

    @Override
    public boolean updateBucketInfo(EPAY_BUCKET bucketinfo) {
        boolean rtresult;// = false;
        EPAY_BUCKETDAO userDAO;
        try {
            userDAO = (EPAY_BUCKETDAO) epay_bucketDAO.getEJBDAO();
            rtresult = userDAO.updateBucket(bucketinfo);
        } catch (Exception ex) {
            rtresult = false;
        }
        return rtresult;
    }

    @Override
    public List ListAllBucketInfo() {
        EPAY_BUCKETDAO pg_bucketDAO = null;
        List rtList = null;
        try {
            pg_bucketDAO = (EPAY_BUCKETDAO) epay_bucketDAO.getEJBDAO();
            rtList = pg_bucketDAO.ListAllBucketInfo();

        } catch (Exception ex) {
        }
        return rtList;
    }

    @Override
    public List getBucketListBySid(String sid) {
        EPAY_BUCKETDAO pg_bucketDAO = null;
        List rtList = null;
        try {
            pg_bucketDAO = (EPAY_BUCKETDAO) epay_bucketDAO.getEJBDAO();
            rtList = pg_bucketDAO.getAllBucketInfo(sid);

        } catch (Exception ex) {
        }
        return rtList;
    }

    @Override
    public EPAY_BUCKET getBucketListBySidAndBid(String sid, String bid) {
        EPAY_BUCKETDAO pg_bucketDAO = null;
        EPAY_BUCKET rtList = null;
        try {
            pg_bucketDAO = (EPAY_BUCKETDAO) epay_bucketDAO.getEJBDAO();
            rtList = pg_bucketDAO.getAllBucketInfoByBid(sid, bid);

        } catch (Exception ex) {
        }
        return rtList;
    }

    @Override
    public EPAY_BUCKET getBucketByRID(int rid) {

        EPAY_BUCKETDAO bucketDAO;// = null;
        EPAY_BUCKET rtresult = null;
        try {
            bucketDAO = (EPAY_BUCKETDAO) epay_bucketDAO.getEJBDAO();
            rtresult = bucketDAO.getBucketByRID(rid);

        } catch (Exception ex) {
//            ex.printStackTrace();
        }
        return rtresult;
    }

    @Override
    public List getTxLogListByPOSCode(String poscode) {
        EPAY_TRANSACTIONDAO pg_transactionDAO;// = null;
        List rtList = null;
        try {
            pg_transactionDAO = (EPAY_TRANSACTIONDAO) epay_transactionDAO.getEJBDAO();
            rtList = pg_transactionDAO.getTx_byPOSCode(poscode);

        } catch (Exception ex) {
        }
        return rtList;
    }

    @Override
    public List getTxLogListByMDN(String mdn) {
        EPAY_TRANSACTIONDAO pg_transactionDAO = null;
        List rtList = null;
        try {
            pg_transactionDAO = (EPAY_TRANSACTIONDAO) epay_transactionDAO.getEJBDAO();
            rtList = pg_transactionDAO.getTx_byMDN(mdn);

        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        return rtList;
    }

    @Override
    public List getTxLogListByMDNAndCDate(String mdn, String cdate) {
        EPAY_TRANSACTIONDAO pg_transactionDAO = null;
        List rtList = null;
        try {
            pg_transactionDAO = (EPAY_TRANSACTIONDAO) epay_transactionDAO.getEJBDAO();
            rtList = pg_transactionDAO.getTx_byMDNAndCDate(mdn, cdate);

        } catch (Exception ex) {
            // ex.printStackTrace();
        }
        return rtList;
    }

    @Override
    public List getTxLogList(String startdatetime, String enddatetime) {
        EPAY_TRANSACTIONDAO pg_transactionDAO = null;
        List rtList = null;
        try {
            pg_transactionDAO = (EPAY_TRANSACTIONDAO) epay_transactionDAO.getEJBDAO();
            rtList = pg_transactionDAO.getTx_byStartDateTimeAndEndtDateTime(startdatetime, enddatetime);

        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        return rtList;
    }

    @Override
    public List getTxLogListByMDNAndDateAndCPID(String mdn, String startdatetime, String enddatetime) {
        EPAY_TRANSACTIONDAO pg_transactionDAO = null;
        List rtList = null;
        try {
            pg_transactionDAO = (EPAY_TRANSACTIONDAO) epay_transactionDAO.getEJBDAO();
            rtList = pg_transactionDAO.getTx_byMDNStartDateTimeAndEndtDateTimeaAndCPID(mdn, startdatetime, enddatetime);

        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        return rtList;
    }

    @Override
    public List getTxLogListByMDNAndDate(String mdn, String startdatetime, String enddatetime) {
        EPAY_TRANSACTIONDAO pg_transactionDAO = null;
        List rtList = null;
        try {
            pg_transactionDAO = (EPAY_TRANSACTIONDAO) epay_transactionDAO.getEJBDAO();
            rtList = pg_transactionDAO.getTx_byMDNStartDateTimeAndEndtDateTime(mdn, startdatetime, enddatetime);

        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        return rtList;
    }

    @Override
    public boolean updateDTOneServiceInfo(EPAY_DTONESERVICE_INFO serviceinfo) {
        boolean rtresult;// = false;
        EPAY_DTONESERVICE_INFODAO userDAO;// = null;
        try {
            userDAO = (EPAY_DTONESERVICE_INFODAO) epay_dtoneserviceInfoDAO.getEJBDAO();
            rtresult = userDAO.updateDTOneServiceInfo(serviceinfo);
        } catch (Exception ex) {
//            ex.printStackTrace();
            rtresult = false;
        }
        return rtresult;
    }

    @Override
    public boolean updateServiceInfo(EPAY_SERVICE_INFO serviceinfo) {
        boolean rtresult;// = false;
        EPAY_SERVICE_INFODAO userDAO;// = null;
        try {
            userDAO = (EPAY_SERVICE_INFODAO) epay_serviceInfoDAO.getEJBDAO();
            rtresult = userDAO.updateServiceInfo(serviceinfo);
        } catch (Exception ex) {
//            ex.printStackTrace();
            rtresult = false;
        }
        return rtresult;
    }

    @Override
    public boolean updateUser(EPAY_COMMON_USER user) {
        boolean rtresult = false;
        EPAY_COMMON_USERDAO userDAO = null;
        try {
            userDAO = (EPAY_COMMON_USERDAO) epay_common_userDAO.getEJBDAO();
            rtresult = userDAO.updateUser(user);
        } catch (Exception ex) {
//            ex.printStackTrace();
            rtresult = false;
        }
        return rtresult;
    }

    @Override
    public boolean deleteUser_ByID(ArrayList<String> id_list) {
        boolean rtresult = false;
        EPAY_COMMON_USERDAO userDAO = null;
        try {
            userDAO = (EPAY_COMMON_USERDAO) epay_common_userDAO.getEJBDAO();
            for (String id : id_list) {
                rtresult = userDAO.deleteUser_ByUserId(id);
            }
        } catch (Exception ex) {
            rtresult = false;
        }
        return rtresult;
    }

    @Override
    public List getAllUserList() {
        EPAY_COMMON_USERDAO userDAO = null;
        EPAY_SYS_ROLESDAO rolesDAO = null;
        List rtList = null;
        try {
            userDAO = (EPAY_COMMON_USERDAO) epay_common_userDAO.getEJBDAO();
            rolesDAO = (EPAY_SYS_ROLESDAO) epay_sys_rolesDAO.getEJBDAO();
            rtList = userDAO.getAllUser();

        } catch (Exception ex) {
        }
        return rtList;
    }

    @Override
    public List getUserList(String user_code, String user_name, String user_mobile, String user_role) {
        EPAY_COMMON_USERDAO userDAO = null;
        EPAY_SYS_ROLESDAO rolesDAO = null;

        List rtList = null;
        try {
            userDAO = (EPAY_COMMON_USERDAO) epay_common_userDAO.getEJBDAO();
            rolesDAO = (EPAY_SYS_ROLESDAO) epay_sys_rolesDAO.getEJBDAO();

            if (user_code.equals("") && user_name.equals("") && user_mobile.equals("") && user_role.equals("")) {
                rtList = userDAO.getAllUser();
            } else if (!user_code.equals("") && user_name.equals("") && user_mobile.equals("") && user_role.equals("")) {
                rtList = userDAO.getUser_ByUserCode(user_code);
            } else if (user_code.equals("") && !user_name.equals("") && user_mobile.equals("") && user_role.equals("")) {
                rtList = userDAO.getUser_ByUserName(user_name);
            } else if (user_code.equals("") && user_name.equals("") && !user_mobile.equals("") && user_role.equals("")) {
                rtList = userDAO.getUser_ByUserMobile(user_mobile);
            } else if (user_code.equals("") && user_name.equals("") && user_mobile.equals("") && !user_role.equals("")) {
                rtList = userDAO.getUser_ByUserRole(user_role);
            } else if (!user_code.equals("") && !user_name.equals("") && user_mobile.equals("") && user_role.equals("")) {
                rtList = userDAO.getUser_ByUserCode(user_code);
            } else if (!user_code.equals("") && user_name.equals("") && !user_mobile.equals("") && user_role.equals("")) {
                rtList = userDAO.getUser_ByUserCode(user_code);
            } else if (!user_code.equals("") && user_name.equals("") && user_mobile.equals("") && !user_role.equals("")) {
                rtList = userDAO.getUser_ByUserCode(user_code);
            } else if (!user_code.equals("") && !user_name.equals("") && !user_mobile.equals("") && user_role.equals("")) {
                rtList = userDAO.getUser_ByUserCode(user_code);
            } else if (!user_code.equals("") && !user_name.equals("") && user_mobile.equals("") && !user_role.equals("")) {
                rtList = userDAO.getUser_ByUserCode(user_code);
            } else if (user_code.equals("") && !user_name.equals("") && !user_mobile.equals("") && user_role.equals("")) {
                rtList = userDAO.getUser_ByUserNameAndUserMobile(user_name, user_mobile);
            } else if (user_code.equals("") && !user_name.equals("") && user_mobile.equals("") && !user_role.equals("")) {
                rtList = userDAO.getUser_ByUserNameAndUserRole(user_name, user_role);
            } else if (user_code.equals("") && user_name.equals("") && !user_mobile.equals("") && !user_role.equals("")) {
                rtList = userDAO.getUser_ByUserMobileAndUserRole(user_mobile, user_role);
            } else if (!user_code.equals("") && user_name.equals("") && !user_mobile.equals("") && !user_role.equals("")) {
                rtList = userDAO.getUser_ByUserCode(user_code);
            } else if (user_code.equals("") && !user_name.equals("") && !user_mobile.equals("") && !user_role.equals("")) {
                rtList = userDAO.getUser_ByUserNameAndUserMobileAndUserRole(user_name, user_mobile, user_role);
            } else if (!user_code.equals("") && !user_name.equals("") && !user_mobile.equals("") && !user_role.equals("")) {
                rtList = userDAO.getUser_ByUserCode(user_code);
            }

        } catch (Exception ex) {
        }
        return rtList;
    }

    @Override
    public boolean addAccount(EPAY_COMMON_USER user) {
        EPAY_COMMON_USERDAO userDAO = null;
        boolean rtresult = false;
        try {
            userDAO = (EPAY_COMMON_USERDAO) epay_common_userDAO.getEJBDAO();
            userDAO.insertAccount(user);
            rtresult = true;

        } catch (Exception ex) {
            rtresult = false;
//            ex.printStackTrace();
        }
        return rtresult;
    }

    @Override
    public boolean addDTOneServiceInfo(EPAY_DTONESERVICE_INFO serviceinfo) {
        EPAY_DTONESERVICE_INFODAO dtoneserviceinfoDAO = null;
        boolean rtresult = false;
        try {
            dtoneserviceinfoDAO = (EPAY_DTONESERVICE_INFODAO) epay_dtoneserviceInfoDAO.getEJBDAO();
            dtoneserviceinfoDAO.insertDTOneServiceinfo(serviceinfo);
            rtresult = true;

        } catch (Exception ex) {
            rtresult = false;
//            ex.printStackTrace();
        }
        return rtresult;
    }

    @Override
    public boolean addServiceInfo(EPAY_SERVICE_INFO serviceinfo) {
        EPAY_SERVICE_INFODAO serviceinfoDAO = null;
        boolean rtresult = false;
        try {
            serviceinfoDAO = (EPAY_SERVICE_INFODAO) epay_serviceInfoDAO.getEJBDAO();
            serviceinfoDAO.insertServiceinfo(serviceinfo);
            rtresult = true;

        } catch (Exception ex) {
            rtresult = false;
//            ex.printStackTrace();
        }
        return rtresult;
    }

    @Override
    public boolean addBucket(EPAY_BUCKET bucket) {
        EPAY_BUCKETDAO bucketDAO = null;
        boolean rtresult = false;
        try {
            bucketDAO = (EPAY_BUCKETDAO) epay_bucketDAO.getEJBDAO();
            bucketDAO.insertBucket(bucket);
            rtresult = true;

        } catch (Exception ex) {
            rtresult = false;
//            ex.printStackTrace();
        }
        return rtresult;
    }

    @Override
    public EPAY_SYS_ROLES getRoles_ByRolesCode(String roles_code) {
        EPAY_SYS_ROLESDAO rolesDAO = null;
        EPAY_SYS_ROLES rtresult = null;
        try {
            rolesDAO = (EPAY_SYS_ROLESDAO) epay_sys_rolesDAO.getEJBDAO();
            rtresult = rolesDAO.getRoles_BYRoleID(roles_code);

        } catch (Exception ex) {
        }
        return rtresult;
    }

    @Override
    public List getAllRoles() {
        EPAY_SYS_ROLESDAO rolesDAO = null;
        List rtList = null;
        try {
            rolesDAO = (EPAY_SYS_ROLESDAO) epay_sys_rolesDAO.getEJBDAO();
            rtList = rolesDAO.getAllRoles();
        } catch (Exception ex) {
        }

        return rtList;
    }

    @Override
    public List getFunction_byID(String user_id) {
        EPAY_COMMON_USERDAO userDAO = null;
        EPAY_SYS_FUNCTIONSDAO functionDAO = null;
        EPAY_SYS_ROLEFUNCSDAO rolefuncionDAO = null;
        EPAY_COMMON_USER user = new EPAY_COMMON_USER();
        EPAY_SYS_ROLESDAO rolesDAO = null;
        EPAY_SYS_ROLES roles = new EPAY_SYS_ROLES();

        List S_list = null;
        try {
            userDAO = (EPAY_COMMON_USERDAO) epay_common_userDAO.getEJBDAO();
            functionDAO = (EPAY_SYS_FUNCTIONSDAO) epay_sys_functionDAO.getEJBDAO();
            rolefuncionDAO = (EPAY_SYS_ROLEFUNCSDAO) epay_sys_rolefuncsDAO.getEJBDAO();
            rolesDAO = (EPAY_SYS_ROLESDAO) epay_sys_rolesDAO.getEJBDAO();

            user = userDAO.getAccount_byUserCode(user_id);
            roles = rolesDAO.findFuncID_BYRoleID(user.getPg_sys_roles().getR_id());
            S_list = functionDAO.ListFunction_ByRole(roles.getR_id());
        } catch (Exception ex) {
        }
        return S_list;
    }

    @Override
    public List getParentFunctionName_byID(String user_id) {
        EPAY_COMMON_USERDAO userDAO = null;
        EPAY_SYS_FUNCTIONSDAO functionDAO = null;
        EPAY_SYS_ROLEFUNCSDAO rolefuncionDAO = null;
        EPAY_SYS_ROLESDAO rolesDAO = null;
        EPAY_COMMON_USER user = new EPAY_COMMON_USER();
        EPAY_SYS_ROLES roles = new EPAY_SYS_ROLES();
        List R_list = null;
        try {
            userDAO = (EPAY_COMMON_USERDAO) epay_common_userDAO.getEJBDAO();
            functionDAO = (EPAY_SYS_FUNCTIONSDAO) epay_sys_functionDAO.getEJBDAO();
            rolefuncionDAO = (EPAY_SYS_ROLEFUNCSDAO) epay_sys_rolefuncsDAO.getEJBDAO();
            rolesDAO = (EPAY_SYS_ROLESDAO) epay_sys_rolesDAO.getEJBDAO();

            user = userDAO.getAccount_byUserCode(user_id);
            roles = rolesDAO.findFuncID_BYRoleID(user.getPg_sys_roles().getR_id());

            R_list = functionDAO.ListParentName_ByRole(roles.getR_id());

        } catch (Exception ex) {
        }

        return R_list;
    }

    /*
     OnlineDeposit
     */
    @Override
    public EPAY_COMMON_USER getCommonUser_ByCode(String user_code) {
        EPAY_COMMON_USER rtresult = null;
        EPAY_COMMON_USERDAO userDAO = null;
        try {
            userDAO = (EPAY_COMMON_USERDAO) epay_common_userDAO.getEJBDAO();
            rtresult = userDAO.getAccount_byUserCode(user_code);
        } catch (Exception ex) {
        }
        return rtresult;
    }

    @Override
    public boolean insertTransaction(EPAY_TRANSACTION bean) throws Exception {
//        log.debug("entring insertTransaction(): " + bean.getLibm());
        EPAY_TRANSACTIONDAO transactiondaobean = null;
        boolean rtresult = false;
        try {
            transactiondaobean = (EPAY_TRANSACTIONDAO) epay_transactionDAO.getEJBDAO();
            rtresult = transactiondaobean.insertTransaction(bean);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return rtresult;
    }

    @Override
    public boolean updateTransaction(EPAY_TRANSACTION bean) throws Exception {
        log.info(bean.getInvoiceContactMobilePhone() + " entring updateTransaction(): " + bean.getLibm());
        boolean rtresult = false;
        EPAY_TRANSACTIONDAO transactiondaobean = null;
        try {
            transactiondaobean = (EPAY_TRANSACTIONDAO) epay_transactionDAO.getEJBDAO();
            rtresult = transactiondaobean.updateTransaction(bean);
        } catch (Exception ex) {
            new ShareBean().printStackTrace(ex);
            rtresult = false;
        }
        return rtresult;
    }

    @Override
    public EPAY_TRANSACTION getTransaction(String libm) throws Exception {
//        log.debug("entring getTransaction(): " + libm);
        EPAY_TRANSACTION result = null;
        try {
            EPAY_TRANSACTIONDAO dao = (EPAY_TRANSACTIONDAO) this.getTransactionDAO().getEJBDAO();
            result = dao.getTransaction(libm);
        } catch (Exception e) {
            log.error("getTransaction() fail!", e);
        }
        return result;
    }

    @Override
    public EPAY_TRANSACTION getTransactionByCPLibm(String libm) throws Exception {
//        log.debug("entring getTransaction(): " + libm);
        EPAY_TRANSACTION result = null;
        try {
            EPAY_TRANSACTIONDAO dao = (EPAY_TRANSACTIONDAO) this.getTransactionDAO().getEJBDAO();
            result = dao.getTransactionByCPLibm(libm);
        } catch (Exception e) {
            log.error("getTransaction() fail!", e);
        }
        return result;
    }

    @Override
    public EPAY_TRANSACTION getTransactionlibmandAuthdateNotNull(String libm) throws Exception {
        log.info("entring getTransactionlibmandNullAuthdate(): " + libm);
        EPAY_TRANSACTION result = null;
        try {
            EPAY_TRANSACTIONDAO dao = (EPAY_TRANSACTIONDAO) this.getTransactionDAO().getEJBDAO();
            result = dao.getTransactionLibmAndAuthDateNotNull(libm);
        } catch (Exception e) {
            log.error("getTransaction() fail!", e);
        }
        return result;
    }

    @Override
    public void removeTransaction(String libm) throws Exception {
        try {
            EPAY_TRANSACTIONDAO dao = (EPAY_TRANSACTIONDAO) this.getTransactionDAO().getEJBDAO();
            dao.removeTransaction(libm);
        } catch (Exception e) {
            log.error("removeActivity() fail!", e);
        }
    }

//    @Override
//    public List<EPAY_TRANSACTION> listTransactionByLibm(String libm) throws Exception {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public List<EPAY_TRANSACTION> listTransactionByItemcode(String itemcode) throws Exception {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    @Override
    public List<EPAY_TRANSACTION> listTransactionByPayStatus(String payStatus) throws Exception {
        EPAY_TRANSACTIONDAO txdaobean = null;
        List<EPAY_TRANSACTION> rtresult = null;
        try {
            rtresult = new ArrayList();
            txdaobean = (EPAY_TRANSACTIONDAO) epay_transactionDAO.getEJBDAO();
            List<EPAY_TRANSACTION> tmpListTx = txdaobean.listTransactionByPayStatus(payStatus);
            for (EPAY_TRANSACTION tmpTx : tmpListTx) {
                rtresult.add(tmpTx);
            }
        } catch (Exception ex) {
        }
        return rtresult;

    }

    @Override
    public boolean insertInvoice(EPAY_INVOICE bean) throws Exception {
//        log.info("insert invoice " + bean.getInvoice_no());
        EPAY_INVOICEDAO invoicedaobean = null;
        boolean rtresult = false;
        try {
            invoicedaobean = (EPAY_INVOICEDAO) epay_invoiceDAO.getEJBDAO();
            rtresult = invoicedaobean.insertInvoice(bean);
        } catch (Exception ex) {
        }
        return rtresult;
    }

    @Override
    public boolean updateInvoice(EPAY_INVOICE bean) throws Exception {
        boolean rtresult = false;
        EPAY_INVOICEDAO invoicedaobean = null;
        try {
            invoicedaobean = (EPAY_INVOICEDAO) epay_invoiceDAO.getEJBDAO();
            rtresult = invoicedaobean.updateInvoice(bean);
        } catch (Exception ex) {
            new ShareBean().printStackTrace(ex);
            rtresult = false;
        }
        return rtresult;
    }

    @Override
    public EPAY_INVOICE getInvoice(Integer invoiceid) throws Exception {
        log.info("entring getInvoice(): " + invoiceid);
        EPAY_INVOICE result = null;
        try {
            EPAY_INVOICEDAO dao = (EPAY_INVOICEDAO) this.getInvoiceDAO().getEJBDAO();
            result = dao.getInvoice(invoiceid);
        } catch (Exception e) {
            log.error("getInvoice() fail!", e);
        }
        return result;
    }

    @Override
    public void removeInvoice(Integer invoiceid) throws Exception {
        try {
            EPAY_INVOICEDAO dao = (EPAY_INVOICEDAO) this.getInvoiceDAO().getEJBDAO();
            dao.removeInvoice(invoiceid);
        } catch (Exception e) {
            log.error("removeActivity() fail!", e);
        }
    }

    @Override
    public EPAY_INVOICE listInvoiceByInvoicenoLibm(String libm) throws Exception {
        EPAY_INVOICEDAO invoiceDAObean = null;
        EPAY_INVOICE rtresult = null;
        try {
            invoiceDAObean = (EPAY_INVOICEDAO) epay_invoiceDAO.getEJBDAO();
            rtresult = invoiceDAObean.getlistInvoiceByInvoicenoLibm(libm);

        } catch (Exception ex) {
//            ex.printStackTrace();
            throw ex;
            //rtresult = null;
        }
        return rtresult;
    }

    @Override
    public List<EPAY_INVOICE> listInvoiceByInvoiceno(String inviceno) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean insertbuckethistory(EPAY_BUCKETHISTORY bean) throws Exception {
        EPAY_BUCKETHISTORYDAO bucket_hisbean = null;
        boolean rtresult = false;
        try {
            bucket_hisbean = (EPAY_BUCKETHISTORYDAO) epay_buckethistoryDAO.getEJBDAO();
            rtresult = bucket_hisbean.insertbuckethistory(bean);
        } catch (Exception ex) {
        }
        return rtresult;
    }

    @Override
    public boolean insertInvoice_Item(EPAY_INVOICE_ITEM bean) throws Exception {
        EPAY_INVOICE_ITEMDAO invoice_itemdaobean = null;
        boolean rtresult = false;
        try {
            invoice_itemdaobean = (EPAY_INVOICE_ITEMDAO) epay_invoice_itemDAO.getEJBDAO();
            rtresult = invoice_itemdaobean.insertInvoice_Item(bean);
        } catch (Exception ex) {
        }
        return rtresult;
    }

    @Override
    public boolean updateInvoice_Item(EPAY_INVOICE_ITEM bean) throws Exception {
        boolean rtresult = false;
        EPAY_INVOICE_ITEMDAO invoice_itemdaobean = null;
        try {
            invoice_itemdaobean = (EPAY_INVOICE_ITEMDAO) epay_invoice_itemDAO.getEJBDAO();
            rtresult = invoice_itemdaobean.updateInvoice_Item(bean);
        } catch (Exception ex) {
            new ShareBean().printStackTrace(ex);
            rtresult = false;
        }
        return rtresult;
    }

    @Override
    public EPAY_INVOICE_ITEM getInvoice_Item(Integer invoiceid) throws Exception {
        log.info("entring getInvoice_Item(): " + invoiceid);
        EPAY_INVOICE_ITEM result = null;
        try {
            EPAY_INVOICE_ITEMDAO dao = (EPAY_INVOICE_ITEMDAO) this.getInvoice_itemDAO().getEJBDAO();
            result = dao.getInvoice_Item(invoiceid);
        } catch (Exception e) {
            log.error("getInvoice_Item() fail!", e);
        }
        return result;
    }

    @Override
    public void removeInvoice_Item(Integer invoiceid) throws Exception {
        try {
            EPAY_INVOICE_ITEMDAO dao = (EPAY_INVOICE_ITEMDAO) this.getInvoice_itemDAO().getEJBDAO();
            dao.removeInvoice_Item(invoiceid);
        } catch (Exception e) {
            log.error("removeActivity() fail!", e);
        }
    }

    @Override
    public List<EPAY_INVOICE_ITEM> listInvoiceItemByInvoiceidNo(Integer invoiceid, String no) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EPAY_CP_INFO getCpInfoTxType(Integer cpid) throws Exception {
        log.info("entring getCpInfoTxType(): " + cpid);
        EPAY_CP_INFO result = null;
        try {
            EPAY_CP_INFODAO dao = (EPAY_CP_INFODAO) this.getCpInfoDAO().getEJBDAO();
            result = dao.getCpInfoTxType(cpid);
        } catch (Exception e) {
            log.error("getCpInfoTxType() fail!", e);
        }
        return result;
    }

    @Override
    public List<EPAY_SERVICE_INFO> listAllServiceInfo(Integer cpid, String promotioncode, int platformtype) throws Exception {
        log.info("entring listAllServiceInfo()(): ");
        List<EPAY_SERVICE_INFO> result = null;
        try {
            EPAY_SERVICE_INFODAO dao = (EPAY_SERVICE_INFODAO) this.getServiceInfoDAO().getEJBDAO();
            result = (List<EPAY_SERVICE_INFO>) dao.getAllServiceInfo(cpid, promotioncode, platformtype);
        } catch (Exception e) {
            log.error("listAllCpInfo() fail!", e);
        }
        return result;
    }

    @Override
    public List<EPAY_DTONESERVICE_INFO> listDTOneServiceInfo(Integer cpid, String promotioncode, int platformtype, String operatorname) throws Exception {
        log.info("entring listDTOneServiceInfo()(): ");
        List<EPAY_DTONESERVICE_INFO> result = null;
        try {
            EPAY_DTONESERVICE_INFODAO dao = (EPAY_DTONESERVICE_INFODAO) this.epay_dtoneserviceInfoDAO.getEJBDAO();
            result = (List<EPAY_DTONESERVICE_INFO>) dao.getDTOneServiceInfo(cpid, promotioncode, platformtype, operatorname);
        } catch (Exception e) {
            log.error("listDTOneServiceInfo() fail!", e);
        }
        return result;
    }

    @Override
    public List<EPAY_SERVICE_INFO> listServiceInfo() throws Exception {
        log.info("entring listAllServiceInfo()(): ");
        List<EPAY_SERVICE_INFO> result = null;
        try {
            EPAY_SERVICE_INFODAO dao = (EPAY_SERVICE_INFODAO) this.getServiceInfoDAO().getEJBDAO();
            result = (List<EPAY_SERVICE_INFO>) dao.getServiceInfo();
        } catch (Exception e) {
            log.error("listAllCpInfo() fail!", e);
        }
        return result;
    }

    @Override
    public List<EPAY_DTONESERVICE_INFO> listDTOneServiceInfo() throws Exception {
        log.info("entring listAllDTOneServiceInfo()(): ");
        List<EPAY_DTONESERVICE_INFO> result = null;
        try {
            EPAY_DTONESERVICE_INFODAO dao = (EPAY_DTONESERVICE_INFODAO) this.epay_dtoneserviceInfoDAO.getEJBDAO();
            result = (List<EPAY_DTONESERVICE_INFO>) dao.getDTOneServiceInfo();
        } catch (Exception e) {
            log.error("listAllDTOneServiceInfo() fail!", e);
        }
        return result;
    }

    @Override
    public List<EPAY_SERVICE_INFO> listServiceInfoBySid(Long sid) throws Exception {
        log.info("entring listAllServiceInfo()(): ");
        List<EPAY_SERVICE_INFO> result = null;
        try {
            EPAY_SERVICE_INFODAO dao = (EPAY_SERVICE_INFODAO) this.getServiceInfoDAO().getEJBDAO();
            result = (List<EPAY_SERVICE_INFO>) dao.getServiceInfoBySid(sid);
        } catch (Exception e) {
            log.error("listAllCpInfo() fail!", e);
        }
        return result;
    }

    @Override
    public List<EPAY_DTONESERVICE_INFO> listDTOneServiceInfoBySid(Long sid) throws Exception {
        log.info("entring listDTOneServiceInfoBySid()(): ");
        List<EPAY_DTONESERVICE_INFO> result = null;
        try {
            EPAY_DTONESERVICE_INFODAO dao = (EPAY_DTONESERVICE_INFODAO) this.epay_dtoneserviceInfoDAO.getEJBDAO();
            result = (List<EPAY_DTONESERVICE_INFO>) dao.getDTOneServiceInfoBySid(sid);
        } catch (Exception e) {
            log.error("listDTOneServiceInfoBySid() fail!", e);
        }
        return result;
    }

    @Override
    public List<EPAY_DTONESERVICE_INFO> listDTOneServiceInfoBySidOnly(Long sid) throws Exception {
        log.info("entring listDTOneServiceInfoBySid()(): ");
        List<EPAY_DTONESERVICE_INFO> result = null;
        try {
            EPAY_DTONESERVICE_INFODAO dao = (EPAY_DTONESERVICE_INFODAO) this.epay_dtoneserviceInfoDAO.getEJBDAO();
            result = (List<EPAY_DTONESERVICE_INFO>) dao.getDTOneServiceInfoBySidOnly(sid);
        } catch (Exception e) {
            log.error("listDTOneServiceInfoBySidOnly() fail!", e);
        }
        return result;
    }

    @Override
    public List<EPAY_SERVICE_INFO> listAdjustAccServiceInfo(Integer cpid, String atype, String xcode, int platformtype) throws Exception {
        log.info("entring listAllServiceInfo()(): ");
        List<EPAY_SERVICE_INFO> result = null;
        try {
            EPAY_SERVICE_INFODAO dao = (EPAY_SERVICE_INFODAO) this.getServiceInfoDAO().getEJBDAO();
            result = (List<EPAY_SERVICE_INFO>) dao.getAdjustAccServiceInfo(cpid, atype, xcode, platformtype);
        } catch (Exception e) {
            log.error("listAdjustAccServiceInfo() fail!", e);
        }
        return result;
    }
    
    @Override
    public List<EPAY_SERVICE_INFO> listAdjustAccNokiaServiceInfo(Integer cpid, String atype, String xcode, int platformtype) throws Exception {
        log.info("entring listAllServiceInfo()(): ");
        List<EPAY_SERVICE_INFO> result = null;
        try {
            EPAY_SERVICE_INFODAO dao = (EPAY_SERVICE_INFODAO) this.getServiceInfoDAO().getEJBDAO();
            result = (List<EPAY_SERVICE_INFO>) dao.getAdjustAccNokiaServiceInfo(cpid, atype, xcode, platformtype);
        } catch (Exception e) {
            log.error("listAdjustAccServiceInfo() fail!", e);
        }
        return result;
    }    

    @Override
    public List<EPAY_CP_INFO> listAllCpInfo() throws Exception {
        log.info("entring listAllCpInfo()(): ");
        List<EPAY_CP_INFO> result = null;
        try {
            EPAY_CP_INFODAO dao = (EPAY_CP_INFODAO) this.getCpInfoDAO().getEJBDAO();
            result = dao.getAllCpInfo();
        } catch (Exception e) {
            log.error("listAllCpInfo() fail!", e);
        }
        return result;
    }

    @Override
    public EPAY_SERVICE_INFO getServiceInfoById(Long serviceid, Integer cid) throws Exception {
        log.info("entring getServiceInfoById(): " + serviceid);
        EPAY_SERVICE_INFO result = null;
        try {
            EPAY_SERVICE_INFODAO dao = (EPAY_SERVICE_INFODAO) this.getServiceInfoDAO().getEJBDAO();
            result = dao.getServiceInfoById(serviceid, cid);
        } catch (Exception e) {
            log.error("getServiceInfoById() fail!", e);
        }
        return result;
    }

    @Override
    public EPAY_DTONESERVICE_INFO getDtoneServiceInfoById(Long serviceid, Integer cid) throws Exception {
        log.info("entring getDtoneServiceInfoById(): " + serviceid);
        EPAY_DTONESERVICE_INFO result = null;
        try {
            EPAY_DTONESERVICE_INFODAO dao = (EPAY_DTONESERVICE_INFODAO) this.epay_dtoneserviceInfoDAO.getEJBDAO();
            result = dao.getDtonServiceInfoById(serviceid, cid);
        } catch (Exception e) {
            log.error("getDtoneServiceInfoById() fail!", e);
        }
        return result;
    }

    @Override
    public EPAY_DTONESERVICE_INFO getDtoneServiceInfoByIdOnly(Long serviceid, Integer cid) throws Exception {
        log.info("entring getDtoneServiceInfoById(): " + serviceid);
        EPAY_DTONESERVICE_INFO result = null;
        try {
            EPAY_DTONESERVICE_INFODAO dao = (EPAY_DTONESERVICE_INFODAO) this.epay_dtoneserviceInfoDAO.getEJBDAO();
            result = dao.getDtonServiceInfoByIdOnly(serviceid, cid);
        } catch (Exception e) {
            log.error("getDtoneServiceInfoById() fail!", e);
        }
        return result;
    }

    @Override
    public EPAY_SERVICE_INFO getServiceInfoById_NoFlag(Long serviceid, Integer cid) throws Exception {
        log.info("entring getServiceInfoById(): " + serviceid);
        EPAY_SERVICE_INFO result = null;
        try {
            EPAY_SERVICE_INFODAO dao = (EPAY_SERVICE_INFODAO) this.getServiceInfoDAO().getEJBDAO();
            result = dao.getServiceInfoById_Noflag(serviceid, cid);
        } catch (Exception e) {
            log.error("getServiceInfoById() fail!", e);
        }
        return result;
    }

    @Override
    public EPAY_CALLER getCallerById(String callerId) throws Exception {
        log.info("entring getCallerById(): " + callerId);
        EPAY_CALLER result = null;
        try {
            EPAY_CALLERDAO dao = (EPAY_CALLERDAO) this.getCallerDAO().getEJBDAO();
            result = dao.getCaller(callerId);
        } catch (Exception e) {
            log.error("getCallerById() fail!", e);
        }
        return result;
    }

    @Override
    public Integer getTotalOfMonthByMobilePhone(String InvoiceContactMobilePhone) throws Exception {
        EPAY_TRANSACTIONDAO txdaobean = null;
        Integer rtresult = null;
        try {
            txdaobean = (EPAY_TRANSACTIONDAO) epay_transactionDAO.getEJBDAO();
            int tmpListTx = txdaobean.sumTransactionByMoilePhone(InvoiceContactMobilePhone);
            log.info("sumTransactionByMoilePhone:" + tmpListTx);
            rtresult = tmpListTx;
        } catch (Exception ex) {
            throw ex;
        }
        return rtresult;
    }

    public InjectorEJBDAO getTransactionDAO() {
        return epay_transactionDAO;
    }

    public void setTransactionDAO(InjectorEJBDAO transactionDAO) {
        this.epay_transactionDAO = transactionDAO;
    }

    public InjectorEJBDAO getInvoiceDAO() {
        return epay_invoiceDAO;
    }

    public void setInvoiceDAO(InjectorEJBDAO invoiceDAO) {
        this.epay_invoiceDAO = invoiceDAO;
    }

    public InjectorEJBDAO getInvoice_itemDAO() {
        return epay_invoice_itemDAO;
    }

    public void setInvoice_itemDAO(InjectorEJBDAO invoice_itemDAO) {
        this.epay_invoice_itemDAO = invoice_itemDAO;
    }

    public InjectorEJBDAO getCpInfoDAO() {
        return epay_cpInfoDAO;
    }

    public void setCpInfoDAO(InjectorEJBDAO cpInfoDAO) {
        this.epay_cpInfoDAO = cpInfoDAO;
    }

    public InjectorEJBDAO getServiceInfoDAO() {
        return epay_serviceInfoDAO;
    }

    public void setServiceInfoDAO(InjectorEJBDAO serviceInfoDAO) {
        this.epay_serviceInfoDAO = serviceInfoDAO;
    }

    public InjectorEJBDAO getCallerDAO() {
        return epay_callerDAO;
    }

    public void setCallerDAO(InjectorEJBDAO callerDAO) {
        this.epay_callerDAO = callerDAO;
    }

    public static Logger getLog() {
        return log;
    }

    public static void setLog(Logger log) {
        EPayBusinessConreollerImpl.log = log;
    }

//    public static HashMap getIllegal_Msg_Cache() {
//        return Illegal_Msg_Cache;
//    }
//
//    public static void setIllegal_Msg_Cache(HashMap Illegal_Msg_Cache) {
//        EPayBusinessConreollerImpl.Illegal_Msg_Cache = Illegal_Msg_Cache;
//    }
//
//    public static HashMap getIllegal_Msg_Allow_Cache() {
//        return Illegal_Msg_Allow_Cache;
//    }
//
//    public static void setIllegal_Msg_Allow_Cache(HashMap Illegal_Msg_Allow_Cache) {
//        EPayBusinessConreollerImpl.Illegal_Msg_Allow_Cache = Illegal_Msg_Allow_Cache;
//    }
//
//    public static HashMap getCommon_Profile_Cache() {
//        return Common_Profile_Cache;
//    }
//
//    public static void setCommon_Profile_Cache(HashMap Common_Profile_Cache) {
//        EPayBusinessConreollerImpl.Common_Profile_Cache = Common_Profile_Cache;
//    }
//
//    public static HashMap getIp_Tables_Cache() {
//        return Ip_Tables_Cache;
//    }
//
//    public static void setIp_Tables_Cache(HashMap Ip_Tables_Cache) {
//        EPayBusinessConreollerImpl.Ip_Tables_Cache = Ip_Tables_Cache;
//    }
//
//    public static HashMap getSid_Cache() {
//        return Sid_Cache;
//    }
//
//    public static void setSid_Cache(HashMap Sid_Cache) {
//        EPayBusinessConreollerImpl.Sid_Cache = Sid_Cache;
//    }
//
//    public static HashMap getContract_Cbn_Cache() {
//        return Contract_Cbn_Cache;
//    }
//
//    public static void setContract_Cbn_Cache(HashMap Contract_Cbn_Cache) {
//        EPayBusinessConreollerImpl.Contract_Cbn_Cache = Contract_Cbn_Cache;
//    }
//
//    public static HashMap getContract_Points_Cache() {
//        return Contract_Points_Cache;
//    }
//
//    public static void setContract_Points_Cache(HashMap Contract_Points_Cache) {
//        EPayBusinessConreollerImpl.Contract_Points_Cache = Contract_Points_Cache;
//    }
//
//    public String getStrLOG() {
//        return strLOG;
//    }
//
//    public void setStrLOG(String strLOG) {
//        this.strLOG = strLOG;
//    }
//    @Override
//    public List<EPAY_TRANSACTION> listTransactionByLibm(String libm) throws Exception {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public List<EPAY_TRANSACTION> listTransactionByItemcode(String itemcode) throws Exception {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    public InjectorEJBDAO getEpay_transactionDAO() {
        return epay_transactionDAO;
    }

    public void setEpay_transactionDAO(InjectorEJBDAO epay_transactionDAO) {
        this.epay_transactionDAO = epay_transactionDAO;
    }

    public InjectorEJBDAO getEpay_invoiceDAO() {
        return epay_invoiceDAO;
    }

    public void setEpay_invoiceDAO(InjectorEJBDAO epay_invoiceDAO) {
        this.epay_invoiceDAO = epay_invoiceDAO;
    }

    public InjectorEJBDAO getEpay_invoice_itemDAO() {
        return epay_invoice_itemDAO;
    }

    public void setEpay_invoice_itemDAO(InjectorEJBDAO epay_invoice_itemDAO) {
        this.epay_invoice_itemDAO = epay_invoice_itemDAO;
    }

    public InjectorEJBDAO getEpay_cpInfoDAO() {
        return epay_cpInfoDAO;
    }

    public void setEpay_cpInfoDAO(InjectorEJBDAO epay_cpInfoDAO) {
        this.epay_cpInfoDAO = epay_cpInfoDAO;
    }

    public InjectorEJBDAO getEpay_serviceInfoDAO() {
        return epay_serviceInfoDAO;
    }

    public void setEpay_serviceInfoDAO(InjectorEJBDAO epay_serviceInfoDAO) {
        this.epay_serviceInfoDAO = epay_serviceInfoDAO;
    }

    public InjectorEJBDAO getEpay_callerDAO() {
        return epay_callerDAO;
    }

    public void setEpay_callerDAO(InjectorEJBDAO epay_callerDAO) {
        this.epay_callerDAO = epay_callerDAO;
    }

    public InjectorEJBDAO getEpay_common_userDAO() {
        return epay_common_userDAO;
    }

    public void setEpay_common_userDAO(InjectorEJBDAO epay_common_userDAO) {
        this.epay_common_userDAO = epay_common_userDAO;
    }

    public InjectorEJBDAO getEpay_sys_functionDAO() {
        return epay_sys_functionDAO;
    }

    public void setEpay_sys_functionDAO(InjectorEJBDAO epay_sys_functionDAO) {
        this.epay_sys_functionDAO = epay_sys_functionDAO;
    }

    public InjectorEJBDAO getEpay_sys_rolefuncsDAO() {
        return epay_sys_rolefuncsDAO;
    }

    public void setEpay_sys_rolefuncsDAO(InjectorEJBDAO epay_sys_rolefuncsDAO) {
        this.epay_sys_rolefuncsDAO = epay_sys_rolefuncsDAO;
    }

    public InjectorEJBDAO getEpay_sys_rolesDAO() {
        return epay_sys_rolesDAO;
    }

    public void setEpay_sys_rolesDAO(InjectorEJBDAO epay_sys_rolesDAO) {
        this.epay_sys_rolesDAO = epay_sys_rolesDAO;
    }

    public InjectorEJBDAO getEpay_bucketDAO() {
        return epay_bucketDAO;
    }

    public void setEpay_bucketDAO(InjectorEJBDAO epay_bucketDAO) {
        this.epay_bucketDAO = epay_bucketDAO;
    }

    @Override
    public EPAY_CP_INFO getCpInfo(Integer cpid) throws Exception {
        log.info("entring getCpInfo(): " + cpid);
        EPAY_CP_INFO result = null;
        try {
            EPAY_CP_INFODAO dao = (EPAY_CP_INFODAO) this.getCpInfoDAO().getEJBDAO();
            result = dao.getCpInfo(cpid);
        } catch (Exception e) {
            log.error("getCpInfo fail!", e);
        }
        return result;
    }

    @Override
    public boolean insertVcard(EPAY_VCARD bean) throws Exception {
//        log.debug("entring insertTransaction(): " + bean.getLibm());
        EPAY_VCARDDAO vcarddaobean = null;
        boolean rtresult = false;
        try {
            vcarddaobean = (EPAY_VCARDDAO) epay_vcardDAO.getEJBDAO();
            rtresult = vcarddaobean.insertVCard(bean);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return rtresult;
    }

    @Override
    public EPAY_VCARD getVCPass(String tokenId) throws Exception {
//        log.debug("entring insertTransaction(): " + bean.getLibm());
        EPAY_VCARDDAO vcarddaobean = null;
        EPAY_VCARD rtresult = new EPAY_VCARD();
        try {
            vcarddaobean = (EPAY_VCARDDAO) epay_vcardDAO.getEJBDAO();
            rtresult = vcarddaobean.getVCPass(tokenId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return rtresult;
    }

    @Override
    public boolean updateVCard(EPAY_VCARD bean) throws Exception {
        boolean rtresult = false;
        EPAY_VCARDDAO userDAO = null;
        try {
            userDAO = (EPAY_VCARDDAO) epay_vcardDAO.getEJBDAO();
            rtresult = userDAO.updateVCard(bean);
        } catch (Exception ex) {
//            ex.printStackTrace();
            rtresult = false;
        }
        return rtresult;
    }

    @Override
    public EPAY_VCARD queryVCard(int id) throws Exception {
//        log.debug("entring insertTransaction(): " + bean.getLibm());
        EPAY_VCARDDAO vcarddaobean = null;
        EPAY_VCARD rtresult = new EPAY_VCARD();
        try {
            vcarddaobean = (EPAY_VCARDDAO) epay_vcardDAO.getEJBDAO();
            rtresult = vcarddaobean.queryVCard(id);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return rtresult;
    }

    @Override
    public List<EPAY_VCARD> queryCardByCardtype(String cardtype) throws Exception {
//        log.debug("entring insertTransaction(): " + bean.getLibm());
        EPAY_VCARDDAO vcarddaobean = null;
        List<EPAY_VCARD> result = null;

        try {
            vcarddaobean = (EPAY_VCARDDAO) epay_vcardDAO.getEJBDAO();
            result = vcarddaobean.queryVCarderByCardType(cardtype);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public EPAY_SERVICE_INFO getServiceInfoByCpidAndServiceId(Long serviceid, Integer cid) throws Exception {
        log.info("entring getServiceInfoById(): " + serviceid);
        EPAY_SERVICE_INFO result = null;
        try {
            EPAY_SERVICE_INFODAO dao = (EPAY_SERVICE_INFODAO) this.getServiceInfoDAO().getEJBDAO();
            result = dao.getServiceInfoByCpidAndServiceId(serviceid, cid);
        } catch (Exception e) {
            log.error("getServiceInfoById() fail!", e);
        }
        return result;
    }

    @Override
    public List<EPAY_VCARDTYPE> listCardTypeInfo() throws Exception {
        EPAY_VCARDTYPEDAO vcardtypedaobean = null;
        List<EPAY_VCARDTYPE> result = null;

        try {
            vcardtypedaobean = (EPAY_VCARDTYPEDAO) epay_vcardtypeDAO.getEJBDAO();
            result = vcardtypedaobean.listAllCardTypeInfo();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public EPAY_VCARDTYPE queryCardTypeByCardType(String cardtype) throws Exception {
        EPAY_VCARDTYPEDAO vcardtypedaobean = null;
        EPAY_VCARDTYPE result = null;

        try {
            vcardtypedaobean = (EPAY_VCARDTYPEDAO) epay_vcardtypeDAO.getEJBDAO();
            result = vcardtypedaobean.queryCardTypeByCardType(cardtype);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean findVCardByFilename(String filesname) throws Exception {
        boolean result = true;
        EPAY_VCARDDAO vcarddaobean = null;
        try {
            vcarddaobean = (EPAY_VCARDDAO) epay_vcardDAO.getEJBDAO();
            result = vcarddaobean.queryVCardByFileName(filesname);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean updateVCardType(EPAY_VCARDTYPE bean) throws Exception {
        boolean rtresult = false;
        EPAY_VCARDTYPEDAO userDAO = null;
        try {
            userDAO = (EPAY_VCARDTYPEDAO) epay_vcardtypeDAO.getEJBDAO();
            rtresult = userDAO.updateVCardType(bean);
        } catch (Exception ex) {
//            ex.printStackTrace();
            rtresult = false;
        }
        return rtresult;
    }

    @Override
    public List<EPAY_SERVICE_INFO> listAllServiceInfoByPromotion(Integer cpid, String promotioncode, String contractstatuscode, String lifecyclestatus) throws Exception {
        log.info("entring listAllServiceInfo()(): ");
        List<EPAY_SERVICE_INFO> result = null;
        try {
            EPAY_SERVICE_INFODAO dao = (EPAY_SERVICE_INFODAO) this.getServiceInfoDAO().getEJBDAO();
            result = (List<EPAY_SERVICE_INFO>) dao.getAllServiceInfoByPromotionCode(cpid, promotioncode, contractstatuscode, lifecyclestatus);
        } catch (Exception e) {
            log.error("listAllCpInfo() fail!", e);
        }
        return result;
    }

    @Override
    public EPAY_VCARD queryVCardByToken(String token, String mdn, String serviceid) throws Exception {
//        log.debug("entring insertTransaction(): " + bean.getLibm());
        EPAY_VCARDDAO vcarddaobean = null;
        EPAY_VCARD rtresult = new EPAY_VCARD();
        try {
            vcarddaobean = (EPAY_VCARDDAO) epay_vcardDAO.getEJBDAO();
            rtresult = vcarddaobean.getVCardByToken(token, mdn, serviceid);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return rtresult;
    }

    @Override
    public boolean chkIpValidation(String target_ip, int cpid) {
        boolean rtresult = false;
        // target_ip = "175.181.45.87";
        log.info(" ** chkIpValidation(), target_ip = " + target_ip + " * cpId = " + cpid);
        boolean chk_ip_1 = false, chk_ip_2 = false, chk_ip_3 = false, chk_ip_4 = false, chk_ip_5 = false, chk_ip_6 = false;
        try {
            if (target_ip != null && target_ip.matches(ShareParm.PARM_REGULAR_IP)) {
                // IP_TABLES ip_tables = getIP_TABLES_ByCt_Id(ct_id);

                EPAY_IP_TABLES ip_tables = getIP_TABLES_Cache_ByCPID(cpid);

                if (ip_tables == null) {
                    ip_tables = getIP_TABLES_ByCPID(cpid);
                    log.info(" ** ip_tables is null read from db. ip_tables = " + ip_tables);
                }
                if (ip_tables != null) {
//                    System.out.println(" ** ip_tables.getIpt_id = "
//                            + ip_tables.getIpt_id()
//                            + ", * ip_tables.getIpaddr1 = "
//                            + ip_tables.getIpaddr1());
                    String ip_tables_1 = ip_tables.getIpaddr1();
                    if (ip_tables_1 != null && !ip_tables_1.equals("")) {
                        StringTokenizer stk_1 = new StringTokenizer(
                                        ip_tables_1, "/");
                        String ip_1 = stk_1.nextToken();
                        String submask_1 = stk_1.nextToken();
                        if (ip_1 != null && submask_1 != null
                                        && ip_1.matches(ShareParm.PARM_REGULAR_IP)
                                        && submask_1.matches(ShareParm.PARM_REGULAR_IP)) {
                            chk_ip_1 = toolsUtil.chkIpAuth(target_ip, ip_1,
                                            submask_1);
                        }
                    }
                    String ip_tables_2 = ip_tables.getIpaddr2();
                    if (ip_tables_2 != null && !ip_tables_2.equals("")) {
                        StringTokenizer stk_2 = new StringTokenizer(
                                        ip_tables_2, "/");
                        String ip_2 = stk_2.nextToken();
                        String submask_2 = stk_2.nextToken();
                        if (ip_2 != null && submask_2 != null
                                        && ip_2.matches(ShareParm.PARM_REGULAR_IP)
                                        && submask_2.matches(ShareParm.PARM_REGULAR_IP)) {
                            chk_ip_2 = toolsUtil.chkIpAuth(target_ip, ip_2,
                                            submask_2);
                        }
                    }
                    String ip_tables_3 = ip_tables.getIpaddr3();
                    if (ip_tables_3 != null && !ip_tables_3.equals("")) {
                        StringTokenizer stk_3 = new StringTokenizer(
                                        ip_tables_3, "/");
                        String ip_3 = stk_3.nextToken();
                        String submask_3 = stk_3.nextToken();
                        if (ip_3 != null && submask_3 != null
                                        && ip_3.matches(ShareParm.PARM_REGULAR_IP)
                                        && submask_3.matches(ShareParm.PARM_REGULAR_IP)) {
                            chk_ip_3 = toolsUtil.chkIpAuth(target_ip, ip_3,
                                            submask_3);
                        }
                    }
                    String ip_tables_4 = ip_tables.getIpaddr4();
                    if (ip_tables_4 != null && !ip_tables_4.equals("")) {
                        StringTokenizer stk_4 = new StringTokenizer(
                                        ip_tables_4, "/");
                        String ip_4 = stk_4.nextToken();
                        String submask_4 = stk_4.nextToken();
                        if (ip_4 != null && submask_4 != null
                                        && ip_4.matches(ShareParm.PARM_REGULAR_IP)
                                        && submask_4.matches(ShareParm.PARM_REGULAR_IP)) {
                            chk_ip_4 = toolsUtil.chkIpAuth(target_ip, ip_4,
                                            submask_4);
                        }
                    }
                    String ip_tables_5 = ip_tables.getIpaddr5();
                    if (ip_tables_5 != null && !ip_tables_5.equals("")) {
                        StringTokenizer stk_5 = new StringTokenizer(
                                        ip_tables_5, "/");
                        String ip_5 = stk_5.nextToken();
                        String submask_5 = stk_5.nextToken();
                        if (ip_5 != null && submask_5 != null
                                        && ip_5.matches(ShareParm.PARM_REGULAR_IP)
                                        && submask_5.matches(ShareParm.PARM_REGULAR_IP)) {
                            chk_ip_5 = toolsUtil.chkIpAuth(target_ip, ip_5,
                                            submask_5);
                        }
                    }
                    String ip_tables_6 = ip_tables.getIpaddr6();
                    if (ip_tables_6 != null && !ip_tables_6.equals("")) {
                        StringTokenizer stk_6 = new StringTokenizer(
                                        ip_tables_6, "/");
                        String ip_6 = stk_6.nextToken();
                        String submask_6 = stk_6.nextToken();
                        if (ip_6 != null && submask_6 != null
                                        && ip_6.matches(ShareParm.PARM_REGULAR_IP)
                                        && submask_6.matches(ShareParm.PARM_REGULAR_IP)) {
                            chk_ip_6 = toolsUtil.chkIpAuth(target_ip, ip_6,
                                            submask_6);
                        }
                    }
                    boolean isValidIp = chk_ip_1 || chk_ip_2 || chk_ip_3
                                    || chk_ip_4 || chk_ip_5 || chk_ip_6;
                    rtresult = new ShareBean().isIptablesAuth(
                                    ip_tables.getBwflag(), isValidIp);
                } else {
                    log.info(" ** ip_tables is null  ");
                }
            } else {
                log.info(" ** getIP_TABLES_Cache_ByCt_Id not found  ");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return rtresult;
    }

    @Override
    public EPAY_IP_TABLES getIP_TABLES_Cache_ByCPID(int cpid) {
        EPAY_IP_TABLES rtResult = null;
        try {
            if (Ip_Tables_Cache.get("ip_tables" + cpid) != null) {
                HashMap map_ip_tables = (HashMap) Ip_Tables_Cache
                                .get("ip_tables" + cpid);
                if (((Date) map_ip_tables.get("updatetime")).after(DateUtil.calcuate_after_time(new Date(), -(ShareParm.PARM_CACHE_IP_TABLES_PERIOD_TIME)))) {
                    rtResult = (EPAY_IP_TABLES) map_ip_tables.get(cpid);
                } else {
                    map_ip_tables.clear();

                    rtResult = getIP_TABLES_ByCPID(cpid);
                    map_ip_tables.put(cpid, rtResult);
                    map_ip_tables.put("updatetime", new Date());
                    Ip_Tables_Cache.put("ip_tables" + cpid, map_ip_tables);
                }
            } else {
                HashMap map_ip_tables = new HashMap();
                rtResult = getIP_TABLES_ByCPID(cpid);
                map_ip_tables.put(cpid, rtResult);
                map_ip_tables.put("updatetime", new Date());
                Ip_Tables_Cache.put("ip_tables" + cpid, map_ip_tables);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return rtResult;
    }

    @Override
    public EPAY_IP_TABLES getIP_TABLES_ByCPID(int cpid) {
        EPAY_IP_TABLESDAO ip_tablesdaobean = null;
        EPAY_IP_TABLES rtresult = null;
        try {
            ip_tablesdaobean = (EPAY_IP_TABLESDAO) epay_iptablesDAO.getEJBDAO();
            rtresult = ip_tablesdaobean.getIP_TABLES_ByCPID(cpid);

        } catch (Exception ex) {
            // ex.printStackTrace();
            rtresult = null;
        }
        return rtresult;
    }

    @Override
    public boolean insertVCardType(EPAY_VCARDTYPE bean) throws Exception {
//        log.debug("entring insertTransaction(): " + bean.getLibm());
        EPAY_VCARDTYPEDAO vcardtypedaobean = null;
        boolean rtresult = false;
        try {
            vcardtypedaobean = (EPAY_VCARDTYPEDAO) epay_vcardtypeDAO.getEJBDAO();
            rtresult = vcardtypedaobean.insertVCardType(bean);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return rtresult;
    }

    @Override
    public EPAY_PROMOTIONCODE getPomtionCode(String promotioncode) throws Exception {
        EPAY_PROMOTIONCODEDAO promotioncodedaobean = null;
        EPAY_PROMOTIONCODE rtresult = null;
        try {
            promotioncodedaobean = (EPAY_PROMOTIONCODEDAO) epay_promotioncodeDAO.getEJBDAO();
            rtresult = promotioncodedaobean.getPlatformType(promotioncode);

        } catch (Exception ex) {
            // ex.printStackTrace();
            rtresult = null;
        }
        return rtresult;
    }

    @Override
    public EPAY_DEALERCARD getDealerCardByCardId(String dealerId, String cardId) throws Exception {
        EPAY_DEALERCARDDAO promotioncodedaobean = null;
        EPAY_DEALERCARD rtresult = null;
        try {
            promotioncodedaobean = (EPAY_DEALERCARDDAO) epay_dealercardDAO.getEJBDAO();
            rtresult = promotioncodedaobean.geDearlCardByCardId(dealerId, cardId);

        } catch (Exception ex) {
            ex.printStackTrace();
            rtresult = null;
        }
        return rtresult;
    }

    @Override
    public EPAY_DEALERMDN getDealerMDNByMDN(String dealerId, String mdn) {
        EPAY_DEALERMDNDAO promotioncodedaobean = null;
        EPAY_DEALERMDN rtresult = null;
        try {
            promotioncodedaobean = (EPAY_DEALERMDNDAO) epay_dealermdnDAO.getEJBDAO();
            rtresult = promotioncodedaobean.geDearlMDNByMDN(dealerId, mdn);

        } catch (Exception ex) {
            // ex.printStackTrace();
            rtresult = null;
        }
        return rtresult;
    }

    @Override
    public EPAY_SERVICE_INFO getServiceInfoByPricePlanCode(String priceplancode) throws Exception {
        log.info("entring getServiceInfoByPricePlanCode(): " + priceplancode);
        EPAY_SERVICE_INFO result = null;
        try {
            EPAY_SERVICE_INFODAO dao = (EPAY_SERVICE_INFODAO) this.getServiceInfoDAO().getEJBDAO();
            result = dao.getServiceInfoByPricePlanCode(priceplancode);
        } catch (Exception e) {
            log.error("getServiceInfoById() fail!", e);
        }
        return result;
    }

    @Override
    public EPAY_SCT_CARD getSCTCardInfoByIccid(String iccid) throws Exception {
        EPAY_SCTCARDDAO sctcardDAO = null;
        EPAY_SCT_CARD rtresult = null;
        try {
            sctcardDAO = (EPAY_SCTCARDDAO) epay_sctcardDAO.getEJBDAO();
            rtresult = sctcardDAO.getSctCardInfoByIccid(iccid);

        } catch (Exception ex) {
//            ex.printStackTrace();
        }
        return rtresult;
    }

    @Override
    public boolean insertSctCard(EPAY_SCT_CARD bean) throws Exception {
//        log.debug("entring insertTransaction(): " + bean.getLibm());
        EPAY_SCTCARDDAO sctcarddao = null;
        boolean rtresult = false;
        try {
            sctcarddao = (EPAY_SCTCARDDAO) epay_sctcardDAO.getEJBDAO();
            rtresult = sctcarddao.insertSctCardInfo(bean);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return rtresult;
    }

    @Override
    public List<EPAY_SCT_CARD> listSctCardInfoByAPN(String apn) throws Exception {
        log.info("entring listSctCardInfoByAPN()(): ");
        List<EPAY_SCT_CARD> result = null;
        try {
            EPAY_SCTCARDDAO dao = (EPAY_SCTCARDDAO) epay_sctcardDAO.getEJBDAO();
            result = (List<EPAY_SCT_CARD>) dao.getSctCardInfoByAPN(apn);
        } catch (Exception e) {
            log.error("listSctCardInfoByAPN() fail!", e);
        }
        return result;
    }

    @Override
    public List<EPAY_SCT_CARD> listSctCardInfo() throws Exception {
        log.info("entring listSctCardInfo()(): ");
        List<EPAY_SCT_CARD> result = null;
        try {
            EPAY_SCTCARDDAO dao = (EPAY_SCTCARDDAO) epay_sctcardDAO.getEJBDAO();
            result = (List<EPAY_SCT_CARD>) dao.getSctCardInfo();
        } catch (Exception e) {
            log.error("listSctCardInfoByAPN() fail!", e);
        }
        return result;
    }

    @Override
    public EPAY_SCT_CARD getSCTCardInfoById(int id) throws Exception {
        EPAY_SCTCARDDAO sctcardDAO = null;
        EPAY_SCT_CARD rtresult = null;
        try {
            sctcardDAO = (EPAY_SCTCARDDAO) epay_sctcardDAO.getEJBDAO();
            rtresult = sctcardDAO.getSctCardInfoById(id);

        } catch (Exception ex) {
//            ex.printStackTrace();
        }
        return rtresult;
    }

    @Override
    public boolean updateSctCard(EPAY_SCT_CARD bean) throws Exception {
        boolean rtresult = false;
        EPAY_SCTCARDDAO userDAO = null;
        try {
            userDAO = (EPAY_SCTCARDDAO) epay_sctcardDAO.getEJBDAO();
            rtresult = userDAO.updateSCTCard(bean);
        } catch (Exception ex) {
//            ex.printStackTrace();
            rtresult = false;
        }
        return rtresult;
    }

    @Override
    public boolean insert_Sys_Func(EPAY_SYS_FUNCTIONS func) {
//        log.debug("entring insertTransaction(): " + bean.getLibm());
        EPAY_SYS_FUNCTIONSDAO funcdao = null;
        boolean rtresult = false;
        try {
            funcdao = (EPAY_SYS_FUNCTIONSDAO) epay_sys_functionDAO.getEJBDAO();
            rtresult = funcdao.insert_SYS_FUNCTIONS(func);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return rtresult;
    }

    @Override
    public List<EPAY_SYS_FUNCTIONS> listAllFunc() throws Exception {
        log.info("entring listAllFunc()(): ");
        List<EPAY_SYS_FUNCTIONS> result = null;
        try {
            EPAY_SYS_FUNCTIONSDAO dao = (EPAY_SYS_FUNCTIONSDAO) epay_sys_functionDAO.getEJBDAO();
            result = (List<EPAY_SYS_FUNCTIONS>) dao.getFuncInfo();
        } catch (Exception e) {
            log.error("listAllFunc() fail!", e);
        }
        return result;
    }

    @Override
    public EPAY_SYS_FUNCTIONS getFunc(String id) throws Exception {
        EPAY_SYS_FUNCTIONSDAO funcDAO = null;
        EPAY_SYS_FUNCTIONS rtresult = null;
        try {
            funcDAO = (EPAY_SYS_FUNCTIONSDAO) epay_sys_functionDAO.getEJBDAO();
            rtresult = funcDAO.getFunc_byUserCode(id);

        } catch (Exception ex) {
//            ex.printStackTrace();
        }
        return rtresult;
    }

    @Override
    public boolean updateFunc(EPAY_SYS_FUNCTIONS bean) throws Exception {
        boolean rtresult = false;
        EPAY_SYS_FUNCTIONSDAO funcDAO = null;
        try {
            funcDAO = (EPAY_SYS_FUNCTIONSDAO) epay_sys_functionDAO.getEJBDAO();
            rtresult = funcDAO.update_SYS_FUNCTIONS(bean);
        } catch (Exception ex) {
//            ex.printStackTrace();
            rtresult = false;
        }
        return rtresult;
    }

    @Override
    public boolean updateRole(EPAY_SYS_ROLES bean) throws Exception {
        boolean rtresult = false;
        EPAY_SYS_ROLESDAO roledao = null;
        try {
            roledao = (EPAY_SYS_ROLESDAO) epay_sys_rolesDAO.getEJBDAO();
            rtresult = roledao.update_SYS_ROLES(bean);
        } catch (Exception ex) {
//            ex.printStackTrace();
            rtresult = false;
        }
        return rtresult;
    }

    @Override
    public boolean insertRole(EPAY_SYS_ROLES bean) throws Exception {
//        log.debug("entring insertTransaction(): " + bean.getLibm());
        EPAY_SYS_ROLESDAO roledao = null;
        boolean rtresult = false;
        try {
            roledao = (EPAY_SYS_ROLESDAO) epay_sys_rolesDAO.getEJBDAO();
            rtresult = roledao.insert_SYS_ROLES(bean);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return rtresult;
    }

    @Override
    public boolean insertRoleFunc(EPAY_SYS_ROLEFUNCS bean) throws Exception {
        EPAY_SYS_ROLEFUNCSDAO rolefuncdao = null;
        boolean rtresult = false;
        try {
            rolefuncdao = (EPAY_SYS_ROLEFUNCSDAO) epay_sys_rolefuncsDAO.getEJBDAO();
            rtresult = rolefuncdao.insert_SYS_ROLE_FUNCTIONS(bean);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return rtresult;
    }

    @Override
    public boolean updateRoleFunc(EPAY_SYS_ROLEFUNCS bean) throws Exception {
        boolean rtresult = false;
        EPAY_SYS_ROLEFUNCSDAO rolefuncdao = null;
        try {
            rolefuncdao = (EPAY_SYS_ROLEFUNCSDAO) epay_sys_rolefuncsDAO.getEJBDAO();
            rtresult = rolefuncdao.update_SYS_ROLE_FUNCTIONS(bean);
        } catch (Exception ex) {
//            ex.printStackTrace();
            rtresult = false;
        }
        return rtresult;
    }

    @Override
    public List<EPAY_SYS_ROLEFUNCS> listAllRuleFunc() throws Exception {
        log.info("entring listAllRuleFunc()(): ");
        List<EPAY_SYS_ROLEFUNCS> result = null;
        try {
            EPAY_SYS_ROLEFUNCSDAO dao = (EPAY_SYS_ROLEFUNCSDAO) epay_sys_rolefuncsDAO.getEJBDAO();
            result = (List<EPAY_SYS_ROLEFUNCS>) dao.getAllRoleFuncs();
        } catch (NamingException e) {
            log.error("listAllRuleFunc() fail!", e);
        }
        return result;
    }

    @Override
    public EPAY_SYS_ROLEFUNCS queryRuleFuncById(int id) throws Exception {
        EPAY_SYS_ROLEFUNCSDAO rulefuncDAO = null;
        EPAY_SYS_ROLEFUNCS rtresult = null;
        try {
            rulefuncDAO = (EPAY_SYS_ROLEFUNCSDAO) epay_sys_rolefuncsDAO.getEJBDAO();
            rtresult = rulefuncDAO.getRoleFuncs_BY_RoleID(id);

        } catch (NamingException ex) {
//            ex.printStackTrace();
        }
        return rtresult;
    }

    @Override
    public List getTxLogListByBatchFie(String batchfile) {
        EPAY_TRANSACTIONDAO pg_transactionDAO = null;
        List rtList = null;
        try {
            pg_transactionDAO = (EPAY_TRANSACTIONDAO) epay_transactionDAO.getEJBDAO();
            rtList = pg_transactionDAO.listTransactionByBatchFile(batchfile);

        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        return rtList;
    }

    @Override
    public List getTxIbonListByDate(String startdatetime, String enddatetime) {
        EPAY_TRANSACTIONDAO pg_transactionDAO = null;
        List rtList = null;
        try {
            pg_transactionDAO = (EPAY_TRANSACTIONDAO) epay_transactionDAO.getEJBDAO();
            rtList = pg_transactionDAO.getIboneTx_byStartDateTimeAndEndtDateTime(startdatetime, enddatetime);

        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        return rtList;
    }

    @Override
    public List getTxIbonListByDateByStatus(String startdatetime, String enddatetime, String status) {
        EPAY_TRANSACTIONDAO pg_transactionDAO = null;
        List rtList = null;
        try {
            pg_transactionDAO = (EPAY_TRANSACTIONDAO) epay_transactionDAO.getEJBDAO();
            rtList = pg_transactionDAO.getIboneTx_byStartDateTimeAndEndtDateTimeByStatus(startdatetime, enddatetime, status);

        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        return rtList;
    }

    @Override
    public List getTxIbonListByDateByMdn(String startdatetime, String enddatetime, String mdn) {
        EPAY_TRANSACTIONDAO pg_transactionDAO = null;
        List rtList = null;
        try {
            pg_transactionDAO = (EPAY_TRANSACTIONDAO) epay_transactionDAO.getEJBDAO();
            rtList = pg_transactionDAO.getIboneTx_byStartDateTimeAndEndtDateTimeByMdn(startdatetime, enddatetime, mdn);

        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        return rtList;
    }

    @Override
    public List getTxIbonListByDateByMdnByStatus(String startdatetime, String enddatetime, String mdn, String status) {
        EPAY_TRANSACTIONDAO pg_transactionDAO = null;
        List rtList = null;
        try {
            pg_transactionDAO = (EPAY_TRANSACTIONDAO) epay_transactionDAO.getEJBDAO();
            rtList = pg_transactionDAO.getIboneTx_byStartDateTimeAndEndtDateTimeByMdnByStatus(startdatetime, enddatetime, mdn, status);

        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        return rtList;
    }

    @Override
    public List getTxCountryCodeListByDate(String startdatetime, String enddatetime) {
        EPAY_TRANSACTIONDAO pg_transactionDAO = null;
        List rtList = null;
        try {
            pg_transactionDAO = (EPAY_TRANSACTIONDAO) epay_transactionDAO.getEJBDAO();
            rtList = pg_transactionDAO.getCountryCodeTx_byStartDateTimeAndEndtDateTime(startdatetime, enddatetime);

        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        return rtList;
    }

    @Override
    public List getTxCountryCodeListByDateBystatus(String startdatetime, String enddatetime, String status) {
        EPAY_TRANSACTIONDAO pg_transactionDAO = null;
        List rtList = null;
        try {
            pg_transactionDAO = (EPAY_TRANSACTIONDAO) epay_transactionDAO.getEJBDAO();
            rtList = pg_transactionDAO.getCountryCodeTx_byStartDateTimeAndEndtDateTimeByStatus(startdatetime, enddatetime, status);

        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        return rtList;
    }

    @Override
    public List getTxCountryCodeListByDateByMdn(String startdatetime, String enddatetime, String mdn) {
        EPAY_TRANSACTIONDAO pg_transactionDAO = null;
        List rtList = null;
        try {
            pg_transactionDAO = (EPAY_TRANSACTIONDAO) epay_transactionDAO.getEJBDAO();
            rtList = pg_transactionDAO.getCountryCodeTx_byStartDateTimeAndEndtDateTimeByMdn(startdatetime, enddatetime, mdn);

        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        return rtList;
    }

    @Override
    public List getTxCountryCodeListByDateByMdnByStatus(String startdatetime, String enddatetime, String mdn, String status) {
        EPAY_TRANSACTIONDAO pg_transactionDAO = null;
        List rtList = null;
        try {
            pg_transactionDAO = (EPAY_TRANSACTIONDAO) epay_transactionDAO.getEJBDAO();
            rtList = pg_transactionDAO.getCountryCodeTx_byStartDateTimeAndEndtDateTimeByMdnByStatus(startdatetime, enddatetime, mdn, status);

        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        return rtList;
    }

    @Override
    public List getDTOneMdnTransAmount(String mdn, String begindate, String enddate) {
        EPAY_TRANSACTIONDAO pg_transactionDAO = null;
        List rtList = null;
        try {
            pg_transactionDAO = (EPAY_TRANSACTIONDAO) epay_transactionDAO.getEJBDAO();
            rtList = pg_transactionDAO.getDTOneQuotaByMdn(mdn, begindate, enddate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return rtList;
    }
    
    @Override
    public List<EPAY_SERVICE_INFO> listNokialServiceInfo(Integer cpid, String promotioncode, int platformtype) throws Exception {
        log.info("entring listNokialServiceInfo()(): ");
        List<EPAY_SERVICE_INFO> result = null;
        try {
            EPAY_SERVICE_INFODAO dao = (EPAY_SERVICE_INFODAO) this.getServiceInfoDAO().getEJBDAO();
            result = (List<EPAY_SERVICE_INFO>) dao.getNokiaServiceInfo(cpid, promotioncode, platformtype);
        } catch (Exception e) {
            log.error("listAllCpInfo() fail!", e);
        }
        return result;
    }    
}
