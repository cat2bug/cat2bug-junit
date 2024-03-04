package com.cat2bug.junit.util;

import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.ByteMemberValue;
import javassist.bytecode.annotation.CharMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.DoubleMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.FloatMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.LongMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.ShortMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public class JavassistUtils {
	/**
	 * 对象转MemberObj
	 * 
	 * @param obj	原始数据
	 * @param cp	ConstPool对象
	 * @return	值
	 * @throws Exception  异常
	 */
	public static MemberValue obj2MemberObj(Object obj, ConstPool cp) throws Exception {
		if (obj instanceof String) {
			return new StringMemberValue((String) obj, cp);
		} else if (obj instanceof Integer) {
			IntegerMemberValue integerMemberValue = new IntegerMemberValue(cp);
			integerMemberValue.setValue((Integer) obj);
			return integerMemberValue; 
		} else if (obj instanceof Boolean) {
			BooleanMemberValue booleanMemberValue = new BooleanMemberValue( cp);
			booleanMemberValue.setValue((Boolean) obj);
			return booleanMemberValue;
		} else if (obj instanceof Byte) {
			ByteMemberValue byteMemberValue = new ByteMemberValue(cp);
			byteMemberValue.setValue((Byte) obj);
			return byteMemberValue;
		} else if (obj instanceof Character) {
			CharMemberValue charMemberValue = new CharMemberValue(cp);
			charMemberValue.setValue((Character) obj);
			return charMemberValue;
		} else if (obj instanceof Class) {
			String className = ((Class<?>) obj).getName();
			return new ClassMemberValue(className, cp);
		} else if (obj instanceof Double) {
			DoubleMemberValue doubleMemberValue = new DoubleMemberValue(cp);
			doubleMemberValue.setValue((Double) obj);
			return doubleMemberValue;
		} else if (obj instanceof Enum) {
			EnumMemberValue emv = new EnumMemberValue(cp);
			emv.setType(obj.getClass().getName());
			emv.setValue(obj.toString());
			return emv;
		} else if (obj instanceof Float) {
			FloatMemberValue floatMemberValue = new FloatMemberValue(cp);
			floatMemberValue.setValue((Float) obj);
			return floatMemberValue;
		} else if (obj instanceof Long) {
			LongMemberValue longMemberValue = new LongMemberValue(cp);
			longMemberValue.setValue((Long) obj);
			return longMemberValue;
		} else if (obj instanceof Short) {
			ShortMemberValue shortMemberValue = new ShortMemberValue(cp);
			shortMemberValue.setValue((Short) obj);
			return shortMemberValue;
		} else {
			return null;
		}
	}
}
