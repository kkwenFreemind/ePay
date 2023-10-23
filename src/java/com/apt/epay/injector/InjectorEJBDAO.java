/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.epay.injector;

import javax.naming.NamingException;

/**
 *
 * @author Administrator
 */
public interface InjectorEJBDAO {

    public Object getEJBDAO() throws NamingException;
}
