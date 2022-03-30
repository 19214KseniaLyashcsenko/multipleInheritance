package MyTests;

import MyException.ObjectInstantiationFailedException;
import MultipleInheritance.MyObject;
import MultipleInheritance.Parent;

import java.util.List;

public class MyClasses {
    private static List<String> construction_order;

    public static void setConstruction_order(List<String> construction_order) {
        MyClasses.construction_order = construction_order;
    }

    public static List<String> getConstruction_order() {
        return construction_order;
    }

    /*

                       H                   F
                   /        \              |
                  A          B             |
                   \        /              |
                        C                  E
                         \                /
                          \              /
                           \            /
                                  D
         */


    public static class H extends MyObject {
        private static int class_counter = 0;
        private int object_id;
        public H() throws ObjectInstantiationFailedException {
            construction_order.add("H");
            object_id = class_counter;
            class_counter ++;
        }

        public String print_H(){
            return "I'm from H class";
        }

        public String sameMethods(){
            return "I'm from H class";
        }
    }

    public static class F extends MyObject {
        public F() throws ObjectInstantiationFailedException {
            construction_order.add("F");
        }
    }

    @Parent(parent = H.class)
    public static class A extends MyObject{
        public A() throws ObjectInstantiationFailedException {
            construction_order.add("A");
        }

        public String sameMethodsForError(){
            return "I'm from C class";
        }
    }

    @Parent(parent = H.class)
    public static class B extends MyObject {
        public B() throws ObjectInstantiationFailedException {
            construction_order.add("B");
        }
        private String private_check() {
            return "private_check_B";
        }

        public String public_check() {
            return "public_check_B";
        }

        public String print_B(){
            return "I'm from B class";
        }

        public String sameMethods(){
            return "I'm from B class";
        }

        public String sameMethodsForError(){
            return "I'm from B class";
        }
    }

    @Parent(parent = A.class)
    @Parent(parent = B.class)
    public static class C extends MyObject {
        public C() throws ObjectInstantiationFailedException {
            construction_order.add("C");
        }
        private String private_check() {
            return "private_check_C";
        }

        public String print_C(){
            return "I'm from C class";
        }

        public String public_check() {
            return "public_check_C";
        }

        public String local_parameter_check(String a, Integer b)
        {
            return "This is a constant string.";
        }

        public String sameMethods(){
            return "I'm from C class";
        }
    }

    @Parent(parent = F.class)
    public static class E extends MyObject {
        public E() throws ObjectInstantiationFailedException {
            construction_order.add("E");
        }
    }

    @Parent(parent = C.class)
    @Parent(parent = E.class)
    public static class D extends MyObject {
        public D() throws ObjectInstantiationFailedException {
            construction_order.add("D");
        }

        public String local_parameter_check(Integer a, Integer b)
        {
            return "This is a constant string.";
        }

        public String print_D(){
            return "I'm from D class";
        }

        public String sameMethods(){
            return "I'm from D class";
        }
    }
}
