package MyTests;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import MyException.AmbiguousMethodException;
import MyException.MethodInvocationFailedException;
import MyException.NoSuchMethodException;
import MyException.ObjectInstantiationFailedException;
import MultipleInheritance.MyObject;
import MultipleInheritance.Parent;

public class MyVirtualTests {
    private static MyVirtualClasses.C simple_virtual_instance;
    private static MyVirtualClasses.A simple_diamond_root;

    @BeforeClass
    public static void initialize_fields() throws ObjectInstantiationFailedException {
        MyVirtualClasses.setConstruction_order(new ArrayList<>());
        simple_virtual_instance = new MyVirtualClasses.C();
        simple_diamond_root = new MyVirtualClasses.A();
    }

    @Test
    public void testVirtualClasses1() {
        // Checks virtual inheritance is registered in the system as inheritance.
        Assert.assertTrue(simple_virtual_instance.multInheritsFrom(MyVirtualClasses.D.class));
    }

    @Test
    public void testVirtualClasses2() {
        // Checks the system recognized inheritance when it passes virtual and then regular inheritance.
        Assert.assertTrue(simple_virtual_instance.multInheritsFrom(MyVirtualClasses.E.class));
    }


    @Test
    public void testVirtualClasses3() throws ObjectInstantiationFailedException {
        MyVirtualClasses.setConstruction_order(new ArrayList<>());
        List<String> expected_order = new ArrayList<>(Arrays.asList("E", "D", "F", "B", "C", "A"));
        MyVirtualClasses.A a_obj = new MyVirtualClasses.A();
        Assert.assertEquals(expected_order, MyVirtualClasses.getConstruction_order());
    }

    @Test
    public void testVirtualClasses4() throws ObjectInstantiationFailedException, NoSuchMethodException, AmbiguousMethodException {
        // Checks classes with single virtual inheritance can inherit methods correctly.
        Assert.assertEquals(MyVirtualClasses.D.class,
                new MyVirtualClasses.B().definingObject("public_to_inherit").getClass());
    }

    @Test
    public void testVirtualClasses5() throws NoSuchMethodException, AmbiguousMethodException {
        // Checks method inheritance flows smoothly through regular (multiple) and virtual inheritance together.
        Assert.assertEquals(MyVirtualClasses.E.class,
                simple_diamond_root.definingObject("older_public_method").getClass());
    }

    @Test(expected = AmbiguousMethodException.class)
    public void testVirtualClasses6() throws NoSuchMethodException, AmbiguousMethodException {
        // Checks virtual diamond inheritance does not solve accidental ambiguity.
        simple_diamond_root.definingObject("almost_accidental_ambiguate");
    }
}