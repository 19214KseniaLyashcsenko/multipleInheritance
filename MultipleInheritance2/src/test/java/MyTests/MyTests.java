package MyTests;

import MyException.AmbiguousMethodException;
import MyException.MethodInvocationFailedException;
import MyException.ObjectInstantiationFailedException;
import MyException.*;

import MyException.NoSuchMethodException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;

public class MyTests {
    private static MyClasses.D multiple_structure_root;

    @BeforeClass
    public static void initialize_fields() throws ObjectInstantiationFailedException {
        MyClasses.setConstruction_order(new ArrayList<>());
        multiple_structure_root = new MyClasses.D();
    }

    public void setup_statics() {
        MyClasses.setConstruction_order(new ArrayList<>());
    }


    @Test
    public void testMultipleStructureClassInheritance1() {
        Assert.assertTrue("class D is inherited from H",
                multiple_structure_root.multInheritsFrom(MyClasses.H.class));
    }


    @Test
    public void testMultipleStructureClassInheritance2() throws ObjectInstantiationFailedException {
        Assert.assertFalse("class C is not inherited from F",
                new MyClasses.C().multInheritsFrom(MyClasses.F.class));
    }

    @Test
    public void testMultipleStructureInitOrder() throws ObjectInstantiationFailedException {
        setup_statics();
        List<String> expected_order = new ArrayList<>(Arrays.asList("H", "A", "H", "B", "C", "F", "E", "D"));
        MyClasses.D d_obj = new MyClasses.D();
        Assert.assertEquals(expected_order, MyClasses.getConstruction_order());
    }

    @Test
    public void testMultipleStructureDefiningObject1() throws ObjectInstantiationFailedException {
        MyClasses.C c_obj = new MyClasses.C();

        try {
            Assert.assertEquals(MyClasses.C.class,  c_obj.definingObject("public_check").getClass());
        } catch (NoSuchMethodException | AmbiguousMethodException e) {
            fail("SimpleStructure: Not Correctly override methods");
        }
    }

    @Test(expected = NoSuchMethodException.class)
    public void testMultipleStructureDefiningObject2() throws ObjectInstantiationFailedException, NoSuchMethodException {
        MyClasses.C c_obj = new MyClasses.C();

        try {
            Assert.assertEquals("SimpleStructure: Do not inherit private methods", MyClasses.C.class,  c_obj.definingObject("private_check").getClass());
        } catch (AmbiguousMethodException e) {
            fail("SimpleStructure: Do not find private methods");
        }
    }

    @Test
    public void testMultipleStructureDefiningObject3() throws NoSuchMethodException, AmbiguousMethodException {
        // Test correct non-inheritance with parameters (same method name, different argument types).
        Assert.assertSame(multiple_structure_root,
                multiple_structure_root.definingObject("local_parameter_check", Integer.class, Integer.class));
    }

    @Test
    public void testMultipleStructureDefiningObject4() throws NoSuchMethodException, AmbiguousMethodException {
        Assert.assertEquals("SimpleStructure: find your superclass's not-overriden methods",
                MyClasses.C.class,
                multiple_structure_root.definingObject("local_parameter_check", String.class, Integer.class).getClass());
    }

    @Test
    public void testMultipleStructureInvocation1() throws ObjectInstantiationFailedException, NoSuchMethodException, MethodInvocationFailedException, AmbiguousMethodException {
        MyClasses.D d_obj = new MyClasses.D();

        // Test that invoking a regular local method works (public_local_check).
        String retval1 = (String) d_obj.invoke("print_C");
        Assert.assertEquals( "I'm from C class", retval1);

        String retval2 = (String) d_obj.invoke("print_B");
        Assert.assertEquals("I'm from B class", retval2);
    }

    @Test
    public void testMultipleStructureInvocation2() throws ObjectInstantiationFailedException, NoSuchMethodException, MethodInvocationFailedException, AmbiguousMethodException {
        MyClasses.D d_obj = new MyClasses.D();

        // Test that calls a method in D class (which is also defined in the parents).
        String retval = (String) d_obj.invoke("sameMethods");
        Assert.assertEquals( "I'm from D class", retval);
    }

    @Test(expected = NoSuchMethodException.class)
    public void testMultipleStructureInvocation3() throws NoSuchMethodException, ObjectInstantiationFailedException, MethodInvocationFailedException, AmbiguousMethodException {
        MyClasses.B b_obj = new MyClasses.B();
        b_obj.invoke("no_such_method", 32, "checking_inheritance", "another parameter");
    }

    @Test(expected = AmbiguousMethodException.class)
    public void testMultipleStructureInvocation4() throws AmbiguousMethodException, ObjectInstantiationFailedException {
        MyClasses.D d_obj = new MyClasses.D();

        try {
            String retval = (String) d_obj.invoke("sameMethodsForError");
            Assert.assertEquals( "I'm from B class", retval);
        } catch (NoSuchMethodException | MethodInvocationFailedException e) {
            fail("MultipleStructure: need to detect when methods are ambiguous");
        }
    }
}