package com.api.framework.listeners;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Annotation transformer that automatically attaches {@link RetryAnalyzer}
 * to every test method — no need to add retryAnalyzer= on each @Test.
 *
 * Register in testng.xml:
 *   <listener class-name="com.api.framework.listeners.RetryListener"/>
 */
public class RetryListener implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation,
                          Class testClass,
                          Constructor testConstructor,
                          Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
}
