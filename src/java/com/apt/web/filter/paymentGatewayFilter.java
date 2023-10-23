package com.apt.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
//import org.apache.log4j.Logger;
//import javax.servlet.http.HttpServletRequestWrapper;
//import javax.servlet.annotation.WebFilter;


/**
 *
 * @author Booker Hsu
 */
//@WebFilter(urlPatterns = {"/api/*"}, description = "PaymentGateway Filter")
public class paymentGatewayFilter implements Filter {
    
    private FilterConfig filterConfig;
    private String urlPayemt = null;
    private String urlECF = null;

    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        this.urlPayemt = filterConfig.getInitParameter("urlPayemt");
        this.urlECF = filterConfig.getInitParameter("urECF");
//        System.out.println("paymentGatewayFilter::init() urlPayemt: " + urlPayemt);
//        System.out.println("paymentGatewayFilter::init() urlInvoice: " + urlInvoice);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

//        System.out.print("paymentGatewayFilter::doFilter()::");

        HttpServletRequest req = (HttpServletRequest) request;
        String requestURI = req.getRequestURI();

        System.out.println("paymentGatewayFilter:getRequestURI():" + requestURI);
       
        if (requestURI.startsWith("/EPAY/api/payment/")) {
            String callId = requestURI.substring(requestURI.lastIndexOf("/") + 1);
            req.getRequestDispatcher(urlPayemt + "?" + callId).forward(request, response);
        } else if (requestURI.startsWith("/EPAY/api/ecfreq/")) {
            System.out.println("paymentGatewayFilter:doFilter():" + urlECF);
            req.getRequestDispatcher(urlECF).forward(request, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        System.out.println("paymentGatewayFilter::destroy()");            
    }
}
