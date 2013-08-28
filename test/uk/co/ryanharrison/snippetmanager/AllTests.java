/**
 * AllTests.java
 */

package uk.co.ryanharrison.snippetmanager;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * JUnit test suite for SnippetManager. Runs all the tests.
 * 
 * @author Ryan Harrison
 */
@RunWith(Suite.class)
@SuiteClasses({ AboutDialogTest.class, DuplicateSnippetExceptionTest.class, FilteredTreeModelTest.class, FindReplaceTest.class,
    GoToDialogTest.class, HintTextFieldTest.class, LanguageTest.class, MainFrameTest.class, PreferencesDialogTest.class,
    PreferencesTest.class, SnippetFilterComparatorTest.class, SnippetInformationEditorTest.class, SnippetManagerTest.class,
    SnippetTest.class, SnippetTextPaneTest.class, SyntaxHighlighterTest.class, XMLFileChooserTest.class })
public class AllTests {

}
