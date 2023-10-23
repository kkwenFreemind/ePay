/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.util;

import com.apt.epay.nokia.bean.NokiaSubscribeBalanceBean;
import com.apt.epay.nokia.main.NokiaMainBasicInfoUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author kevinchang
 */
public class OCSUtil {

    private static final Logger log = Logger.getLogger("EPAY");

    public boolean CheckOCSFlag(int platformtype, String LC) {
        boolean result = false;
        if (platformtype == 1 || platformtype == 2) {
            if (("ACTIVE".equalsIgnoreCase(LC))
                            || ("EXPIRED".equalsIgnoreCase(LC))
                            || ("CED1".equalsIgnoreCase(LC))
                            || ("CED10".equalsIgnoreCase(LC))
                            || ("CED3".equalsIgnoreCase(LC))) {

                result = true;

            }
        }

        if (platformtype == 3) {
            if (("ACTIVE".equalsIgnoreCase(LC))
                            || ("EXPIRED".equalsIgnoreCase(LC))
                            || ("PREACTIVE".equalsIgnoreCase(LC))
                            || ("PREACTIVE1".equalsIgnoreCase(LC))) {
                result = true;
            }
        }
        return result;
    }

    public boolean check5000flag(NokiaSubscribeBalanceBean bean, String mdn, String libm, double db_check_value) {
        
        boolean result = false;

        double tmp_d_value_650 = 0.0;
        tmp_d_value_650 = Double.valueOf(bean.getAPT5GVoice_650_Counter()) / 10000;
        double d_value_650 = Math.round(tmp_d_value_650 * 100.0) / 100.0;

        double tmp_d_value_670 = 0.0;
        tmp_d_value_670 = Double.valueOf(bean.getAPT5GIDD_670_Counter()) / 10000;
        double d_value_670 = Math.round(tmp_d_value_670 * 100.0) / 100.0;

        double tmp_d_value_750 = 0.0;
        tmp_d_value_750 = Double.valueOf(bean.getAPT5GData_750_Counter()) / 1048576;
        double d_value_750 = Math.round(tmp_d_value_750 * 100.0) / 100.0;

        log.info(mdn + " Nokia b650==>" + d_value_650 + ", Nokia b670==>" + d_value_670);
        log.info(mdn + " Nokia b750==>" + d_value_750);
        double count1x = d_value_650 + d_value_670;
        double count2x = d_value_750;

        double sumx = (double) count1x + (double) count2x;

        log.info(mdn + ":count1x()==>" + count1x);
        log.info(mdn + ":count2x()==>" + count2x);
        log.info(mdn + ":sumx===>" + sumx);
        log.info(mdn + ":db_check_value==>" + db_check_value);

        double checkvalue = sumx + db_check_value;

        log.info(mdn + ":sumx + db_check_value(5000)==>" + checkvalue);

        if (checkvalue < 5000) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

}
