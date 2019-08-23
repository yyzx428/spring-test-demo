package com.example.testdemo.controller.parameter.assembly;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Parameterized;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.parameterized.TestWithParameters;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class SpringRunnerWithParameters extends SpringJUnit4ClassRunner {

    private final Object[] parameters;

    private final String name;

    public SpringRunnerWithParameters(TestWithParameters test) throws InitializationError {
        super(test.getTestClass().getJavaClass());
        parameters = test.getParameters().toArray(
                new Object[test.getParameters().size()]);
        name = test.getName();
    }

    @Override
    protected String getName() {
        return fieldsAreAnnotated() || !hasNoArgumentConstructors() ? this.name : super.getName();
    }

    private boolean hasNoArgumentConstructors() {
        return getTestClass().getJavaClass().getConstructors().length == 1
                && (getTestClass().getOnlyConstructor().getParameterTypes().length == 0);
    }

    @Override
    public Object createTest() throws Exception {
        Object testInstance;
        if (fieldsAreAnnotated()) {
            testInstance = createTestUsingFieldInjection();
        } else if (hasNoArgumentConstructors()) {
            testInstance = getTestClass().getOnlyConstructor().newInstance();
        } else {
            testInstance = createTestUsingConstructorInjection();
        }
        getTestContextManager().prepareTestInstance(testInstance);
        return testInstance;
    }

    private Object createTestUsingConstructorInjection() throws Exception {
        return getTestClass().getOnlyConstructor().newInstance(parameters);
    }

    private Object createTestUsingFieldInjection() throws Exception {
        List<FrameworkField> annotatedFieldsByParameter = getAnnotatedFieldsByParameter();
        if (annotatedFieldsByParameter.size() != parameters.length) {
            throw new Exception(
                    "Wrong number of parameters and @Parameter fields."
                            + " @Parameter fields counted: "
                            + annotatedFieldsByParameter.size()
                            + ", available parameters: " + parameters.length
                            + ".");
        }
        Object testClassInstance = getTestClass().getJavaClass().newInstance();
        for (FrameworkField each : annotatedFieldsByParameter) {
            Field field = each.getField();
            Parameterized.Parameter annotation = field.getAnnotation(Parameterized.Parameter.class);
            int index = annotation.value();
            try {
                field.set(testClassInstance, parameters[index]);
            } catch (IllegalArgumentException iare) {
                throw new Exception(getTestClass().getName()
                        + ": Trying to set " + field.getName()
                        + " with the value " + parameters[index]
                        + " that is not the right type ("
                        + parameters[index].getClass().getSimpleName()
                        + " instead of " + field.getType().getSimpleName()
                        + ").", iare);
            }
        }
        return testClassInstance;
    }

    @Override
    protected void validateFields(List<Throwable> errors) {
        super.validateFields(errors);
        if (fieldsAreAnnotated()) {
            List<FrameworkField> annotatedFieldsByParameter = getAnnotatedFieldsByParameter();
            int[] usedIndices = new int[annotatedFieldsByParameter.size()];
            for (FrameworkField each : annotatedFieldsByParameter) {
                int index = each.getField().getAnnotation(Parameterized.Parameter.class)
                        .value();
                if (index < 0 || index > annotatedFieldsByParameter.size() - 1) {
                    errors.add(new Exception("Invalid @Parameter value: "
                            + index + ". @Parameter fields counted: "
                            + annotatedFieldsByParameter.size()
                            + ". Please use an index between 0 and "
                            + (annotatedFieldsByParameter.size() - 1) + "."));
                } else {
                    usedIndices[index]++;
                }
            }
            for (int index = 0; index < usedIndices.length; index++) {
                int numberOfUse = usedIndices[index];
                if (numberOfUse == 0) {
                    errors.add(new Exception("@Parameter(" + index
                            + ") is never used."));
                } else if (numberOfUse > 1) {
                    errors.add(new Exception("@Parameter(" + index
                            + ") is used more than once (" + numberOfUse + ")."));
                }
            }
        }
    }

    @Override
    protected Statement classBlock(RunNotifier notifier) {
        return childrenInvoker(notifier);
    }

    private List<FrameworkField> getAnnotatedFieldsByParameter() {
        return getTestClass().getAnnotatedFields(Parameterized.Parameter.class);
    }

    private boolean fieldsAreAnnotated() {
        return !getAnnotatedFieldsByParameter().isEmpty();
    }

    private boolean hasParameters() {
        return !getTestClass().getAnnotatedFields(Parameterized.Parameters.class).isEmpty();
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        if (fieldsAreAnnotated() || hasParameters()) {
            return super.computeTestMethods().stream()
                    // 当使用参数化时过滤父类的测试
                    .filter(frameworkMethod -> getTestClass().getJavaClass().isAssignableFrom(frameworkMethod.getDeclaringClass()))
                    .collect(Collectors.toList());
        }
        return super.computeTestMethods();
    }
}
