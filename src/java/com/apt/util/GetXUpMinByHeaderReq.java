package com.apt.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetXUpMinByHeaderReq extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet GetXUpMinByHeaderReq</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Get X-Up-Min header.</h1>");
            //
            Enumeration enuma = request.getHeaderNames();
            while (enuma.hasMoreElements()) {
                String header_name = (String) enuma.nextElement();
                String header_value = request.getHeader(header_name);
                String head_info = String.format("header_name<=>[%s], header_value<=>[%s]", header_name, header_value);
                out.println(head_info+"\n");
                // X-Up-Min
                if (header_name != null && header_name.toLowerCase().equals("x-up-min")) {
                    out.println("-------------------------------------------------------------------");
                    out.println("[" + header_name + "] is " + "[" + header_value + "]");
                    out.println("-------------------------------------------------------------------");
                }
            }
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
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
     * Handles the HTTP
     * <code>POST</code> method.
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
