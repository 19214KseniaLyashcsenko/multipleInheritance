package MultipleInheritance;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Parents.class)
public @interface Parent {
    Class<?> parent(); // Default case will not be checked
    boolean isVirtual() default false;
}