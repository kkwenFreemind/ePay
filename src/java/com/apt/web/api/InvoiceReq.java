package com.apt.web.api;

import com.apt.epay.beans.InvoiceItemBean;
import com.apt.epay.beans.InvoiceReqBean;
import com.apt.epay.controller.EPayBusinessConreoller;
import com.apt.epay.share.ShareBean;
import com.apt.epay.share.ShareParm;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import com.apt.util.ApolSecuredUrlMsg;
import com.epay.ejb.bean.EPAY_CALLER;
import com.epay.ejb.bean.EPAY_INVOICE;
import java.io.StringReader;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 發票開立通知
 *
 * @author Booker Hsu
 */
public class InvoiceReq extends HttpServlet {

//    private static final long serialVersionUID = -37660343432563813L;
    private static final String md5Param = "&identifyCode=";
    private static final String desParam = "&returnOutMac=";
    private static final SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
    private static final SimpleDateFormat sdf7 = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT7);
    private static final Logger log = Logger.getLogger("EPAY");
    private EPayBusinessConreoller epaybusinesscontroller = null;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        log.info("InvoiceReq.processRequest");
//        String logMsg = "Client IP :" + request.getRemoteAddr() + ", getQueryString():" + request.getQueryString();
        String logMsg = "Client IP :" + request.getRemoteAddr();
        log.info(logMsg);

        String callId = ShareParm.EPAY_CALLID;

        ServletOutputStream output = response.getOutputStream();
        response.setCharacterEncoding("UTF-8");
        String invoiceEncryptRtnMsg = request.getParameter("encryptedMsg");
        log.info("invoiceEncryptRtnMsg====>" + invoiceEncryptRtnMsg);

        if ("".equals(invoiceEncryptRtnMsg) || invoiceEncryptRtnMsg == null) {
            throw new ServletException("No Data Received, encryptedMsg is null");
        }

        try {
            epaybusinesscontroller = (EPayBusinessConreoller) new ShareBean().getBusinessBean("epaybusinesscontroller", this.getServletContext());
            InvoiceReqBean apirequestbean = new InvoiceReqBean();
            String key = "";
            String identifyCode = "";
            String xmlResponse = "";
            String returnOutMac = "";
            String isMd5Match = "";
            String decodeMsg = null;

            try {
                EPAY_CALLER caller = epaybusinesscontroller.getCallerById(callId);
                log.info("epaybusinesscontroller.getCallerById(callId)==>" + callId);

                key = caller.getPgEnkey();
                identifyCode = caller.getPgIdentify();
                ApolSecuredUrlMsg asum = new ApolSecuredUrlMsg(caller.getPgEnkey(), caller.getPgIdentify());
                log.info("ApolSecuredUrlMsg(caller.getPgEnkey(), caller.getPgIdentify())==>" + caller.getPgEnkey() + "," + caller.getPgIdentify());
                decodeMsg = asum.decode(invoiceEncryptRtnMsg, md5Param, desParam);

            } catch (Exception e) {
                log.error("Decoding Message Error", e);
                throw e;
            }

            if ("".equalsIgnoreCase(decodeMsg)) {
                log.error("Decoding failed");
                return;
            }

            log.info("decodeMsg = " + decodeMsg);

            String forMD5 = "";
            StringTokenizer st = new StringTokenizer(decodeMsg, "&");
            String returnXmlM5 = "";

            while (st.hasMoreTokens()) {
                String tokenStr = st.nextToken();
                forMD5 = tokenStr + "&identifyCode=" + identifyCode;
                returnXmlM5 = doMd5(forMD5);

                if (tokenStr.contains("xmlResponse")) {
                    xmlResponse = tokenStr.substring(tokenStr.indexOf("=") + 1, tokenStr.length());
                }
                String[] tokenArrays = tokenStr.split("=");

                if ("returnOutMac".equalsIgnoreCase(tokenArrays[0])) {
                    returnOutMac = tokenArrays[1];
                }
                if ("isMd5Match".equalsIgnoreCase(tokenArrays[0])) {
                    isMd5Match = tokenArrays[1];
                }

            }
//            while (st.hasMoreTokens()) {
//                String parms = st.nextElement().toString();
//                String returnItem[] = parms.split("=");
//                apirequestbean = encapBean(apirequestbean, returnItem);
//            }

            apirequestbean = parseXMLString(xmlResponse);

            if (converAndInsertInvoice(apirequestbean)) { //insert invoice

                String respon = "1";
                output.write(respon.getBytes(ShareParm.PARM_CHARSETNAME_UTF8));

            } else {
                log.error("Invoice data DB update fail");
            }
        } catch (Exception ex) {
            log.error("Invoice error", ex);
            output.write("0".getBytes(ShareParm.PARM_CHARSETNAME_UTF8));
        } finally {
            try {
                output.flush();
                output.close();
            } catch (IOException ex) {
                log.error(null, ex);
            }
        }
    }

    /**
     * insert發票明細資料
     *
     * @param apiBean
     * @return
     */
