/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.injector.impl;

import com.apt.epay.injector.InjectorEJBDAO;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Administrator
 */
public class InjectorEJBDAOImpl implements InjectorEJBDAO {

    private String lookupKeys;

    @Override
    public Object getEJBDAO() throws NamingException {

        InitialContext ctx = new InitialContext();

        return ctx.lookup(lookupKeys);
    }

    public void setLookupKeys(String lookupKeys) {
        this.lookupKeys = lookupKeys;
    }

    public String getLookupKeys() {
        return this.lookupKeys;
    }
}
