package com.example.testdemo.controller.parameter.assembly;

import org.assertj.core.util.Lists;
import org.junit.runner.Runner;
import org.junit.runners.Parameterized;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;
import org.junit.runners.parameterized.BlockJUnit4ClassRunnerWithParametersFactory;
import org.junit.runners.parameterized.ParametersRunnerFactory;
import org.junit.runners.parameterized.TestWithParameters;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CompatibleParameterized extends Suite {
    private static final ParametersRunnerFactory DEFAULT_FACTORY = new BlockJUnit4ClassRunnerWithParametersFactory();

    private static final List<Runner> NO_RUNNERS = Collections.<Runner>emptyList();

    private final List<Runner> runners;

    /**
     * Only called reflectively. Do not use programmatically.
     */
    public CompatibleParameterized(Class<?> klass) throws Throwable {
        super(klass, NO_RUNNERS);
        ParametersRunnerFactory runnerFactory = getParametersRunnerFactory(
                klass);
        Parameterized.Parameters parameters = null;
        List<Object> lists = Lists.newArrayList();
        lists.add(new Object());
        Iterable<Object> objects = lists;
        try {
            parameters = getParametersMethod().getAnnotation(
                    Parameterized.Parameters.class);
            objects = allParameters();
        } catch (Exception e) {

        }
        runners = Collections.unmodifiableList(createRunnersForParameters(
                objects, parameters == null ? "" : parameters.name(), runnerFactory));
    }

    private static TestWithParameters createTestWithParameters(
            TestClass testClass, String pattern, int index, Object[] parameters) {
        String finalPattern = pattern.replaceAll("\\{index\\}",
                Integer.toString(index));
        String name = MessageFormat.format(finalPattern, parameters);
        return new TestWithParameters("[" + name + "]", testClass,
                Arrays.asList(parameters));
    }

    private ParametersRunnerFactory getParametersRunnerFactory(Class<?> klass)
            throws InstantiationException, IllegalAccessException {
        Parameterized.UseParametersRunnerFactory annotation = klass
                .getAnnotation(Parameterized.UseParametersRunnerFactory.class);
        if (annotation == null) {
            return DEFAULT_FACTORY;
        } else {
            Class<? extends ParametersRunnerFactory> factoryClass = annotation
                    .value();
            return factoryClass.newInstance();
        }
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

    private TestWithParameters createTestWithNotNormalizedParameters(
            String pattern, int index, Object parametersOrSingleParameter) {
        Object[] parameters = (parametersOrSingleParameter instanceof Object[]) ? (Object[]) parametersOrSingleParameter
                : new Object[]{parametersOrSingleParameter};
        return createTestWithParameters(getTestClass(), pattern, index,
                parameters);
    }

    @SuppressWarnings("unchecked")
    private Iterable<Object> allParameters() throws Throwable {
        Object parameters = getParametersMethod().invokeExplosively(null);
        if (parameters instanceof Iterable) {
            return (Iterable<Object>) parameters;
        } else if (parameters instanceof Object[]) {
            return Arrays.asList((Object[]) parameters);
        } else {
            throw parametersMethodReturnedWrongType();
        }
    }

    private FrameworkMethod getParametersMethod() throws Exception {
        List<FrameworkMethod> methods = getTestClass().getAnnotatedMethods(
                Parameterized.Parameters.class);
        for (FrameworkMethod each : methods) {
            if (each.isStatic() && each.isPublic()) {
                return each;
            }
        }

        throw new Exception("No public static parameters method on class "
                + getTestClass().getName());
    }

    private List<Runner> createRunnersForParameters(
            Iterable<Object> allParameters, String namePattern,
            ParametersRunnerFactory runnerFactory)
            throws InitializationError,
            Exception {
        try {
            List<TestWithParameters> tests = createTestsForParameters(
                    allParameters, namePattern);
            List<Runner> runners = new ArrayList<Runner>();
            for (TestWithParameters test : tests) {
                runners.add(runnerFactory
                        .createRunnerForTestWithParameters(test));
            }
            return runners;
        } catch (ClassCastException e) {
            throw parametersMethodReturnedWrongType();
        }
    }

    private List<TestWithParameters> createTestsForParameters(
            Iterable<Object> allParameters, String namePattern)
            throws Exception {
        int i = 0;
        List<TestWithParameters> children = new ArrayList<TestWithParameters>();
        for (Object parametersOfSingleTest : allParameters) {
            children.add(createTestWithNotNormalizedParameters(namePattern,
                    i++, parametersOfSingleTest));
        }
        return children;
    }

    private Exception parametersMethodReturnedWrongType() throws Exception {
        String className = getTestClass().getName();
        String methodName = getParametersMethod().getName();
        String message = MessageFormat.format(
                "{0}.{1}() must return an Iterable of arrays.",
                className, methodName);
        return new Exception(message);
    }
}
