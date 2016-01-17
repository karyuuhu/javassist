package com.hjl.study.javassist.extend;

import java.io.IOException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

public class Main {
	public static void main(String[] args) {
		ClassPool pool = ClassPool.getDefault();
		try {
//			pool.insertClassPath(".\\target");// 设置根路径。（这里设置的根路径显然没被writeFile使用）

			CtClass cc = pool
					.makeClass("com.hjl.study.javassist.extend.EditableChanged");// 模拟Hibernate代理模式，我们创建一个新类
			cc.setSuperclass(pool
					.get("com.hjl.study.javassist.extend.Editable"));// 设置其父类
			CtMethod cm = CtNewMethod
					.make("public void showInfo(){super.showInfo();System.out.println(\"CustomInsert HJL Test!\");}",
							cc);// 追加一个方法，注意它覆盖了父类中的方法。
			cc.addMethod(cm);
			cc.writeFile("./target/classes");// 这里比较重要，空参的结果就是没有保存到eclipse字节码根路径里。
		} catch (NotFoundException | CannotCompileException | IOException e) {
			e.printStackTrace();
		}
		try {
			Class<?> cl = Class
					.forName("com.hjl.study.javassist.extend.EditableChanged");// 加载我们的新类
			Editable ed = (Editable) cl.newInstance();// 由于其继承与Editable类，这里和Hibernate里的load道理一样。
			ed.showInfo();// 调用方法。
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
