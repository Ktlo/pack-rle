package spbstu.ktlo.task2.cli;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Parameter {

    String name();
    String usage() default "";

}
