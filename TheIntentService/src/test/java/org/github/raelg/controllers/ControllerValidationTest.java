package org.github.raelg.controllers;

import com.google.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: rael
 * Date: 02/05/2013
 * Time: 17:11
 */
@RunWith(PowerMockRunner.class)
public class ControllerValidationTest {

    @Test
    public void test_All_Controllers_Have_Inject_Annotation_On_Constructors() throws Exception {
        Reflections reflections = new Reflections("org.github.raelg");
        Set<Class<? extends BaseController>> subTypes = reflections.getSubTypesOf(BaseController.class);
        for (Class<? extends BaseController> subType : subTypes) {
            if (Modifier.isAbstract(subType.getModifiers())) {
                System.out.println("skipping: " + subType.getName());
                continue;
            }
            System.out.println("checking: " + subType.getName());

            Constructor<?>[] constructors = subType.getConstructors();
            assertEquals(constructors.length, 1);
            boolean annotationPresent = constructors[0].isAnnotationPresent(Inject.class);
            assertTrue(subType.getName() + " should have @Inject annotation on constructor", annotationPresent);


            // Check there is a static createIntent method
            Method[] methods = subType.getMethods();
            boolean found = false;
            for (Method method : methods) {
                if (method.getName().equals("createIntent")) {
                    assertTrue(Modifier.isStatic(method.getModifiers()));
                    found = true;
                }
            }
            assertTrue(found);
        }
    }

}
