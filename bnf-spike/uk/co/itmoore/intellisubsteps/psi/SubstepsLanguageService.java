package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

import java.io.InputStream;

/**
 * @author yole
 */
public class SubstepsLanguageService {
  private SubstepsKeywordProvider myKeywordProvider;

  public static SubstepsLanguageService getInstance(Project project) {
    return ServiceManager.getService(project, SubstepsLanguageService.class);
  }

  @SuppressWarnings("UnusedParameters")
  public SubstepsLanguageService(Project project) {
  }

  public SubstepsKeywordProvider getKeywordProvider() {
    if (myKeywordProvider == null) {
      final ClassLoader classLoader = SubstepsLanguageService.class.getClassLoader();
      if (classLoader != null) {
        @SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
        final InputStream inputStream = classLoader.getResourceAsStream("i18n.json");
        if (inputStream != null) {
//          myKeywordProvider = new JsonSubstepsKeywordProvider(inputStream);
        }
      }

      if (myKeywordProvider == null) {
        myKeywordProvider = new PlainSubstepsKeywordProvider();
      }
    }
    return myKeywordProvider;
  }
}
