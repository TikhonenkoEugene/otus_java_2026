package ru.otus.appcontainer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

@SuppressWarnings("squid:S1068")
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);

        Object configInstance;
        try {
            configInstance = configClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to instantiate config class " + configClass.getName(), e);
        }

        List<Method> componentMethods = Arrays.stream(configClass.getMethods())
                .filter(method -> method.isAnnotationPresent(AppComponent.class))
                .sorted(Comparator.comparingInt(
                        method -> method.getAnnotation(AppComponent.class).order()))
                .toList();

        checkUniqueOrders(configClass, componentMethods);

        for (Method method : componentMethods) {
            String name = method.getAnnotation(AppComponent.class).name();
            if (appComponentsByName.containsKey(name)) {
                throw new IllegalStateException(String.format("Component with name '%s' is already defined", name));
            }

            Object[] args = Arrays.stream(method.getParameterTypes())
                    .map(this::getAppComponent)
                    .toArray();

            Object component;
            try {
                component = method.invoke(configInstance, args);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Failed to invoke component method " + method.getName(), e);
            }

            appComponents.add(component);
            appComponentsByName.put(name, component);
        }
    }

    private void checkUniqueOrders(Class<?> configClass, List<Method> componentMethods) {
        Map<Integer, String> methodNameByOrder = new HashMap<>();
        for (Method method : componentMethods) {
            int order = method.getAnnotation(AppComponent.class).order();
            String previousMethodName = methodNameByOrder.put(order, method.getName());
            if (previousMethodName != null) {
                throw new IllegalStateException(String.format(
                        "Config class '%s' has more than one @AppComponent with order %d: '%s' and '%s'",
                        configClass.getName(), order, previousMethodName, method.getName()));
            }
        }
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getAppComponent(Class<C> componentClass) {
        List<Object> matched =
                appComponents.stream().filter(componentClass::isInstance).toList();

        if (matched.isEmpty()) {
            throw new NoSuchElementException(
                    String.format("No component of type '%s' found", componentClass.getName()));
        }
        if (matched.size() > 1) {
            throw new IllegalStateException(
                    String.format("More than one component of type '%s' found", componentClass.getName()));
        }
        return (C) matched.get(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getAppComponent(String componentName) {
        Object component = appComponentsByName.get(componentName);
        if (component == null) {
            throw new NoSuchElementException(String.format("No component with name '%s' found", componentName));
        }
        return (C) component;
    }
}
