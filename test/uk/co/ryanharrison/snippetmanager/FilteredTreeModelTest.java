/**
 * FilteredTreeModelTest.java
 */

package uk.co.ryanharrison.snippetmanager;

import static org.junit.Assert.assertEquals;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.junit.Test;

import uk.co.ryanharrison.snippetmanager.FilteredTreeModel;

/**
 * Test class for FilteredTreeModel
 * 
 * @author Ryan Harrison 
 */
public class FilteredTreeModelTest {

  /**
   * Test that a new object of FilteredTreeModel can be made successfully.
   */
  @Test
  public void testCreation() {
    TreeNode root = new DefaultMutableTreeNode("root node");
    TreeModel model = new DefaultTreeModel(root);

    FilteredTreeModel filteredModel = new FilteredTreeModel(model);

    // Make sure that the underlying tree model and the root node of the model are retrieved successfully
    assertEquals("Models are not the same", model, filteredModel.getTreeModel());
    assertEquals("Root nodes are not the same", root, filteredModel.getTreeModel().getRoot());
  }

}
