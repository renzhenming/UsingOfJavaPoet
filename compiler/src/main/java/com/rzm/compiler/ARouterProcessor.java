package com.rzm.compiler;

import com.google.auto.service.AutoService;
import com.rzm.annotations.ARouter;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
//@SupportedSourceVersion(SourceVersion.RELEASE_8)
//@SupportedAnnotationTypes("com.rzm.annotations.MyAnnotation")
//@SupportedOptions("student")
public class ARouterProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Filer filer;
    private Messager messager;
    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        typeUtils = processingEnvironment.getTypeUtils();
        messager.printMessage(Diagnostic.Kind.NOTE, " -------------------  MyProcessor init");
    }

    @Override
    public Set<String> getSupportedOptions() {
        HashSet<String> hashSet = new HashSet<>();
        hashSet.add("hello");
        return hashSet;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> hashSet = new HashSet<>();
        hashSet.add(ARouter.class.getName());
        return hashSet;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        messager.printMessage(Diagnostic.Kind.NOTE, " MyProcessor process");

        if (set.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "MyProcessor process set is empty");
            return false;
        }

        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ARouter.class);
        for (Element element : elements) {
            Name simpleName = element.getSimpleName();
            messager.printMessage(Diagnostic.Kind.NOTE, "MyProcessor process element = " + simpleName);


            String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
            String className = simpleName.toString();
            messager.printMessage(Diagnostic.Kind.NOTE, "被@ARetuer注解的类有 packageName = " + packageName + " className = " + className);

            String finalClassName = className + "$$$$$$$$$ARouter";
            /**
             模板：
             public class MainActivity3$$$$$$$$$ARouter {

             public static Class findTargetClass(String path) {
             return path.equals("/app/MainActivity3") ? MainActivity3.class : null;
             }

             }
             */
            ARouter annotation = element.getAnnotation(ARouter.class);
            MethodSpec findTargetClass = MethodSpec.methodBuilder("findTargetClass")
                    .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                    .addParameter(String.class,"path")
                    .addStatement("return path.equals($S) ? $T.class : null",annotation.path(), ClassName.get((TypeElement) element))
                    .returns(Class.class)
                    .build();

            TypeSpec MainActivity3$$$$$$$$$ARouter = TypeSpec.classBuilder(finalClassName)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(findTargetClass)
                    .build();

            JavaFile javaFile = JavaFile.builder(packageName, MainActivity3$$$$$$$$$ARouter).build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }


//            MethodSpec methodSpec = MethodSpec.methodBuilder("main")
//                    .addModifiers(Modifier.PUBLIC,Modifier.STATIC,Modifier.FINAL)
//                    .addParameter(String[].class,"args")
//                    .addStatement("$T.out.print($S)",System.class,"main 方法执行了")
//                    .returns(TypeName.VOID)
//                    .build();
//
//            TypeSpec typeSpec = TypeSpec.classBuilder("HelloWorld")
//                    .addModifiers(Modifier.PUBLIC)
//                    .addMethod(methodSpec)
//                    .build();
//
//            JavaFile javaFile = JavaFile.builder("com.renzhenming.app",typeSpec).build();
//
//            try {
//                javaFile.writeTo(filer);
//            } catch (IOException e) {
//                e.printStackTrace();
//                messager.printMessage(Diagnostic.Kind.NOTE, "MyProcessor process writeTo fail");
//            }
        }


        return true;
    }
}
