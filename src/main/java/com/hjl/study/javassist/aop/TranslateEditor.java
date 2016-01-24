package com.hjl.study.javassist.aop;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

public class TranslateEditor
{
    public static void main(String[] args) {
    	//com.hjl.study.javassist.aop.Bean m_a com.hjl.study.javassist.aop.BeanTest
        if (args.length >= 3) {
            try {
                
                // set up class loader with translator
                EditorTranslator xlat =
                    new EditorTranslator(args[0], new FieldSetEditor(args[1]));
                ClassPool pool = ClassPool.getDefault();
                Loader loader = new Loader(pool);
                loader.addTranslator(pool, xlat);
                
                // invoke the "main" method of the application class
                String[] pargs = new String[args.length-3];
                System.arraycopy(args, 3, pargs, 0, pargs.length);
                loader.run(args[2], pargs);
                
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
            
        } else {
            System.out.println("Usage: TranslateEditor clas-name field-name main-class args...");
        }
    }
    
    public static String reverse(String value) {
        int length = value.length();
        StringBuffer buff = new StringBuffer(length);
        for (int i = length-1; i >= 0; i--) {
            buff.append(value.charAt(i));
        }
        System.out.println("TranslateEditor.reverse returning " + buff);
        return buff.toString();
    }
    
    public static class EditorTranslator implements Translator
    {
        private String m_className;
        private ExprEditor m_editor;
        
        private EditorTranslator(String cname, ExprEditor editor) {
            m_className = cname;
            m_editor = editor;
        }
        
        public void start(ClassPool pool) {}
        
        public void onLoad(ClassPool pool, String cname)
            throws NotFoundException, CannotCompileException {
            if (cname.equals(m_className)) {
                CtClass clas = pool.get(cname);
                clas.instrument(m_editor);
            }
        }
    }
    
    public static class FieldSetEditor extends ExprEditor
    {
        private String m_fieldName;
        
        private FieldSetEditor(String fname) {
            m_fieldName = fname;
        }
        
        public void edit(FieldAccess arg) throws CannotCompileException {
            if (arg.getFieldName().equals(m_fieldName) && arg.isWriter()) {
                StringBuffer code = new StringBuffer();
                code.append("$0.");
                code.append(arg.getFieldName());
                code.append("=com.hjl.study.javassist.aop.TranslateEditor.reverse($1);");
                arg.replace(code.toString());
            }
        }
    }
}