//    private boolean converAndInsertInvoiceItem(InvoiceReqBean apiBean) {
//        boolean res = false;
//        try {
//            //用發票號碼和訂單編號，取得相關發票資料(Invoice_id) 當主key,塞入發票明細表格。
//            EPAY_INVOICE inv = epaybusinesscontroller.listInvoiceByInvoicenoLibm(apiBean.getLibm());
//            if (inv == null) {
//                return false;
//            }
//            List<InvoiceItemReqBean> invItemList = apiBean.getInvoiceItems();
//            for (InvoiceItemReqBean invoiceItemBean : invItemList) {
//                EPAY_INVOICE_ITEM invItem = new EPAY_INVOICE_ITEM();
//                invItem.setInvoiceid(inv.getInvoiceid());
//                invItem.setItem_no(Integer.parseInt(invoiceItemBean.getNo()));
//                invItem.setProduct_name(invoiceItemBean.getProduct_name());
//                invItem.setQuantity(Integer.parseInt(invoiceItemBean.getQuantity()));
//                invItem.setPrice(Integer.parseInt(invoiceItemBean.getPrice()));
//                invItem.setNot_include_tax(Integer.parseInt(invoiceItemBean.getNot_include_tax()));
//                invItem.setTax(Integer.parseInt(invoiceItemBean.getTax()));
//                invItem.setSubtotal(Integer.parseInt(invoiceItemBean.getSubtotal()));
//                EPAY_INVOICE_ITEM tempInvItem = epaybusinesscontroller.getInvoice_Item(inv.getInvoiceid());
//                if (tempInvItem == null) {
//                    res = epaybusinesscontroller.insertInvoice_Item(invItem);
//                } else {
//                    res = epaybusinesscontroller.updateInvoice_Item(invItem);
//                }
//            }
//        } catch (Exception ex) {
//            log.error("Invoice Item Update or Insert fail", ex);
//        }
//        return res;
//    }
    /**
     * insert發票資料
     *
     * @param apiBean
     * @return
     */
    private boolean converAndInsertInvoice(InvoiceReqBean apiBean) {

        boolean res = false;
        try {
            EPAY_INVOICE invoice = new EPAY_INVOICE();
            invoice = epaybusinesscontroller.listInvoiceByInvoicenoLibm(apiBean.getLibm());
            log.info("converAndInsertInvoice.listInvoiceByInvoicenoLibm(apiBean.getLibm()==>" + apiBean.getLibm());

            invoice.setLibm(apiBean.getLibm());
            log.info("apiBean.getLibm()==>" + apiBean.getLibm());

            invoice.setStatus(apiBean.getStatus());
            log.info("apiBean.getStatus()==>" + apiBean.getStatus());
            
            invoice.setDonate(apiBean.getDonate());
            log.info("apiBean.getDonate()==>" + apiBean.getDonate());

//            invoice.setDiscount_no(apiBean.getDiscount_no());
            if (!"".equalsIgnoreCase(apiBean.getInvoice_created_date())) {
                invoice.setInvoice_created_date(sdf7.parse(apiBean.getInvoice_created_date()));
                log.info("apiBean.getInvoice_created_date()==>" + sdf7.parse(apiBean.getInvoice_created_date()));
            }

            invoice.setInvoice_no(apiBean.getInvoice_no());
            log.info("apiBean.getInvoice_no()==>" + apiBean.getInvoice_no());

            if (!"".equalsIgnoreCase(apiBean.getResponse_time())) {
                invoice.setResponse_time(sdf.parse(apiBean.getResponse_time()));
                log.info("apiBean.getResponse_time()==>" + sdf.parse(apiBean.getResponse_time()));
            }

            String tmp = apiBean.getResponse_msg();
//            log.info("tmp---->"+tmp);
//            String tmp2 = new String(apiBean.getResponse_msg().getBytes("ISO-8859-1"), "UTF-8");
            invoice.setResponse_msg(apiBean.getResponse_msg());
//            log.info("tmp2==>" + tmp2);

            if (!"".equals(apiBean.getRandom_no())) {
                invoice.setRandom_no(Integer.parseInt(apiBean.getRandom_no()));
                log.info("apiBean.getRandom_no()==>" + apiBean.getRandom_no());
            }

            invoice.setBuyer_id(apiBean.getBuyer_id());
            log.info("apiBean.getBuyer_id()==>" + apiBean.getBuyer_id());

//            if (!"".equals(apiBean.getTax_type())) {
//                invoice.setTax_type(Integer.parseInt(apiBean.getTax_type()));
//            }
            invoice.setInvoicetype(1);

//            EPAY_INVOICE tempInv = epaybusinesscontroller.listInvoiceByInvoicenoLibm(apiBean.getLibm());
            if (invoice == null) {
                res = epaybusinesscontroller.insertInvoice(invoice);
                log.info("epaybusinesscontroller.insertInvoice(invoice)==>" + invoice.getLibm());
            } else {
//                invoice.setInvoiceid(invoice.getInvoiceid());
                res = epaybusinesscontroller.updateInvoice(invoice);
                log.info("epaybusinesscontroller.updateInvoice(invoice)==>" + invoice.getLibm());
            }
            log.info("converAndInsertInvoice result = " + res);
        } catch (Exception ex) {
            log.error("Invoice Update or Insert fail", ex);
        }
        return res;
    }

