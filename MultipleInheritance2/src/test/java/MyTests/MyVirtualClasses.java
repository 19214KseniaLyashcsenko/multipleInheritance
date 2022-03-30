package MyTests;

import MyException.AmbiguousMethodException;
import MyException.MethodInvocationFailedException;
import MyException.NoSuchMethodException;
import MyException.ObjectInstantiationFailedException;
import MultipleInheritance.MyObject;
import MultipleInheritance.Parent;

import java.util.List;

public class MyVirtualClasses {
    private static List<String> construction_order;

    public static void setConstruction_order(List<String> construction_ord) {
        construction_order = construction_ord;
    }

    public static List<String> getConstruction_order() {
        return construction_order;
    }
    /*

                                              E
                                              |
                                              D
                                         //      \\
                                    F    B        C
                                    \     \      /
                                      ----    A
     */

    public static class E extends MyObject {
        public E() throws ObjectInstantiationFailedException {
            construction_order.add("E");
        }

        public String older_public_method() {
            return "opm_E";
        }
    }

    @Parent(parent = E.class)
    public static class D extends MyObject {
        public int identifier;
        public D() throws ObjectInstantiationFailedException {
            construction_order.add("D");
            identifier = 1;
        }

        public int getIdentifier() {
            return this.identifier;
        }

        public void incrementIdentifier() {
            identifier += 1;
        }

        public String public_to_inherit() {
            return "pti_D";
        }
    }

    @Parent(parent = D.class, isVirtual = true)
    public static class C extends MyObject {
        public C() throws ObjectInstantiationFailedException {
            construction_order.add("C");
        }

        public int no_ambiguity_C() throws NoSuchMethodException, MethodInvocationFailedException, AmbiguousMethodException {
            return (Integer)this.invoke("getIdentifier");
        }

        public String almost_accidental_ambiguate() {
            return "aaa_C";
        }
    }

    @Parent(parent = D.class, isVirtual = true)
    public static class B extends MyObject {
        public B() throws ObjectInstantiationFailedException {
            construction_order.add("B");
        }

        public void no_ambiguity_B() throws NoSuchMethodException {
            try{
                this.invoke("incrementIdentifier");
            } catch (NoSuchMethodException | AmbiguousMethodException | MethodInvocationFailedException e) {
                throw new NoSuchMethodException();
            }
        }

        public String almost_accidental_ambiguate() {
            return "aaa_B";
        }
    }

    public static class F extends MyObject {
        public F() throws ObjectInstantiationFailedException {
            construction_order.add("F");
        }
    }

    @Parent(parent = F.class)
    @Parent(parent = B.class)
    @Parent(parent = C.class)
    public static class A extends MyObject {
        public A() throws ObjectInstantiationFailedException {
            construction_order.add("A");
        }

        public String root_function() {
            return "This is SD_A's function";
        }
    }

}
