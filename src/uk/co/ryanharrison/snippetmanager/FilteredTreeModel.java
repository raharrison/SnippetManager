package uk.co.ryanharrison.snippetmanager;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Custom tree model that wraps an underlying tree model into a filter so that only nodes that match the filter are displayed in the
 * tree view
 * 
 * @author Ryan Harrison
 */
public class FilteredTreeModel implements TreeModel {

  /** The underlying tree model that is being filtered */
  private TreeModel               treeModel;

  /** The filter text string that is used to determine which nodes should be displayed */
  private String                  filter;

  /** Comparator used to check whether or not a snippet matches the filter text */
  private SnippetFilterComparator comparator;

  /**
   * Create a new filtered tree model with specified underlying tree model that should be filtered
   * 
   * @param treeModel
   *          The underlying tree model that will be filtered
   */
  public FilteredTreeModel(TreeModel treeModel) {
    super();
    this.treeModel = treeModel;
    this.filter = "";
    this.comparator = new SnippetFilterComparator();
  }

  /**
   * Add a tree listener that monitors for changes in a trees model to the underlying tree model
   * 
   * @param l
   *          The listener to add
   * 
   * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
   */
  @Override
  public void addTreeModelListener(TreeModelListener l) {
    this.treeModel.addTreeModelListener(l);
  }

  /**
   * Apply a filter to a node, and all of its children recursively, to check whether it passes through the filter and so should be
   * displayed in the tree. The custom comparator is used to apply the filter to the node (if it is a snippet object).
   * 
   * @param node
   *          The node to check
   * @param filter
   *          The filter to apply
   * @return True if the node, or any of its children, pass the filter
   */
  private boolean applyFilter(Object node, String filter) {
    // Normalise the filter
    filter = filter.trim().toLowerCase();
    // Determine if the node matches only the name of the node first
    boolean matches = node.toString().toLowerCase().contains(filter);

    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
    // If the node is a snippet
    if (treeNode.getUserObject() instanceof Snippet) {
      // Use the custom comparator to check if any part of the snippet passes the filter
      matches |= this.comparator.isMatch((Snippet) treeNode.getUserObject(), filter);
    }

    // Get the number of children that this node has
    int children = this.treeModel.getChildCount(node);

    // Loop through every child node of the current node
    for (int i = 0; i < children; i++) {
      // Get the child node as an object
      Object child = this.treeModel.getChild(node, i);
      // Apply the filter to each child node as well
      matches |= this.applyFilter(child, filter);
    }

    return matches;
  }

  /**
   * Returns the child of the parent node at the specified index
   * 
   * @param parent
   *          The parent node
   * @param index
   *          The index corresponding to the child node to return
   * @return The child node of parent at index
   * 
   * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
   */
  @Override
  public Object getChild(Object parent, int index) {
    int counter = 0;
    // Get the max number of children
    int children = this.treeModel.getChildCount(parent);
    // Loop through each child
    for (int i = 0; i < children; i++) {
      // Get the child as an object
      Object child = this.treeModel.getChild(parent, i);
      // We are only interested in children that pass the filter
      if (this.applyFilter(child, this.filter)) {
        // If we have reached the index, return the child
        if (counter == index) {
          return child;
        }
        // Increment the counter
        counter++;
      }
    }
    return null;
  }

  /**
   * Return the number of children nodes that a parent node has. This applies the filter to the nodes and only takes into account
   * those that are matched by the filter
   * 
   * @param parent
   *          The parent node
   * @return The number of children nodes that the parent has
   * 
   * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
   */
  @Override
  public int getChildCount(Object parent) {
    int children = 0;
    // Get the max number of children from the model
    int maxChildren = this.treeModel.getChildCount(parent);
    // Loop through each child of the parent
    for (int i = 0; i < maxChildren; i++) {
      // Get the object representing the child
      Object child = this.treeModel.getChild(parent, i);
      // If the child passes the filter, increment the counter
      if (this.applyFilter(child, this.filter)) {
        children++;
      }
    }
    return children;
  }

  /**
   * Returns the index of childToFind in parent
   * 
   * @param parent
   *          The parent to search in
   * @param childToFind
   *          The child to find
   * @return The index of childToFind in parent
   * 
   * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
   */
  @Override
  public int getIndexOfChild(Object parent, Object childToFind) {
    // Get the overall number of children
    int children = this.treeModel.getChildCount(parent);
    // Loop through each child node
    for (int i = 0; i < children; i++) {
      // Get the child node as an object
      Object child = this.treeModel.getChild(parent, i);
      // If the child passes through the filter
      if (this.applyFilter(child, this.filter)) {
        // If the current child matches the child to found return the current index
        if (childToFind.equals(child)) {
          return i;
        }
      }
    }
    // If the child is not found return -1
    return -1;
  }

  /**
   * Return the root node of the current tree
   * 
   * @return The root node
   * 
   * @see javax.swing.tree.TreeModel#getRoot()
   */
  @Override
  public Object getRoot() {
    return this.treeModel.getRoot();
  }

  /**
   * Get the underlying tree model that is being filtered
   * 
   * @return The underlying tree model that is being filtered
   */
  public TreeModel getTreeModel() {
    return this.treeModel;
  }

  /**
   * Determines whether or not a node is a leaf node
   * 
   * @param node
   *          The node to check
   * @return True if the node is a leaf node, otherwise false
   * 
   * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
   */
  @Override
  public boolean isLeaf(Object node) {
    return this.treeModel.isLeaf(node);
  }

  /**
   * Remove a tree listener that monitors for changes in a trees model from the underlying tree model
   * 
   * @param listener
   *          The listener to remove
   * 
   * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
   */
  @Override
  public void removeTreeModelListener(TreeModelListener listener) {
    this.treeModel.removeTreeModelListener(listener);
  }

  /**
   * Set the filter text that will be used to filter the tree nodes
   * 
   * @param filter
   *          The new filter to use
   */
  public void setFilter(String filter) {
    this.filter = filter;
  }

  /**
   * Called when the value for the item identified by path is altered to newValue
   * 
   * @param path
   *          The path to the item
   * @param newValue
   *          The new value that the path has been altered to
   * 
   * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
   */
  @Override
  public void valueForPathChanged(TreePath path, Object newValue) {
    this.treeModel.valueForPathChanged(path, newValue);
  }
}