//
//    private String getCharacterDataFromElement(Element e) {
//        Node child = e.getFirstChild();
//        if (child instanceof CharacterData) {
//            CharacterData cd = (CharacterData) child;
//            return cd.getData();
//        }
//        return "";
//    }
//    private String doMd5(String msg) throws Exception {
//        String characterSet = "utf-8";
//        MessageDigest md5 = MessageDigest.getInstance("MD5");
//        return bin2Hex(md5.digest(msg.getBytes(characterSet)));
//    }
//    private String bin2Hex(byte byteAry[]) {
//        int bufLength = byteAry.length;
//        StringBuffer strbuf = new StringBuffer(bufLength * 2);
//
//        for (int i = 0; i < bufLength; i++) {
//            if (((int) byteAry[i] & 0xff) < 0x10) {
//                strbuf.append("0");
//            }
//            strbuf.append(Long.toString((int) byteAry[i] & 0xff, 16));
//        }
//        return strbuf.toString();
//    }
    private InvoiceReqBean encapBean(InvoiceReqBean invoiceReqBean, String... items) {

        try {
            if ("libm".equalsIgnoreCase(items[0])) {
                //new String(URLDecoder.decode(items[1],"ISO-8859-1").getBytes("ISO-8859-1"));
                invoiceReqBean.setLibm(items.length > 1 ? new String(URLDecoder.decode(items[1], "ISO-8859-1").getBytes("ISO-8859-1")) : "");
            }
            if ("type".equalsIgnoreCase(items[0])) {
                invoiceReqBean.setType(items.length > 1 ? new String(URLDecoder.decode(items[1], "ISO-8859-1").getBytes("ISO-8859-1")) : "");
            }
            if ("status".equalsIgnoreCase(items[0])) {
                invoiceReqBean.setStatus(items.length > 1 ? new String(URLDecoder.decode(items[1], "ISO-8859-1").getBytes("ISO-8859-1")) : "");
            }
            if ("invoiceNo".equalsIgnoreCase(items[0])) {
                invoiceReqBean.setInvoice_no(items.length > 1 ? new String(URLDecoder.decode(items[1], "ISO-8859-1").getBytes("ISO-8859-1")) : "");
            }
            if ("randomNo".equalsIgnoreCase(items[0])) {
                invoiceReqBean.setRandom_no(items.length > 1 ? new String(URLDecoder.decode(items[1], "ISO-8859-1").getBytes("ISO-8859-1")) : "");
            }
            if ("invoiceCreatedDate".equalsIgnoreCase(items[0])) {
                invoiceReqBean.setInvoice_created_date(items.length > 1 ? new String(URLDecoder.decode(items[1], "ISO-8859-1").getBytes("ISO-8859-1")) : "");
            }

            if ("responseMsg".equalsIgnoreCase(items[0])) {
                invoiceReqBean.setResponse_msg(items.length > 1 ? items[1] : "");
                log.info("invoiceReqBean.responseMsg==>" + invoiceReqBean.getResponse_msg());
            }

            if ("buyerId".equalsIgnoreCase(items[0])) {
                invoiceReqBean.setBuyer_id(items.length > 1 ? new String(URLDecoder.decode(items[1], "ISO-8859-1").getBytes("ISO-8859-1")) : "");
            }

            if ("responseTime".equalsIgnoreCase(items[0])) {
                invoiceReqBean.setResponse_time(items.length > 1 ? new String(URLDecoder.decode(items[1], "ISO-8859-1").getBytes("ISO-8859-1")) : "");
            }
            if ("discount_no".equalsIgnoreCase(items[0])) {
                invoiceReqBean.setDiscount_no(items.length > 1 ? new String(URLDecoder.decode(items[1], "ISO-8859-1").getBytes("ISO-8859-1")) : "");
            }
            if ("no".equalsIgnoreCase(items[0])) {
                invoiceReqBean.setNo(items.length > 1 ? new String(URLDecoder.decode(items[1], "ISO-8859-1").getBytes("ISO-8859-1")) : "");
            }
            if ("product_name".equalsIgnoreCase(items[0])) {
                invoiceReqBean.setProduct_name(items.length > 1 ? new String(URLDecoder.decode(items[1], "ISO-8859-1").getBytes("ISO-8859-1")) : "");
            }
            if ("quantity".equalsIgnoreCase(items[0])) {
                invoiceReqBean.setQuantity(items.length > 1 ? new String(URLDecoder.decode(items[1], "ISO-8859-1").getBytes("ISO-8859-1")) : "");
            }
            if ("price".equalsIgnoreCase(items[0])) {
                invoiceReqBean.setPrice(items.length > 1 ? new String(URLDecoder.decode(items[1], "ISO-8859-1").getBytes("ISO-8859-1")) : "");
            }

            if ("not_include_tax".equalsIgnoreCase(items[0])) {
                invoiceReqBean.setNot_include_tax(items.length > 1 ? new String(URLDecoder.decode(items[1], "ISO-8859-1").getBytes("ISO-8859-1")) : "");
            }
            if ("tax".equalsIgnoreCase(items[0])) {
                invoiceReqBean.setTax(items.length > 1 ? new String(URLDecoder.decode(items[1], "ISO-8859-1").getBytes("ISO-8859-1")) : "");
            }
            if ("subtotal".equalsIgnoreCase(items[0])) {
                invoiceReqBean.setSubtotal(items.length > 1 ? new String(URLDecoder.decode(items[1], "ISO-8859-1").getBytes("ISO-8859-1")) : "");
            }
//            if ("checknumber".equalsIgnoreCase(items[0])) {
//                invoiceReqBean.setChecknumber(items.length > 1 ? new String(URLDecoder.decode(items[1], "ISO-8859-1").getBytes("ISO-8859-1")) : "");
//            }
            if ("donate".equalsIgnoreCase(items[0])) {
                invoiceReqBean.setDonate(items.length > 1 ? new String(URLDecoder.decode(items[1], "ISO-8859-1").getBytes("ISO-8859-1")) : "");
            }
//            if ("uniform".equalsIgnoreCase(items[0])) {
//                invoiceReqBean.setUniform(items.length > 1 ? new String(URLDecoder.decode(items[1], "ISO-8859-1").getBytes("ISO-8859-1")) : "");
//            }            
        } catch (Exception ex) {
            log.error(ex);
        }
        return invoiceReqBean;
    }

    private String doMd5(String msg) throws Exception {
        String characterSet = "utf-8";
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        return bin2Hex(md5.digest(msg.getBytes(characterSet)));
    }

    private String bin2Hex(byte byteAry[]) {
        int bufLength = byteAry.length;
        StringBuffer strbuf = new StringBuffer(bufLength * 2);

        for (int i = 0; i < bufLength; i++) {
            if (((int) byteAry[i] & 0xff) < 0x10) {
                strbuf.append("0");
            }
            strbuf.append(Long.toString((int) byteAry[i] & 0xff, 16));
        }
        return strbuf.toString();
    }

    private InvoiceReqBean parseXMLString(String xmlRecords) {
        InvoiceReqBean aPIInvoiceRequestBean = new InvoiceReqBean();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));

            Document doc = db.parse(is);

            NodeList InvoiceRes = doc.getElementsByTagName("InvoiceRes");
            for (int i = 0; i < InvoiceRes.getLength(); i++) {
                Element element = (Element) InvoiceRes.item(i);

                NodeList nodes = element.getElementsByTagName("libm");
                Element line = (Element) nodes.item(0);
//                System.out.println("Name: " + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setLibm(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("type");
                line = (Element) nodes.item(0);
//                System.out.println("type: " + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setType(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("status");
                line = (Element) nodes.item(0);
//                System.out.println("status: " + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setStatus(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("discount_no");
                line = (Element) nodes.item(0);
//                System.out.println("discount_no: " + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setDiscount_no(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("invoice_no");
                line = (Element) nodes.item(0);
//                System.out.println("invoice_no: " + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setInvoice_no(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("invoice_created_date");
                line = (Element) nodes.item(0);
//                System.out.println("invoice_created_date: " + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setInvoice_created_date(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("random_no");
                line = (Element) nodes.item(0);
//                System.out.println("random_no: " + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setRandom_no(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("buyer_id");
                line = (Element) nodes.item(0);
//                System.out.println("buyer_id: " + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setBuyer_id(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("tax_type");
                line = (Element) nodes.item(0);
//                System.out.println("tax_type: " + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setTax_type(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("response_msg");
                line = (Element) nodes.item(0);
//                System.out.println("response_msg: " + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setResponse_msg(getCharacterDataFromElement(line));

                nodes = element.getElementsByTagName("response_time");
                line = (Element) nodes.item(0);
//                System.out.println("response_time: " + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setResponse_time(getCharacterDataFromElement(line));

//                nodes = element.getElementsByTagName("checknumber");
//                line = (Element) nodes.item(0);
////                System.out.println("response_time: " + getCharacterDataFromElement(line));
//                aPIInvoiceRequestBean.setChecknumber(getCharacterDataFromElement(line));
//                
                nodes = element.getElementsByTagName("donate");
                line = (Element) nodes.item(0);
//                System.out.println("response_time: " + getCharacterDataFromElement(line));
                aPIInvoiceRequestBean.setDonate(getCharacterDataFromElement(line));
//                
//                nodes = element.getElementsByTagName("uniform");
//                line = (Element) nodes.item(0);
////                System.out.println("response_time: " + getCharacterDataFromElement(line));
//                aPIInvoiceRequestBean.setUniform(getCharacterDataFromElement(line));                
                
                //發票明細
                List<InvoiceItemBean> invoceItem = new ArrayList<InvoiceItemBean>();
                NodeList itemList = doc.getElementsByTagName("item");
                for (int j = 0; j < itemList.getLength(); j++) {
                    InvoiceItemBean invoiceItemBean = new InvoiceItemBean();

                    element = (Element) itemList.item(j);

                    NodeList items = element.getElementsByTagName("no");
                    line = (Element) items.item(0);
//                    System.out.println("no: " + getCharacterDataFromElement(line));
                    invoiceItemBean.setNo(getCharacterDataFromElement(line));

                    items = element.getElementsByTagName("product_name");
                    line = (Element) items.item(0);
//                    System.out.println("product_name: " + getCharacterDataFromElement(line));
                    invoiceItemBean.setProduct_name(getCharacterDataFromElement(line));

                    items = element.getElementsByTagName("quantity");
                    line = (Element) items.item(0);
//                    System.out.println("quantity: " + getCharacterDataFromElement(line));
                    invoiceItemBean.setQuantity(getCharacterDataFromElement(line));

                    items = element.getElementsByTagName("price");
                    line = (Element) items.item(0);
//                    System.out.println("price: " + getCharacterDataFromElement(line));
                    invoiceItemBean.setPrice(getCharacterDataFromElement(line));

                    items = element.getElementsByTagName("not_include_tax");
                    line = (Element) items.item(0);
//                    System.out.println("not_include_tax: " + getCharacterDataFromElement(line));
                    invoiceItemBean.setNot_include_tax(getCharacterDataFromElement(line));

                    items = element.getElementsByTagName("tax");
                    line = (Element) items.item(0);
//                    System.out.println("tax: " + getCharacterDataFromElement(line));
                    invoiceItemBean.setTax(getCharacterDataFromElement(line));
                    items = element.getElementsByTagName("subtotal");
                    line = (Element) items.item(0);
//                    System.out.println("subtotal: " + getCharacterDataFromElement(line));
                    invoiceItemBean.setSubtotal(getCharacterDataFromElement(line));

                    invoceItem.add(invoiceItemBean);
                }
                //加入發票明細
                //aPIInvoiceRequestBean.setInvoiceItems(invoceItem);
            }
        } catch (IOException e) {
//            Logger.getLogger(APIInvoiceRequest.class.getName()).log(Level.SEVERE, null, e);
            Logger.getLogger("ECF").error(e);
        } catch (ParserConfigurationException e) {
//            Logger.getLogger(APIInvoiceRequest.class.getName()).log(Level.SEVERE, null, e);
            Logger.getLogger("ECF").error(e);
        } catch (SAXException e) {
//            Logger.getLogger(APIInvoiceRequest.class.getName()).log(Level.SEVEE, null, e);
            Logger.getLogger("ECF").error(e);
        }
        return aPIInvoiceRequestBean;
    }

    private String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }
