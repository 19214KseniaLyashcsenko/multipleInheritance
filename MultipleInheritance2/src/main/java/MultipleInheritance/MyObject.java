package MultipleInheritance;

import MyException.AmbiguousMethodException;
import MyException.MethodInvocationFailedException;
import MyException.NoSuchMethodException;
import MyException.ObjectInstantiationFailedException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Method;

public class MyObject {

    ArrayList<Object> directParents;
    Map<String, Object> virtualAncestors;
    private static Map<String, Object> staticVirtualAncestors;
    private static boolean isStaticVirtualAncestorsInitiated = false; // True if staticVirtualAncestors was initiated.
    private boolean isMostDerived = false; // True if the constructor was called for the most derived object.

    static private void initStaticVirtualAncestors(Parent[] parents) throws ObjectInstantiationFailedException {
        if (parents.length == 0) return;
        for (Parent i : parents) {
            if (i.isVirtual()) {
                // If it inherits virtually, first check if the object was already initiated.
                try {
                    if (!staticVirtualAncestors.containsKey(i.parent().getSimpleName())) {
                        Constructor constructor = i.parent().getDeclaredConstructor();

                        // Do not call a private constructor, only protected.
                        if (Modifier.isPrivate(constructor.getModifiers())) {
                            throw new ObjectInstantiationFailedException();
                        }
                        constructor.setAccessible(true);

                        staticVirtualAncestors.put(i.parent().getSimpleName(), constructor.newInstance());
                    }
                } catch (Exception e) {
                    throw new ObjectInstantiationFailedException();
                }
            }
            else {
                initStaticVirtualAncestors(i.parent().getAnnotationsByType(Parent.class));
            }
        }
    }

    public MyObject() throws ObjectInstantiationFailedException {
        directParents = new ArrayList<>();
        if (!isStaticVirtualAncestorsInitiated) {
            staticVirtualAncestors = new HashMap<>();
            isMostDerived = true;
            isStaticVirtualAncestorsInitiated = true;
        }
        Class<?> c = this.getClass();
        Parent[] parents = c.getAnnotationsByType(Parent.class); // This fixed it
        if (parents.length != 0) {
            try {
                // Create the virtual ancestors structure.
                initStaticVirtualAncestors(parents);

                for (Parent i : parents) {
                    if (!i.isVirtual()) {
                        // If the inheritance is non-virtual, create an instance.
                        Constructor constructor = i.parent().getDeclaredConstructor();

                        // Do not call a private constructor, only protected.
                        if (Modifier.isPrivate(constructor.getModifiers())) {
                            throw new ObjectInstantiationFailedException();
                        }
                        constructor.setAccessible(true);
                        directParents.add(constructor.newInstance());
                    } else {
                        // In this case the inheritance is virtual, so the object was already initiated previously.
                        directParents.add(staticVirtualAncestors.get(i.parent().getSimpleName()));
                    }
                }
            } catch (Exception e) {
                isStaticVirtualAncestorsInitiated = false;
                isMostDerived = false;
                throw new ObjectInstantiationFailedException();
            }
        }
        if (isMostDerived) {
            virtualAncestors = staticVirtualAncestors;
            isStaticVirtualAncestorsInitiated = false;
            isMostDerived = false;
        }
    }

    public boolean multInheritsFrom(Class<?> cls) {
        for (Object i : directParents) {
            if ( (i.getClass() == cls) ||
                    (!(i instanceof  MyObject) && cls.isAssignableFrom(i.getClass())) ||
                    ((i instanceof MyObject) && ((MyObject) i).multInheritsFrom(cls)) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Receives a class, a method name and arguments and checks if the method is defined within the class. The purpose
     * is to avoid the exception thrown from getMethod.
     */
    static boolean isMethodSelfDefined(Class<?> c, String methodName, Class<?> ...argTypes) {
        try {
            c.getMethod(methodName, argTypes);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Object definingObject(String methodName, Class<?> ...argTypes)
            throws AmbiguousMethodException, NoSuchMethodException {
        ArrayList<Object> definers = new ArrayList<>(); // Hold all of the objects defining the method.
        if (isMethodSelfDefined(this.getClass(), methodName, argTypes)) return this; // Check if the current class defines the method.

        // Check if any of the objects in directParents defines the methods
        for (Object i : directParents) {
            if (i instanceof MyObject) {
                // If the object is of type MyObject, call recursively.
                try {
                    Object o = ((MyObject) i).definingObject(methodName, argTypes);
                    if (!definers.contains(o)) {
                        definers.add(o);
                    }
                } catch (NoSuchMethodException ignored) {} // Ignore this exception, continue iterating.
            }
            else {
                // If the object is of type Object, use getMethod.
                if (isMethodSelfDefined(i.getClass(), methodName, argTypes)) definers.add(i);
            }
        }
        if (definers.size() > 1) {
            throw new AmbiguousMethodException();
        }
        else if (definers.size() == 0) {
            throw new NoSuchMethodException();
        }

        return definers.get(0);
    }

    public Object invoke(String methodName, Object... callArgs) throws
            AmbiguousMethodException, NoSuchMethodException, MethodInvocationFailedException {
        Class<?>[] args = new Class<?>[callArgs.length];
        for (int i = 0 ; i < callArgs.length ; i++) {
            args[i] = callArgs[i].getClass();
        }
        Object o = definingObject(methodName, args);
        try {
            Method m = o.getClass().getMethod(methodName, args);
            return m.invoke(o, callArgs);
        } catch (Exception e) {
            throw new MethodInvocationFailedException();
        }
    }
}