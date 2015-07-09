package uk.co.itmoore.intellisubsteps.test;

import com.intellij.openapi.util.IconLoader;
import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;

public class SomeTests{

        @Test
        public void aTest(){

                Icon icon = IconLoader.getIcon("/icons/cucumber.png", SomeTests.class);
                Assert.assertNotNull(icon);

        }
}