//    /**
//     * 從PaymentGateway回傳的XML資訊,組成Invoice和Invoice Items Bean
//     *
//     * @param xmlRecords
//     * @return
//     */
//    private InvoiceReqBean parseXMLString(String xmlRecords) throws Exception {
//        InvoiceReqBean aPIInvoiceRequestBean = new InvoiceReqBean();
//        try {
//            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//            DocumentBuilder db = dbf.newDocumentBuilder();
//            InputSource is = new InputSource();
//            is.setCharacterStream(new StringReader(xmlRecords));
//
//            Document doc = db.parse(is);
//
//            NodeList InvoiceRes = doc.getElementsByTagName("InvoiceRes");
//            for (int i = 0; i < InvoiceRes.getLength(); i++) {
//                Element element = (Element) InvoiceRes.item(i);
//
//                NodeList nodes = element.getElementsByTagName("libm");
//                Element line = (Element) nodes.item(0);
//                aPIInvoiceRequestBean.setLibm(getCharacterDataFromElement(line));
//
//                nodes = element.getElementsByTagName("type");
//                line = (Element) nodes.item(0);
//                aPIInvoiceRequestBean.setType(getCharacterDataFromElement(line));
//
//                nodes = element.getElementsByTagName("status");
//                line = (Element) nodes.item(0);
//                aPIInvoiceRequestBean.setStatus(getCharacterDataFromElement(line));
//
//                nodes = element.getElementsByTagName("discount_no");
//                line = (Element) nodes.item(0);
//                aPIInvoiceRequestBean.setDiscount_no(getCharacterDataFromElement(line));
//
//                nodes = element.getElementsByTagName("invoice_no");
//                line = (Element) nodes.item(0);
//                aPIInvoiceRequestBean.setInvoice_no(getCharacterDataFromElement(line));
//
//                nodes = element.getElementsByTagName("invoice_created_date");
//                line = (Element) nodes.item(0);
//                aPIInvoiceRequestBean.setInvoice_created_date(getCharacterDataFromElement(line));
//
//                nodes = element.getElementsByTagName("random_no");
//                line = (Element) nodes.item(0);
//                aPIInvoiceRequestBean.setRandom_no(getCharacterDataFromElement(line));
//
//                nodes = element.getElementsByTagName("buyer_id");
//                line = (Element) nodes.item(0);
//                aPIInvoiceRequestBean.setBuyer_id(getCharacterDataFromElement(line));
//
//                nodes = element.getElementsByTagName("tax_type");
//                line = (Element) nodes.item(0);
//                aPIInvoiceRequestBean.setTax_type(getCharacterDataFromElement(line));
//
//                nodes = element.getElementsByTagName("response_msg");
//                line = (Element) nodes.item(0);
//                aPIInvoiceRequestBean.setResponse_msg(getCharacterDataFromElement(line));
//                log.info("aPIInvoiceRequestBean==>"+aPIInvoiceRequestBean.getResponse_msg());
//
//                nodes = element.getElementsByTagName("response_time");
//                line = (Element) nodes.item(0);
//                aPIInvoiceRequestBean.setResponse_time(getCharacterDataFromElement(line));
//
//                //發票明細
//                List<InvoiceItemReqBean> invoceItem = new ArrayList<InvoiceItemReqBean>();
//                NodeList itemList = doc.getElementsByTagName("item");
//                for (int j = 0; j < itemList.getLength(); j++) {
//                    InvoiceItemReqBean invoiceItemBean = new InvoiceItemReqBean();
//
//                    element = (Element) itemList.item(j);
//
//                    NodeList items = element.getElementsByTagName("no");
//                    line = (Element) items.item(0);
////                    System.out.println("no: " + getCharacterDataFromElement(line));
//                    invoiceItemBean.setNo(getCharacterDataFromElement(line));
//
//                    items = element.getElementsByTagName("product_name");
//                    line = (Element) items.item(0);
////                    System.out.println("product_name: " + getCharacterDataFromElement(line));
//                    invoiceItemBean.setProduct_name(getCharacterDataFromElement(line));
//
//                    items = element.getElementsByTagName("quantity");
//                    line = (Element) items.item(0);
////                    System.out.println("quantity: " + getCharacterDataFromElement(line));
//                    invoiceItemBean.setQuantity(getCharacterDataFromElement(line));
//
//                    items = element.getElementsByTagName("price");
//                    line = (Element) items.item(0);
////                    System.out.println("price: " + getCharacterDataFromElement(line));
//                    invoiceItemBean.setPrice(getCharacterDataFromElement(line));
//
//                    items = element.getElementsByTagName("not_include_tax");
//                    line = (Element) items.item(0);
//                    invoiceItemBean.setNot_include_tax(getCharacterDataFromElement(line));
//
//                    items = element.getElementsByTagName("tax");
//                    line = (Element) items.item(0);
//                    invoiceItemBean.setTax(getCharacterDataFromElement(line));
//                    items = element.getElementsByTagName("subtotal");
//                    line = (Element) items.item(0);
//                    invoiceItemBean.setSubtotal(getCharacterDataFromElement(line));
//
//                    invoceItem.add(invoiceItemBean);
//                }
//                //加入發票明細
//                aPIInvoiceRequestBean.setInvoiceItems(invoceItem);
//            }
////        } catch (IOException e) {
////            throw e;
////        } catch (ParserConfigurationException e) {
////            throw e;
//        } catch (Exception e) {
//            throw e;
//        }
//        return aPIInvoiceRequestBean;
//    }    
//    
//    private String parseProperty(String propertyList, String system) {
//        List<String> proCallerList = Arrays.asList(propertyList.split(","));
//        String result = proCallerList.get(0).split("\\|")[0];
//        for (String items : proCallerList) {
//            String[] item = items.split("\\|");
//            if (system.equalsIgnoreCase(item[0])) {
//                result = item[1];
//            }
//        }
//
//        return result;
//    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
