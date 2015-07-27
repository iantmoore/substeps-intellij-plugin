package uk.co.itmoore.intellisubsteps.test;

import com.google.gson.Gson;
import com.intellij.openapi.util.IconLoader;
import com.technophobia.substeps.glossary.StepDescriptor;
import com.technophobia.substeps.glossary.StepImplementationsDescriptor;
import com.technophobia.substeps.glossary.XMLSubstepsGlossarySerializer;
import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SomeTests {


    @Test
    public void testLoadOfSubstepsMetaInfo() throws Exception {

        XMLSubstepsGlossarySerializer serializer = new XMLSubstepsGlossarySerializer();

        String path = "/home/ian/.m2/repository/com/technophobia/substeps/webdriver-substeps/1.1.2/webdriver-substeps-1.1.2.jar";

        JarFile jarFile = new JarFile(new File(path));


        final List<StepImplementationsDescriptor> stepImplementationDescriptors = serializer
                .loadStepImplementationsDescriptorFromJar(jarFile);

        for (StepImplementationsDescriptor d : stepImplementationDescriptors){


            for (StepDescriptor expr : d.getExpressions()){
                System.out.println("expression: " + expr.getExpression() + " description: " + expr.getDescription());
            }
        }

        Gson gson = new Gson();

        System.out.println("gson\n" +
                gson.toJson(stepImplementationDescriptors));

    }

    @Test
    public void testRegExAndnumericParameters() {


        String src = "Check <no_of_bets> bets placed";
//        String src = "Check 1 bets placed";

        String expression = "Check (\\d+) bets placed";

        String parameterRegEx = ".*(<[^>]*>).*";
        Pattern p2 = Pattern.compile(parameterRegEx);

        if(p2.matcher(src).matches()){
            String src2 = src.replaceAll("<[^>]*>", "");

            String expression2 = expression.replaceAll("\\([^\\)]*\\)", "");

            if (src2.equals(expression2)){
                System.out.println("Match!");
            }
            else {
                System.out.println("no match, expression2: " + expression2 + " src2: " + src2);
            }
        }
        else {

            Pattern p = Pattern.compile(expression);

            Matcher m = p.matcher(src);

            if (m.matches()) {

                System.out.println("m matches");

            } else {

                System.out.println("m not match");
            }

            System.out.println("group count: " + m.groupCount());
        }
    }
}
