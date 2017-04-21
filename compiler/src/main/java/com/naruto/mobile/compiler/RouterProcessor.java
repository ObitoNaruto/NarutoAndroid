package com.naruto.mobile.compiler;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import com.google.auto.service.AutoService;
import com.naruto.mobile.RouterMap;
import com.naruto.mobile.compiler.exception.TargetErrorException;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

@AutoService(Processor.class)
public class RouterProcessor extends AbstractProcessor {
    private Messager mMessager;
    private Filer mFiler;
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessager = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
    }

    /**
     * 定义我们针对生成的注解类
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(RouterMap.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(RouterMap.class);

        try {
            TypeSpec type = getRouterTableInitializer(elements);
            if(type != null) {
                JavaFile.builder("com.naruto.mobile.base.Router.andRouter.router", type).build().writeTo(mFiler);
            }
        } catch (FilerException e){
            e.printStackTrace();
        } catch (Exception e) {
            error(e.getMessage());
        }

        return true;
    }

    private TypeSpec getRouterTableInitializer(Set<? extends Element> elements) throws ClassNotFoundException,
            TargetErrorException {
        if(elements == null || elements.size() == 0){
            return null;
        }
        //Activity
        TypeElement activityType = elementUtils.getTypeElement("android.app.Activity");

        //Map<String, Class<? extends Activity>>
        ParameterizedTypeName mapTypeName = ParameterizedTypeName
                .get(ClassName.get(Map.class),
                        ClassName.get(String.class),
                        ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(ClassName.get(activityType)))
                );
        //生成参数：Map<String, Class<? extends Activity>> router
        ParameterSpec mapParameterSpec = ParameterSpec.builder(mapTypeName, "router")
                .build();
        //生成方法：public void initRouterTable(Map<String, Class<? extends Activity>> router)
        MethodSpec.Builder routerInitBuilder = MethodSpec.methodBuilder("initRouterTable")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(mapParameterSpec)
                ;
        for(Element element : elements){
            if(element.getKind() != ElementKind.CLASS){
                throw new TargetErrorException();
            }
            RouterMap router = element.getAnnotation(RouterMap.class);//获取所有注解的类
            String [] routerUrls = router.value();
            if(routerUrls != null){
                for(String routerUrl : routerUrls){//执行router的put方法
                    routerInitBuilder.addStatement("router.put($S, $T.class)", routerUrl, ClassName.get((TypeElement) element));
                }
            }
        }
        MethodSpec routerInitMethod = routerInitBuilder.build();
        //IActivityRouteTableInitializer
        TypeElement routerInitializerType = elementUtils.getTypeElement("com.naruto.mobile.base.Router.andRouter.router.IActivityRouteTableInitializer");
        //返回类IActivityRouteTableInitializer
        return TypeSpec.classBuilder("AnnotatedRouterTableInitializer")//生成的类名
                .addSuperinterface(ClassName.get(routerInitializerType))//该类的父接口类型
                .addModifiers(Modifier.PUBLIC)//修饰符是public类型
                .addMethod(routerInitMethod)//添加类的方法
                .build();//构建
    }


    private void error(String error){
        mMessager.printMessage(Diagnostic.Kind.ERROR, error);
    }


}
