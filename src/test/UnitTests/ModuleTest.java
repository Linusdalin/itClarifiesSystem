package test.UnitTests;

import module.ContractingModule;
import module.ModuleInterface;
import module.ModuleNode;
import org.junit.Test;
import test.PukkaTest;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class ModuleTest extends PukkaTest{

    /******************************************************************
     *
     *
     *                  Unit tests for the module concept
     *
     *
     */



    @Test
    public void getNodeForTag(){

        ModuleInterface contracting = new ContractingModule();

        ModuleNode node = contracting.getNodeForTag("#DATE");

        assertNotNull(node);
        assertThat(node.type.getName(), is("#DATE"));

    }


    @Test
    public void getChildren(){

        ModuleInterface contracting = new ContractingModule();

        ModuleNode node = contracting.getNodeForTag("#DATE");
        List<String> children = contracting.getChildren(node);

        assertThat(children.size(), is( 0 ));

        System.out.println(children);

        node = contracting.getNodeForTag("#General");
        children = contracting.getChildren(node);

        System.out.println(children);

        assertThat(children.size(), is( 7 ));

    }

    @Test
    public void wrongNode(){

        ModuleInterface contracting = new ContractingModule();

        ModuleNode node = contracting.getNodeForTag("#THISDOESNOTEXIST");
        List<String> children = contracting.getChildren(node);

        assertThat(children.size(), is( 0 ));


    }


}

