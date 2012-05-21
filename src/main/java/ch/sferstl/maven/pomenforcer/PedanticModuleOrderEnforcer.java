package ch.sferstl.maven.pomenforcer;

import java.util.List;
import java.util.Set;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import ch.sferstl.maven.pomenforcer.reader.DeclaredModulesReader;


public class PedanticModuleOrderEnforcer extends AbstractPedanticEnforcer {

  /** All modules in this set won't be checked for the correct order. */
  private final Set<String> ignoredModules;

  public PedanticModuleOrderEnforcer() {
    this.ignoredModules = Sets.newLinkedHashSet();
  }

  public void setIgnoredModules(String ignoredModules) {
    this.splitAndAddToCollection(ignoredModules, this.ignoredModules);
  }

  @Override
  public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
    MavenProject project = getMavenProject(helper);
    // Do nothing if the project is not a parent project
    if (!isPomProject(project)) {
      return;
    }

    Log log = helper.getLog();
    log.info("Enforcing alphabetical module order.");
    log.info("  -> These modules are ignored: " + COMMA_JOINER.join(this.ignoredModules));

    // Read the POM
    Document pomDoc = XmlParser.parseXml(project.getFile());

    // Remove all modules to be ignored.
    List<String> declaredModules = new DeclaredModulesReader(pomDoc).read();
    declaredModules.removeAll(this.ignoredModules);

    // Enforce the module order
    Ordering<String> moduleOrdering = Ordering.natural();
    if (!moduleOrdering.isOrdered(declaredModules)) {
      ImmutableList<String> orderedModules = moduleOrdering.immutableSortedCopy(declaredModules);
      String message = "One does not simply declare modules! "
          + "You have to sort your modules alphabetically: " + orderedModules;
      if (!this.ignoredModules.isEmpty()) {
        message += " You may place these modules in any order: " + this.ignoredModules;
      }
      throw new EnforcerRuleException(message);
    }
  }

  private boolean isPomProject(MavenProject project) {
    return "pom".equals(project.getPackaging());
  }
}
