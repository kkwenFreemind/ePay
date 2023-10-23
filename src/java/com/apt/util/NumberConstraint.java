package com.apt.util;

//import java.util.Date;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;
//import org.zkoss.zul.Datebox;

public class NumberConstraint implements Constraint {

	private int minValue;

	public NumberConstraint(int min) {
		this.minValue = min;
	}

	@Override
	public void validate(Component comp, Object value) throws WrongValueException {
           if (value == null || Integer.parseInt(value.toString()) < minValue) //assume used with intbox
            throw new WrongValueException(comp, "交易金額不能小於"+minValue);
    }
}
