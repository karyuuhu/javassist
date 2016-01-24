package com.hjl.study.javassist.aop;

import javassist.*;

public class TranslateConvert
{
    public static void main(String[] args) {
    	//com.hjl.study.javassist.aop.Bean setA com.hjl.study.javassist.aop.BeanTest
        if (args.length >= 3) {
            try {
                
                // set up class loader with translator
                ConverterTranslator xlat = new ConverterTranslator();
                ClassPool pool = ClassPool.getDefault();
                CodeConverter convert = new CodeConverter();
                CtMethod smeth = pool.get(args[0]).getDeclaredMethod(args[1]);
                CtMethod pmeth = pool.get("com.hjl.study.javassist.aop.TranslateConvert").
                    getDeclaredMethod("reportSet");
                convert.insertBeforeMethod(smeth, pmeth);
                xlat.setConverter(convert);
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
            System.out.println("Usage: TranslateConvert " +
                "clas-name set-name main-class args...");
        }
    }
    
    public static void reportSet(Bean target, String value) {
        System.out.println("Call to set value " + value);
    }
    
    public static class ConverterTranslator implements Translator
    {
        private CodeConverter m_converter;
        
        private void setConverter(CodeConverter convert) {
            m_converter = convert;
        }
        
        public void start(ClassPool pool) {}
        
        public void onLoad(ClassPool pool, String cname)
            throws NotFoundException, CannotCompileException {
            CtClass clas = pool.get(cname);
            clas.instrument(m_converter);
        }
    }
}